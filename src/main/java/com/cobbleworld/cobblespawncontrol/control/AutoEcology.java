package com.cobbleworld.cobblespawncontrol.control;

import com.cobbleworld.cobblespawncontrol.config.SpeciesConfig;
import com.cobbleworld.cobblespawncontrol.integration.CobblemonBridge;
import com.cobbleworld.cobblespawncontrol.util.Glob;
import java.util.ArrayList;
import java.util.LinkedHashMap;
import java.util.LinkedHashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.concurrent.ConcurrentHashMap;

public final class AutoEcology {
   private static final Map<String, SpeciesConfig.Rule> CACHE = new ConcurrentHashMap<>();
   private static final List<AutoEcology.OverrideRule> OVERRIDES = overrides();
   private static final Map<String, Set<String>> TYPE_HABITATS = typeHabitats();

   private AutoEcology() {
   }

   public static SpeciesConfig.Rule ruleFor(String var0) {
      return var0 != null && !var0.isBlank() && !var0.equals("unknown:unknown") ? CACHE.computeIfAbsent(var0.toLowerCase(), AutoEcology::buildRule) : null;
   }

   public static void clearCache() {
      CACHE.clear();
   }

   private static SpeciesConfig.Rule buildRule(String var0) {
      AutoEcology.OverrideRule var1 = findOverride(var0);
      Set<String> var2 = CobblemonBridge.speciesTypes(var0);
      if (var1 != null) {
         SpeciesConfig.Rule var7 = generatedRule("auto_override:" + var1.name, var0, var1.habitats);
         var7.spawnAcceptance = var1.acceptance;
         var7.localCap = var1.localCap;
         return var7;
      }

      if (var2.isEmpty()) {
         return null;
      }

      LinkedHashSet<String> var3 = new LinkedHashSet<>();

      for (String var5 : var2) {
         Set var6 = TYPE_HABITATS.get(var5);
         if (var6 != null) {
            var3.addAll(var6);
         }
      }

      var3.add("general");
      SpeciesConfig.Rule var8 = generatedRule("auto_types:" + String.join("+", var2), var0, var3);
      var8.spawnAcceptance = autoAcceptance(var2);
      return var8;
   }

   private static SpeciesConfig.Rule generatedRule(String var0, String var1, Set<String> var2) {
      SpeciesConfig.Rule var3 = new SpeciesConfig.Rule();
      var3.name = var0;
      var3.priority = -1000;
      var3.species = new ArrayList<>(List.of(var1));
      var3.allowedHabitats = new ArrayList<>(var2);
      var3.blockedHabitats = new ArrayList<>();
      var3.allowedTimes = new ArrayList<>();
      var3.spawnAcceptance = 1.0;
      var3.localCap = null;
      var3.minLevel = null;
      var3.maxLevel = null;
      var3.ignoreEvolutionRarity = false;
      return var3;
   }

   private static double autoAcceptance(Set<String> var0) {
      if (var0.contains("dragon")) {
         return 0.72;
      } else if (var0.contains("ghost")) {
         return 0.82;
      } else {
         return var0.contains("ice") ? 0.88 : 1.0;
      }
   }

   private static AutoEcology.OverrideRule findOverride(String var0) {
      for (AutoEcology.OverrideRule var2 : OVERRIDES) {
         for (String var4 : var2.speciesPatterns) {
            if (Glob.matches(var4, var0)) {
               return var2;
            }
         }
      }

      return null;
   }

   private static Map<String, Set<String>> typeHabitats() {
      LinkedHashMap var0 = new LinkedHashMap();
      var0.put("normal", set("grassland", "temperate_forest", "taiga", "savanna", "wetland", "coast"));
      var0.put("fire", set("desert", "savanna", "grassland", "alpine", "rocky", "volcanic", "nether"));
      var0.put("water", set("freshwater", "wetland", "coast", "ocean", "deep_ocean"));
      var0.put("electric", set("grassland", "temperate_forest", "alpine", "rocky", "coast"));
      var0.put("grass", set("grassland", "temperate_forest", "tropical_forest", "wetland", "savanna", "taiga", "mushroom"));
      var0.put("ice", set("tundra", "alpine", "coast", "ocean", "deep_ocean"));
      var0.put("fighting", set("grassland", "alpine", "savanna", "cave", "rocky"));
      var0.put("poison", set("wetland", "tropical_forest", "temperate_forest", "cave", "grassland", "mushroom"));
      var0.put("ground", set("desert", "savanna", "grassland", "alpine", "rocky", "cave", "deep_cave"));
      var0.put("flying", set("grassland", "temperate_forest", "tropical_forest", "taiga", "savanna", "alpine", "coast", "tundra"));
      var0.put("psychic", set("grassland", "temperate_forest", "alpine", "cave", "end"));
      var0.put("bug", set("temperate_forest", "tropical_forest", "grassland", "wetland", "taiga", "mushroom"));
      var0.put("rock", set("rocky", "alpine", "cave", "deep_cave", "desert", "coast", "volcanic"));
      var0.put("ghost", set("cave", "deep_cave", "wetland", "temperate_forest", "mushroom", "end"));
      var0.put("dragon", set("alpine", "rocky", "cave", "deep_cave", "desert", "ocean", "freshwater", "end"));
      var0.put("dark", set("temperate_forest", "taiga", "cave", "deep_cave", "wetland", "alpine", "end"));
      var0.put("steel", set("cave", "deep_cave", "rocky", "alpine", "grassland", "volcanic", "nether"));
      var0.put("fairy", set("temperate_forest", "grassland", "wetland", "alpine", "tropical_forest", "mushroom"));
      return var0;
   }

   private static List<AutoEcology.OverrideRule> overrides() {
      ArrayList var0 = new ArrayList();
      var0.add(o("cave_bats", set("cave", "deep_cave"), 0.95, 5, "*:zubat", "*:golbat", "*:crobat", "*:woobat", "*:swoobat", "*:noibat", "*:noivern"));
      var0.add(
         o(
            "cave_rocks",
            set("cave", "deep_cave", "rocky", "alpine"),
            0.95,
            4,
            "*:geodude",
            "*:graveler",
            "*:golem",
            "*:onix",
            "*:steelix",
            "*:aron",
            "*:lairon",
            "*:aggron",
            "*:roggenrola",
            "*:boldore",
            "*:gigalith",
            "*:carbink",
            "*:nacli",
            "*:naclstack",
            "*:garganacl"
         )
      );
      var0.add(
         o("cave_ground", set("cave", "deep_cave", "rocky", "desert"), 0.95, 4, "*:diglett", "*:dugtrio", "*:drilbur", "*:excadrill", "*:glimmet", "*:glimmora")
      );
      var0.add(
         o(
            "forest_insects",
            set("temperate_forest", "tropical_forest", "grassland"),
            1.0,
            5,
            "*:caterpie",
            "*:metapod",
            "*:butterfree",
            "*:weedle",
            "*:kakuna",
            "*:beedrill",
            "*:wurmple",
            "*:silcoon",
            "*:beautifly",
            "*:cascoon",
            "*:dustox",
            "*:sewaddle",
            "*:swadloon",
            "*:leavanny"
         )
      );
      var0.add(
         o(
            "fungal_wildlife",
            set("mushroom", "temperate_forest", "tropical_forest", "wetland", "cave"),
            0.85,
            4,
            "*:paras",
            "*:parasect",
            "*:shroomish",
            "*:breloom",
            "*:foongus",
            "*:amoonguss",
            "*:morelull",
            "*:shiinotic",
            "*:toedscool",
            "*:toedscruel"
         )
      );
      var0.add(
         o(
            "desert_burrowers",
            set("desert", "rocky"),
            0.9,
            4,
            "*:sandshrew",
            "*:sandslash",
            "*:trapinch",
            "*:vibrava",
            "*:flygon",
            "*:cacnea",
            "*:cacturne",
            "*:hippopotas",
            "*:hippowdon",
            "*:sandile",
            "*:krokorok",
            "*:krookodile",
            "*:silicobra",
            "*:sandaconda"
         )
      );
      var0.add(
         o("desert_reptiles", set("desert", "savanna", "rocky"), 0.85, 4, "*:helioptile", "*:heliolisk", "*:scraggy", "*:scrafty", "*:salandit", "*:salazzle")
      );
      var0.add(
         o(
            "cold_wildlife",
            set("tundra", "alpine", "taiga"),
            0.9,
            4,
            "*:snover",
            "*:abomasnow",
            "*:bergmite",
            "*:avalugg",
            "*:snorunt",
            "*:glalie",
            "*:froslass",
            "*:swinub",
            "*:piloswine",
            "*:mamoswine",
            "*:cubchoo",
            "*:beartic",
            "*:snom",
            "*:frosmoth"
         )
      );
      var0.add(o("polar_coast", set("tundra", "coast", "ocean"), 0.85, 4, "*:spheal", "*:sealeo", "*:walrein", "*:seel", "*:dewgong", "*:eiscue"));
      var0.add(o("magikarp", set("freshwater", "ocean", "coast"), 1.0, 6, "*:magikarp"));
      var0.add(o("gyarados", set("freshwater", "ocean", "deep_ocean"), 0.35, 1, "*:gyarados"));
      var0.add(o("lapras", set("ocean", "deep_ocean", "coast"), 0.22, 1, "*:lapras"));
      var0.add(
         o("deep_sea", set("deep_ocean", "ocean"), 0.65, 3, "*:relicanth", "*:clamperl", "*:huntail", "*:gorebyss", "*:frillish", "*:jellicent", "*:dhelmise")
      );
      var0.add(o("ocean_predators", set("ocean", "deep_ocean"), 0.55, 2, "*:carvanha", "*:sharpedo", "*:barraskewda", "*:veluza"));
      var0.add(o("large_ocean", set("ocean", "deep_ocean"), 0.45, 2, "*:wailmer", "*:wailord", "*:dondozo"));
      var0.add(
         o(
            "reef_coast",
            set("coast", "ocean"),
            0.8,
            4,
            "*:corsola",
            "*:mareanie",
            "*:toxapex",
            "*:pyukumuku",
            "*:bruxish",
            "*:luvdisc",
            "*:finneon",
            "*:lumineon"
         )
      );
      var0.add(o("coastal_birds", set("coast", "ocean"), 0.95, 5, "*:wingull", "*:pelipper", "*:wattrel", "*:kilowattrel"));
      var0.add(o("coastal_crabs", set("coast", "wetland"), 0.9, 4, "*:krabby", "*:kingler", "*:crabrawler", "*:wimpod", "*:golisopod"));
      var0.add(
         o(
            "freshwater_fish",
            set("freshwater", "wetland"),
            0.95,
            5,
            "*:goldeen",
            "*:seaking",
            "*:barboach",
            "*:whiscash",
            "*:feebas",
            "*:milotic",
            "*:basculin",
            "*:basculegion",
            "*:chewtle",
            "*:drednaw",
            "*:arrokuda"
         )
      );
      var0.add(
         o(
            "freshwater_amphibious",
            set("freshwater", "wetland", "grassland"),
            0.95,
            5,
            "*:psyduck",
            "*:golduck",
            "*:poliwag",
            "*:poliwhirl",
            "*:poliwrath",
            "*:politoed",
            "*:wooper",
            "*:quagsire",
            "*:clodsire",
            "*:lotad",
            "*:lombre",
            "*:ludicolo",
            "*:surskit",
            "*:buizel",
            "*:floatzel",
            "*:tympole",
            "*:palpitoad",
            "*:seismitoad"
         )
      );
      var0.add(o("electric_aquatic", set("freshwater", "wetland"), 0.75, 3, "*:tynamo", "*:eelektrik", "*:eelektross", "*:stunfisk"));
      var0.add(o("dratini", set("freshwater", "deep_ocean"), 0.22, 1, "*:dratini", "*:dragonair"));
      var0.add(o("dragonite", set("coast", "ocean", "alpine"), 0.12, 1, "*:dragonite"));
      var0.add(o("beach_ghosts", set("coast"), 0.7, 2, "*:sandygast", "*:palossand"));
      var0.add(o("wetland_birds", set("wetland", "freshwater", "grassland"), 0.9, 4, "*:ducklett", "*:swanna", "*:flamigo"));
      var0.add(
         o(
            "mountain_dragons",
            set("alpine", "rocky", "cave"),
            0.35,
            2,
            "*:bagon",
            "*:shelgon",
            "*:salamence",
            "*:gible",
            "*:gabite",
            "*:garchomp",
            "*:axew",
            "*:fraxure",
            "*:haxorus",
            "*:jangmoo",
            "*:hakamoo",
            "*:kommoo",
            "*:frigibax",
            "*:arctibax",
            "*:baxcalibur"
         )
      );
      return var0;
   }

   private static AutoEcology.OverrideRule o(String var0, Set<String> var1, double var2, Integer var4, String... var5) {
      return new AutoEcology.OverrideRule(var0, List.of(var5), var1, var2, var4);
   }

   private static Set<String> set(String... var0) {
      return new LinkedHashSet<>(List.of(var0));
   }

   private record OverrideRule(String name, List<String> speciesPatterns, Set<String> habitats, double acceptance, Integer localCap) {
   }
}
