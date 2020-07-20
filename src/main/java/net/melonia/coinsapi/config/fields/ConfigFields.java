package net.melonia.coinsapi.config.fields;

public enum ConfigFields {

    HOST("host","0.0.0.0.0"),
    PORT("port","0000"),
    USER("user", "0000"),
    PASSWORD("password","0000");

    public final String key, defaultEntry;

    ConfigFields(String key, String defaultEntry){
        this.key = key;
        this.defaultEntry = defaultEntry;
    }

}
