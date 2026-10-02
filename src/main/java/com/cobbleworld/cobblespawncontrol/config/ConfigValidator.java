package com.cobbleworld.cobblespawncontrol.config;

import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.Locale;
import java.util.Set;

public final class ConfigValidator {
   private ConfigValidator() {
   }

   public static List<ConfigValidator.Issue> validate(GeneralConfig var0, HabitatConfig var1, SpeciesConfig var2) {
      ArrayList var3 = new ArrayList();
      if (var0 == null) {
         var3.add(new ConfigValidator.Issue(ConfigValidator.Severity.ERROR, "general.json", "Configuration is missing."));
         return var3;
      }

      if (var1 == null) {
         var3.add(new ConfigValidator.Issue(ConfigValidator.Severity.ERROR, "habitats.json", "Configuration is missing."));
         return var3;
      }

      if (var2 == null) {
         var3.add(new ConfigValidator.Issue(ConfigValidator.Severity.ERROR, "species.json", "Configuration is missing."));
         return var3;
      }

      if (var0.density.minimumSpawningZoneDistanceFromPlayer > var0.density.maximumSpawningZoneDistanceFromPlayer) {
         var3.add(
            new ConfigValidator.Issue(ConfigValidator.Severity.ERROR, "general.density", "Minimum spawn distance is greater than maximum spawn distance.")
         );
      }

      if (var0.levels.globalMin > var0.levels.globalMax) {
         var3.add(new ConfigValidator.Issue(ConfigValidator.Severity.ERROR, "general.levels", "globalMin is greater than globalMax."));
      }

      if (var0.levels.levelNearSpawn > var0.levels.maxDistanceLevel) {
         var3.add(
            new ConfigValidator.Issue(
               ConfigValidator.Severity.WARNING, "general.levels", "levelNearSpawn is greater than maxDistanceLevel; distance progression will run backwards."
            )
         );
      }

      if (var0.population.totalCap < var0.population.defaultSpeciesCap) {
         var3.add(
            new ConfigValidator.Issue(ConfigValidator.Severity.WARNING, "general.population", "Default species cap exceeds the total local population cap.")
         );
      }

      if (var0.evolutionRarity.base < 0.0
         || var0.evolutionRarity.base > 1.0
         || var0.evolutionRarity.stage1 < 0.0
         || var0.evolutionRarity.stage1 > 1.0
         || var0.evolutionRarity.stage2 < 0.0
         || var0.evolutionRarity.stage2 > 1.0
         || var0.evolutionRarity.stage3Plus < 0.0
         || var0.evolutionRarity.stage3Plus > 1.0) {
         var3.add(new ConfigValidator.Issue(ConfigValidator.Severity.ERROR, "general.evolutionRarity", "All evolution rarity values must be between 0 and 1."));
      }

      HashSet var4 = new HashSet();
      boolean var5 = false;
      if (var1.habitats != null && !var1.habitats.isEmpty()) {
         for (int var6 = 0; var6 < var1.habitats.size(); var6++) {
            HabitatConfig.Habitat var7 = var1.habitats.get(var6);
            String var8 = "habitats[" + var6 + "]";
            if (var7 != null && var7.id != null && !var7.id.isBlank()) {
               String var9 = var7.id.toLowerCase(Locale.ROOT);
               if (!var4.add(var9)) {
                  var3.add(new ConfigValidator.Issue(ConfigValidator.Severity.ERROR, var8 + ".id", "Duplicate habitat ID '" + var7.id + "'."));
               }

               if (var9.equals(String.valueOf(var1.defaultHabitat).toLowerCase(Locale.ROOT))) {
                  var5 = true;
               }

               if (var7.spawnAcceptance < 0.0 || var7.spawnAcceptance > 1.0) {
                  var3.add(new ConfigValidator.Issue(ConfigValidator.Severity.ERROR, var8 + ".spawnAcceptance", "Must be between 0 and 1."));
               }

               if (var7.minY != null && var7.maxY != null && var7.minY > var7.maxY) {
                  var3.add(new ConfigValidator.Issue(ConfigValidator.Severity.ERROR, var8, "minY is greater than maxY."));
               }
            } else {
               var3.add(new ConfigValidator.Issue(ConfigValidator.Severity.ERROR, var8, "Habitat ID is blank."));
            }
         }
      } else {
         var3.add(new ConfigValidator.Issue(ConfigValidator.Severity.ERROR, "habitats.habitats", "At least one habitat definition is required."));
      }

      if (!var5) {
         var3.add(
            new ConfigValidator.Issue(
               ConfigValidator.Severity.ERROR, "habitats.defaultHabitat", "Default habitat '" + var1.defaultHabitat + "' is not defined."
            )
         );
      }

      HashSet var10 = new HashSet();
      if (var2.rules != null) {
         for (int var11 = 0; var11 < var2.rules.size(); var11++) {
            SpeciesConfig.Rule var12 = var2.rules.get(var11);
            String var13 = "species.rules[" + var11 + "]";
            if (var12 != null) {
               if (var12.name == null || var12.name.isBlank()) {
                  var3.add(new ConfigValidator.Issue(ConfigValidator.Severity.ERROR, var13 + ".name", "Rule name is blank."));
               } else if (!var10.add(var12.name.toLowerCase(Locale.ROOT))) {
                  var3.add(new ConfigValidator.Issue(ConfigValidator.Severity.WARNING, var13 + ".name", "Duplicate rule name '" + var12.name + "'."));
               }

               if (var12.spawnAcceptance < 0.0 || var12.spawnAcceptance > 1.0) {
                  var3.add(new ConfigValidator.Issue(ConfigValidator.Severity.ERROR, var13 + ".spawnAcceptance", "Must be between 0 and 1."));
               }

               if (var12.localCap != null && var12.localCap < 1) {
                  var3.add(new ConfigValidator.Issue(ConfigValidator.Severity.ERROR, var13 + ".localCap", "Must be null or at least 1."));
               }

               if (var12.minLevel != null && var12.maxLevel != null && var12.minLevel > var12.maxLevel) {
                  var3.add(new ConfigValidator.Issue(ConfigValidator.Severity.ERROR, var13, "minLevel is greater than maxLevel."));
               }

               checkHabitats(var3, var4, var12.allowedHabitats, var13 + ".allowedHabitats");
               checkHabitats(var3, var4, var12.blockedHabitats, var13 + ".blockedHabitats");
            }
         }
      }

      return var3;
   }

   private static void checkHabitats(List<ConfigValidator.Issue> var0, Set<String> var1, List<String> var2, String var3) {
      if (var2 != null) {
         for (String var5 : var2) {
            if (var5 != null && !var5.isBlank() && !var5.contains("*") && !var1.contains(var5.toLowerCase(Locale.ROOT))) {
               var0.add(new ConfigValidator.Issue(ConfigValidator.Severity.WARNING, var3, "Unknown habitat reference '" + var5 + "'."));
            }
         }
      }
   }

   public static long errorCount(List<ConfigValidator.Issue> var0) {
      return var0.stream().filter(var0x -> var0x.severity == ConfigValidator.Severity.ERROR).count();
   }

   public static long warningCount(List<ConfigValidator.Issue> var0) {
      return var0.stream().filter(var0x -> var0x.severity == ConfigValidator.Severity.WARNING).count();
   }

   public record Issue(ConfigValidator.Severity severity, String path, String message) {
      @Override
      public String toString() {
         return this.severity + " " + this.path + ": " + this.message;
      }
   }

   public enum Severity {
      ERROR,
      WARNING;
   }
}
