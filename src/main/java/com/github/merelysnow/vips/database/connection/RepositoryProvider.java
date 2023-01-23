package com.github.merelysnow.vips.database.connection;

import com.henryfabio.sqlprovider.connector.SQLConnector;
import com.henryfabio.sqlprovider.connector.type.SQLDatabaseType;
import com.henryfabio.sqlprovider.connector.type.impl.MySQLDatabaseType;
import com.henryfabio.sqlprovider.connector.type.impl.SQLiteDatabaseType;
import lombok.AllArgsConstructor;
import lombok.Data;
import org.bukkit.configuration.ConfigurationSection;
import org.bukkit.configuration.file.FileConfiguration;
import org.bukkit.plugin.Plugin;

import java.io.File;
import java.util.logging.Logger;

@AllArgsConstructor
public class RepositoryProvider {

    private final Plugin plugin;

    public SQLConnector prepare() {
        FileConfiguration configuration = plugin.getConfig();
        ConfigurationSection databaseSection = configuration.getConfigurationSection("database");

        String sqlType = databaseSection.getString("type");
        Logger logger = plugin.getLogger();

        SQLConnector sqlConnector;

        if (sqlType.equalsIgnoreCase("mysql")) {

            ConfigurationSection mysqlSection = databaseSection.getConfigurationSection("mysql");
            sqlConnector = mySqlDatabaseType(mysqlSection).connect();

        } else if (sqlType.equalsIgnoreCase("sqlite")) {

            ConfigurationSection sqliSection = databaseSection.getConfigurationSection("sqlite");
            sqlConnector = sqLiteDatabase(sqliSection).connect();

        } else {
            logger.severe("Database type is invalid");
            return null;
        }

        return sqlConnector;
    }

    private SQLDatabaseType sqLiteDatabase(ConfigurationSection section) {
        return SQLiteDatabaseType.builder()
                .file(new File(plugin.getDataFolder(), section.getString("file")))
                .build();
    }

    private SQLDatabaseType mySqlDatabaseType(ConfigurationSection section) {
        return MySQLDatabaseType.builder()
                .address(section.getString("address"))
                .username(section.getString("username"))
                .password(section.getString("password"))
                .database(section.getString("database"))
                .build();
    }
}
