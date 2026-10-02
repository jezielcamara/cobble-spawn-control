package com.cobbleworld.cobblespawncontrol.control;

import com.cobbleworld.cobblespawncontrol.config.ConfigManager;
import com.cobbleworld.cobblespawncontrol.config.GeneralConfig;
import com.cobbleworld.cobblespawncontrol.config.SpeciesConfig;
import com.cobbleworld.cobblespawncontrol.integration.CobblemonBridge;
import com.cobbleworld.cobblespawncontrol.util.Glob;
import java.lang.reflect.Method;
import java.util.Map;
import java.util.Map.Entry;
import java.util.concurrent.ThreadLocalRandom;
import net.minecraft.core.BlockPos;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.world.entity.Entity;

public final class LevelScaler {
   private LevelScaler() {
   }

   public static int compute(ServerLevel var0, Entity var1, HabitatResolver.HabitatContext var2, String var3, SpeciesConfig.Rule var4) {
      GeneralConfig.Levels var5 = ConfigManager.general().levels;
      int var6 = LevelScaler.CobbleSpawnMath.clamp(CobblemonBridge.currentLevel(var1), var5.globalMin, var5.globalMax);
      if (var5.enabled && !isExcluded(var3, var5)) {
         BlockPos var7 = var1.blockPosition();
         double var8 = horizontalDistance(var7, sharedSpawn(var0));
         int var10 = distanceLevel(var8, var5);
         if (var5.depth != null && var5.depth.enabled && var7.getY() < var5.depth.startY) {
            double var11 = var5.depth.startY - var7.getY();
            double var13 = Math.min(var5.depth.maxIncrease, var11 * var5.depth.percentagePerBlock);
            var10 = (int)Math.round(var10 * (1.0 + var13));
         }

         if (var5.randomVariance > 0) {
            var10 += ThreadLocalRandom.current().nextInt(-var5.randomVariance, var5.randomVariance + 1);
         }

         var10 = applyRangeMap(var10, var2.dimensionId(), var5.dimensionRanges);
         var10 = applyRangeMap(var10, var2.biomeId(), var5.biomeRanges);
         if (var4 != null) {
            if (var4.minLevel != null) {
               var10 = Math.max(var10, var4.minLevel);
            }

            if (var4.maxLevel != null) {
               var10 = Math.min(var10, var4.maxLevel);
            }
         }

         return LevelScaler.CobbleSpawnMath.clamp(var10, var5.globalMin, var5.globalMax);
      } else {
         return var6;
      }
   }

   public static int preview(ServerLevel var0, BlockPos var1, HabitatResolver.HabitatContext var2) {
      GeneralConfig.Levels var3 = ConfigManager.general().levels;
      double var4 = horizontalDistance(var1, sharedSpawn(var0));
      int var6 = distanceLevel(var4, var3);
      if (var3.depth != null && var3.depth.enabled && var1.getY() < var3.depth.startY) {
         double var7 = var3.depth.startY - var1.getY();
         double var9 = Math.min(var3.depth.maxIncrease, var7 * var3.depth.percentagePerBlock);
         var6 = (int)Math.round(var6 * (1.0 + var9));
      }

      var6 = applyRangeMap(var6, var2.dimensionId(), var3.dimensionRanges);
      var6 = applyRangeMap(var6, var2.biomeId(), var3.biomeRanges);
      return LevelScaler.CobbleSpawnMath.clamp(var6, var3.globalMin, var3.globalMax);
   }

   private static int distanceLevel(double var0, GeneralConfig.Levels var2) {
      if (var2.distanceForMaxLevel <= 0.0) {
         return var2.maxDistanceLevel;
      }

      double var3 = Math.min(1.0, Math.max(0.0, var0 / var2.distanceForMaxLevel));
      double var5 = Math.pow(var3, Math.max(0.01, var2.exponent));
      return (int)Math.round(var2.levelNearSpawn + (var2.maxDistanceLevel - var2.levelNearSpawn) * var5);
   }

   private static boolean isExcluded(String var0, GeneralConfig.Levels var1) {
      if (var0 != null && var1.excludedSpecies != null) {
         for (String var3 : var1.excludedSpecies) {
            if (Glob.matches(var3, var0)) {
               return true;
            }
         }

         return false;
      } else {
         return false;
      }
   }

   private static int applyRangeMap(int var0, String var1, Map<String, GeneralConfig.LevelRange> var2) {
      if (var1 != null && var2 != null) {
         for (Entry var4 : var2.entrySet()) {
            if (Glob.matches((String)var4.getKey(), var1)) {
               GeneralConfig.LevelRange var5 = (GeneralConfig.LevelRange)var4.getValue();
               if (var5 == null) {
                  return var0;
               }

               return LevelScaler.CobbleSpawnMath.clamp(var0, var5.min, var5.max);
            }
         }

         return var0;
      } else {
         return var0;
      }
   }

   private static double horizontalDistance(BlockPos var0, BlockPos var1) {
      long var2 = (long)var0.getX() - var1.getX();
      long var4 = (long)var0.getZ() - var1.getZ();
      return Math.sqrt((double)var2 * var2 + (double)var4 * var4);
   }

   private static BlockPos sharedSpawn(ServerLevel var0) {
      for (String var4 : new String[]{"getSharedSpawnPos", "getRespawnData"}) {
         try {
            Method var5 = var0.getClass().getMethod(var4);
            if (var5.invoke(var0) instanceof BlockPos var7) {
               return var7;
            }
         } catch (ReflectiveOperationException var8) {
         }
      }

      return BlockPos.ZERO;
   }

   private static final class CobbleSpawnMath {
      static int clamp(int var0, int var1, int var2) {
         if (var2 < var1) {
            int var3 = var1;
            var1 = var2;
            var2 = var3;
         }

         return Math.max(var1, Math.min(var2, var0));
      }
   }
}
