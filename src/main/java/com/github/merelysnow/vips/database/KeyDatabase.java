package com.github.merelysnow.vips.database;

import com.github.merelysnow.vips.database.connection.RepositoryProvider;
import com.github.merelysnow.vips.model.Key;
import com.google.common.reflect.TypeToken;
import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import com.henryfabio.sqlprovider.connector.SQLConnector;
import com.henryfabio.sqlprovider.executor.SQLExecutor;
import com.henryfabio.sqlprovider.executor.adapter.SQLResultAdapter;
import com.henryfabio.sqlprovider.executor.result.SimpleResultSet;
import org.bukkit.plugin.Plugin;

import java.lang.reflect.Type;
import java.time.Instant;
import java.util.Set;

public class KeyDatabase extends RepositoryProvider {

    private static final String TABLE_NAME = "user_table";
    private static final Gson GSON = new GsonBuilder().create();
    private SQLExecutor sqlExecutor;

    public KeyDatabase(Plugin plugin) {
        super(plugin);
        this.prepare();

        this.handleTable();
    }

    @Override
    public SQLConnector prepare() {
        final SQLConnector connector = super.prepare();
        this.sqlExecutor = new SQLExecutor(connector);

        return connector;
    }

    public void handleTable() {
        this.sqlExecutor.updateQuery("CREATE TABLE IF NOT EXISTS " + TABLE_NAME + "(" +
                "id VARCHAR(9) NOT NULL PRIMARY KEY," +
                "grupo TEXT NOT NULL," +
                "instant TEXT NOT NULL" +
                ");");
    }

    public Set<Key> selectMany() {
        return this.sqlExecutor.resultManyQuery(
                "SELECT * FROM " + TABLE_NAME, simpleStatement -> {}, KeyDatabaseAdapter.class);
    }

    public void insert(Key key) {
        this.sqlExecutor.updateQuery("REPLACE INTO " + TABLE_NAME + " VALUES(?,?,?)", simpleStatement -> {
            simpleStatement.set(1, key.getId());
            simpleStatement.set(2, key.getGroup());
            simpleStatement.set(3, GSON.toJson(key.getInstant()));
        });
    }

    public void deleteOne(Key key) {
        this.sqlExecutor.updateQuery(
                "DELETE FROM " + TABLE_NAME + " WHERE id = ?",
                simpleStatement -> simpleStatement.set(1, key.getId()));
    }

    public static class KeyDatabaseAdapter implements SQLResultAdapter<Key> {

        @Override
        public Key adaptResult(SimpleResultSet resultSet) {

            Type type = new TypeToken<Instant>() {}.getType();

            return new Key(
                    resultSet.get("id"),
                    resultSet.get("grupo"),
                    GSON.fromJson((String) resultSet.get("instant"), type)
            );
        }
    }
}
