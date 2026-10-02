package com.cobbleworld.cobblespawncontrol.client;

import net.neoforged.api.distmarker.Dist;
import net.neoforged.fml.ModContainer;
import net.neoforged.fml.common.Mod;
import net.neoforged.neoforge.client.gui.IConfigScreenFactory;

@Mod(value = "cobblespawncontrol", dist = Dist.CLIENT)
public final class CobbleSpawnControlClient {
   public CobbleSpawnControlClient(ModContainer var1) {
      var1.registerExtensionPoint(IConfigScreenFactory.class, (IConfigScreenFactory)(var0, var1x) -> new CobbleSpawnConfigScreen(var1x));
   }
}
