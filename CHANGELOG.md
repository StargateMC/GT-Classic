# Changelog
# 1.03 (Not Released)
- Added working on ore vein/ocean deposits that are extruded onto the ocean floor.
- Added in world blocks for gases and fluids, fluids are fluids, gases float up and disperse. WIP
- Fixed ocean sand not replacing in some chunks near biome borders.

# 1.02
- WARNING! Changed IDSU power tier from 4 to 5. This might cause existing setups to explode :)
- Added Spring Boots, inspired by QwerTech for GT6. Still slightly WIP.
- Added the Electric Translocator, has no internal inventory but a 9 slot filter to pull items behind it to in front of it.
- Changed all newly placed buffers disable power transfer by default and you must enable it per tile.
- Added Basalt dust, working on a way for it to be obtainable without modded basalt stone.
- Fixed tubes can now pick up fluids in addition to already being able to place them.
- LESU now outputs multiple packets per every 10 blocks added.
- Fixed Rock Cutter not having silk touch if you cheated/spawned it in.
- Changed Fusion casing recipe to be not a such chrome nightmare.
- Added the Light Helmet like old GT's! Creates light around the player in dark areas.
- Added insufficient power warning to Blast Furnace, Fusion, and Centrifuge GUI's
- Fixed Hydrogen and Methane processing yielding a net gain of energy.
- Fixed Player Detector constantly setting state changes despite no actual change.
- Fixed Fusion casings using the wrong casings.
- Fixed the Quantum Chest give full stacks if the internal storage is above 64 when clicked.
- Fixed gravel being constantly overwritten with sand on chunk load.
- Fixed Emerald not having a macerator recipe into emerald dust
- Removed the default thick reflector recipe for harder one
- Fixed Rock Cutter defaulting to regular tool enchants in electric enchanter

# 1.01
- I recommend regenerating configs in this version.
- Added option to replace ocean gravel with sand like newer MC versions.
- Added option to reduce fog/haze underwater like newer MC versions
- Added configurable ore dict for any instance of ingotWroughtIron and ingotRefinedIron.
- Fixed some electric items spamming the "use" animation.
- Fixed a bug with the Quantum Chest that would stop it from reaching full capacity.
- Fixed another bug with the Quantum Chest that would ignore items max stack size on output.
- Fixed AESU getting stuck when adjusting near the min/max.
- Added Supercontainer, for end game stuff - not a real use yet.
- Added Supercondensator, not much real use yet just for recipes and stuff.
- Changed Matter Fabricator tier raised to near infinite input.
- Changed Fusion Reactor teir to 5 instead of 6 to avoid confusion.
- Fixed various EU values in later game machines.
- Changed both IC2C macerators to be more expensive like GT1.
- Added GT6 style "safe spawn" zone, 128 block radius and toggable with configs.
- Fixed error in my code that prevented disabling of Bauxite ore.
- Removed useless Draconic Evolution compat, thats really up to mod pack devs.
- Fixed and added some more Thermal compat things.
- Added very thorough EnderIO compat.
- Added a buttload of config options.