package com.ani.simpletodo;

import android.app.DatePickerDialog;
import android.app.DialogFragment;
import android.graphics.Color;
import android.graphics.Point;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.view.Display;
import android.view.Gravity;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.Window;
import android.view.WindowManager;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.DatePicker;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.Spinner;
import android.widget.TextView;

import java.text.DateFormat;
import java.util.Calendar;

public class EditItemFragment extends DialogFragment {
    private static final String ITEM_ID_KEY = "id";
    private static final String ITEM_KEY = "item";

    private Long itemId;

    private EditText mEditText;
    private TextView tvDate;
    private Spinner spinnerPriority;
    private ImageView ivPriority;
    private Spinner spinnerCompleted;

    private final Calendar calendar = Calendar.getInstance();

    public EditItemFragment() {
        // Empty constructor is required for DialogFragment
    }

    public static EditItemFragment newInstance(long id, TodoItem item) {
        EditItemFragment frag = new EditItemFragment();
        Bundle args = new Bundle();
        args.putLong(ITEM_ID_KEY, id);
        args.putByteArray(ITEM_KEY, item.toBytes());

        frag.setArguments(args);
        return frag;
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        return inflater.inflate(R.layout.fragment_edit_item, container);
    }

    @Override
    public void onViewCreated(View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);
        itemId = getArguments().getLong(ITEM_ID_KEY);
        TodoItem item = TodoItem.fromBytes(getArguments().getByteArray(ITEM_KEY));
        calendar.setTime(item.date());

        mEditText = (EditText) view.findViewById(R.id.etEditItem);
        Button btnSaveItem = (Button) view.findViewById(R.id.btnSaveItem);
        tvDate = (TextView) view.findViewById(R.id.tvDate);
        spinnerPriority = (Spinner) view.findViewById(R.id.spinnerPriority);
        ivPriority = (ImageView) view.findViewById(R.id.ivPriority);
        spinnerCompleted = (Spinner) view.findViewById(R.id.spinnerCompleted);

        mEditText.setText(item.name());
        mEditText.requestFocus();

        btnSaveItem.setOnClickListener(new OnSaveClickListener());

        setDate();
        tvDate.setOnClickListener(new OnDateClickListener());

        initSpinner(spinnerPriority, R.array.priority_array, item.priority().ordinal());
        spinnerPriority.setOnItemSelectedListener(new OnPrioritySelectedListener());

        initSpinner(spinnerCompleted, R.array.completed_array, item.status().ordinal());
    }

    private void initSpinner(Spinner spinner, int textArrayResId, int selection) {
        ArrayAdapter<CharSequence> spinnerAdapter = ArrayAdapter.createFromResource(
                getActivity(), textArrayResId, android.R.layout.simple_spinner_item);
        spinnerAdapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
        spinner.setAdapter(spinnerAdapter);
        spinner.setSelection(selection);
    }

    private class OnSaveClickListener implements Button.OnClickListener {
        @Override
        public void onClick(View v) {
            Listener listener = (Listener) getActivity();
            TodoItem.Priority priority = TodoItem.Priority
                    .values()[spinnerPriority.getSelectedItemPosition()];
            TodoItem.Status status = TodoItem.Status
                    .values()[spinnerCompleted.getSelectedItemPosition()];
            TodoItem updatedItem = new TodoItem.Builder()
                    .setName(mEditText.getText().toString())
                    .setDate(calendar.getTime())
                    .setPriority(priority)
                    .setStatus(status)
                    .build();

            listener.onFinishEditDialog(itemId, updatedItem);
            dismiss();
        }
    }

    private class OnDateClickListener implements View.OnClickListener {
        @Override
        public void onClick(View v) {
            new DatePickerDialog(getActivity(), new DatePickerDialog.OnDateSetListener() {
                @Override
                public void onDateSet(DatePicker view, int year, int month, int dayOfMonth) {
                    calendar.set(year, month, dayOfMonth);
                    setDate();
                }
            }, calendar.get(Calendar.YEAR), calendar.get(Calendar.MONTH), calendar.get(Calendar.DAY_OF_MONTH)).show();
        }
    }

    private class OnPrioritySelectedListener implements AdapterView.OnItemSelectedListener {
        @Override
        public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {
            ivPriority.setColorFilter(flagColor(position));
        }

        @Override
        public void onNothingSelected(AdapterView<?> parent) {

        }

        private int flagColor(int position) {
            switch (position) {
                case 0: // high
                    return Color.parseColor("#F44336");
                case 1: // medium
                    return Color.parseColor("#FF9800");
                case 2: // low
                default:
                    return Color.BLACK;
            }
        }
    }

    private void setDate() {
        DateFormat dateFormat = android.text.format.DateFormat.getMediumDateFormat(
                getActivity().getApplicationContext());
        tvDate.setText(dateFormat.format(calendar.getTime()));
    }

    @Override
    public void onResume() {
        // http://guides.codepath.com/android/Using-DialogFragment#runtime-dimensions

        // Store access variables for window and blank point
        Window window = getDialog().getWindow();
        Point size = new Point();
        // Store dimensions of the screen in `size`
        Display display = window.getWindowManager().getDefaultDisplay();
        display.getSize(size);
        window.setLayout(size.x, WindowManager.LayoutParams.WRAP_CONTENT);
        window.setGravity(Gravity.CENTER);
        // Call super onResume after sizing
        super.onResume();
    }

    public interface Listener {
        void onFinishEditDialog(long id, TodoItem item);
    }
}
