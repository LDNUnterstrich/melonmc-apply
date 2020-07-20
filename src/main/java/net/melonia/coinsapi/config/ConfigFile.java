package net.melonia.coinsapi.config;

import net.melonia.coinsapi.config.fields.ConfigFields;
import org.simpleyaml.configuration.file.YamlConfiguration;

import java.io.File;
import java.io.IOException;

public class ConfigFile {

    public final File file;
    public final YamlConfiguration yamlConfiguration;

    public ConfigFile(final String parent, final String name){
        this.file = new File(parent,name);

        if(!this.file.exists()) {
            try {
                this.file.createNewFile();
            } catch (IOException e) {
                e.printStackTrace();
            }
        }

        this.yamlConfiguration = YamlConfiguration.loadConfiguration(this.file);

        // Lade die benötigten Fields in die Config-Datei
        for (ConfigFields fields : ConfigFields.values()) {
            this.yamlConfiguration.addDefault(fields.key,fields.defaultEntry);
        }
        this.yamlConfiguration.options().copyDefaults(true);
        this.save();
    }

    /**
     * Speichere die Config-Datei
     */
    public void save(){
        try {
            this.yamlConfiguration.save(this.file);
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    /**
     * Ruft Inhalt aus der gecachten Config-Datei auf
     * @param key
     * @return
     */
    public Object get(final String key){
        return this.yamlConfiguration.get(key);
    }


    /**
     * Überprüfen, ob je ein Wert der Fields verändert worden ist
     * @return
     */
    public boolean hasBeenChanged(){

        for (ConfigFields fields : ConfigFields.values()) {
            if(this.yamlConfiguration.get(fields.key).toString().equals(fields.defaultEntry))return false;
        }

        return true;
    }

}
