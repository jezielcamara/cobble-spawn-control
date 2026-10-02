package com.cobbleworld.cobblespawncontrol;

import com.cobbleworld.cobblespawncontrol.command.CscCommands;
import com.cobbleworld.cobblespawncontrol.config.ConfigManager;
import com.cobbleworld.cobblespawncontrol.control.SpawnController;
import com.cobbleworld.cobblespawncontrol.integration.CobblemonBridge;
import net.neoforged.bus.api.SubscribeEvent;
import net.neoforged.fml.ModContainer;
import net.neoforged.fml.common.Mod;
import net.neoforged.neoforge.common.NeoForge;
import net.neoforged.neoforge.event.RegisterCommandsEvent;
import net.neoforged.neoforge.event.server.ServerStartingEvent;

@Mod("cobblespawncontrol")
public final class CobbleSpawnControl {
   public static final String MOD_ID = "cobblespawncontrol";
   public static final String VERSION = "0.4.1";

   public CobbleSpawnControl(ModContainer var1) {
      ConfigManager.load();
      CobblemonBridge.installSpawnHook(SpawnController::handleSpawnEvent);
      NeoForge.EVENT_BUS.register(this);
      ConfigManager.log("Loaded v0.4.1.");
   }

   @SubscribeEvent
   public void onServerStarting(ServerStartingEvent var1) {
      ConfigManager.load();
      ConfigManager.syncFromNeoForge();
      CobblemonBridge.installSpawnHook(SpawnController::handleSpawnEvent);
      CobblemonBridge.applyDensityConfig();
   }

   @SubscribeEvent
   public void onRegisterCommands(RegisterCommandsEvent var1) {
      CscCommands.register(var1.getDispatcher());
   }
}
