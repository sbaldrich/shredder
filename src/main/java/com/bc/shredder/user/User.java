package com.bc.shredder.user;

public class User {
    private long userId;
    private String handle;

    public User(long userId, String handle) {
        this.userId = userId;
        this.handle = handle;
    }

    public User(String handle) {
        this.handle = handle;
    }

    public long getUserId() {
        return userId;
    }

    public String getHandle() {
        return handle;
    }

    @Override
    public String toString() {
        return String.format("User {userId: %d, handle: %s}", userId, handle);
    }
}
