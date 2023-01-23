package com.github.merelysnow.vips.controller;

import com.github.merelysnow.vips.database.KeyDatabase;
import com.github.merelysnow.vips.model.Key;
import lombok.RequiredArgsConstructor;

import java.util.HashMap;

@RequiredArgsConstructor
public class KeysController {

    private final HashMap<String, Key> cache = new HashMap<>();
    private final KeyDatabase keyDatabase;

    public void registerKey(Key key) {
        this.cache.put(key.getId(), key);
        this.keyDatabase.insert(key);
    }

    public void unregisterKey(Key key) {
        this.cache.remove(key.getId());
        this.keyDatabase.deleteOne(key);
    }

    public Key get(String ID) {
        if(this.cache.containsKey(ID)) {
            return this.cache.get(ID);
        }

        return null;
    }
}
