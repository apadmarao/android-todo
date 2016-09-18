package com.ani.todo;

import org.junit.Test;

import java.util.UUID;

import static org.junit.Assert.assertEquals;

public class TodoItemTest {
    @Test
    public void testSerDe() throws Exception {
        TodoItem item = new TodoItem(UUID.randomUUID().toString());
        assertEquals(TodoItem.fromBytes(item.toBytes()), item);
    }
}
