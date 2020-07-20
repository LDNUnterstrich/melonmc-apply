package net.melonia.coinsapi.sql;

import net.melonia.coinsapi.sql.creditials.SQLCreditals;

import java.sql.*;
import java.util.UUID;
import java.util.concurrent.CompletableFuture;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

public class SQLConnection {

    private Connection connection;
    private SQLCreditals sqlCreditals;

    public SQLConnection(final SQLCreditals sqlCreditals, final String database) throws SQLException {
        this.sqlCreditals = sqlCreditals;

        this.connect(database);
    }

    /**
     * Stelle eine Verbindung zur SQL-Datenbank her
     */
    private void connect(final String database) throws SQLException {
        if(this.isConnected())this.close();

        this.connection = DriverManager.getConnection(this.sqlCreditals.toURI());

    }

    /**
     * Bricht die Verbindung zur SQL-Datenbank ab.
     */
    private void close(){
        try {
            this.connection.close();
            System.out.println("Die aktuelle SQL-Verbindung wurde aufgelöst.");
        } catch (SQLException throwables) {
            System.out.println("Die aktuelle SQL-Verbindung konnte nicht aufgelöst werden: " +
                    throwables.getMessage());
        }
    }

    /**
     * Überprüft, ob eine Verbindung existiert
     * @return
     * @throws SQLException
     */
    private boolean isConnected() throws SQLException {
        if(this.connection == null)return false;
        return this.connection.isClosed();
    }

    /**
     * Speicher einen Spieler in der Datenbank
     * @param uuid
     * @return
     */
    public CompletableFuture<Boolean> insert(final UUID uuid){
        final CompletableFuture<Boolean> result = new CompletableFuture<>();
        final ExecutorService executorService = Executors.newSingleThreadExecutor();

        executorService.execute(() -> {
            final ResultSet resultSet;
            final PreparedStatement preparedStatement;
            try {
                preparedStatement = this.connection.prepareStatement("INSERT INTO `users`(`uuid`, `coins`) VALUES (?,?)");
                preparedStatement.setString(1,uuid.toString());
                preparedStatement.setInt(2,0);

                preparedStatement.executeQuery();
                result.complete(true);

            } catch (SQLException throwables) {
                throwables.printStackTrace();
            }

        });
        executorService.shutdown();

        return result;
    }

    /**
     * Setze den Coins-Wert eines Spielers auf den angegebenen Wert
     * @param uuid
     * @param coins
     * @return
     */
    public CompletableFuture<Boolean> set(final UUID uuid, final int coins){
        final CompletableFuture<Boolean> result = new CompletableFuture<>();
        final ExecutorService executorService = Executors.newSingleThreadExecutor();

        executorService.execute(() -> {
            final ResultSet resultSet;
            final PreparedStatement preparedStatement;
            try {
                preparedStatement = this.connection.prepareStatement("UPDATE users SET coins = ? WHERE `users`.`uuid` = ?;");
                preparedStatement.setInt(1,coins);
                preparedStatement.setString(2,uuid.toString());

                preparedStatement.executeQuery();
                result.complete(true);

            } catch (SQLException throwables) {
                throwables.printStackTrace();
            }

        });
        executorService.shutdown();

        return result;
    }

    /**
     * Überprüft, ob der Spieler in der Datenbank gespeichert ist
     * @param uuid
     * @return
     */
    public CompletableFuture<Boolean> contains(final UUID uuid){
        final CompletableFuture<Boolean> result = new CompletableFuture<>();
        final ExecutorService executorService = Executors.newSingleThreadExecutor();

        executorService.execute(() -> {
            final ResultSet resultSet;
            final PreparedStatement preparedStatement;
            try {
                preparedStatement = this.connection.prepareStatement("SELECT coins FROM users WHERE uuid=?;");

                preparedStatement.setString(1,uuid.toString());
                resultSet = preparedStatement.executeQuery();

                result.complete(resultSet.next());
            } catch (SQLException throwables) {
                throwables.printStackTrace();
            }

        });
        executorService.shutdown();

        return result;
    }

    /**
     * Ruft den gespeicherten Coins-Wert eines Spielers ab
     * @param uuid
     * @return
     */
    public Integer get(final UUID uuid){
        final CompletableFuture<Integer> result = new CompletableFuture<>();
        final ExecutorService executorService = Executors.newSingleThreadExecutor();

        executorService.execute(() -> {
            final ResultSet resultSet;
            final PreparedStatement preparedStatement;
            try {
                preparedStatement = this.connection.prepareStatement("SELECT coins FROM users WHERE uuid=?;");

                preparedStatement.setString(1,uuid.toString());

                resultSet = preparedStatement.executeQuery();

                if(resultSet.next())result.complete(resultSet.getInt("coins"));
            } catch (SQLException throwables) {
                throwables.printStackTrace();
            }

        });
        executorService.shutdown();

        return result;
    }
}
