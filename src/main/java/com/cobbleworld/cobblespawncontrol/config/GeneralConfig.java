package com.cobbleworld.cobblespawncontrol.config;

import java.util.ArrayList;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;

public final class GeneralConfig {
   public String _description = "Core Cobble Spawn Control settings. Edit this file directly or use Mods > Cobble Spawn Control > Config. The in-game screen writes back to this JSON file.";
   public GeneralConfig.Density density = new GeneralConfig.Density();
   public GeneralConfig.Levels levels = new GeneralConfig.Levels();
   public GeneralConfig.EvolutionRarity evolutionRarity = new GeneralConfig.EvolutionRarity();
   public GeneralConfig.Population population = new GeneralConfig.Population();
   public GeneralConfig.Ecology ecology = new GeneralConfig.Ecology();
   public boolean debugLogging = false;
   public Map<String, String> _help = help("debugLogging", "Writes detailed spawn rejection and ecology diagnostics to the log. Leave false for normal play.");

   private static Map<String, GeneralConfig.LevelRange> defaultDimensions() {
      LinkedHashMap var0 = new LinkedHashMap();
      var0.put("minecraft:the_nether", new GeneralConfig.LevelRange(35, 60));
      var0.put("minecraft:the_end", new GeneralConfig.LevelRange(55, 75));
      return var0;
   }

   private static Map<String, GeneralConfig.LevelRange> defaultBiomes() {
      LinkedHashMap var0 = new LinkedHashMap();
      var0.put("minecraft:deep_dark", new GeneralConfig.LevelRange(55, 75));
      return var0;
   }

   private static List<String> defaultExcludedSpecies() {
      return new ArrayList<>(
         List.of(
            "cobblemon:articuno",
            "cobblemon:zapdos",
            "cobblemon:moltres",
            "cobblemon:mew",
            "cobblemon:mewtwo",
            "cobblemon:deoxys",
            "cobblemon:raikou",
            "cobblemon:entei",
            "cobblemon:suicune",
            "cobblemon:lugia",
            "cobblemon:hooh",
            "cobblemon:regirock",
            "cobblemon:regice",
            "cobblemon:registeel",
            "cobblemon:latias",
            "cobblemon:latios",
            "cobblemon:kyogre",
            "cobblemon:groudon",
            "cobblemon:rayquaza",
            "cobblemon:uxie",
            "cobblemon:mesprit",
            "cobblemon:azelf",
            "cobblemon:dialga",
            "cobblemon:palkia",
            "cobblemon:heatran",
            "cobblemon:regigigas",
            "cobblemon:giratina",
            "cobblemon:cresselia",
            "cobblemon:cobalion",
            "cobblemon:terrakion",
            "cobblemon:virizion",
            "cobblemon:tornadus",
            "cobblemon:thundurus",
            "cobblemon:landorus",
            "cobblemon:reshiram",
            "cobblemon:zekrom",
            "cobblemon:kyurem",
            "cobblemon:xerneas",
            "cobblemon:yveltal",
            "cobblemon:zygarde",
            "cobblemon:tapukoko",
            "cobblemon:tapulele",
            "cobblemon:tapubulu",
            "cobblemon:tapufini",
            "cobblemon:solgaleo",
            "cobblemon:lunala",
            "cobblemon:necrozma",
            "cobblemon:typenull",
            "cobblemon:silvally",
            "cobblemon:zacian",
            "cobblemon:zamazenta",
            "cobblemon:eternatus",
            "cobblemon:regieleki",
            "cobblemon:regidrago",
            "cobblemon:glastrier",
            "cobblemon:spectrier",
            "cobblemon:calyrex",
            "cobblemon:kubfu",
            "cobblemon:urshifu",
            "cobblemon:koraidon",
            "cobblemon:miraidon",
            "cobblemon:tinglu",
            "cobblemon:chienpao",
            "cobblemon:wochien",
            "cobblemon:chiyu",
            "cobblemon:ogerpon",
            "cobblemon:celebi",
            "cobblemon:jirachi",
            "cobblemon:phione",
            "cobblemon:manaphy",
            "cobblemon:darkrai",
            "cobblemon:shaymin",
            "cobblemon:arceus",
            "cobblemon:victini",
            "cobblemon:keldeo",
            "cobblemon:meloetta",
            "cobblemon:genesect",
            "cobblemon:diancie",
            "cobblemon:hoopa",
            "cobblemon:volcanion",
            "cobblemon:magearna",
            "cobblemon:marshadow",
            "cobblemon:zeraora",
            "cobblemon:meltan",
            "cobblemon:melmetal",
            "cobblemon:zarude",
            "cobblemon:pecharunt",
            "cobblemon:okidogi",
            "cobblemon:munkidori",
            "cobblemon:fezandipiti",
            "cobblemon:terapagos",
            "cobblemon:cosmog",
            "cobblemon:cosmoem",
            "cobblemon:enamorus"
         )
      );
   }

   private static Map<String, String> help(String... var0) {
      LinkedHashMap var1 = new LinkedHashMap();

      for (byte var2 = 0; var2 + 1 < var0.length; var2 += 2) {
         var1.put(var0[var2], var0[var2 + 1]);
      }

      return var1;
   }

   public static final class Density {
      public String _description = "Controls how densely Cobblemon attempts to populate the area around players.";
      public boolean enabled = true;
      public double pokemonPerChunk = 0.6;
      public double minimumDistanceBetweenEntities = 14.0;
      public double ticksBetweenSpawnAttempts = 30.0;
      public double minimumSpawningZoneDistanceFromPlayer = 20.0;
      public double maximumSpawningZoneDistanceFromPlayer = 64.0;
      public int maximumSpawnsPerPass = 6;
      public Map<String, String> _help = GeneralConfig.help(
         "enabled",
         "Allows CSC to apply its density settings to Cobblemon when a world starts or /csc reload is used.",
         "pokemonPerChunk",
         "Target wild Pokemon density per chunk. Lower values produce a quieter world.",
         "minimumDistanceBetweenEntities",
         "Minimum spacing in blocks Cobblemon should keep between spawned Pokemon.",
         "ticksBetweenSpawnAttempts",
         "Ticks between spawn attempts. Higher values mean slower population recovery.",
         "minimumSpawningZoneDistanceFromPlayer",
         "Nearest distance in blocks where new wild spawns may be selected.",
         "maximumSpawningZoneDistanceFromPlayer",
         "Farthest distance in blocks where new wild spawns may be selected.",
         "maximumSpawnsPerPass",
         "Maximum number of Pokemon Cobblemon may create during one spawn pass."
      );
   }

   public static final class Depth {
      public String _description = "Adds a level bonus as the player travels below startY.";
      public boolean enabled = true;
      public int startY = 64;
      public double percentagePerBlock = 0.003;
      public double maxIncrease = 0.3;
      public Map<String, String> _help = GeneralConfig.help(
         "enabled",
         "Enables depth-based level scaling.",
         "startY",
         "Y level below which the depth bonus starts.",
         "percentagePerBlock",
         "Fractional increase per block below startY. 0.003 means 0.3% per block.",
         "maxIncrease",
         "Maximum fractional depth bonus. 0.30 means up to +30%."
      );
   }

   public static final class Ecology {
      public String _description = "Controls species-to-habitat filtering. Detailed habitat detection is configured in habitats.json and species overrides are configured in species.json.";
      public boolean enabled = true;
      public boolean rejectUnmappedSpecies = false;
      public Map<String, String> _help = GeneralConfig.help(
         "enabled",
         "Enables ecology filtering.",
         "rejectUnmappedSpecies",
         "Rejects species with no explicit or automatic ecology profile. Keep false for addon compatibility."
      );
   }

   public static final class EvolutionRarity {
      public String _description = "Reduces the chance that evolved Pokemon survive CSC's spawn filter, making base stages much more common.";
      public boolean enabled = true;
      public double base = 1.0;
      public double stage1 = 0.3;
      public double stage2 = 0.08;
      public double stage3Plus = 0.04;
      public Map<String, String> _help = GeneralConfig.help(
         "enabled",
         "Enables evolution-stage rarity filtering.",
         "base",
         "Acceptance multiplier for base-stage Pokemon.",
         "stage1",
         "Acceptance multiplier for first evolved stages.",
         "stage2",
         "Acceptance multiplier for second evolved stages.",
         "stage3Plus",
         "Acceptance multiplier for later evolution stages."
      );
   }

   public static final class LevelRange {
      public int min;
      public int max;

      public LevelRange() {
      }

      public LevelRange(int var1, int var2) {
         this.min = var1;
         this.max = var2;
      }
   }

   public static final class Levels {
      public String _description = "World-driven wild level progression. Distance is the main backbone; depth, biome and dimension ranges can modify it.";
      public boolean enabled = true;
      public int levelNearSpawn = 3;
      public int maxDistanceLevel = 60;
      public double distanceForMaxLevel = 20000.0;
      public double exponent = 1.5;
      public int randomVariance = 2;
      public int globalMin = 1;
      public int globalMax = 100;
      public GeneralConfig.Depth depth = new GeneralConfig.Depth();
      public Map<String, GeneralConfig.LevelRange> dimensionRanges = GeneralConfig.defaultDimensions();
      public Map<String, GeneralConfig.LevelRange> biomeRanges = GeneralConfig.defaultBiomes();
      public List<String> excludedSpecies = GeneralConfig.defaultExcludedSpecies();
      public Map<String, String> _help = GeneralConfig.help(
         "enabled",
         "Enables CSC wild level scaling.",
         "levelNearSpawn",
         "Baseline level near world spawn before random variance and environmental modifiers.",
         "maxDistanceLevel",
         "Maximum baseline level reached by distance progression.",
         "distanceForMaxLevel",
         "Distance in blocks where the distance curve reaches maxDistanceLevel.",
         "exponent",
         "Distance curve exponent. 1.0 is linear; values above 1 keep early areas lower for longer.",
         "randomVariance",
         "Random plus/minus levels added around the calculated baseline.",
         "globalMin",
         "Absolute minimum level CSC can assign.",
         "globalMax",
         "Absolute maximum level CSC can assign.",
         "dimensionRanges",
         "Optional hard level ranges by dimension ID.",
         "biomeRanges",
         "Optional hard level ranges by biome ID.",
         "excludedSpecies",
         "Species CSC will not rescale. Intended for legendary/special encounter systems."
      );
   }

   public static final class Population {
      public String _description = "Local carrying-capacity limits used to prevent overcrowding and excessive repetition.";
      public boolean enabled = true;
      public double radius = 64.0;
      public int totalCap = 18;
      public int defaultSpeciesCap = 4;
      public Map<String, String> _help = GeneralConfig.help(
         "enabled",
         "Enables local total and per-species population caps.",
         "radius",
         "Radius in blocks used when counting nearby wild Pokemon.",
         "totalCap",
         "Maximum total wild Pokemon allowed inside the radius.",
         "defaultSpeciesCap",
         "Default maximum number of the same species inside the radius unless species.json overrides it."
      );
   }
}
