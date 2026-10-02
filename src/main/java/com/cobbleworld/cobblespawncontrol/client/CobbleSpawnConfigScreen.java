package com.cobbleworld.cobblespawncontrol.client;

import com.cobbleworld.cobblespawncontrol.config.ConfigManager;
import com.cobbleworld.cobblespawncontrol.config.GeneralConfig;
import com.cobbleworld.cobblespawncontrol.config.HabitatConfig;
import com.cobbleworld.cobblespawncontrol.config.SpeciesConfig;
import java.util.ArrayList;
import java.util.List;
import java.util.Locale;
import java.util.function.Consumer;
import java.util.function.DoubleConsumer;
import net.minecraft.client.gui.components.Button;
import net.minecraft.client.gui.components.CycleButton;
import net.minecraft.client.gui.components.EditBox;
import net.minecraft.client.gui.components.Tooltip;
import net.minecraft.client.gui.screens.Screen;
import net.minecraft.network.chat.Component;
import net.neoforged.neoforge.client.gui.widget.ExtendedSlider;

public final class CobbleSpawnConfigScreen extends Screen {
   private static final int SIDEBAR_WIDTH = 138;
   private static final int ROW_HEIGHT = 20;
   private final Screen parent;
   private final GeneralConfig general;
   private final HabitatConfig habitats;
   private final SpeciesConfig species;
   private final CobbleSpawnConfigScreen.Page page;
   private final int selectedHabitat;
   private final int selectedRule;

   public CobbleSpawnConfigScreen(Screen var1) {
      this(var1, ConfigManager.copyGeneral(), ConfigManager.copyHabitats(), ConfigManager.copySpecies(), CobbleSpawnConfigScreen.Page.DENSITY, 0, 0);
   }

   private CobbleSpawnConfigScreen(
      Screen var1, GeneralConfig var2, HabitatConfig var3, SpeciesConfig var4, CobbleSpawnConfigScreen.Page var5, int var6, int var7
   ) {
      super(Component.literal("Cobble Spawn Control"));
      this.parent = var1;
      this.general = var2;
      this.habitats = var3;
      this.species = var4;
      this.page = var5;
      this.selectedHabitat = var6;
      this.selectedRule = var7;
   }

   protected void init() {
      Button var1 = Button.builder(Component.literal("Cobble Spawn Control - " + this.page.label), var0 -> {})
         .bounds(Math.max(152, this.width / 3), 10, Math.max(220, this.width - Math.max(152, this.width / 3) - 18), 20)
         .build();
      var1.active = false;
      this.addRenderableWidget(var1);
      byte var2 = 10;
      byte var3 = 34;

      for (CobbleSpawnConfigScreen.Page var7 : CobbleSpawnConfigScreen.Page.values()) {
         Button var8 = Button.builder(Component.literal(var7.label), var2x -> this.openPage(var7)).bounds(var2, var3, 122, 16).build();
         var8.active = var7 != this.page;
         this.addRenderableWidget(var8);
         var3 += 17;
      }

      switch (this.page) {
         case DENSITY:
            this.densityPage();
            break;
         case LEVELS:
            this.levelsPage();
            break;
         case DEPTH:
            this.depthPage();
            break;
         case POPULATION:
            this.populationPage();
            break;
         case RARITY:
            this.rarityPage();
            break;
         case ECOLOGY:
            this.ecologyPage();
            break;
         case BIOMES:
            this.biomesPage();
            break;
         case CLIMATE:
            this.climatePage();
            break;
         case HABITATS:
            this.habitatsEditorPage();
            break;
         case SPECIES:
            this.speciesEditorPage();
      }

      int var9 = this.height - 22;
      this.addRenderableWidget(Button.builder(Component.literal("Save"), var1x -> this.saveAndClose()).bounds(this.width - 218, var9, 98, 20).build());
      this.addRenderableWidget(Button.builder(Component.literal("Cancel"), var1x -> this.closeWithoutSaving()).bounds(this.width - 112, var9, 98, 20).build());
   }

   private void densityPage() {
      int var1 = 0;
      this.addToggle(
         var1++,
         "Density Control",
         this.general.density.enabled,
         var1x -> this.general.density.enabled = var1x,
         "Lets CSC apply these density values to Cobblemon. Density values take effect on world start or /csc reload."
      );
      this.addSlider(
         var1++,
         "Pokemon / Chunk",
         0.05,
         3.0,
         this.general.density.pokemonPerChunk,
         0.05,
         2,
         var1x -> this.general.density.pokemonPerChunk = var1x,
         "",
         "Target wild Pokemon density per chunk. CobbleWorld default: 0.60."
      );
      this.addSlider(
         var1++,
         "Minimum Spacing",
         2.0,
         48.0,
         this.general.density.minimumDistanceBetweenEntities,
         1.0,
         0,
         var1x -> this.general.density.minimumDistanceBetweenEntities = var1x,
         " blocks",
         "Minimum spacing Cobblemon should keep between spawned Pokemon."
      );
      this.addSlider(
         var1++,
         "Spawn Attempt Delay",
         5.0,
         120.0,
         this.general.density.ticksBetweenSpawnAttempts,
         1.0,
         0,
         var1x -> this.general.density.ticksBetweenSpawnAttempts = var1x,
         " ticks",
         "Higher values reduce how quickly new spawns are attempted."
      );
      this.addSlider(
         var1++,
         "Minimum Spawn Distance",
         4.0,
         96.0,
         this.general.density.minimumSpawningZoneDistanceFromPlayer,
         1.0,
         0,
         var1x -> this.general.density.minimumSpawningZoneDistanceFromPlayer = var1x,
         " blocks",
         "Nearest distance from a player where spawn zones may be selected."
      );
      this.addSlider(
         var1++,
         "Maximum Spawn Distance",
         24.0,
         192.0,
         this.general.density.maximumSpawningZoneDistanceFromPlayer,
         1.0,
         0,
         var1x -> this.general.density.maximumSpawningZoneDistanceFromPlayer = var1x,
         " blocks",
         "Farthest distance from a player where spawn zones may be selected."
      );
      this.addSlider(
         var1,
         "Max Spawns / Pass",
         1.0,
         24.0,
         this.general.density.maximumSpawnsPerPass,
         1.0,
         0,
         var1x -> this.general.density.maximumSpawnsPerPass = (int)Math.round(var1x),
         "",
         "Maximum Pokemon Cobblemon may create in one spawn pass."
      );
   }

   private void levelsPage() {
      int var1 = 0;
      this.addToggle(
         var1++,
         "Wild Level Control",
         this.general.levels.enabled,
         var1x -> this.general.levels.enabled = var1x,
         "World-driven wild level scaling instead of party-based scaling."
      );
      this.addSlider(
         var1++,
         "Level Near Spawn",
         1.0,
         40.0,
         this.general.levels.levelNearSpawn,
         1.0,
         0,
         var1x -> this.general.levels.levelNearSpawn = (int)Math.round(var1x),
         "",
         "Baseline wild level near world spawn."
      );
      this.addSlider(
         var1++,
         "Maximum Distance Level",
         10.0,
         100.0,
         this.general.levels.maxDistanceLevel,
         1.0,
         0,
         var1x -> this.general.levels.maxDistanceLevel = (int)Math.round(var1x),
         "",
         "Baseline level reached at the configured maximum distance."
      );
      this.addSlider(
         var1++,
         "Distance for Maximum",
         1000.0,
         100000.0,
         this.general.levels.distanceForMaxLevel,
         1000.0,
         0,
         var1x -> this.general.levels.distanceForMaxLevel = var1x,
         " blocks",
         "Distance from world spawn where the level curve reaches its maximum baseline."
      );
      this.addSlider(
         var1++,
         "Distance Curve",
         0.5,
         3.0,
         this.general.levels.exponent,
         0.1,
         1,
         var1x -> this.general.levels.exponent = var1x,
         "",
         "1.0 is linear. Higher values keep early areas low-level for longer."
      );
      this.addSlider(
         var1++,
         "Random Variance",
         0.0,
         12.0,
         this.general.levels.randomVariance,
         1.0,
         0,
         var1x -> this.general.levels.randomVariance = (int)Math.round(var1x),
         " levels",
         "Random plus/minus variation around the calculated baseline."
      );
      this.addSlider(
         var1++,
         "Global Minimum",
         1.0,
         100.0,
         this.general.levels.globalMin,
         1.0,
         0,
         var1x -> this.general.levels.globalMin = (int)Math.round(var1x),
         "",
         "Absolute minimum level CSC may assign."
      );
      this.addSlider(
         var1,
         "Global Maximum",
         1.0,
         100.0,
         this.general.levels.globalMax,
         1.0,
         0,
         var1x -> this.general.levels.globalMax = (int)Math.round(var1x),
         "",
         "Absolute maximum level CSC may assign. Dimension/biome range lists remain editable in general.json."
      );
   }

   private void depthPage() {
      int var1 = 0;
      this.addToggle(
         var1++,
         "Depth Scaling",
         this.general.levels.depth.enabled,
         var1x -> this.general.levels.depth.enabled = var1x,
         "Raises wild levels as the player travels deeper underground."
      );
      this.addSlider(
         var1++,
         "Depth Start Y",
         -64.0,
         192.0,
         this.general.levels.depth.startY,
         1.0,
         0,
         var1x -> this.general.levels.depth.startY = (int)Math.round(var1x),
         "",
         "Depth bonus begins below this Y level."
      );
      this.addSlider(
         var1++,
         "Bonus per Block",
         0.0,
         0.02,
         this.general.levels.depth.percentagePerBlock,
         5.0E-4,
         4,
         var1x -> this.general.levels.depth.percentagePerBlock = var1x,
         "",
         "Fractional bonus per block. CobbleWorld default 0.003 = 0.3% per block."
      );
      this.addSlider(
         var1,
         "Maximum Depth Bonus",
         0.0,
         1.0,
         this.general.levels.depth.maxIncrease,
         0.05,
         2,
         var1x -> this.general.levels.depth.maxIncrease = var1x,
         "",
         "Maximum fractional increase. 0.30 means up to +30%."
      );
   }

   private void populationPage() {
      int var1 = 0;
      this.addToggle(
         var1++,
         "Population Caps",
         this.general.population.enabled,
         var1x -> this.general.population.enabled = var1x,
         "Prevents local overcrowding and repeated species."
      );
      this.addSlider(
         var1++,
         "Population Radius",
         16.0,
         160.0,
         this.general.population.radius,
         4.0,
         0,
         var1x -> this.general.population.radius = var1x,
         " blocks",
         "Radius used to count nearby wild Pokemon."
      );
      this.addSlider(
         var1++,
         "Total Local Cap",
         4.0,
         64.0,
         this.general.population.totalCap,
         1.0,
         0,
         var1x -> this.general.population.totalCap = (int)Math.round(var1x),
         "",
         "Maximum total wild Pokemon inside the population radius."
      );
      this.addSlider(
         var1,
         "Default Species Cap",
         1.0,
         16.0,
         this.general.population.defaultSpeciesCap,
         1.0,
         0,
         var1x -> this.general.population.defaultSpeciesCap = (int)Math.round(var1x),
         "",
         "Maximum of one species locally unless species.json gives that species another cap."
      );
   }

   private void rarityPage() {
      int var1 = 0;
      this.addToggle(
         var1++,
         "Evolution Rarity",
         this.general.evolutionRarity.enabled,
         var1x -> this.general.evolutionRarity.enabled = var1x,
         "Makes evolved forms less common than base stages."
      );
      this.addPercentSlider(
         var1++,
         "Base Stage",
         this.general.evolutionRarity.base,
         var1x -> this.general.evolutionRarity.base = var1x,
         "Acceptance multiplier for base-stage Pokemon."
      );
      this.addPercentSlider(
         var1++,
         "First Evolution",
         this.general.evolutionRarity.stage1,
         var1x -> this.general.evolutionRarity.stage1 = var1x,
         "Acceptance multiplier for first evolved stages."
      );
      this.addPercentSlider(
         var1++,
         "Second Evolution",
         this.general.evolutionRarity.stage2,
         var1x -> this.general.evolutionRarity.stage2 = var1x,
         "Acceptance multiplier for second evolved stages."
      );
      this.addPercentSlider(
         var1,
         "Later Evolutions",
         this.general.evolutionRarity.stage3Plus,
         var1x -> this.general.evolutionRarity.stage3Plus = var1x,
         "Acceptance multiplier for later evolution stages."
      );
   }

   private void ecologyPage() {
      int var1 = 0;
      this.addToggle(
         var1++,
         "Ecology Filtering",
         this.general.ecology.enabled,
         var1x -> this.general.ecology.enabled = var1x,
         "Restricts Pokemon to suitable CSC habitats."
      );
      this.addToggle(
         var1++,
         "Reject Unmapped Species",
         this.general.ecology.rejectUnmappedSpecies,
         var1x -> this.general.ecology.rejectUnmappedSpecies = var1x,
         "Keep disabled for addon compatibility. Automatic type ecology normally handles unmapped species."
      );
      this.addModeSelector(var1++);
      this.addToggle(
         var1++,
         "Use Biome Names",
         this.habitats.detection.useBiomeNames,
         var1x -> this.habitats.detection.useBiomeNames = var1x,
         "Uses registry IDs such as modid:redwood_forest as automatic ecology clues."
      );
      this.addToggle(
         var1,
         "Use Biome Tags",
         this.habitats.detection.useBiomeTags,
         var1x -> this.habitats.detection.useBiomeTags = var1x,
         "Reads biome tags from vanilla and biome mods. Recommended for modded biome compatibility."
      );
   }

   private void biomesPage() {
      int var1 = 0;
      this.addToggle(
         var1++,
         "Use Climate",
         this.habitats.detection.useClimate,
         var1x -> this.habitats.detection.useClimate = var1x,
         "Uses biome temperature and downfall when those values are available."
      );
      this.addToggle(
         var1++,
         "Use Precipitation",
         this.habitats.detection.usePrecipitation,
         var1x -> this.habitats.detection.usePrecipitation = var1x,
         "Uses whether a biome supports precipitation as an ecology clue."
      );
      this.addToggle(
         var1++,
         "Use Water Position",
         this.habitats.detection.useWater,
         var1x -> this.habitats.detection.useWater = var1x,
         "Treats the actual spawn position being in water as a strong aquatic signal."
      );
      this.addToggle(
         var1++,
         "Use Sky Visibility",
         this.habitats.detection.useSkyVisibility,
         var1x -> this.habitats.detection.useSkyVisibility = var1x,
         "No-sky positions are recognized as cave ecology before surface climate classification."
      );
      this.addToggle(
         var1++,
         "Use Altitude",
         this.habitats.detection.useAltitude,
         var1x -> this.habitats.detection.useAltitude = var1x,
         "Uses Y level for alpine and deep-cave classification."
      );
      this.addToggle(
         var1,
         "Use Dimension",
         this.habitats.detection.useDimension,
         var1x -> this.habitats.detection.useDimension = var1x,
         "Uses Nether/End dimension identity as a high-priority signal."
      );
   }

   private void climatePage() {
      int var1 = 0;
      this.addSlider(
         var1++,
         "Cold Temperature",
         -0.5,
         1.0,
         this.habitats.detection.coldTemperature,
         0.05,
         2,
         var1x -> this.habitats.detection.coldTemperature = var1x,
         "",
         "At or below this temperature, tundra ecology receives a strong signal."
      );
      this.addSlider(
         var1++,
         "Cool Temperature",
         -0.25,
         1.25,
         this.habitats.detection.coolTemperature,
         0.05,
         2,
         var1x -> this.habitats.detection.coolTemperature = var1x,
         "",
         "At or below this temperature, taiga and cool-forest ecology receive a signal."
      );
      this.addSlider(
         var1++,
         "Hot Temperature",
         0.5,
         2.5,
         this.habitats.detection.hotTemperature,
         0.05,
         2,
         var1x -> this.habitats.detection.hotTemperature = var1x,
         "",
         "At or above this temperature, warm-biome ecology receives a signal."
      );
      this.addSlider(
         var1++,
         "Very Hot Temperature",
         0.8,
         3.0,
         this.habitats.detection.veryHotTemperature,
         0.05,
         2,
         var1x -> this.habitats.detection.veryHotTemperature = var1x,
         "",
         "At or above this temperature, desert ecology receives a strong signal."
      );
      this.addSlider(
         var1++,
         "Dry Downfall",
         0.0,
         1.0,
         this.habitats.detection.dryDownfall,
         0.05,
         2,
         var1x -> this.habitats.detection.dryDownfall = var1x,
         "",
         "Downfall at or below this value is considered dry."
      );
      this.addSlider(
         var1++,
         "Wet Downfall",
         0.0,
         1.0,
         this.habitats.detection.wetDownfall,
         0.05,
         2,
         var1x -> this.habitats.detection.wetDownfall = var1x,
         "",
         "Downfall at or above this value is considered wet."
      );
      this.addSlider(
         var1++,
         "Alpine Start Y",
         64.0,
         256.0,
         this.habitats.detection.alpineY,
         4.0,
         0,
         var1x -> this.habitats.detection.alpineY = (int)Math.round(var1x),
         "",
         "Surface positions above this height receive an alpine classification signal."
      );
      this.addSlider(
         var1,
         "Deep Cave Y",
         -64.0,
         64.0,
         this.habitats.detection.deepCaveY,
         1.0,
         0,
         var1x -> this.habitats.detection.deepCaveY = (int)Math.round(var1x),
         "",
         "No-sky underground positions at or below this Y level are classified as deep cave."
      );
   }

   private void habitatsEditorPage() {
      if (this.habitats.habitats == null) {
         this.habitats.habitats = new ArrayList<>();
      }

      if (this.habitats.habitats.isEmpty()) {
         this.habitats.habitats.add(new HabitatConfig.Habitat());
      }

      int var1 = Math.max(0, Math.min(this.selectedHabitat, this.habitats.habitats.size() - 1));
      HabitatConfig.Habitat var2 = this.habitats.habitats.get(var1);
      List<String> var3 = this.habitats.habitats.stream().map(var0 -> var0.id == null ? "unnamed" : var0.id).toList();
      byte var4 = 72;
      int var5 = Math.max(100, this.contentWidth() - var4 * 2 - 8);
      CycleButton var6 = CycleButton.builder(Component::literal)
         .withValues(var3)
         .withInitialValue((String)var3.get(var1))
         .create(this.contentX(), this.rowY(0), var5, 20, Component.literal("Habitat"), (var2x, var3x) -> this.selectHabitat(var3.indexOf(var3x)));
      var6.setTooltip(Tooltip.create(Component.literal("Choose a habitat definition from habitats.json.")));
      this.addRenderableWidget(var6);
      this.addRenderableWidget(
         Button.builder(Component.literal("Add"), var1x -> this.addHabitat()).bounds(this.contentX() + var5 + 4, this.rowY(0), var4, 20).build()
      );
      Button var7 = Button.builder(Component.literal("Delete"), var2x -> this.deleteHabitat(var1))
         .bounds(this.contentX() + var5 + var4 + 8, this.rowY(0), var4, 20)
         .build();
      var7.active = this.habitats.habitats.size() > 1 && !String.valueOf(var2.id).equals(this.habitats.defaultHabitat);
      var7.setTooltip(Tooltip.create(Component.literal("The default habitat cannot be deleted from the GUI.")));
      this.addRenderableWidget(var7);
      this.addTextValue(
         1,
         "Habitat ID",
         var2.id,
         var1x -> var2.id = var1x.trim(),
         "Unique habitat ID used by species rules. Avoid spaces; examples: temperate_forest, deep_ocean."
      );
      this.addPercentSlider(
         2,
         "Spawn Acceptance",
         var2.spawnAcceptance,
         var1x -> var2.spawnAcceptance = var1x,
         "Chance that a spawn survives the habitat carrying-capacity filter."
      );
      this.addSlider(
         3,
         "Priority",
         0.0,
         250.0,
         var2.priority,
         1.0,
         0,
         var1x -> var2.priority = (int)Math.round(var1x),
         "",
         "Higher priority habitats win when several explicit rules match."
      );
      this.addTriState(
         4, "Requires Sky", var2.requiresSky, var1x -> var2.requiresSky = var1x, "Any = ignore sky; Yes = surface only; No = positions without visible sky."
      );
      this.addTriState(
         5,
         "Requires Water",
         var2.requiresWater,
         var1x -> var2.requiresWater = var1x,
         "Any = either; Yes = spawn position must be in water; No = must not be in water."
      );
      this.addTextList(
         6, "Biome patterns", var2.biomePatterns, var1x -> var2.biomePatterns = var1x, "Comma-separated registry patterns, e.g. *:*forest*, *:*grove*."
      );
      this.addTextList(
         7,
         "Biome tag patterns",
         var2.biomeTagPatterns,
         var1x -> var2.biomeTagPatterns = var1x,
         "Comma-separated biome tag patterns. Useful for modded biome compatibility."
      );
      this.addTextList(
         8,
         "Dimension patterns",
         var2.dimensionPatterns,
         var1x -> var2.dimensionPatterns = var1x,
         "Comma-separated dimension IDs/patterns. Leave blank for any dimension. minY/maxY remain available in habitats.json."
      );
   }

   private void speciesEditorPage() {
      if (this.species.rules == null) {
         this.species.rules = new ArrayList<>();
      }

      if (this.species.rules.isEmpty()) {
         this.species.rules.add(new SpeciesConfig.Rule());
      }

      int var1 = Math.max(0, Math.min(this.selectedRule, this.species.rules.size() - 1));
      SpeciesConfig.Rule var2 = this.species.rules.get(var1);
      List<String> var3 = this.species.rules.stream().map(var0 -> var0.name == null ? "unnamed" : var0.name).toList();
      byte var4 = 72;
      int var5 = Math.max(100, this.contentWidth() - var4 * 2 - 8);
      CycleButton var6 = CycleButton.builder(Component::literal)
         .withValues(var3)
         .withInitialValue((String)var3.get(var1))
         .create(this.contentX(), this.rowY(0), var5, 20, Component.literal("Species Rule"), (var2x, var3x) -> this.selectRule(var3.indexOf(var3x)));
      var6.setTooltip(Tooltip.create(Component.literal("Explicit species rules override CSC automatic type-based ecology.")));
      this.addRenderableWidget(var6);
      this.addRenderableWidget(
         Button.builder(Component.literal("Add"), var1x -> this.addRule()).bounds(this.contentX() + var5 + 4, this.rowY(0), var4, 20).build()
      );
      Button var7 = Button.builder(Component.literal("Delete"), var2x -> this.deleteRule(var1))
         .bounds(this.contentX() + var5 + var4 + 8, this.rowY(0), var4, 20)
         .build();
      var7.active = this.species.rules.size() > 1;
      this.addRenderableWidget(var7);
      this.addTextValue(
         1,
         "Rule name",
         var2.name,
         var1x -> var2.name = var1x.trim(),
         "Unique label for this override rule. This is for configuration organization, not a Pokemon ID."
      );
      this.addPercentSlider(
         2,
         "Spawn Acceptance",
         var2.spawnAcceptance,
         var1x -> var2.spawnAcceptance = var1x,
         "Additional acceptance multiplier for Pokemon matched by this rule."
      );
      this.addSlider(
         3,
         "Priority",
         0.0,
         250.0,
         var2.priority,
         1.0,
         0,
         var1x -> var2.priority = (int)Math.round(var1x),
         "",
         "Higher priority species rules win when more than one rule matches."
      );
      this.addSlider(
         4,
         "Local Cap (0=default)",
         0.0,
         16.0,
         var2.localCap == null ? 0.0 : var2.localCap.intValue(),
         1.0,
         0,
         var1x -> var2.localCap = Math.round(var1x) <= 0L ? null : (int)Math.round(var1x),
         "",
         "0 uses the default species cap from general.json."
      );
      this.addToggle(
         5,
         "Ignore Evolution Rarity",
         var2.ignoreEvolutionRarity,
         var1x -> var2.ignoreEvolutionRarity = var1x,
         "Use for species whose rarity is already controlled entirely by this rule."
      );
      this.addTextList(
         6, "Species IDs / patterns", var2.species, var1x -> var2.species = var1x, "Comma-separated IDs or globs, e.g. cobblemon:lapras or addon:*."
      );
      this.addTextList(
         7,
         "Allowed habitats",
         var2.allowedHabitats,
         var1x -> var2.allowedHabitats = var1x,
         "Comma-separated CSC habitat IDs. Blank means no allow-list restriction."
      );
      this.addTextList(
         8,
         "Blocked habitats",
         var2.blockedHabitats,
         var1x -> var2.blockedHabitats = var1x,
         "Comma-separated habitats this rule explicitly denies. Time and level overrides remain available in species.json."
      );
   }

   private void addTriState(int var1, String var2, Boolean var3, Consumer<Boolean> var4, String var5) {
      CobbleSpawnConfigScreen.TriState var6 = CobbleSpawnConfigScreen.TriState.from(var3);
      CycleButton<CobbleSpawnConfigScreen.TriState> var7 = CycleButton.<CobbleSpawnConfigScreen.TriState>builder(var0 -> Component.literal(var0.label))
         .withValues(CobbleSpawnConfigScreen.TriState.values())
         .withInitialValue(var6)
         .create(this.contentX(), this.rowY(var1), this.contentWidth(), 20, Component.literal(var2), (var1x, var2x) -> var4.accept(var2x.value));
      var7.setTooltip(Tooltip.create(Component.literal(var5)));
      this.addRenderableWidget(var7);
   }

   private void addTextValue(int var1, String var2, String var3, Consumer<String> var4, String var5) {
      EditBox var6 = new EditBox(this.font, this.contentX(), this.rowY(var1), this.contentWidth(), 20, Component.literal(var2));
      var6.setMaxLength(256);
      var6.setValue(var3 == null ? "" : var3);
      var6.setHint(Component.literal(var2));
      var6.setResponder(var4);
      var6.setTooltip(Tooltip.create(Component.literal(var5)));
      this.addRenderableWidget(var6);
   }

   private void addTextList(int var1, String var2, List<String> var3, Consumer<List<String>> var4, String var5) {
      EditBox var6 = new EditBox(this.font, this.contentX(), this.rowY(var1), this.contentWidth(), 20, Component.literal(var2));
      var6.setMaxLength(2048);
      var6.setValue(var3 == null ? "" : String.join(", ", var3));
      var6.setHint(Component.literal(var2 + " (comma-separated)"));
      var6.setResponder(var1x -> var4.accept(parseList(var1x)));
      var6.setTooltip(Tooltip.create(Component.literal(var5)));
      this.addRenderableWidget(var6);
   }

   private static List<String> parseList(String var0) {
      if (var0 != null && !var0.isBlank()) {
         ArrayList var1 = new ArrayList();

         for (String var5 : var0.split(",")) {
            String var6 = var5.trim();
            if (!var6.isEmpty()) {
               var1.add(var6);
            }
         }

         return var1;
      } else {
         return new ArrayList<>();
      }
   }

   private void selectHabitat(int var1) {
      if (this.minecraft != null && var1 >= 0) {
         this.minecraft
            .setScreen(
               new CobbleSpawnConfigScreen(
                  this.parent, this.general, this.habitats, this.species, CobbleSpawnConfigScreen.Page.HABITATS, var1, this.selectedRule
               )
            );
      }
   }

   private void selectRule(int var1) {
      if (this.minecraft != null && var1 >= 0) {
         this.minecraft
            .setScreen(
               new CobbleSpawnConfigScreen(
                  this.parent, this.general, this.habitats, this.species, CobbleSpawnConfigScreen.Page.SPECIES, this.selectedHabitat, var1
               )
            );
      }
   }

   private void addHabitat() {
      HabitatConfig.Habitat var1 = new HabitatConfig.Habitat();
      var1.id = this.uniqueHabitatId("new_habitat");
      var1.description = "Custom habitat created in the CSC config screen.";
      var1.spawnAcceptance = 0.65;
      this.habitats.habitats.add(var1);
      this.selectHabitat(this.habitats.habitats.size() - 1);
   }

   private void deleteHabitat(int var1) {
      if (this.habitats.habitats.size() > 1 && var1 >= 0 && var1 < this.habitats.habitats.size()) {
         HabitatConfig.Habitat var2 = this.habitats.habitats.get(var1);
         if (!String.valueOf(var2.id).equals(this.habitats.defaultHabitat)) {
            this.habitats.habitats.remove(var1);
            this.selectHabitat(Math.max(0, var1 - 1));
         }
      }
   }

   private String uniqueHabitatId(String var1) {
      String var2 = var1;
      int var3 = 2;

      while (this.containsHabitatId(var2)) {
         var2 = var1 + "_" + var3++;
      }

      return var2;
   }

   private boolean containsHabitatId(String var1) {
      for (HabitatConfig.Habitat var3 : this.habitats.habitats) {
         if (var1.equals(var3.id)) {
            return true;
         }
      }

      return false;
   }

   private void addRule() {
      SpeciesConfig.Rule var1 = new SpeciesConfig.Rule();
      var1.name = this.uniqueRuleName("new_rule");
      var1.description = "Custom species ecology rule created in the CSC config screen.";
      this.species.rules.add(var1);
      this.selectRule(this.species.rules.size() - 1);
   }

   private void deleteRule(int var1) {
      if (this.species.rules.size() > 1 && var1 >= 0 && var1 < this.species.rules.size()) {
         this.species.rules.remove(var1);
         this.selectRule(Math.max(0, var1 - 1));
      }
   }

   private String uniqueRuleName(String var1) {
      String var2 = var1;
      int var3 = 2;

      while (this.containsRuleName(var2)) {
         var2 = var1 + "_" + var3++;
      }

      return var2;
   }

   private boolean containsRuleName(String var1) {
      for (SpeciesConfig.Rule var3 : this.species.rules) {
         if (var1.equals(var3.name)) {
            return true;
         }
      }

      return false;
   }

   private void addModeSelector(int var1) {
      CobbleSpawnConfigScreen.DetectionMode var2 = CobbleSpawnConfigScreen.DetectionMode.from(this.habitats.detection.mode);
      CycleButton<CobbleSpawnConfigScreen.DetectionMode> var3 = CycleButton.<CobbleSpawnConfigScreen.DetectionMode>builder(var0 -> Component.literal(var0.label))
         .withValues(CobbleSpawnConfigScreen.DetectionMode.values())
         .withInitialValue(var2)
         .create(
            this.contentX(),
            this.rowY(var1),
            this.contentWidth(),
            20,
            Component.literal("Biome Detection Mode"),
            (var1x, var2x) -> this.habitats.detection.mode = var2x.id
         );
      var3.setTooltip(
         Tooltip.create(
            Component.literal(
               "Patterns Only uses only habitats.json patterns. Balanced uses tags/climate when confident. Aggressive classifies most unknown modded biomes."
            )
         )
      );
      this.addRenderableWidget(var3);
   }

   private void addToggle(int var1, String var2, boolean var3, Consumer<Boolean> var4, String var5) {
      CycleButton var6 = CycleButton.booleanBuilder(Component.literal("Enabled"), Component.literal("Disabled"))
         .withInitialValue(var3)
         .create(this.contentX(), this.rowY(var1), this.contentWidth(), 20, Component.literal(var2), (var1x, var2x) -> var4.accept(var2x));
      var6.setTooltip(Tooltip.create(Component.literal(var5)));
      this.addRenderableWidget(var6);
   }

   private void addPercentSlider(int var1, String var2, double var3, DoubleConsumer var5, String var6) {
      this.addSlider(var1, var2, 0.0, 1.0, var3, 0.01, 2, var5, "", var6);
   }

   private void addSlider(
      int var1, String var2, double var3, double var5, double var7, double var9, int var11, final DoubleConsumer var12, String var13, String var14
   ) {
      ExtendedSlider var15 = new ExtendedSlider(
         this.contentX(),
         this.rowY(var1),
         this.contentWidth(),
         20,
         Component.literal(var2 + ": "),
         Component.literal(var13),
         var3,
         var5,
         var7,
         var9,
         var11,
         true
      ) {
         protected void applyValue() {
            var12.accept(this.getValue());
         }
      };
      var15.setTooltip(Tooltip.create(Component.literal(var14)));
      this.addRenderableWidget(var15);
   }

   private int contentX() {
      return Math.max(152, this.width / 3);
   }

   private int contentWidth() {
      return Math.max(100, this.width - this.contentX() - 14);
   }

   private int rowY(int var1) {
      return 34 + var1 * 20;
   }

   private void openPage(CobbleSpawnConfigScreen.Page var1) {
      if (var1 != this.page && this.minecraft != null) {
         this.minecraft
            .setScreen(new CobbleSpawnConfigScreen(this.parent, this.general, this.habitats, this.species, var1, this.selectedHabitat, this.selectedRule));
      }
   }

   private void saveAndClose() {
      if (this.general.density.maximumSpawningZoneDistanceFromPlayer < this.general.density.minimumSpawningZoneDistanceFromPlayer) {
         this.general.density.maximumSpawningZoneDistanceFromPlayer = this.general.density.minimumSpawningZoneDistanceFromPlayer;
      }

      if (this.general.levels.globalMax < this.general.levels.globalMin) {
         this.general.levels.globalMax = this.general.levels.globalMin;
      }

      if (this.general.levels.maxDistanceLevel < this.general.levels.levelNearSpawn) {
         this.general.levels.maxDistanceLevel = this.general.levels.levelNearSpawn;
      }

      if (this.habitats.detection.coolTemperature < this.habitats.detection.coldTemperature) {
         this.habitats.detection.coolTemperature = this.habitats.detection.coldTemperature;
      }

      if (this.habitats.detection.hotTemperature < this.habitats.detection.coolTemperature) {
         this.habitats.detection.hotTemperature = this.habitats.detection.coolTemperature;
      }

      if (this.habitats.detection.veryHotTemperature < this.habitats.detection.hotTemperature) {
         this.habitats.detection.veryHotTemperature = this.habitats.detection.hotTemperature;
      }

      if (this.habitats.detection.wetDownfall < this.habitats.detection.dryDownfall) {
         this.habitats.detection.wetDownfall = this.habitats.detection.dryDownfall;
      }

      ConfigManager.saveAll(this.general, this.habitats, this.species);
      if (this.minecraft != null) {
         this.minecraft.setScreen(this.parent);
      }
   }

   private void closeWithoutSaving() {
      if (this.minecraft != null) {
         this.minecraft.setScreen(this.parent);
      }
   }

   public void onClose() {
      this.closeWithoutSaving();
   }

   private enum DetectionMode {
      PATTERNS_ONLY("patterns_only", "Patterns Only"),
      BALANCED("balanced", "Balanced"),
      AGGRESSIVE("aggressive", "Aggressive");

      final String id;
      final String label;

      DetectionMode(String nullxx, String nullxxx) {
         this.id = nullxx;
         this.label = nullxxx;
      }

      static CobbleSpawnConfigScreen.DetectionMode from(String var0) {
         if (var0 != null) {
            String var1 = var0.toLowerCase(Locale.ROOT);

            for (CobbleSpawnConfigScreen.DetectionMode var5 : values()) {
               if (var5.id.equals(var1)) {
                  return var5;
               }
            }
         }

         return BALANCED;
      }
   }

   private enum Page {
      DENSITY("Spawn Density"),
      LEVELS("Wild Levels"),
      DEPTH("Depth Scaling"),
      POPULATION("Population"),
      RARITY("Evolution Rarity"),
      ECOLOGY("Ecology"),
      BIOMES("Biome Signals"),
      CLIMATE("Climate Thresholds"),
      HABITATS("Habitat Editor"),
      SPECIES("Species Rules");

      final String label;

      Page(String nullxx) {
         this.label = nullxx;
      }
   }

   private enum TriState {
      ANY(null, "Any"),
      YES(Boolean.TRUE, "Yes"),
      NO(Boolean.FALSE, "No");

      final Boolean value;
      final String label;

      TriState(Boolean nullxx, String nullxxx) {
         this.value = nullxx;
         this.label = nullxxx;
      }

      static CobbleSpawnConfigScreen.TriState from(Boolean var0) {
         return var0 == null ? ANY : (var0 ? YES : NO);
      }
   }
}
