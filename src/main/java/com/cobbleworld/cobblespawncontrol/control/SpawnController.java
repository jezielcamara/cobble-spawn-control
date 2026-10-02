package com.cobbleworld.cobblespawncontrol.control;

import com.cobbleworld.cobblespawncontrol.config.ConfigManager;
import com.cobbleworld.cobblespawncontrol.config.GeneralConfig;
import com.cobbleworld.cobblespawncontrol.config.SpeciesConfig;
import com.cobbleworld.cobblespawncontrol.integration.CobblemonBridge;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;
import java.util.concurrent.ThreadLocalRandom;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.phys.AABB;

public final class SpawnController {
   private SpawnController() {
   }

   public static void handleSpawnEvent(Object var0) {
      try {
         Object var1 = CobblemonBridge.spawnEventEntity(var0);
         if (!(var1 instanceof Entity var2)) {
            return;
         }

         if (!(var2.level() instanceof ServerLevel var3)) {
            return;
         }

         if (!CobblemonBridge.isNaturalWildPokemon(var1)) {
            return;
         }

         String var13 = CobblemonBridge.speciesId(var1);
         HabitatResolver.HabitatContext var5 = HabitatResolver.resolve(var3, var2);
         SpeciesConfig.Rule var6 = SpeciesRules.findRule(var13);
         GeneralConfig var7 = ConfigManager.general();
         if (var7.ecology != null
            && var7.ecology.enabled
            && (!SpeciesRules.habitatAllowed(var6, var5.habitatId(), var7.ecology.rejectUnmappedSpecies) || !SpeciesRules.timeAllowed(var6, var3))) {
            reject(var0, var13, "ecology");
            return;
         }

         double var8 = var5.spawnAcceptance();
         if (var6 != null) {
            var8 *= Math.max(0.0, var6.spawnAcceptance);
         }

         var8 *= evolutionAcceptance(var1, var6, var7.evolutionRarity);
         var8 = Math.max(0.0, Math.min(1.0, var8));
         if (ThreadLocalRandom.current().nextDouble() > var8) {
            reject(var0, var13, "rarity");
            return;
         }

         if (var7.population != null && var7.population.enabled) {
            SpawnController.PopulationSnapshot var10 = inspectPopulation(var3, var2, var7.population.radius);
            int var11 = var6 != null && var6.localCap != null ? Math.max(0, var6.localCap) : Math.max(0, var7.population.defaultSpeciesCap);
            if (var10.total() >= var7.population.totalCap || var10.speciesCounts().getOrDefault(var13, 0) >= var11) {
               reject(var0, var13, "population cap");
               return;
            }
         }

         if (var7.levels != null && var7.levels.enabled) {
            int var16 = LevelScaler.compute(var3, var2, var5, var13, var6);
            CobblemonBridge.setLevel(var1, var16);
         }
      } catch (Throwable var12) {
         ConfigManager.error("Spawn control failed open for one spawn", var12);
      }
   }

   private static void reject(Object var0, String var1, String var2) {
      CobblemonBridge.cancelSpawnEvent(var0);
      if (ConfigManager.general().debugLogging) {
         ConfigManager.log("Rejected " + var1 + " (" + var2 + ")");
      }
   }

   private static double evolutionAcceptance(Object var0, SpeciesConfig.Rule var1, GeneralConfig.EvolutionRarity var2) {
      if (var2 != null && var2.enabled && (var1 == null || !var1.ignoreEvolutionRarity)) {
         int var3 = CobblemonBridge.evolutionStage(var0);
         if (var3 <= 0) {
            return var2.base;
         } else if (var3 == 1) {
            return var2.stage1;
         } else {
            return var3 == 2 ? var2.stage2 : var2.stage3Plus;
         }
      } else {
         return 1.0;
      }
   }

   public static SpawnController.PopulationSnapshot inspectPopulation(ServerLevel var0, Entity var1, double var2) {
      Class var4 = CobblemonBridge.pokemonEntityClass();
      if (var4 == null) {
         return new SpawnController.PopulationSnapshot(0, Map.of());
      }

      AABB var5 = var1.getBoundingBox().inflate(var2);
      List var6 = var0.getEntitiesOfClass(var4, var5, var0x -> true);
      LinkedHashMap<String, Integer> var7 = new LinkedHashMap<>();
      int var8 = 0;

      for (Object var10 : var6) {
         if (CobblemonBridge.isWildPokemon(var10)) {
            var8++;
            String var11 = CobblemonBridge.speciesId(var10);
            var7.merge(var11, 1, Integer::sum);
         }
      }

      return new SpawnController.PopulationSnapshot(var8, var7);
   }

   public record PopulationSnapshot(int total, Map<String, Integer> speciesCounts) {
   }
}
