package com.cobbleworld.cobblespawncontrol.config;

import com.cobbleworld.cobblespawncontrol.control.AutoEcology;
import com.cobbleworld.cobblespawncontrol.control.HabitatResolver;
import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import java.io.BufferedReader;
import java.io.BufferedWriter;
import java.io.IOException;
import java.nio.charset.StandardCharsets;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.ArrayList;
import java.util.LinkedHashMap;

public final class ConfigManager {
   private static final Gson GSON = new GsonBuilder().setPrettyPrinting().disableHtmlEscaping().create();
   private static final Path DIR = Path.of("config", "cobblespawncontrol");
   private static final Path GENERAL_PATH = DIR.resolve("general.json");
   private static final Path HABITATS_PATH = DIR.resolve("habitats.json");
   private static final Path SPECIES_PATH = DIR.resolve("species.json");
   private static volatile GeneralConfig general = new GeneralConfig();
   private static volatile HabitatConfig habitats = new HabitatConfig();
   private static volatile SpeciesConfig species = new SpeciesConfig();

   private ConfigManager() {
   }

   public static synchronized void load() {
      try {
         Files.createDirectories(DIR);
         general = loadOrCreate(GENERAL_PATH, GeneralConfig.class, new GeneralConfig());
         habitats = loadOrCreate(HABITATS_PATH, HabitatConfig.class, new HabitatConfig());
         species = loadOrCreate(SPECIES_PATH, SpeciesConfig.class, new SpeciesConfig());
         normalize();
         AutoEcology.clearCache();
         HabitatResolver.clearCache();
         log("Loaded JSON config from " + DIR.toAbsolutePath());
      } catch (Exception var1) {
         error("Failed to load config. Keeping the last valid settings.", var1);
      }
   }

   public static synchronized void syncFromNeoForge() {
      normalize();
   }

   public static synchronized boolean saveGeneralAndHabitats(GeneralConfig var0, HabitatConfig var1) {
      try {
         Files.createDirectories(DIR);
         general = var0 == null ? new GeneralConfig() : var0;
         habitats = var1 == null ? new HabitatConfig() : var1;
         normalize();
         write(GENERAL_PATH, general);
         write(HABITATS_PATH, habitats);
         AutoEcology.clearCache();
         HabitatResolver.clearCache();
         log("Saved general.json and habitats.json");
         return true;
      } catch (Exception var3) {
         error("Failed to save configuration.", var3);
         return false;
      }
   }

   public static synchronized boolean saveAll(GeneralConfig var0, HabitatConfig var1, SpeciesConfig var2) {
      try {
         Files.createDirectories(DIR);
         general = var0 == null ? new GeneralConfig() : var0;
         habitats = var1 == null ? new HabitatConfig() : var1;
         species = var2 == null ? new SpeciesConfig() : var2;
         normalize();
         write(GENERAL_PATH, general);
         write(HABITATS_PATH, habitats);
         write(SPECIES_PATH, species);
         AutoEcology.clearCache();
         HabitatResolver.clearCache();
         log("Saved general.json, habitats.json and species.json");
         return true;
      } catch (Exception var4) {
         error("Failed to save configuration.", var4);
         return false;
      }
   }

   public static synchronized boolean saveAll() {
      try {
         Files.createDirectories(DIR);
         normalize();
         write(GENERAL_PATH, general);
         write(HABITATS_PATH, habitats);
         write(SPECIES_PATH, species);
         AutoEcology.clearCache();
         HabitatResolver.clearCache();
         return true;
      } catch (Exception var1) {
         error("Failed to save configuration.", var1);
         return false;
      }
   }

   public static GeneralConfig copyGeneral() {
      synchronized (ConfigManager.class) {
         normalize();
         return (GeneralConfig)GSON.fromJson(GSON.toJson(general), GeneralConfig.class);
      }
   }

   public static HabitatConfig copyHabitats() {
      synchronized (ConfigManager.class) {
         normalize();
         return (HabitatConfig)GSON.fromJson(GSON.toJson(habitats), HabitatConfig.class);
      }
   }

   public static SpeciesConfig copySpecies() {
      synchronized (ConfigManager.class) {
         normalize();
         return (SpeciesConfig)GSON.fromJson(GSON.toJson(species), SpeciesConfig.class);
      }
   }

   private static void normalize() {
      if (general == null) {
         general = new GeneralConfig();
      }

      if (habitats == null) {
         habitats = new HabitatConfig();
      }

      if (species == null) {
         species = new SpeciesConfig();
      }

      if (general.density == null) {
         general.density = new GeneralConfig.Density();
      }

      if (general.levels == null) {
         general.levels = new GeneralConfig.Levels();
      }

      if (general.levels.depth == null) {
         general.levels.depth = new GeneralConfig.Depth();
      }

      if (general.evolutionRarity == null) {
         general.evolutionRarity = new GeneralConfig.EvolutionRarity();
      }

      if (general.population == null) {
         general.population = new GeneralConfig.Population();
      }

      if (general.ecology == null) {
         general.ecology = new GeneralConfig.Ecology();
      }

      if (general.levels.dimensionRanges == null) {
         general.levels.dimensionRanges = new LinkedHashMap<>();
      }

      if (general.levels.biomeRanges == null) {
         general.levels.biomeRanges = new LinkedHashMap<>();
      }

      if (general.levels.excludedSpecies == null) {
         general.levels.excludedSpecies = new ArrayList<>();
      }

      if (habitats.detection == null) {
         habitats.detection = new HabitatConfig.Detection();
      }

      if (habitats.habitats == null) {
         habitats.habitats = new ArrayList<>();
      }

      for (HabitatConfig.Habitat var1 : habitats.habitats) {
         if (var1.biomePatterns == null) {
            var1.biomePatterns = new ArrayList<>();
         }

         if (var1.biomeTagPatterns == null) {
            var1.biomeTagPatterns = new ArrayList<>();
         }

         if (var1.dimensionPatterns == null) {
            var1.dimensionPatterns = new ArrayList<>();
         }
      }

      if (species.rules == null) {
         species.rules = new ArrayList<>();
      }

      for (SpeciesConfig.Rule var3 : species.rules) {
         if (var3.species == null) {
            var3.species = new ArrayList<>();
         }

         if (var3.allowedHabitats == null) {
            var3.allowedHabitats = new ArrayList<>();
         }

         if (var3.blockedHabitats == null) {
            var3.blockedHabitats = new ArrayList<>();
         }

         if (var3.allowedTimes == null) {
            var3.allowedTimes = new ArrayList<>();
         }
      }
   }

   private static <T> T loadExisting(Path var0, Class<T> var1, T var2) throws IOException {
      try (BufferedReader var3 = Files.newBufferedReader(var0, StandardCharsets.UTF_8)) {
         Object var4 = GSON.fromJson(var3, var1);
         return (T)(var4 == null ? var2 : var4);
      }
   }

   private static <T> T loadOrCreate(Path var0, Class<T> var1, T var2) throws IOException {
      if (!Files.exists(var0)) {
         write(var0, var2);
         return (T)var2;
      } else {
         return loadExisting(var0, var1, (T)var2);
      }
   }

   private static void write(Path var0, Object var1) throws IOException {
      try (BufferedWriter var2 = Files.newBufferedWriter(var0, StandardCharsets.UTF_8)) {
         GSON.toJson(var1, var2);
      }
   }

   public static GeneralConfig general() {
      return general;
   }

   public static HabitatConfig habitats() {
      return habitats;
   }

   public static SpeciesConfig species() {
      return species;
   }

   public static Path directory() {
      return DIR;
   }

   public static void log(String var0) {
      System.out.println("[Cobble Spawn Control] " + var0);
   }

   public static void debug(String var0) {
      if (general != null && general.debugLogging) {
         System.out.println("[Cobble Spawn Control/DEBUG] " + var0);
      }
   }

   public static void warn(String var0) {
      System.err.println("[Cobble Spawn Control/WARN] " + var0);
   }

   public static void error(String var0, Throwable var1) {
      System.err.println("[Cobble Spawn Control/ERROR] " + var0);
      if (var1 != null) {
         var1.printStackTrace(System.err);
      }
   }
}
