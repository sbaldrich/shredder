package com.bc.shredder.store;

public interface Store<T>{
    T save(T t);
    T fetch(long id);
}
