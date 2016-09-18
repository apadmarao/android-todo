package com.ani.todo;

import android.content.Context;
import android.database.Cursor;
import android.graphics.Color;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.CursorAdapter;
import android.widget.TextView;

import java.text.DateFormat;
import java.util.Calendar;
import java.util.Date;

public class TodoCursorAdapter extends CursorAdapter {

    public TodoCursorAdapter(Context context, Cursor c, boolean autoRequery) {
        super(context, c, autoRequery);
    }

    @Override
    public View newView(Context context, Cursor cursor, ViewGroup parent) {
        return LayoutInflater.from(context).inflate(R.layout.item, parent, false);
    }

    @Override
    public void bindView(View view, Context context, Cursor cursor) {
        TextView tvBody = (TextView) view.findViewById(R.id.tvName);
        TodoItem item = TodoItem.fromBytes(cursor.getBlob(cursor.getColumnIndexOrThrow("data")));

        tvBody.setText(item.name());
        if (item.status() == TodoItem.Status.COMPLETED) {
            tvBody.setTextColor(Color.GRAY);
            tvBody.getPaint().setStrikeThruText(true);
        } else {
            tvBody.setTextColor(Color.BLACK);
            tvBody.getPaint().setStrikeThruText(false);
        }

        View viewPriority = view.findViewById(R.id.viewPriority);
        viewPriority.setBackgroundColor(priorityColor(item.priority()));

        TextView tvItemDate = (TextView) view.findViewById(R.id.tvItemDate);
        DateFormat dateFormat = android.text.format.DateFormat.getMediumDateFormat(context);
        tvItemDate.setText(dateFormat.format(item.date()));

        TodoItem prevItem = null;
        // get previous item's date, for comparison
        if (cursor.getPosition() > 0 && cursor.moveToPrevious()) {
            prevItem = TodoItem.fromBytes(cursor.getBlob(cursor.getColumnIndexOrThrow("data")));
            cursor.moveToNext();
        }

        // enable section heading if it's the first one, or
        // different from the previous one
        if (prevItem == null || !equals(prevItem.date(), item.date())) {
            tvItemDate.setVisibility(View.VISIBLE);
        } else {
            tvItemDate.setVisibility(View.GONE);
        }
    }

    // Dates are equal iff year + month + day are the same
    private static boolean equals(Date date1, Date date2) {
        Calendar cal1 = Calendar.getInstance();
        cal1.setTime(date1);

        Calendar cal2 = Calendar.getInstance();
        cal2.setTime(date2);

        return cal1.get(Calendar.YEAR) == cal2.get(Calendar.YEAR)
                && cal1.get(Calendar.MONTH) == cal2.get(Calendar.MONTH)
                && cal1.get(Calendar.DAY_OF_MONTH) == cal2.get(Calendar.DAY_OF_MONTH);
    }

    private static int priorityColor(TodoItem.Priority priority) {
        switch (priority) {
            case HIGH:
                return Color.parseColor("#F44336");
            case MEDIUM:
                return Color.parseColor("#FF9800");
            case LOW:
            default:
                return Color.TRANSPARENT;
        }
    }
}
