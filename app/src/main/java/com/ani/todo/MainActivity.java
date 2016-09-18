package com.ani.todo;

import android.app.FragmentManager;
import android.content.Context;
import android.database.Cursor;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.view.View;
import android.widget.AdapterView;
import android.widget.CursorAdapter;
import android.widget.EditText;
import android.widget.ListView;
import android.widget.Toast;

public class MainActivity extends AppCompatActivity implements EditItemFragment.Listener,
        AdapterView.OnItemClickListener, AdapterView.OnItemLongClickListener {

    private TodoStore databaseHelper;
    private CursorAdapter adapter;
    private EditText etItem;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        databaseHelper = TodoStore.instance(getApplicationContext());
        // FIXME : This may be inefficient with many rows
        adapter = new TodoCursorAdapter(this, databaseHelper.getAll(), false);

        etItem = (EditText) findViewById(R.id.etNewItem);
        ListView lvItems = (ListView) findViewById(R.id.lvItems);
        lvItems.setAdapter(adapter);

        lvItems.setOnItemLongClickListener(this);
        lvItems.setOnItemClickListener(this);
    }

    @Override
    public boolean onItemLongClick(AdapterView<?> parent, View view, int position, long id) {
        TodoItem item = databaseHelper.get(id);
        if (item.status() == TodoItem.Status.COMPLETED) {
            databaseHelper.delete(adapter.getItemId(position));
        } else {
            databaseHelper.update(id,
                    new TodoItem.Builder(item).setStatus(TodoItem.Status.COMPLETED).build());
        }
        adapter.swapCursor(databaseHelper.getAll());
        return true;
    }

    @Override
    public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
        Cursor cursor = (Cursor) adapter.getItem(position);
        long itemId = cursor.getLong(cursor.getColumnIndexOrThrow("_id"));
        TodoItem item = TodoItem.fromBytes(cursor.getBlob(
                cursor.getColumnIndexOrThrow("data")));
        showEditDialog(itemId, item);
    }

    private void showEditDialog(long id, TodoItem item) {
        FragmentManager fm = getFragmentManager();
        EditItemFragment editNameDialogFragment = EditItemFragment.newInstance(id, item);
        editNameDialogFragment.show(fm, "fragment_edit_item");
    }

    @Override
    public void onFinishEditDialog(long id, TodoItem item) {
        databaseHelper.update(id, item);
        adapter.swapCursor(databaseHelper.getAll());
    }

    public void onAddItem(View view) {
        if (etItem.getText().toString().isEmpty()) {
            showNoContentToast();
            return;
        }

        databaseHelper.insert(new TodoItem.Builder().setName(etItem.getText().toString()).build());
        adapter.swapCursor(databaseHelper.getAll());
        etItem.setText("");
    }

    private void showNoContentToast() {
        Context context = getApplicationContext();
        CharSequence text = "Content is required";
        int duration = Toast.LENGTH_SHORT;

        Toast toast = Toast.makeText(context, text, duration);
        toast.show();
    }
}
