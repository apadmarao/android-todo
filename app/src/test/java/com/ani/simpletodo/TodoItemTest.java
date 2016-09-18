package com.ani.simpletodo;

import org.junit.Test;

import java.util.Calendar;
import java.util.Date;
import java.util.GregorianCalendar;
import java.util.UUID;

import static org.junit.Assert.assertEquals;

public class TodoItemTest {
    @Test
    public void testSerDe() throws Exception {
        TodoItem item = new TodoItem(UUID.randomUUID().toString());
        assertEquals(TodoItem.fromBytes(item.toBytes()), item);
    }
}
