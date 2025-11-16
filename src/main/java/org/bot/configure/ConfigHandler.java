package org.bot.configure;

import org.yaml.snakeyaml.Yaml;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.InputStream;
import java.net.URI;
import java.net.URISyntaxException;

public class ConfigHandler {
   private static final File configFile;

   private static ConfigHandler configHandler;
   private Config config;

   static {
      try {
         URI uri = ConfigHandler.class.getResource("/config.yaml").toURI();
         configFile = new File(uri);
      } catch (URISyntaxException e) {
         throw new RuntimeException(e);
      }
   }

   private ConfigHandler() {
      config = loadConfig();
   }

   public static ConfigHandler getInstance() {
      if (configHandler == null) {
         configHandler = new ConfigHandler();
      }
      return configHandler;
   }

   private Config loadConfig() {
      try {
         InputStream inputStream = new FileInputStream(configFile);
         Yaml yaml = new Yaml();
         return yaml.loadAs(inputStream, Config.class);
      } catch (FileNotFoundException e) {
         throw new RuntimeException(e);
      }
   }

   public Config getConfig() {
      return config;
   }
}
