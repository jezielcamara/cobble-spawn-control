# Cobble Spawn Control

Cobble Spawn Control adds world-scale control over wild Cobblemon population,
ecology, rarity, density, and levels.

## Compatibility

| Component | Supported version |
| --- | --- |
| Minecraft | 1.21.1 |
| Mod loader | NeoForge 21.1+ |
| Cobblemon | 1.8.1 up to, but not including, 1.9 |
| Java | 21 |

Version 0.5.0 restores the complete source project and updates the runtime
compatibility contract for Cobblemon 1.8.1.

## Features

- Applies configurable Cobblemon density and spawn-distance settings.
- Scales wild Pokemon levels using distance, depth, dimension, biome, and species rules.
- Enforces configurable local total-population and per-species caps.
- Classifies vanilla and modded biomes into configurable ecological habitats.
- Generates automatic type-based ecology for species without an explicit rule, including addon species.
- Supports species rules, habitat allow/block lists, time windows, spawn acceptance, local caps, and level clamps.
- Includes in-game configuration screens and `/csc` diagnostics commands.
- Fails open when a single rule cannot be evaluated so one bad entry does not stop all Cobblemon spawning.

## Installation

1. Install Minecraft 1.21.1, NeoForge 21.1 or newer, and Cobblemon 1.8.1.
2. Place the Cobble Spawn Control JAR in the instance's `mods` folder.
3. Start the game once to create the configuration files.

## Configuration

Configuration is created under `config/cobblespawncontrol/`:

- `general.json` controls density, world-driven levels, population limits, evolution rarity, and global behavior.
- `habitats.json` defines habitat detection and carrying capacity.
- `species.json` defines explicit species and addon-species overrides.

Use `/csc reload` after editing configuration. `/csc inspect` reports the current location, habitat, local population, and predicted wild level. The diagnostics command can produce an ecology report under the configuration directory.

## Building

The repository includes a Gradle wrapper and uses NeoForge ModDevGradle.

```text
gradlew.bat clean build
```

The resulting JAR is written to `build/libs/`.

## Links

- [GitHub repository](https://github.com/jezielcamara/cobble-spawn-control)
- [Issue tracker](https://github.com/jezielcamara/cobble-spawn-control/issues)

## Source and licensing

The project metadata declares All Rights Reserved. No additional license grant is provided by this repository.
