package com.bc.shredder.user;

import com.bc.shredder.store.DataManager;
import com.bc.shredder.store.Store;

public interface UserStore extends Store<User> {
    static UserStore get(DataManager manager){
        return new UserStoreImpl(manager);
    }
}
