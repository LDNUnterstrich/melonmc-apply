package net.melonia.coinsapi;

import net.melonia.coinsapi.config.ConfigFile;
import net.melonia.coinsapi.sql.SQLConnection;

import java.util.Map;
import java.util.UUID;
import java.util.concurrent.ExecutionException;

public interface CoinsAPI {

    void setCoins(final UUID uuid, final int value) throws ExecutionException, InterruptedException;

    void decrementCoins(final UUID uuid, final int value) throws ExecutionException, InterruptedException;

    void incrementCoins(final UUID uuid, final int value) throws ExecutionException, InterruptedException;

    Integer getCoins(final UUID uuid) throws ExecutionException, InterruptedException;

    void pushCache(final UUID uuid);
    void pushGlobalCache();

    SQLConnection getConnection();
    Map<UUID,Integer> getCache();
    ConfigFile getConfigFile();

}
