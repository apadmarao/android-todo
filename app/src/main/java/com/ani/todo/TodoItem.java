package com.ani.todo;

import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.io.DataInputStream;
import java.io.DataOutputStream;
import java.io.IOException;
import java.util.Date;

public class TodoItem {

    public enum Priority {
        HIGH,
        MEDIUM,
        LOW
    }

    public enum Status {
        COMPLETED,
        UNCOMPLETED
    }

    private final String name;
    private final Date date;
    private final Priority priority;
    private final Status status;

    private TodoItem(String name, Date date, Priority priority, Status status) {
        this.name = name;
        this.date = date;
        this.priority = priority;
        this.status = status;
    }

    public String name() {
        return name;
    }

    public Date date() {
        return date;
    }

    public Priority priority() {
        return priority;
    }

    public Status status() {
        return status;
    }

    @Override
    public boolean equals(Object obj) {
        if (!(obj instanceof TodoItem)) {
            return false;
        }
        TodoItem that = (TodoItem) obj;

        return name.equals(that.name) && date.equals(that.date);
    }

    @Override
    public String toString() {
        return String.format("TodoItem{name=%s}", name);
    }

    public byte[] toBytes() {
        ByteArrayOutputStream bos = new ByteArrayOutputStream();
        DataOutputStream out = null;
        try {
            out = new DataOutputStream(bos);
            out.writeUTF(name);
            out.writeLong(date.getTime());
            out.writeInt(priority.ordinal());
            out.writeBoolean(status.ordinal() == 0);
            return bos.toByteArray();
        } catch (IOException e) {
            throw new RuntimeException(e);
        } finally {
            try {
                if (out != null) {
                    out.close();
                }
            } catch (IOException ex) {
                // ignore close exception
            }
            try {
                bos.close();
            } catch (IOException ex) {
                // ignore close exception
            }
        }
    }

    public static TodoItem fromBytes(byte[] bytes) {
        ByteArrayInputStream bis = new ByteArrayInputStream(bytes);
        DataInputStream in = null;
        try {
            in = new DataInputStream(bis);
            String name = in.readUTF();
            long dueDate = in.readLong();
            Priority priority = Priority.values()[in.readInt()];
            Status status = Status.values()[in.readBoolean() ? 0 : 1];

            return new Builder()
                    .setName(name)
                    .setDate(dueDate)
                    .setPriority(priority)
                    .setStatus(status)
                    .build();
        } catch (IOException e) {
            throw new RuntimeException(e);
        } finally {
            try {
                bis.close();
            } catch (IOException ex) {
                // ignore close exception
            }
            try {
                if (in != null) {
                    in.close();
                }
            } catch (IOException ex) {
                // ignore close exception
            }
        }
    }

    public static class Builder {
        private String name;
        private Date date;
        private Priority priority;
        private Status status;

        public Builder() {
        }

        public Builder(TodoItem item) {
            name = item.name();
            date = item.date();
            priority = item.priority();
            status = item.status();
        }

        public Builder setName(String name) {
            this.name = name;
            return this;
        }

        public Builder setDate(long date) {
            this.date = new Date(date);
            return this;
        }

        public Builder setDate(Date date) {
            this.date = date;
            return this;
        }

        public Builder setPriority(Priority priority) {
            this.priority = priority;
            return this;
        }

        public Builder setStatus(Status status) {
            this.status = status;
            return this;
        }

        public TodoItem build() {
            requireNonNull(name);

            if (date == null) {
                date = new Date();
            }

            if (priority == null) {
                priority = Priority.LOW;
            }

            if (status == null) {
                status = Status.UNCOMPLETED;
            }

            return new TodoItem(name, date, priority, status);
        }
    }

    private static <T> T requireNonNull(T obj) {
        if (obj == null)
            throw new NullPointerException();
        return obj;
    }
}
