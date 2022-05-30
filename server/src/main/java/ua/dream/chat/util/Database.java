package ua.dream.chat.util;

import com.zaxxer.hikari.HikariConfig;
import com.zaxxer.hikari.HikariDataSource;
import lombok.*;
import ua.dream.chat.util.logger.Logger;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.util.Arrays;
import java.util.concurrent.CompletableFuture;
import java.util.logging.Level;

@Getter
@RequiredArgsConstructor
public class Database {

    private final String user;
    private final String host;
    private final String databaseName;
    private final String password;

    private Connection connection = null;

    @SneakyThrows
    public Connection getConnection() {
        if (connection == null || connection.isClosed() || !connection.isValid(1000)) {
            val config = new HikariConfig();

            config.setDriverClassName("com.mysql.jdbc.Driver");
            config.setJdbcUrl("jdbc:mysql://" + host + ":3306/" + databaseName + "?characterEncoding=utf8&useUnicode=true");

            config.setUsername(user);
            config.setPassword(password);

            config.addDataSourceProperty("prepStmtCacheSize", "250");
            config.addDataSourceProperty("prepStmtCacheSqlLimit", "2048");

            val dataSource = new HikariDataSource(config);

            connection = dataSource.getConnection();
        }

        return connection;
    }

    @SneakyThrows
    private PreparedStatement createStatement(String query, Object... args) {
        val statement = getConnection().prepareStatement(query);

        for (int i = 0; i < args.length; i++) {
            statement.setObject(i + 1, args[i]);
        }

        return statement;
    }

    @SneakyThrows
    public void update(String query, Object... args) {
        try(val statement = createStatement(query, args)) {
            statement.executeUpdate();
        } catch (Throwable t) {
            Logger.LOGGER.log(Level.SEVERE, "Error while update " + query + " " + Arrays.toString(args), t);
        }
    }

    @SneakyThrows
    public void updateAsync(String query, Object... args) {
        CompletableFuture.runAsync(() -> update(query, args));
    }

    @SneakyThrows
    public ResultSet query(String query, Object... args) {
        try {
            val statement = createStatement(query, args);

            return statement.executeQuery();
        } catch (Throwable t) {
            Logger.LOGGER.log(Level.SEVERE, "Error while query " + query + " " + Arrays.toString(args), t);
            return null;
        }
    }

    @SneakyThrows
    public CompletableFuture<ResultSet> queryAsync(String query, Object... args) {
        return CompletableFuture.supplyAsync(() -> query(query, args));
    }

}
