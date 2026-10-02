package com.cobbleworld.cobblespawncontrol.config;

import java.util.ArrayList;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;

public final class HabitatConfig {
   public String _description = "Habitat detection rules. CSC checks explicit biome names/tags first, then the smart detector can use climate, precipitation, altitude, water, sky visibility and dimension. The in-game Config screen includes a Habitat Editor for acceptance, priority, sky/water requirements, biome patterns, tag patterns and dimension patterns; advanced fields remain editable here.";
   public HabitatConfig.Detection detection = new HabitatConfig.Detection();
   public String defaultHabitat = "general";
   public List<HabitatConfig.Habitat> habitats = defaults();
   public Map<String, String> _help = help(
      "defaultHabitat",
      "Habitat used when no explicit rule or smart detector result can classify a position.",
      "habitats",
      "Ordered habitat definitions. Higher priority wins when multiple explicit rules match."
   );

   private static HabitatConfig.Habitat h(String var0, String var1, int var2, List<String> var3, List<String> var4, Boolean var5, Boolean var6, double var7) {
      return new HabitatConfig.Habitat(var0, var1, var2, var3, var4, List.of(), var5, var6, null, null, var7);
   }

   private static HabitatConfig.Habitat d(String var0, String var1, int var2, String var3, double var4) {
      return new HabitatConfig.Habitat(var0, var1, var2, List.of("*"), List.of(), List.of(var3), null, null, null, null, var4);
   }

   private static List<HabitatConfig.Habitat> defaults() {
      ArrayList var0 = new ArrayList();
      var0.add(d("Nether dimensions. Dimension rules intentionally win over ordinary biome climate.", "nether", 220, "minecraft:the_nether", 0.55));
      var0.add(d("The End dimension.", "end", 210, "minecraft:the_end", 0.45));
      var0.add(
         new HabitatConfig.Habitat(
            "Underground positions at Y 0 or below with no sky and no water.", "deep_cave", 200, List.of("*"), List.of(), List.of(), false, false, null, 0, 0.5
         )
      );
      var0.add(
         new HabitatConfig.Habitat(
            "Underground positions with no sky and no water.", "cave", 190, List.of("*"), List.of(), List.of(), false, false, null, null, 0.6
         )
      );
      var0.add(
         h(
            "Deep ocean biomes while the spawn point is actually in water.",
            "deep_ocean",
            180,
            List.of("*:*deep*ocean*"),
            List.of("*:*deep*ocean*", "*:*deep_ocean*"),
            null,
            true,
            0.7
         )
      );
      var0.add(h("Ocean biomes while the spawn point is actually in water.", "ocean", 170, List.of("*:*ocean*"), List.of("*:*ocean*"), null, true, 0.85));
      var0.add(
         h(
            "Rivers, lakes and other freshwater-tagged biomes while in water.",
            "freshwater",
            165,
            List.of("*:*river*", "*:*lake*"),
            List.of("*:*river*", "*:*lake*", "*:*freshwater*"),
            null,
            true,
            0.8
         )
      );
      var0.add(
         h(
            "Beaches, shores and coastal biomes.",
            "coast",
            160,
            List.of("*:*beach*", "*:*shore*", "*:*coast*"),
            List.of("*:*beach*", "*:*shore*", "*:*coast*"),
            true,
            null,
            0.75
         )
      );
      var0.add(
         h(
            "Volcanic, basalt and crater-like surface biomes.",
            "volcanic",
            155,
            List.of("*:*volcan*", "*:*basalt*", "*:*crater*"),
            List.of("*:*volcan*", "*:*basalt*", "*:*crater*"),
            true,
            false,
            0.5
         )
      );
      var0.add(
         h(
            "Swamps, marshes, mangroves, bogs and wetland-tagged biomes.",
            "wetland",
            150,
            List.of("*:*swamp*", "*:*marsh*", "*:*mangrove*", "*:*bog*"),
            List.of("*:*swamp*", "*:*marsh*", "*:*mangrove*", "*:*bog*", "*:*wetland*"),
            true,
            null,
            0.75
         )
      );
      var0.add(
         h(
            "Jungles, rainforests and tropical forests.",
            "tropical_forest",
            140,
            List.of("*:*jungle*", "*:*rainforest*", "*:*tropical*forest*"),
            List.of("*:*jungle*", "*:*rainforest*", "*:*tropical*forest*"),
            true,
            false,
            0.85
         )
      );
      var0.add(
         h(
            "Taiga and coniferous forests.",
            "taiga",
            130,
            List.of("*:*taiga*", "*:*conifer*", "*:*pine*", "*:*spruce*"),
            List.of("*:*taiga*", "*:*conifer*", "*:*pine*", "*:*spruce*"),
            true,
            false,
            0.75
         )
      );
      var0.add(
         h(
            "Snowy, frozen, icy and tundra biomes.",
            "tundra",
            125,
            List.of("*:*snow*", "*:*frozen*", "*:*ice*", "*:*tundra*"),
            List.of("*:*snow*", "*:*frozen*", "*:*ice*", "*:*tundra*", "*:*cold*"),
            true,
            false,
            0.6
         )
      );
      var0.add(
         h(
            "Deserts, badlands, dunes and arid biomes.",
            "desert",
            120,
            List.of("*:*desert*", "*:*badlands*", "*:*dune*", "*:*arid*"),
            List.of("*:*desert*", "*:*badlands*", "*:*dune*", "*:*arid*", "*:*dry*", "*:*hot*"),
            true,
            false,
            0.55
         )
      );
      var0.add(h("Savanna biomes.", "savanna", 115, List.of("*:*savanna*"), List.of("*:*savanna*"), true, false, 0.7));
      var0.add(
         h(
            "Mountain peaks, highlands and slopes.",
            "alpine",
            110,
            List.of("*:*peak*", "*:*mountain*", "*:*highland*", "*:*slope*"),
            List.of("*:*peak*", "*:*mountain*", "*:*highland*", "*:*slope*", "*:*mountain*"),
            true,
            false,
            0.55
         )
      );
      var0.add(
         h(
            "Rocky, stony, gravelly, cliff and windswept terrain.",
            "rocky",
            105,
            List.of("*:*stony*", "*:*rocky*", "*:*gravel*", "*:*cliff*", "*:*windswept*"),
            List.of("*:*stony*", "*:*rocky*", "*:*gravel*", "*:*cliff*", "*:*windswept*"),
            true,
            false,
            0.6
         )
      );
      var0.add(
         h(
            "Mushroom and fungal biomes.",
            "mushroom",
            100,
            List.of("*:*mushroom*", "*:*fungal*", "*:*fungus*"),
            List.of("*:*mushroom*", "*:*fungal*", "*:*fungus*"),
            true,
            false,
            0.5
         )
      );
      var0.add(
         h(
            "Temperate forests, woods, groves and cherry forests.",
            "temperate_forest",
            90,
            List.of("*:*forest*", "*:*woods*", "*:*grove*", "*:*cherry*"),
            List.of("*:*forest*", "*:*woods*", "*:*grove*", "*:*cherry*"),
            true,
            false,
            0.85
         )
      );
      var0.add(
         h(
            "Plains, meadows, fields, prairie and steppe.",
            "grassland",
            80,
            List.of("*:*plains*", "*:*meadow*", "*:*field*", "*:*prairie*", "*:*steppe*"),
            List.of("*:*plains*", "*:*meadow*", "*:*field*", "*:*prairie*", "*:*steppe*"),
            true,
            false,
            0.8
         )
      );
      var0.add(
         new HabitatConfig.Habitat(
            "Safety fallback for anything CSC cannot classify.", "general", 0, List.of("*"), List.of(), List.of(), null, null, null, null, 0.65
         )
      );
      return var0;
   }

   private static Map<String, String> help(String... var0) {
      LinkedHashMap var1 = new LinkedHashMap();

      for (byte var2 = 0; var2 + 1 < var0.length; var2 += 2) {
         var1.put(var0[var2], var0[var2 + 1]);
      }

      return var1;
   }

   public static final class Detection {
      public String _description = "Automatic biome intelligence for modded/unknown biomes. 'patterns_only' disables smart fallback, 'balanced' requires reasonable confidence, and 'aggressive' always tries to choose an ecological habitat.";
      public String mode = "balanced";
      public boolean useBiomeNames = true;
      public boolean useBiomeTags = true;
      public boolean useClimate = true;
      public boolean usePrecipitation = true;
      public boolean useAltitude = true;
      public boolean useWater = true;
      public boolean useSkyVisibility = true;
      public boolean useDimension = true;
      public double coldTemperature = 0.25;
      public double coolTemperature = 0.6;
      public double hotTemperature = 1.0;
      public double veryHotTemperature = 1.5;
      public double dryDownfall = 0.25;
      public double wetDownfall = 0.65;
      public int alpineY = 110;
      public int deepCaveY = 0;
      public String unknownWaterHabitat = "freshwater";
      public String unknownSurfaceHabitat = "general";
      public Map<String, String> _help = HabitatConfig.help(
         "mode",
         "patterns_only = explicit rules only; balanced = smart classification when confidence is good; aggressive = classify most unknown surface biomes too.",
         "useBiomeNames",
         "Use biome registry IDs as an automatic classification signal.",
         "useBiomeTags",
         "Read every tag attached to the biome and use tag names as automatic classification signals. Recommended for modded biomes.",
         "useClimate",
         "Use biome temperature and downfall when available.",
         "usePrecipitation",
         "Use whether the biome supports precipitation when available.",
         "useAltitude",
         "Use Y level as a signal for alpine and deep-cave ecology.",
         "useWater",
         "Use whether the spawn position is in water as a strong aquatic signal.",
         "useSkyVisibility",
         "Use sky visibility to distinguish caves from surface habitats.",
         "useDimension",
         "Use the current dimension as a classification signal.",
         "coldTemperature",
         "Temperature at or below this value strongly suggests tundra/cold ecology.",
         "coolTemperature",
         "Temperature at or below this value can suggest taiga/cool ecology.",
         "hotTemperature",
         "Temperature at or above this value contributes to savanna/tropical ecology.",
         "veryHotTemperature",
         "Temperature at or above this value strongly contributes to desert ecology.",
         "dryDownfall",
         "Downfall at or below this value is considered dry.",
         "wetDownfall",
         "Downfall at or above this value is considered wet.",
         "alpineY",
         "Surface positions at or above this Y level receive an alpine classification signal.",
         "deepCaveY",
         "Underground positions at or below this Y level are treated as deep cave.",
         "unknownWaterHabitat",
         "Fallback habitat for an unknown biome when the exact spawn position is in water.",
         "unknownSurfaceHabitat",
         "Fallback habitat used by aggressive mode if no surface category wins. Balanced mode normally falls back to defaultHabitat instead."
      );
   }

   public static final class Habitat {
      public String description = "";
      public String id = "general";
      public int priority = 0;
      public List<String> biomePatterns = new ArrayList<>();
      public List<String> biomeTagPatterns = new ArrayList<>();
      public List<String> dimensionPatterns = new ArrayList<>();
      public Boolean requiresSky = null;
      public Boolean requiresWater = null;
      public Integer minY = null;
      public Integer maxY = null;
      public double spawnAcceptance = 1.0;

      public Habitat() {
      }

      Habitat(
         String var1,
         String var2,
         int var3,
         List<String> var4,
         List<String> var5,
         List<String> var6,
         Boolean var7,
         Boolean var8,
         Integer var9,
         Integer var10,
         double var11
      ) {
         this.description = var1;
         this.id = var2;
         this.priority = var3;
         this.biomePatterns = new ArrayList<>(var4);
         this.biomeTagPatterns = new ArrayList<>(var5);
         this.dimensionPatterns = new ArrayList<>(var6);
         this.requiresSky = var7;
         this.requiresWater = var8;
         this.minY = var9;
         this.maxY = var10;
         this.spawnAcceptance = var11;
      }
   }
}
