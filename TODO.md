Last: Save equipment and inventory to account character
Next: Shops

On Deck:
- Gold / items for mobiles
- Stats for mobiles
- Skills
- Battle system 1.0

Later:
- Make screen width adjustable on connection (currently fixed to 80)
- Spell Casting
- Classes and Races
- Game world calendar
- Weather
- Banks (items and gold)
- Account for item weight
- Use a connection proxy so the game can be fully recompiled and reloaded

Bugs:
- Shops commands should refer to the shop owner and be more flavorful
- Area reload must remove all shop events from the game (have World keep a
  list of shops that gets populated during area parsing)
- Move command will fail across areas (uses area.getRoom, need global lookup)
- System log levels from env (e.g. `LOG_LEVEL=trace`)
- Fix the look command
  - Doesn't wrap item descriptions
  - Cannot look at items in inventory
