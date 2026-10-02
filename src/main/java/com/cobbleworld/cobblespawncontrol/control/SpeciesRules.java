package com.cobbleworld.cobblespawncontrol.control;

import com.cobbleworld.cobblespawncontrol.config.ConfigManager;
import com.cobbleworld.cobblespawncontrol.config.SpeciesConfig;
import com.cobbleworld.cobblespawncontrol.util.Glob;
import java.util.Comparator;
import net.minecraft.server.level.ServerLevel;

public final class SpeciesRules {
   private SpeciesRules() {
   }

   public static SpeciesConfig.Rule findRule(String var0) {
      SpeciesConfig.Rule var1 = ConfigManager.species()
         .rules
         .stream()
         .sorted(Comparator.<SpeciesConfig.Rule>comparingInt(var0x -> var0x.priority).reversed())
         .filter(var1x -> var1x.species != null && var1x.species.stream().anyMatch(var1xx -> Glob.matches(var1xx, var0)))
         .findFirst()
         .orElse(null);
      return var1 != null ? var1 : AutoEcology.ruleFor(var0);
   }

   public static boolean habitatAllowed(SpeciesConfig.Rule var0, String var1, boolean var2) {
      if (var0 == null) {
         return !var2;
      } else {
         return var0.blockedHabitats != null && var0.blockedHabitats.stream().anyMatch(var1x -> Glob.matches(var1x, var1))
            ? false
            : var0.allowedHabitats == null || var0.allowedHabitats.isEmpty() || var0.allowedHabitats.stream().anyMatch(var1x -> Glob.matches(var1x, var1));
      }
   }

   public static boolean timeAllowed(SpeciesConfig.Rule var0, ServerLevel var1) {
      boolean var2 = var1.isDay();
      if (var0 != null && var0.allowedTimes != null && !var0.allowedTimes.isEmpty()) {
         String var3 = var2 ? "day" : "night";
         return var0.allowedTimes.stream().anyMatch(var1x -> var1x.equalsIgnoreCase(var3) || var1x.equalsIgnoreCase("any"));
      } else {
         return true;
      }
   }
}
