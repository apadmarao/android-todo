package com.ani.simpletodo;

import android.test.AndroidTestCase;

import org.junit.After;
import org.junit.Before;

import java.util.UUID;

public class TodoStoreTest extends AndroidTestCase {
    private TodoStore db;

    @Before
    public void setUp() throws Exception {
        db = new TodoStore(mContext);
        // recreate the database
        db.drop();
        db.onCreate(db.getWritableDatabase());
    }

    @After
    public void tearDown() throws Exception {
        db.drop();
        db.close();
    }

    public void testInsert() throws Exception {
        final TodoItem item = randomItem();
        long id = db.insert(item);
        assertEquals(db.get(id), item);
    }

    public void testUpdate() throws Exception {
        final TodoItem item = randomItem();
        long id = db.insert(item);
        final TodoItem updated = new TodoItem("hello");
        db.update(id, updated);
        assertEquals(db.get(id), updated);
    }

    public void testDelete() throws Exception {
        final TodoItem item = randomItem();
        long id = db.insert(item);
        assertEquals(db.get(id), item); // check insert worked
        db.delete(id);
        assertNull(db.get(id));
    }

    private TodoItem randomItem() {
        return new TodoItem(UUID.randomUUID().toString());
    }
}
