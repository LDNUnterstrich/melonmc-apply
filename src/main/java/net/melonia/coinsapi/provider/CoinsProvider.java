package net.melonia.coinsapi.provider;

import net.melonia.coinsapi.CoinsAPI;
import net.melonia.coinsapi.config.ConfigFile;
import net.melonia.coinsapi.config.fields.ConfigFields;
import net.melonia.coinsapi.sql.SQLConnection;
import net.melonia.coinsapi.sql.creditials.SQLCreditals;

import java.sql.SQLException;
import java.util.HashMap;
import java.util.Map;
import java.util.UUID;
import java.util.concurrent.ExecutionException;

public class CoinsProvider implements CoinsAPI {

    private Map<UUID,Integer> cache;
    private SQLConnection sqlConnection;

    private ConfigFile configFile;

    public CoinsProvider(final SQLCreditals sqlCreditals, final String database) throws SQLException {
        if(this.hasSQLDriver()){
            this.cache = new HashMap<UUID, Integer>();
            this.configFile = new ConfigFile("plugins/MeloniaCoins","sql.yml");
            if(sqlCreditals != null){ //Falls null, dann versuche die Daten aus einer Config-File zu lesen
                System.out.println("Lade SQL-Daten von SQLCreditials.");
                this.sqlConnection = new SQLConnection(sqlCreditals,database);
            }else{
                if(this.configFile.hasBeenChanged()){ //Überprüfung, ob Daten aus der Config bearbeitet worden
                    this.sqlConnection = new SQLConnection(new SQLCreditals(
                            (String)this.configFile.get(ConfigFields.HOST.key),
                            (Integer) this.configFile.get(ConfigFields.PORT.key),
                            (String)this.configFile.get(ConfigFields.USER.key),
                            (String)this.configFile.get(ConfigFields.PASSWORD.key)),database);
                }else System.out.println("API konnte nicht initalisert werden, da die Config-Daten nicht editiert worden.");
            }



        }else System.out.println("API konnte nicht initialisiert werden, da der SQL-Driver fehlt. Überprüfe deine Java Version.");
    }

    /**
     * Setze die Coins eines Spielers auf den angegebenen Betrag
     * @param uuid
     */
    public void setCoins(UUID uuid, int value) throws ExecutionException, InterruptedException {
        if(!this.cache.containsKey(uuid)){
            if(!this.sqlConnection.contains(uuid).get()){
                this.sqlConnection.insert(uuid);
            }
            this.cache.put(uuid,value);
        }else{
            this.cache.put(uuid,value);
        }
    }

    /**
     * Dekrementiert die Coins des angegebenen Spielers
     * @param uuid
     * @param value
     */
    public void decrementCoins(UUID uuid, int value) throws ExecutionException, InterruptedException {
        if(!this.cache.containsKey(uuid)){
            if(!this.sqlConnection.contains(uuid).get()){
                this.sqlConnection.insert(uuid);
            }
            this.cache.put(uuid,this.sqlConnection.get(uuid)-value);
        }else this.cache.put(uuid,this.cache.get(uuid) - value);

    }

    /**
     * Inkrementiert die Coins des angegebenen Spielers
     * @param uuid
     * @param value
     */
    public void incrementCoins(UUID uuid, int value) throws ExecutionException, InterruptedException {
        if(!this.cache.containsKey(uuid)){
            if(!this.sqlConnection.contains(uuid).get()){
                this.sqlConnection.insert(uuid);
            }
            this.cache.put(uuid,this.sqlConnection.get(uuid) + value);
        }else this.cache.put(uuid,this.cache.get(uuid) + value);
    }

    /**
     * Gibt die Coins den angegebenen Spielers wieder
     * @param uuid
     * @return
     */
    @Override
    public Integer getCoins(UUID uuid) throws ExecutionException, InterruptedException {
        if(!this.cache.containsKey(uuid)){
            if(!this.sqlConnection.contains(uuid).get()){
                this.sqlConnection.insert(uuid);
            }
            this.cache.put(uuid,this.sqlConnection.get(uuid));
        }
        return this.cache.get(uuid);
    }

    /**
     * Übergibt die lokalen Daten des angegebenen gecachten Spieler an die SQL-Datenbank
     * @param uuid
     */
    @Override
    public void pushCache(UUID uuid) {
        this.sqlConnection.set(uuid,this.cache.get(uuid));
    }

    /**
     * Übergibt die lokalen Daten alle gecachten Spieler an die SQL-Datenbank
     */
    @Override
    public void pushGlobalCache() {
        this.cache.forEach((uuid, integer) -> this.sqlConnection.set(uuid,integer));
    }

    /**
     * Gibt die Verbindung zur SQL-Datenbank wieder
     * @return
     */
    public SQLConnection getConnection() {
        return this.sqlConnection;
    }

    /**
     * Gibt den "Cache", in dem die Spieler-UUIDs mit den Coins von ihnen gespeichert sind.
     * @return
     */
    public Map<UUID, Integer> getCache() {
        return this.cache;
    }

    /**
     * Gibt die Config-File wieder, in der die SQL-Verbindungsdaten angegeben werden können.
     * @return
     */
    @Override
    public ConfigFile getConfigFile() {
        return this.configFile;
    }

    /**
     * Überprüft, ob der benötigte SQL-Driver zur Verfügung steht.
     * @return
     */
    private boolean hasSQLDriver(){
        try {
            Class<?> c = Class.forName("com.mysql.jdbc.Driver");
            if (c != null) {
                System.out.println("JDBC-Treiber geladen");
            }
            return true;
        } catch (ClassNotFoundException e) {
            System.err.println("Fehler beim Laden des JDBC-Treibers");
        }
        return false;
    }
}
