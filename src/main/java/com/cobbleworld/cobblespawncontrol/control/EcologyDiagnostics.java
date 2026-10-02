package com.cobbleworld.cobblespawncontrol.control;

import com.cobbleworld.cobblespawncontrol.config.ConfigManager;
import com.cobbleworld.cobblespawncontrol.config.ConfigValidator;
import com.cobbleworld.cobblespawncontrol.config.SpeciesConfig;
import com.cobbleworld.cobblespawncontrol.integration.CobblemonBridge;
import com.cobbleworld.cobblespawncontrol.util.Glob;
import java.lang.reflect.Field;
import java.lang.reflect.Method;
import java.nio.charset.StandardCharsets;
import java.nio.file.Files;
import java.nio.file.Path;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.Collection;
import java.util.Comparator;
import java.util.LinkedHashMap;
import java.util.LinkedHashSet;
import java.util.List;
import java.util.Locale;
import java.util.Map;
import java.util.Set;
import java.util.Map.Entry;
import net.minecraft.server.level.ServerLevel;

public final class EcologyDiagnostics {
   private EcologyDiagnostics() {
   }

   public static EcologyDiagnostics.AuditSummary writeReport(ServerLevel var0) {
      Set<String> var1 = registeredBiomeIds(var0);
      Map<String, HabitatResolver.HabitatObservation> var2 = HabitatResolver.observations();
      Set<String> var3 = CobblemonBridge.loadedSpeciesIds();
      List<ConfigValidator.Issue> var4 = ConfigValidator.validate(ConfigManager.general(), ConfigManager.habitats(), ConfigManager.species());
      int var5 = 0;
      LinkedHashMap<String, Integer> var6 = new LinkedHashMap<>();

      for (String var8 : var1) {
         String var9 = HabitatResolver.predictHabitatForBiomeId(var8);
         if (var9 == null) {
            var9 = ConfigManager.habitats().defaultHabitat;
         }

         if (var9.equals(ConfigManager.habitats().defaultHabitat)) {
            var5++;
         }

         var6.merge(var9, 1, Integer::sum);
      }

      int var14 = 0;
      int var15 = 0;
      int var16 = 0;

      for (String var11 : var3) {
         if (findExplicitRule(var11) != null) {
            var14++;
         } else if (AutoEcology.ruleFor(var11) != null) {
            var15++;
         } else {
            var16++;
         }
      }

      Path var17 = ConfigManager.directory().resolve("reports");
      Path var18 = var17.resolve("ecology-report.txt");

      try {
         Files.createDirectories(var17);
         ArrayList<String> var12 = new ArrayList<>();
         var12.add("Cobble Spawn Control Ecology Report");
         var12.add("Generated: " + LocalDateTime.now());
         var12.add("");
         var12.add("CONFIG VALIDATION");
         var12.add("Errors: " + ConfigValidator.errorCount(var4));
         var12.add("Warnings: " + ConfigValidator.warningCount(var4));
         if (var4.isEmpty()) {
            var12.add("OK - no validation issues found.");
         } else {
            var4.forEach(var1x -> var12.add("- " + var1x));
         }

         var12.add("");
         var12.add("BIOMES");
         var12.add("Registered biomes: " + var1.size());
         var12.add("Observed at runtime: " + var2.size());
         var12.add("Registry-name predictions falling back to '" + ConfigManager.habitats().defaultHabitat + "': " + var5);
         var6.entrySet().stream().sorted(Entry.comparingByKey()).forEach(var1x -> var12.add("- " + (String)var1x.getKey() + ": " + var1x.getValue()));
         var12.add("");
         var12.add("REGISTERED BIOME PREDICTIONS");
         var1.stream().sorted().forEach(var1x -> var12.add(var1x + " -> " + HabitatResolver.predictHabitatForBiomeId(var1x)));
         var12.add("");
         var12.add("OBSERVED BIOME CLASSIFICATIONS");
         if (var2.isEmpty()) {
            var12.add("No runtime observations yet. Visit areas or allow wild spawns, then generate the report again.");
         }

         var2.values()
            .stream()
            .sorted(Comparator.comparing(HabitatResolver.HabitatObservation::biomeId))
            .forEach(
               var1x -> var12.add(
                  var1x.biomeId()
                     + " -> "
                     + var1x.habitatId()
                     + " | source="
                     + var1x.source()
                     + " | confidence="
                     + var1x.confidence()
                     + "% | temp="
                     + fmt(var1x.temperature())
                     + " | downfall="
                     + fmt(var1x.downfall())
                     + " | tags="
                     + var1x.tagCount()
               )
            );
         var12.add("");
         var12.add("POKEMON ECOLOGY");
         var12.add("Loaded species: " + var3.size());
         var12.add("Explicit species.json coverage: " + var14);
         var12.add("Automatic ecology coverage: " + var15);
         var12.add("Unresolved/fail-open species: " + var16);
         if (var16 > 0) {
            var12.add("");
            var12.add("UNRESOLVED SPECIES");
            var3.stream()
               .sorted()
               .filter(var0x -> findExplicitRule(var0x) == null && AutoEcology.ruleFor(var0x) == null)
               .forEach(var1x -> var12.add("- " + var1x));
         }

         Files.write(var18, var12, StandardCharsets.UTF_8);
      } catch (Exception var13) {
         ConfigManager.error("Failed to write ecology report", var13);
      }

      return new EcologyDiagnostics.AuditSummary(
         var1.size(),
         var5,
         var2.size(),
         var3.size(),
         var14,
         var15,
         var16,
         (int)ConfigValidator.errorCount(var4),
         (int)ConfigValidator.warningCount(var4),
         var18
      );
   }

   public static Set<String> registeredBiomeIds(ServerLevel var0) {
      LinkedHashSet var1 = new LinkedHashSet();

      try {
         Object var2 = invokeNoArg(var0, "registryAccess");
         Class var3 = Class.forName("net.minecraft.core.registries.Registries");
         Field var4 = var3.getField("BIOME");
         Object var5 = var4.get(null);
         Object var6 = invokeOneCompatible(var2, "registryOrThrow", var5);
         Object var7 = invokeNoArg(var6, "keySet");
         if (var7 instanceof Collection) {
            for (Object var11 : (Collection)var7) {
               if (var11 != null) {
                  var1.add(var11.toString().toLowerCase(Locale.ROOT));
               }
            }
         } else if (var7 instanceof Iterable) {
            for (Object var14 : (Iterable)var7) {
               if (var14 != null) {
                  var1.add(var14.toString().toLowerCase(Locale.ROOT));
               }
            }
         }
      } catch (Throwable var12) {
         ConfigManager.debug("Could not enumerate biome registry: " + var12.getClass().getSimpleName());
      }

      if (var1.isEmpty()) {
         var1.addAll(HabitatResolver.observations().keySet());
      }

      return var1;
   }

   private static SpeciesConfig.Rule findExplicitRule(String var0) {
      return ConfigManager.species() != null && ConfigManager.species().rules != null
         ? ConfigManager.species()
            .rules
            .stream()
            .sorted(Comparator.<SpeciesConfig.Rule>comparingInt(var0x -> var0x.priority).reversed())
            .filter(var1 -> var1 != null && var1.species != null && var1.species.stream().anyMatch(var1x -> Glob.matches(var1x, var0)))
            .findFirst()
            .orElse(null)
         : null;
   }

   private static Object invokeNoArg(Object var0, String var1) throws ReflectiveOperationException {
      if (var0 == null) {
         return null;
      }

      Method var2 = var0.getClass().getMethod(var1);
      return var2.invoke(var0);
   }

   private static Object invokeOneCompatible(Object var0, String var1, Object var2) throws ReflectiveOperationException {
      if (var0 == null) {
         return null;
      }

      for (Method var6 : var0.getClass().getMethods()) {
         if (var6.getName().equals(var1) && var6.getParameterCount() == 1) {
            Class var7 = var6.getParameterTypes()[0];
            if (var2 == null || var7.isInstance(var2)) {
               return var6.invoke(var0, var2);
            }
         }
      }

      throw new NoSuchMethodException(var1);
   }

   private static String fmt(double var0) {
      return Double.isNaN(var0) ? "?" : String.format(Locale.ROOT, "%.2f", var0);
   }

   public record AuditSummary(
      int registeredBiomes,
      int predictedGeneral,
      int observedBiomes,
      int loadedSpecies,
      int explicitSpecies,
      int automaticSpecies,
      int unresolvedSpecies,
      int errors,
      int warnings,
      Path reportPath
   ) {
   }
}
