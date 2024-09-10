package com.ewyboy.mineconomy;

import net.minecraft.core.registries.BuiltInRegistries;

public class Mineconomy {

    public static void init() {
        DatabaseManager.openConnection();

        DatabaseManager.runQuery("""
                CREATE TABLE IF NOT EXISTS items (
                 id integer PRIMARY KEY,
                 name text NOT NULL,
                 price integer
                );"""
        );

        BuiltInRegistries.ITEM.forEach(item -> DatabaseManager.runQuery(
                "INSERT INTO items (name, price) VALUES ('" + BuiltInRegistries.ITEM.getKey(item) + "', 0);")
        );

        DatabaseManager.runQuery("""
                CREATE TABLE IF NOT EXISTS blocks (
                 id integer PRIMARY KEY,
                 name text NOT NULL,
                 price integer
                );"""
        );

        BuiltInRegistries.BLOCK.forEach(block -> DatabaseManager.runQuery(
                "INSERT INTO blocks (name, price) VALUES ('" + BuiltInRegistries.BLOCK.getKey(block) + "', 0);")
        );

        DatabaseManager.closeConnection();
    }

}