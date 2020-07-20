package net.melonia.coinsapi.sql.creditials;

public class SQLCreditals {

    public final int port;
    public final String host, user, password;


    /**
     * Constructor für die SQLCerdials.class, die zur Vereinfachung der Anmeldedaten dient.
     * @param host
     * @param port
     * @param user
     * @param password
     */
    public SQLCreditals(final String host, final int port, final String user, final String password){
        this.host = host;
        this.port = port;
        this.user = user;
        this.password = password;
    }


    /**
     * Convertiert die SQLCreditials zum URI-Format
     * @return
     */
    public String toURI(){
        return "mysql://" + this.user + (this.password != null ? ":" + this.password : "") + "@" + this.host + ":" + this.port + "?get-server-public-key=true";
    }


}
