package com.cobbleworld.cobblespawncontrol.config;

import java.util.ArrayList;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;

public final class SpeciesConfig {
   public String _description = "Optional species-specific ecology overrides. Rules here take priority over CSC's automatic type-based ecology. Use namespace:id species IDs; addon species are supported. The in-game Config screen includes a Species Rules editor for the common fields; advanced fields remain editable here. Empty/unmapped species fall back to automatic ecology unless general.json rejects them.";
   public List<SpeciesConfig.Rule> rules = defaults();
   public Map<String, String> _help = help(
      "rules", "Higher priority rules win. A rule can restrict habitats/times, change spawn acceptance, set a local population cap, or clamp levels."
   );

   private static List<SpeciesConfig.Rule> defaults() {
      ArrayList var0 = new ArrayList();
      var0.add(
         new SpeciesConfig.Rule(
            "Common early-route forest insects.",
            "forest_insects",
            30,
            List.of("cobblemon:caterpie", "cobblemon:metapod", "cobblemon:butterfree", "cobblemon:weedle", "cobblemon:kakuna", "cobblemon:beedrill"),
            List.of("temperate_forest", "tropical_forest", "grassland"),
            0.9,
            5,
            false
         )
      );
      var0.add(
         new SpeciesConfig.Rule(
            "Bat-like Pokemon should remain underground.",
            "cave_bats",
            40,
            List.of("cobblemon:zubat", "cobblemon:golbat", "cobblemon:crobat", "cobblemon:woobat", "cobblemon:swoobat", "cobblemon:noibat", "cobblemon:noivern"),
            List.of("cave", "deep_cave"),
            0.85,
            5,
            false
         )
      );
      var0.add(
         new SpeciesConfig.Rule(
            "Dry-land burrowers and desert-adapted lines.",
            "desert_burrowers",
            40,
            List.of(
               "cobblemon:sandshrew",
               "cobblemon:sandslash",
               "cobblemon:trapinch",
               "cobblemon:vibrava",
               "cobblemon:flygon",
               "cobblemon:cacnea",
               "cobblemon:cacturne"
            ),
            List.of("desert", "rocky"),
            0.8,
            4,
            false
         )
      );
      var0.add(
         new SpeciesConfig.Rule(
            "Cold-adapted wildlife.",
            "cold_wildlife",
            40,
            List.of(
               "cobblemon:snover",
               "cobblemon:abomasnow",
               "cobblemon:bergmite",
               "cobblemon:avalugg",
               "cobblemon:snorunt",
               "cobblemon:glalie",
               "cobblemon:froslass"
            ),
            List.of("tundra", "alpine", "taiga"),
            0.75,
            4,
            false
         )
      );
      var0.add(
         new SpeciesConfig.Rule(
            "Common aquatic prey fish.", "magikarp", 60, List.of("cobblemon:magikarp"), List.of("freshwater", "ocean", "coast"), 0.9, 6, true
         )
      );
      var0.add(
         new SpeciesConfig.Rule(
            "Large aquatic predator; intentionally rare and locally solitary.",
            "gyarados",
            70,
            List.of("cobblemon:gyarados"),
            List.of("freshwater", "ocean", "deep_ocean"),
            0.2,
            1,
            false
         )
      );
      var0.add(
         new SpeciesConfig.Rule("Rare large marine Pokemon.", "lapras", 80, List.of("cobblemon:lapras"), List.of("ocean", "deep_ocean", "coast"), 0.12, 1, true)
      );
      var0.add(
         new SpeciesConfig.Rule(
            "Rare aquatic juvenile dragon line.",
            "dratini",
            75,
            List.of("cobblemon:dratini", "cobblemon:dragonair"),
            List.of("freshwater", "deep_ocean"),
            0.12,
            1,
            false
         )
      );
      var0.add(
         new SpeciesConfig.Rule(
            "Extremely uncommon mature Dragonite encounters near coasts, oceans and high mountains.",
            "dragonite",
            80,
            List.of("cobblemon:dragonite"),
            List.of("coast", "ocean", "alpine"),
            0.08,
            1,
            false
         )
      );
      var0.add(
         new SpeciesConfig.Rule(
            "Fungal Pokemon favor damp forests, wetlands, caves and mushroom environments.",
            "fungal_wildlife",
            50,
            List.of(
               "cobblemon:paras",
               "cobblemon:parasect",
               "cobblemon:shroomish",
               "cobblemon:breloom",
               "cobblemon:foongus",
               "cobblemon:amoonguss",
               "cobblemon:morelull",
               "cobblemon:shiinotic"
            ),
            List.of("mushroom", "temperate_forest", "tropical_forest", "wetland", "cave"),
            0.75,
            4,
            false
         )
      );
      return var0;
   }

   private static Map<String, String> help(String... var0) {
      LinkedHashMap var1 = new LinkedHashMap();

      for (byte var2 = 0; var2 + 1 < var0.length; var2 += 2) {
         var1.put(var0[var2], var0[var2 + 1]);
      }

      return var1;
   }

   public static final class Rule {
      public String description = "";
      public String name = "rule";
      public int priority = 0;
      public List<String> species = new ArrayList<>();
      public List<String> allowedHabitats = new ArrayList<>();
      public List<String> blockedHabitats = new ArrayList<>();
      public List<String> allowedTimes = new ArrayList<>();
      public double spawnAcceptance = 1.0;
      public Integer localCap = null;
      public Integer minLevel = null;
      public Integer maxLevel = null;
      public boolean ignoreEvolutionRarity = false;
      public Map<String, String> _help = SpeciesConfig.help(
         "species",
         "Species IDs or wildcard patterns matched by this rule.",
         "allowedHabitats",
         "If non-empty, the species may only spawn in these CSC habitat IDs.",
         "blockedHabitats",
         "Habitats explicitly denied by this rule.",
         "allowedTimes",
         "Optional time windows understood by CSC. Empty means any time.",
         "spawnAcceptance",
         "Additional 0-1 acceptance multiplier applied after habitat acceptance.",
         "localCap",
         "Optional per-species local population cap. null uses the default from general.json.",
         "minLevel",
         "Optional minimum level after CSC scaling.",
         "maxLevel",
         "Optional maximum level after CSC scaling.",
         "ignoreEvolutionRarity",
         "If true, the global evolved-form rarity multiplier is not applied."
      );

      public Rule() {
      }

      Rule(String var1, String var2, int var3, List<String> var4, List<String> var5, double var6, Integer var8, boolean var9) {
         this.description = var1;
         this.name = var2;
         this.priority = var3;
         this.species = new ArrayList<>(var4);
         this.allowedHabitats = new ArrayList<>(var5);
         this.spawnAcceptance = var6;
         this.localCap = var8;
         this.ignoreEvolutionRarity = var9;
      }
   }
}
