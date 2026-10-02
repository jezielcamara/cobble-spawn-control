package com.cobbleworld.cobblespawncontrol.control;

import com.cobbleworld.cobblespawncontrol.config.ConfigManager;
import com.cobbleworld.cobblespawncontrol.config.HabitatConfig;
import com.cobbleworld.cobblespawncontrol.util.Glob;
import java.lang.reflect.Method;
import java.util.ArrayList;
import java.util.Comparator;
import java.util.LinkedHashMap;
import java.util.LinkedHashSet;
import java.util.List;
import java.util.Locale;
import java.util.Map;
import java.util.Set;
import java.util.Map.Entry;
import java.util.concurrent.ConcurrentHashMap;
import java.util.stream.Stream;
import net.minecraft.core.BlockPos;
import net.minecraft.core.Holder;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.tags.FluidTags;
import net.minecraft.world.entity.Entity;

public final class HabitatResolver {
   private static final Map<String, HabitatResolver.StaticBiomeSignals> BIOME_SIGNAL_CACHE = new ConcurrentHashMap<>();
   private static final Map<String, HabitatResolver.HabitatObservation> OBSERVATIONS = new ConcurrentHashMap<>();

   private HabitatResolver() {
   }

   public static void clearCache() {
      BIOME_SIGNAL_CACHE.clear();
      OBSERVATIONS.clear();
   }

   public static Map<String, HabitatResolver.HabitatObservation> observations() {
      return Map.copyOf(OBSERVATIONS);
   }

   public static HabitatResolver.HabitatContext resolve(ServerLevel var0, Entity var1) {
      BlockPos var2 = var1.blockPosition();
      String var3 = biomeId(var0, var2);
      String var4 = var0.dimension().location().toString();
      boolean var5 = var0.canSeeSky(var2);
      boolean var6 = var0.getFluidState(var2).is(FluidTags.WATER);
      int var7 = var2.getY();
      HabitatResolver.BiomeSignals var8 = readBiomeSignals(var0, var2, var3, var4, var5, var6, var7);
      HabitatConfig var9 = ConfigManager.habitats();
      HabitatConfig.Habitat var10 = var9.habitats
         .stream()
         .filter(var1x -> var1x != null && var1x.id != null && !var1x.id.equals(var9.defaultHabitat))
         .sorted(Comparator.<HabitatConfig.Habitat>comparingInt(var0x -> var0x.priority).reversed())
         .filter(var1x -> matches(var1x, var8))
         .findFirst()
         .orElse(null);
      String var11 = "explicit";
      if (var10 == null) {
         String var12 = smartClassify(var9.detection, var8);
         if (var12 != null) {
            var10 = findHabitat(var9, var12);
            var11 = "smart";
         }
      }

      if (var10 == null) {
         var10 = findHabitat(var9, var9.defaultHabitat);
         var11 = "fallback";
      }

      String var17 = var10 == null ? var9.defaultHabitat : var10.id;
      double var13 = var10 == null ? 1.0 : clamp01(var10.spawnAcceptance);
      List var15 = evidenceFor(var9.detection, var8, var17, var11);
      int var16 = confidenceFor(var11, var15, var17, var9.defaultHabitat);
      OBSERVATIONS.put(
         var3,
         new HabitatResolver.HabitatObservation(var3, var17, var11, var16, var8.temperature, var8.downfall, var8.biomeTags.size(), System.currentTimeMillis())
      );
      if (ConfigManager.general().debugLogging) {
         ConfigManager.debug(
            "Biome "
               + var3
               + " -> "
               + var17
               + " ("
               + var11
               + ", confidence="
               + var16
               + "%, temp="
               + fmt(var8.temperature)
               + ", downfall="
               + fmt(var8.downfall)
               + ", precip="
               + var8.hasPrecipitation
               + ", tags="
               + var8.biomeTags.size()
               + ")"
         );
      }

      return new HabitatResolver.HabitatContext(
         var17, var3, var4, var5, var6, var7, var13, var11, List.copyOf(var8.biomeTags), var8.temperature, var8.downfall, var8.hasPrecipitation, var16, var15
      );
   }

   private static HabitatConfig.Habitat findHabitat(HabitatConfig var0, String var1) {
      return var0 != null && var0.habitats != null && var1 != null
         ? var0.habitats.stream().filter(var1x -> var1x != null && var1.equals(var1x.id)).findFirst().orElse(null)
         : null;
   }

   private static boolean matches(HabitatConfig.Habitat var0, HabitatResolver.BiomeSignals var1) {
      if (var0.requiresSky != null && var0.requiresSky != var1.skyVisible) {
         return false;
      }

      if (var0.requiresWater != null && var0.requiresWater != var1.inWater) {
         return false;
      }

      if (var0.minY != null && var1.y < var0.minY) {
         return false;
      }

      if (var0.maxY != null && var1.y > var0.maxY) {
         return false;
      }

      if (var0.dimensionPatterns != null
         && !var0.dimensionPatterns.isEmpty()
         && var0.dimensionPatterns.stream().noneMatch(var1x -> Glob.matches(var1x, var1.dimensionId))) {
         return false;
      }

      boolean var2 = var0.biomePatterns != null && !var0.biomePatterns.isEmpty();
      boolean var3 = var0.biomeTagPatterns != null && !var0.biomeTagPatterns.isEmpty();
      if (var2 || var3) {
         boolean var4 = var2 && var0.biomePatterns.stream().anyMatch(var1x -> Glob.matches(var1x, var1.biomeId));
         boolean var5 = var3 && var1.biomeTags.stream().anyMatch(var1x -> var0.biomeTagPatterns.stream().anyMatch(var1xx -> Glob.matches(var1xx, var1x)));
         if (!var4 && !var5) {
            return false;
         }
      }

      return true;
   }

   private static String smartClassify(HabitatConfig.Detection var0, HabitatResolver.BiomeSignals var1) {
      if (var0 == null) {
         return null;
      }

      String var2 = var0.mode == null ? "balanced" : var0.mode.toLowerCase(Locale.ROOT);
      if (!var2.equals("patterns_only") && !var2.equals("off") && !var2.equals("disabled")) {
         boolean var3 = var2.equals("aggressive");
         String var4 = buildSearchable(var0, var1);
         if (var0.useDimension) {
            String var5 = var1.dimensionId.toLowerCase(Locale.ROOT);
            if (var5.contains("nether")) {
               return "nether";
            }

            if (var5.equals("minecraft:the_end") || var5.endsWith(":the_end") || var5.contains("end_dimension")) {
               return "end";
            }
         }

         if (!var0.useSkyVisibility || var1.skyVisible || var0.useWater && var1.inWater) {
            if (!var0.useWater || !var1.inWater) {
               LinkedHashMap<String, Integer> var8 = new LinkedHashMap<>();
               addMarkerScore(var8, var4, "wetland", 8, "swamp", "marsh", "mangrove", "bog", "wetland", "fen");
               addMarkerScore(var8, var4, "tropical_forest", 8, "jungle", "rainforest", "tropical_forest", "tropical forest");
               addMarkerScore(var8, var4, "taiga", 8, "taiga", "conifer", "pine", "spruce", "boreal");
               addMarkerScore(var8, var4, "tundra", 8, "tundra", "snow", "frozen", "icy", "ice_spikes", "cold");
               addMarkerScore(var8, var4, "desert", 8, "desert", "badlands", "dune", "arid", "xeric");
               addMarkerScore(var8, var4, "savanna", 8, "savanna", "savannah");
               addMarkerScore(var8, var4, "alpine", 8, "mountain", "peak", "highland", "slope", "alpine");
               addMarkerScore(var8, var4, "rocky", 8, "rocky", "stony", "gravel", "cliff", "windswept", "crag");
               addMarkerScore(var8, var4, "mushroom", 8, "mushroom", "fungal", "fungus", "mycel");
               addMarkerScore(var8, var4, "volcanic", 8, "volcan", "basalt", "crater", "lava_field", "lava field");
               addMarkerScore(var8, var4, "coast", 8, "beach", "shore", "coast", "coastal");
               addMarkerScore(var8, var4, "temperate_forest", 7, "forest", "woods", "woodland", "grove", "cherry");
               addMarkerScore(var8, var4, "grassland", 7, "plains", "meadow", "field", "prairie", "steppe", "grassland");
               if (var0.useAltitude && var1.skyVisible && var1.y >= var0.alpineY) {
                  add(var8, "alpine", 4);
                  add(var8, "rocky", 1);
               }

               if (var0.useClimate && !Double.isNaN(var1.temperature)) {
                  if (var1.temperature <= var0.coldTemperature) {
                     add(var8, "tundra", 5);
                     add(var8, "taiga", 1);
                  } else if (var1.temperature <= var0.coolTemperature) {
                     add(var8, "taiga", 3);
                     add(var8, "temperate_forest", 1);
                  }

                  if (var1.temperature >= var0.veryHotTemperature) {
                     add(var8, "desert", 4);
                     add(var8, "savanna", 2);
                  } else if (var1.temperature >= var0.hotTemperature) {
                     add(var8, "savanna", 2);
                     add(var8, "tropical_forest", 1);
                  }
               }

               if (var0.usePrecipitation && var1.hasPrecipitation != null && !var1.hasPrecipitation) {
                  add(var8, "desert", 4);
                  add(var8, "rocky", 1);
               }

               if (var0.useClimate && !Double.isNaN(var1.downfall)) {
                  if (var1.downfall <= var0.dryDownfall) {
                     add(var8, "desert", 3);
                     add(var8, "savanna", 2);
                     add(var8, "rocky", 1);
                  } else if (var1.downfall >= var0.wetDownfall) {
                     add(var8, "tropical_forest", 3);
                     add(var8, "wetland", 3);
                     add(var8, "temperate_forest", 1);
                  } else {
                     add(var8, "temperate_forest", 1);
                     add(var8, "grassland", 1);
                  }
               }

               Entry<String, Integer> var6 = var8.entrySet().stream().max(Entry.comparingByValue()).orElse(null);
               if (var6 == null) {
                  return var3 ? validFallback(var0.unknownSurfaceHabitat, "general") : null;
               }

               int var7 = var3 ? 1 : 4;
               return var6.getValue() < var7 ? null : var6.getKey();
            } else if (containsAny(var4, "deep_ocean", "deep ocean", "deepocean")) {
               return "deep_ocean";
            } else if (containsAny(var4, "ocean", "marine", "sea")) {
               return "ocean";
            } else if (containsAny(var4, "river", "lake", "freshwater", "fresh_water")) {
               return "freshwater";
            } else {
               return containsAny(var4, "beach", "shore", "coast") ? "coast" : validFallback(var0.unknownWaterHabitat, "freshwater");
            }
         } else {
            return var0.useAltitude && var1.y <= var0.deepCaveY ? "deep_cave" : "cave";
         }
      } else {
         return null;
      }
   }

   private static String buildSearchable(HabitatConfig.Detection var0, HabitatResolver.BiomeSignals var1) {
      StringBuilder var2 = new StringBuilder();
      if (var0.useBiomeNames) {
         var2.append(var1.biomeId.toLowerCase(Locale.ROOT)).append(' ');
      }

      if (var0.useBiomeTags) {
         for (String var4 : var1.biomeTags) {
            var2.append(var4.toLowerCase(Locale.ROOT)).append(' ');
         }
      }

      return var2.toString();
   }

   private static void addMarkerScore(Map<String, Integer> var0, String var1, String var2, int var3, String... var4) {
      if (containsAny(var1, var4)) {
         add(var0, var2, var3);
      }
   }

   private static boolean containsAny(String var0, String... var1) {
      for (String var5 : var1) {
         if (var0.contains(var5)) {
            return true;
         }
      }

      return false;
   }

   private static void add(Map<String, Integer> var0, String var1, int var2) {
      var0.merge(var1, var2, Integer::sum);
   }

   private static String validFallback(String var0, String var1) {
      return var0 != null && !var0.isBlank() ? var0.trim().toLowerCase(Locale.ROOT) : var1;
   }

   public static String predictHabitatForBiomeId(String var0) {
      HabitatConfig var1 = ConfigManager.habitats();
      if (var1 != null && var1.habitats != null) {
         String var2 = var0 == null ? "unknown:unknown" : var0.toLowerCase(Locale.ROOT);
         HabitatConfig.Habitat var3 = var1.habitats
            .stream()
            .filter(var1x -> var1x != null && var1x.id != null && !var1x.id.equals(var1.defaultHabitat))
            .sorted(Comparator.<HabitatConfig.Habitat>comparingInt(var0x -> var0x.priority).reversed())
            .filter(
               var1x -> var1x.biomePatterns != null
                  && var1x.biomePatterns.stream().anyMatch(var1xx -> var1xx != null && !var1xx.isBlank() && !var1xx.equals("*") && Glob.matches(var1xx, var2))
            )
            .findFirst()
            .orElse(null);
         if (var3 != null) {
            return var3.id;
         }

         boolean var4 = containsAny(var2, "ocean", "river", "lake", "sea", "marine");
         HabitatResolver.BiomeSignals var5 = new HabitatResolver.BiomeSignals(var2, "unknown:unknown", true, var4, 64, Set.of(), Double.NaN, Double.NaN, null);
         String var6 = smartClassify(var1.detection, var5);
         return var6 == null ? var1.defaultHabitat : var6;
      } else {
         return "general";
      }
   }

   private static List<String> evidenceFor(HabitatConfig.Detection var0, HabitatResolver.BiomeSignals var1, String var2, String var3) {
      ArrayList var4 = new ArrayList();
      var4.add("classification=" + var3);
      if (var0 != null && var0.useDimension && !var1.dimensionId.equals("minecraft:overworld")) {
         var4.add("dimension=" + var1.dimensionId);
      }

      if (var0 != null && var0.useSkyVisibility) {
         var4.add(var1.skyVisible ? "sky visible" : "no sky");
      }

      if (var0 != null && var0.useWater && var1.inWater) {
         var4.add("spawn position is in water");
      }

      if (var0 != null && var0.useAltitude) {
         var4.add("Y=" + var1.y);
      }

      if (var0 != null && var0.useBiomeNames) {
         var4.add("biome=" + var1.biomeId);
      }

      if (var0 != null && var0.useBiomeTags && !var1.biomeTags.isEmpty()) {
         var4.add("biome tags=" + Math.min(6, var1.biomeTags.size()) + (var1.biomeTags.size() > 6 ? "+" : ""));
      }

      if (var0 != null && var0.useClimate && !Double.isNaN(var1.temperature)) {
         var4.add("temperature=" + fmt(var1.temperature));
      }

      if (var0 != null && var0.useClimate && !Double.isNaN(var1.downfall)) {
         var4.add("downfall=" + fmt(var1.downfall));
      }

      if (var0 != null && var0.usePrecipitation && var1.hasPrecipitation != null) {
         var4.add("precipitation=" + var1.hasPrecipitation);
      }

      var4.add("result=" + var2);
      return List.copyOf(var4);
   }

   private static int confidenceFor(String var0, List<String> var1, String var2, String var3) {
      if ("explicit".equals(var0)) {
         return 100;
      } else if (!"fallback".equals(var0) && !var2.equals(var3)) {
         int var4 = Math.max(0, var1.size() - 2);
         return Math.min(95, 55 + var4 * 6);
      } else {
         return 25;
      }
   }

   public static String biomeId(ServerLevel var0, BlockPos var1) {
      return var0.getBiome(var1).unwrapKey().map(var0x -> var0x.location().toString()).orElse("unknown:unknown");
   }

   private static HabitatResolver.BiomeSignals readBiomeSignals(ServerLevel var0, BlockPos var1, String var2, String var3, boolean var4, boolean var5, int var6) {
      HabitatResolver.StaticBiomeSignals var7 = BIOME_SIGNAL_CACHE.get(var2);
      if (var7 == null) {
         LinkedHashSet var8 = new LinkedHashSet();
         double var9 = Double.NaN;
         double var11 = Double.NaN;
         Boolean var13 = null;

         try {
            Holder var14 = var0.getBiome(var1);
            if (invokeNoArg(var14, "tags") instanceof Stream var16) {
               try (var16) {
                  var16.forEach(var1x -> {
                     Object var2x = invokeNoArg(var1x, "location");
                     if (var2x != null) {
                        var8.add(var2x.toString());
                     } else if (var1x != null) {
                        var8.add(var1x.toString());
                     }
                  });
               }
            }

            Object var23 = invokeNoArg(var14, "value");
            if (var23 != null) {
               var9 = number(invokeNoArg(var23, "getBaseTemperature"), Double.NaN);
               var11 = number(invokeNoArg(var23, "getDownfall"), Double.NaN);
               if (invokeNoArg(var23, "hasPrecipitation") instanceof Boolean var18) {
                  var13 = var18;
               }

               if (var13 == null) {
                  Object var25 = invokeOneArg(var23, "getPrecipitationAt", BlockPos.class, var1);
                  if (var25 != null) {
                     var13 = !var25.toString().toUpperCase(Locale.ROOT).contains("NONE");
                  }
               }

               if (Double.isNaN(var11)) {
                  Object var26 = invokeNoArg(var23, "getModifiedClimateSettings", "climateSettings");
                  if (var26 != null) {
                     var11 = number(invokeNoArg(var26, "downfall"), Double.NaN);
                  }
               }
            }
         } catch (Throwable var22) {
            ConfigManager.debug("Biome signal reflection unavailable for " + var2 + ": " + var22.getClass().getSimpleName());
         }

         var7 = new HabitatResolver.StaticBiomeSignals(Set.copyOf(var8), var9, var11, var13);
         BIOME_SIGNAL_CACHE.put(var2, var7);
      }

      return new HabitatResolver.BiomeSignals(var2, var3, var4, var5, var6, var7.biomeTags, var7.temperature, var7.downfall, var7.hasPrecipitation);
   }

   private static Object invokeNoArg(Object var0, String... var1) {
      if (var0 == null) {
         return null;
      }

      for (String var5 : var1) {
         try {
            Method var6 = var0.getClass().getMethod(var5);
            return var6.invoke(var0);
         } catch (Throwable var7) {
         }
      }

      return null;
   }

   private static Object invokeOneArg(Object var0, String var1, Class<?> var2, Object var3) {
      if (var0 == null) {
         return null;
      }

      try {
         Method var4 = var0.getClass().getMethod(var1, var2);
         return var4.invoke(var0, var3);
      } catch (Throwable var5) {
         return null;
      }
   }

   private static double number(Object var0, double var1) {
      return var0 instanceof Number var3 ? var3.doubleValue() : var1;
   }

   private static double clamp01(double var0) {
      return Math.max(0.0, Math.min(1.0, var0));
   }

   private static String fmt(double var0) {
      return Double.isNaN(var0) ? "?" : String.format(Locale.ROOT, "%.2f", var0);
   }

   private record BiomeSignals(
      String biomeId,
      String dimensionId,
      boolean skyVisible,
      boolean inWater,
      int y,
      Set<String> biomeTags,
      double temperature,
      double downfall,
      Boolean hasPrecipitation
   ) {
   }

   public record HabitatContext(
      String habitatId,
      String biomeId,
      String dimensionId,
      boolean skyVisible,
      boolean inWater,
      int y,
      double spawnAcceptance,
      String classificationSource,
      List<String> biomeTags,
      double temperature,
      double downfall,
      Boolean hasPrecipitation,
      int confidence,
      List<String> evidence
   ) {
   }

   public record HabitatObservation(
      String biomeId, String habitatId, String source, int confidence, double temperature, double downfall, int tagCount, long lastSeenMillis
   ) {
   }

   private record StaticBiomeSignals(Set<String> biomeTags, double temperature, double downfall, Boolean hasPrecipitation) {
   }
}
