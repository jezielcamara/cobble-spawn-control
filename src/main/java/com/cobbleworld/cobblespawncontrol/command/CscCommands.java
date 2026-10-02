package com.cobbleworld.cobblespawncontrol.command;

import com.cobbleworld.cobblespawncontrol.config.ConfigManager;
import com.cobbleworld.cobblespawncontrol.config.ConfigValidator;
import com.cobbleworld.cobblespawncontrol.control.EcologyDiagnostics;
import com.cobbleworld.cobblespawncontrol.control.HabitatResolver;
import com.cobbleworld.cobblespawncontrol.control.LevelScaler;
import com.cobbleworld.cobblespawncontrol.control.SpawnController;
import com.cobbleworld.cobblespawncontrol.integration.CobblemonBridge;
import com.mojang.brigadier.CommandDispatcher;
import com.mojang.brigadier.builder.LiteralArgumentBuilder;
import com.mojang.brigadier.exceptions.CommandSyntaxException;
import java.util.Comparator;
import java.util.List;
import java.util.Locale;
import java.util.Map.Entry;
import java.util.stream.Collectors;
import net.minecraft.commands.CommandSourceStack;
import net.minecraft.commands.Commands;
import net.minecraft.network.chat.Component;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.server.level.ServerPlayer;

public final class CscCommands {
   private CscCommands() {
   }

   public static void register(CommandDispatcher<CommandSourceStack> var0) {
      var0.register(
         (LiteralArgumentBuilder)((LiteralArgumentBuilder)((LiteralArgumentBuilder)((LiteralArgumentBuilder)((LiteralArgumentBuilder)((LiteralArgumentBuilder)Commands.literal(
                              "csc"
                           )
                           .then(Commands.literal("status").executes(var0x -> status((CommandSourceStack)var0x.getSource()))))
                        .then(
                           ((LiteralArgumentBuilder)Commands.literal("reload").requires(var0x -> var0x.hasPermission(2)))
                              .executes(var0x -> reload((CommandSourceStack)var0x.getSource()))
                        ))
                     .then(Commands.literal("inspect").executes(var0x -> inspect((CommandSourceStack)var0x.getSource()))))
                  .then(
                     ((LiteralArgumentBuilder)Commands.literal("biome").executes(var0x -> biome((CommandSourceStack)var0x.getSource())))
                        .then(
                           ((LiteralArgumentBuilder)Commands.literal("audit").requires(var0x -> var0x.hasPermission(2)))
                              .executes(var0x -> report((CommandSourceStack)var0x.getSource(), true))
                        )
                  ))
               .then(Commands.literal("validate").executes(var0x -> validate((CommandSourceStack)var0x.getSource()))))
            .then(
               ((LiteralArgumentBuilder)Commands.literal("report").requires(var0x -> var0x.hasPermission(2)))
                  .executes(var0x -> report((CommandSourceStack)var0x.getSource(), false))
            )
      );
   }

   private static int status(CommandSourceStack var0) {
      var0.sendSuccess(
         () -> Component.literal("Cobble Spawn Control: hook=" + CobblemonBridge.isHookInstalled() + ", config=" + ConfigManager.directory()), false
      );
      var0.sendSuccess(
         () -> Component.literal(
            "Ecology cache: observedBiomes=" + HabitatResolver.observations().size() + ", loadedSpecies=" + CobblemonBridge.loadedSpeciesIds().size()
         ),
         false
      );
      return 1;
   }

   private static int reload(CommandSourceStack var0) {
      ConfigManager.load();
      boolean var1 = CobblemonBridge.applyDensityConfig();
      var0.sendSuccess(() -> Component.literal("Cobble Spawn Control reloaded. Density applied=" + var1 + ". Ecology caches cleared."), true);
      return 1;
   }

   private static int inspect(CommandSourceStack var0) throws CommandSyntaxException {
      ServerPlayer var1 = var0.getPlayerOrException();
      ServerLevel var2 = var0.getLevel();
      HabitatResolver.HabitatContext var3 = HabitatResolver.resolve(var2, var1);
      double var4 = ConfigManager.general().population.radius;
      SpawnController.PopulationSnapshot var6 = SpawnController.inspectPopulation(var2, var1, var4);
      int var7 = LevelScaler.preview(var2, var1.blockPosition(), var3);
      String var8 = var6.speciesCounts()
         .entrySet()
         .stream()
         .sorted(Comparator.<Entry<String, Integer>>comparingInt(Entry::getValue).reversed())
         .limit(6L)
         .map(var0x -> shortId(var0x.getKey()) + "=" + var0x.getValue())
         .collect(Collectors.joining(", "));
      if (var8.isBlank()) {
         var8 = "none";
      }

      var0.sendSuccess(
         () -> Component.literal(
            "[CSC Inspect] "
               + var3.biomeId()
               + " -> "
               + var3.habitatId()
               + " | source="
               + var3.classificationSource()
               + " | confidence="
               + var3.confidence()
               + "%"
         ),
         false
      );
      var0.sendSuccess(
         () -> Component.literal(
            "Dimension="
               + var3.dimensionId()
               + " | Y="
               + var3.y()
               + " | Water="
               + var3.inWater()
               + " | Sky="
               + var3.skyVisible()
               + " | Habitat acceptance="
               + pct(var3.spawnAcceptance())
         ),
         false
      );
      var0.sendSuccess(
         () -> Component.literal(
            "Climate: temperature="
               + fmt(var3.temperature())
               + " | downfall="
               + fmt(var3.downfall())
               + " | precipitation="
               + var3.hasPrecipitation()
               + " | biome tags="
               + var3.biomeTags().size()
         ),
         false
      );
      String var9 = var3.evidence().stream().limit(7L).collect(Collectors.joining("; "));
      var0.sendSuccess(() -> Component.literal("Signals: " + var9), false);
      var0.sendSuccess(
         () -> Component.literal(
            "Local wild Pokemon=" + var6.total() + "/" + ConfigManager.general().population.totalCap + " (r=" + (int)var4 + ") | Level baseline~" + var7
         ),
         false
      );
      String var10 = var8;
      var0.sendSuccess(() -> Component.literal("Top species: " + var10), false);
      return 1;
   }

   private static int biome(CommandSourceStack var0) throws CommandSyntaxException {
      ServerPlayer var1 = var0.getPlayerOrException();
      HabitatResolver.HabitatContext var2 = HabitatResolver.resolve(var0.getLevel(), var1);
      var0.sendSuccess(
         () -> Component.literal(
            "Biome "
               + var2.biomeId()
               + " is classified as "
               + var2.habitatId()
               + " ("
               + var2.classificationSource()
               + ", "
               + var2.confidence()
               + "% confidence)."
         ),
         false
      );
      var0.sendSuccess(() -> Component.literal("Use /csc inspect for signals, or /csc biome audit to write a full registered-biome ecology report."), false);
      return 1;
   }

   private static int validate(CommandSourceStack var0) {
      List var1 = ConfigValidator.validate(ConfigManager.general(), ConfigManager.habitats(), ConfigManager.species());
      long var2 = ConfigValidator.errorCount(var1);
      long var4 = ConfigValidator.warningCount(var1);
      var0.sendSuccess(() -> Component.literal("CSC config validation: " + var2 + " error(s), " + var4 + " warning(s)."), false);
      var1.stream().limit(8L).forEach(var1x -> var0.sendSuccess(() -> Component.literal("- " + var1x), false));
      if (var1.size() > 8) {
         var0.sendSuccess(() -> Component.literal("... " + (var1.size() - 8) + " more. Use /csc report for the complete list."), false);
      }

      return var2 == 0L ? 1 : 0;
   }

   private static int report(CommandSourceStack var0, boolean var1) {
      EcologyDiagnostics.AuditSummary var2 = EcologyDiagnostics.writeReport(var0.getLevel());
      var0.sendSuccess(() -> Component.literal((var1 ? "Biome audit" : "Ecology report") + " written to " + var2.reportPath()), true);
      var0.sendSuccess(
         () -> Component.literal(
            "Biomes: "
               + var2.registeredBiomes()
               + " registered, "
               + var2.observedBiomes()
               + " observed, "
               + var2.predictedGeneral()
               + " registry-name fallbacks."
         ),
         false
      );
      var0.sendSuccess(
         () -> Component.literal(
            "Species: "
               + var2.loadedSpecies()
               + " loaded | explicit="
               + var2.explicitSpecies()
               + " | automatic="
               + var2.automaticSpecies()
               + " | unresolved="
               + var2.unresolvedSpecies()
         ),
         false
      );
      var0.sendSuccess(() -> Component.literal("Config: " + var2.errors() + " error(s), " + var2.warnings() + " warning(s)."), false);
      return var2.errors() == 0 ? 1 : 0;
   }

   private static String shortId(String var0) {
      int var1 = var0.indexOf(58);
      return var1 >= 0 ? var0.substring(var1 + 1) : var0;
   }

   private static String fmt(double var0) {
      return Double.isNaN(var0) ? "?" : String.format(Locale.ROOT, "%.2f", var0);
   }

   private static String pct(double var0) {
      return Math.round(var0 * 100.0) + "%";
   }
}
