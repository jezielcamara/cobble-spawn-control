# Changelog

All notable changes to Cobble Spawn Control will be documented in this file.

The format is based on [Keep a Changelog](https://keepachangelog.com/en/1.1.0/), and this project intends to use [Semantic Versioning](https://semver.org/).

## [Unreleased]

## [0.5.0] - 2026-10-02

### Added

- Restored the complete NeoForge source project and Gradle wrapper.
- Added reproducible Minecraft 1.21.1 / NeoForge 21.1.252 builds.
- Added full feature, configuration, command, and build documentation.

### Changed

- Updated required Cobblemon range from `1.7.3 - <1.8` to `1.8.1 - <1.9`.
- Updated project version to `0.5.0`.
- Repaired recovered generic type information so the project builds on Java 21.

### Verified

- Confirmed the Cobblemon 1.8.1 spawn event, entity, species, level, and density API entry points used by the compatibility bridge are still present.
- Confirmed `gradlew.bat clean build` produces the distributable JAR.

## [0.4.1]

### Added

- World-scale population, ecology, rarity, density, and level controls.
