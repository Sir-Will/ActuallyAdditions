package ellpeck.actuallyadditions.config;

import cpw.mods.fml.client.event.ConfigChangedEvent;
import cpw.mods.fml.common.eventhandler.SubscribeEvent;
import ellpeck.actuallyadditions.util.ModUtil;
import ellpeck.actuallyadditions.util.Util;
import net.minecraftforge.common.config.Configuration;

import java.io.File;

public class ConfigurationHandler{

    public static final String ISSUES_WARNING = " [THIS COULD CAUSE ISSUES, CHANGE AT YOUR OWN RISK!]";

    public static Configuration config;

    public ConfigurationHandler(File configFile){
        ModUtil.LOGGER.info("Grabbing Configurations...");

        Util.registerEvent(this);

        if(config == null){
            config = new Configuration(configFile);
            loadConfig();
        }
    }

    private static void loadConfig(){
        ConfigValues.defineConfigValues(config);

        if(config.hasChanged()){
            config.save();
        }
    }

    @SubscribeEvent
    public void onConfigurationChangedEvent(ConfigChangedEvent.OnConfigChangedEvent event){
        if (event.modID.equalsIgnoreCase(ModUtil.MOD_ID)){
            loadConfig();
        }
    }
}