package com.cobbleworld.cobblespawncontrol.integration;

import com.cobbleworld.cobblespawncontrol.config.ConfigManager;
import com.cobbleworld.cobblespawncontrol.config.GeneralConfig;
import java.lang.reflect.Field;
import java.lang.reflect.Method;
import java.lang.reflect.Modifier;
import java.util.Collections;
import java.util.IdentityHashMap;
import java.util.LinkedHashSet;
import java.util.Set;
import java.util.function.Consumer;

public final class CobblemonBridge {
   private static final String POKEMON_ENTITY = "com.cobblemon.mod.common.entity.pokemon.PokemonEntity";
   private static final String COBBLEMON_EVENTS = "com.cobblemon.mod.common.api.events.CobblemonEvents";
   private static final String COBBLEMON_MAIN = "com.cobblemon.mod.common.Cobblemon";
   private static volatile boolean hookInstalled;
   private static volatile Class<?> pokemonEntityClass;

   private CobblemonBridge() {
   }

   public static synchronized boolean installSpawnHook(Consumer<Object> var0) {
      if (hookInstalled) {
         return true;
      }

      try {
         Class var1 = Class.forName("com.cobblemon.mod.common.api.events.CobblemonEvents");
         Field var2 = var1.getField("POKEMON_ENTITY_SPAWN");
         Object var3 = var2.get(null);
         Method var4 = findSubscribe(var3.getClass());
         var4.invoke(var3, var0);
         hookInstalled = true;
         ConfigManager.log("Cobblemon POKEMON_ENTITY_SPAWN hook installed.");
         return true;
      } catch (Throwable var5) {
         ConfigManager.error("Could not install Cobblemon spawn hook", var5);
         return false;
      }
   }

   private static Method findSubscribe(Class<?> var0) throws NoSuchMethodException {
      for (Method var4 : var0.getMethods()) {
         if (var4.getName().equals("subscribe")) {
            Class[] var5 = var4.getParameterTypes();
            if (var5.length == 1 && Consumer.class.isAssignableFrom(var5[0])) {
               return var4;
            }
         }
      }

      throw new NoSuchMethodException("No Java Consumer subscribe overload on " + var0.getName());
   }

   public static boolean isHookInstalled() {
      return hookInstalled;
   }

   public static Class<?> pokemonEntityClass() {
      Class var0 = pokemonEntityClass;
      if (var0 != null) {
         return var0;
      }

      try {
         var0 = Class.forName("com.cobblemon.mod.common.entity.pokemon.PokemonEntity");
         pokemonEntityClass = var0;
         return var0;
      } catch (ClassNotFoundException var2) {
         return null;
      }
   }

   public static Object spawnEventEntity(Object var0) {
      return invoke(var0, "getEntity");
   }

   public static void cancelSpawnEvent(Object var0) {
      invoke(var0, "cancel");
   }

   public static boolean isPokemonEntity(Object var0) {
      Class var1 = pokemonEntityClass();
      return var1 != null && var1.isInstance(var0);
   }

   public static boolean isNaturalWildPokemon(Object var0) {
      return !isWildPokemon(var0) ? false : invoke(var0, "getSpawnCause") != null;
   }

   public static boolean isWildPokemon(Object var0) {
      if (!isPokemonEntity(var0)) {
         return false;
      }

      Object var1 = invoke(var0, "getPokemon");
      return invoke(var1, "isWild") instanceof Boolean var3 ? var3 : invoke(var1, "getOwnerUUID") == null;
   }

   public static String speciesId(Object var0) {
      Object var1 = invoke(var0, "getPokemon");
      Object var2 = invoke(var1, "getSpecies");
      Object var3 = invoke(var2, "getResourceIdentifier");
      return var3 == null ? "unknown:unknown" : var3.toString().toLowerCase();
   }

   public static Set<String> loadedSpeciesIds() {
      LinkedHashSet var0 = new LinkedHashSet();

      try {
         Class var1 = Class.forName("com.cobblemon.mod.common.api.pokemon.PokemonSpecies");
         Method var2 = var1.getMethod("getSpecies");
         Object var3 = var2.invoke(null);
         if (var3 instanceof Iterable) {
            for (Object var6 : (Iterable)var3) {
               Object var7 = invoke(var6, "getResourceIdentifier");
               if (var7 != null) {
                  var0.add(var7.toString().toLowerCase());
               }
            }
         }
      } catch (Throwable var8) {
         ConfigManager.debug("Could not enumerate loaded Cobblemon species: " + var8.getClass().getSimpleName());
      }

      return var0;
   }

   public static Set<String> speciesTypes(String var0) {
      LinkedHashSet var1 = new LinkedHashSet();
      if (var0 != null && !var0.isBlank()) {
         try {
            Class var2 = Class.forName("com.cobblemon.mod.common.api.pokemon.PokemonSpecies");
            int var4 = var0.indexOf(58);
            String var5 = var4 >= 0 ? var0.substring(0, var4) : "cobblemon";
            String var6 = var4 >= 0 ? var0.substring(var4 + 1) : var0;
            Object var3;
            if (var5.equals("cobblemon")) {
               Method var7 = var2.getMethod("getByName", String.class);
               var3 = var7.invoke(null, var6);
            } else {
               Class var14 = Class.forName("net.minecraft.resources.ResourceLocation");

               Object var8;
               try {
                  Method var9 = var14.getMethod("parse", String.class);
                  var8 = var9.invoke(null, var0);
               } catch (NoSuchMethodException var12) {
                  Method var10 = var14.getMethod("fromNamespaceAndPath", String.class, String.class);
                  var8 = var10.invoke(null, var5, var6);
               }

               Method var17 = var2.getMethod("getByIdentifier", var14);
               var3 = var17.invoke(null, var8);
            }

            if (var3 == null) {
               return var1;
            }

            Object var15 = invoke(var3, "getTypes");
            if (var15 instanceof Iterable) {
               for (Object var19 : (Iterable)var15) {
                  Object var11 = invoke(var19, "getName");
                  if (var11 != null) {
                     var1.add(var11.toString().toLowerCase());
                  }
               }
            }
         } catch (Throwable var13) {
            ConfigManager.debug("Could not resolve types for " + var0 + ": " + var13.getClass().getSimpleName());
         }

         return var1;
      } else {
         return var1;
      }
   }

   public static int currentLevel(Object var0) {
      Object var1 = invoke(var0, "getPokemon");
      return invoke(var1, "getLevel") instanceof Number var3 ? var3.intValue() : 1;
   }

   public static boolean setLevel(Object var0, int var1) {
      try {
         Object var2 = invokeRequired(var0, "getPokemon");
         Method var3 = var2.getClass().getMethod("setLevel", int.class);
         var3.invoke(var2, var1);
         return true;
      } catch (Throwable var4) {
         ConfigManager.error("Failed to set wild Pokemon level", var4);
         return false;
      }
   }

   public static int evolutionStage(Object var0) {
      try {
         Object var1 = invokeRequired(var0, "getPokemon");
         Object var2 = invokeRequired(var1, "getSpecies");
         Set var3 = Collections.newSetFromMap(new IdentityHashMap());

         int var4;
         for (var4 = 0; var2 != null && var4 < 8 && var3.add(var2); var4++) {
            Object var5 = invoke(var2, "getPreEvolution");
            if (var5 == null) {
               break;
            }

            Object var6 = invoke(var5, "getSpecies");
            if (var6 == null) {
               break;
            }

            var2 = var6;
         }

         return var4;
      } catch (Throwable var7) {
         return 0;
      }
   }

   public static boolean applyDensityConfig() {
      GeneralConfig.Density var0 = ConfigManager.general().density;
      if (var0 != null && var0.enabled) {
         try {
            Object var1 = cobblemonConfig();
            if (var1 == null) {
               throw new IllegalStateException("Cobblemon config instance unavailable");
            }

            invokeSetter(var1, "setPokemonPerChunk", float.class, (float)var0.pokemonPerChunk);
            invokeSetter(var1, "setMinimumDistanceBetweenEntities", double.class, var0.minimumDistanceBetweenEntities);
            invokeSetter(var1, "setTicksBetweenSpawnAttempts", float.class, (float)var0.ticksBetweenSpawnAttempts);
            invokeSetter(var1, "setMinimumSpawningZoneDistanceFromPlayer", float.class, (float)var0.minimumSpawningZoneDistanceFromPlayer);
            invokeSetter(var1, "setMaximumSpawningZoneDistanceFromPlayer", float.class, (float)var0.maximumSpawningZoneDistanceFromPlayer);
            invokeSetter(var1, "setMaximumSpawnsPerPass", int.class, var0.maximumSpawnsPerPass);
            ConfigManager.log("Applied Cobblemon density controls.");
            return true;
         } catch (Throwable var2) {
            ConfigManager.error("Could not apply Cobblemon density controls", var2);
            return false;
         }
      } else {
         return true;
      }
   }

   private static Object cobblemonConfig() throws ReflectiveOperationException {
      Class var0 = Class.forName("com.cobblemon.mod.common.Cobblemon");
      Method var1 = var0.getMethod("getConfig");
      if (Modifier.isStatic(var1.getModifiers())) {
         return var1.invoke(null);
      }

      Field var2 = var0.getField("INSTANCE");
      Object var3 = var2.get(null);
      return var1.invoke(var3);
   }

   private static void invokeSetter(Object var0, String var1, Class<?> var2, Object var3) throws ReflectiveOperationException {
      Method var4 = var0.getClass().getMethod(var1, var2);
      var4.invoke(var0, var3);
   }

   private static Object invoke(Object var0, String var1) {
      if (var0 == null) {
         return null;
      }

      try {
         return invokeRequired(var0, var1);
      } catch (Throwable var3) {
         return null;
      }
   }

   private static Object invokeRequired(Object var0, String var1) throws ReflectiveOperationException {
      Method var2 = var0.getClass().getMethod(var1);
      return var2.invoke(var0);
   }
}
