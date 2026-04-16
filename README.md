# Wynnpelago

An [Archipelago](https://archipelago.gg) client for the [Wynncraft](https://wynncraft.com) Minecraft MMORPG.

Wynncraft is a MMORPG featuring a completely custom abilities and combat system, and a large world with lots of content for the player to explore.

> Archipelago is a cross-game modification system which randomizes different games, then uses the result to build a single unified multi-player game. 
> Items from one game may be present in another, and you will need your fellow players to find items you need in their games to help you complete your own.
> 
> \- *The archipelago.gg homepage*

This mod is a bridge between Wynncraft and Archipelago, allowing you to play the [Wynncraft APWorld](https://github.com/EpicPuppy613/Wynncraft-APWorld).

## Setup

This mod is for the Fabric modloader and Minecraft version 1.21.11.
This mod also requires [Wynntils](https://modrinth.com/mod/wynntils) and [Fabric API](https://modrinth.com/mod/fabric-api) as dependencies.

### Connecting to Archipelago

1. Log in to Wynncraft. Wynntils should provide a button to do this from the main menu.
2. Create a new character. Ironman mode is recommended, but not required.
3. The tutorial is optional; run `/skiptutorial` to skip it.
4. Run `/archipelago connect <host> <port> <slot> [password]` to connect to an Archipelago server.

If you are playing in a multiworld, and are not the one hosting, contact whoever's hosting and ask them for instructions.
If you are hosting the multiworld, or playing solo, follow these instructions:

1. Download the [Archipelago](https://github.com/ArchipelagoMW/Archipelago/releases/latest) software
2. Download the latest [Wynncraft APWorld](https://github.com/EpicPuppy613/Wynncraft-APWorld/releases)
3. In Archipelago Launcher, import the Wynncraft .apworld
4. Use the 'Options Creator' tool to choose the randomization options
5. Move all of the .yaml files of everyone playing into the `Players` directory of your Archipelago install
6. Use the 'Generate' tool to generate the multiworld
7. Either upload the multiworld to an online host like https://archipelago.gg (recommended) or host the multiworld yourself
8. Connect to the multiworld in-game by using the command `/archipelago connect`

A more detailed setup guide can be found on the [Archipelago website](https://archipelago.gg/tutorial/Archipelago/setup_en).

## Features

This list uses the following definitions of items and checks:
- A check is where a single, random item is rewarded.
- An item is a single additional feature or thing that is unlocked in Wynncraft.

**Region Lock**
- The vast majority of the wynncraft world starts locked
- To enter a region, it must first be unlocked by getting an item
- __Entering a region before it is unlocked will kill the player__

**Level Limit**
- Max combat level starts at 5
- The limit increases with items
- Note: This is done using Wynncraft's guild experience feature. You will need to be in a Wynncraft guild for this feature to function properly.

**Questsanity**
- Completion of a quest gives a check
- Completion of a mini-quest gives a check

**Cavesanity**
- Completion of a cave for the first time gives a check
- You must meet the minimum level requirement of the cave

**Dungeonsanity**
- Completion of a dungeon for the first time gives a check

## Commands

Wynnpelago provides the following commands to manage the mod and Archipelago connection:

- `/wynnpelago version`: prints the version of Wynnpelago installed
- `/archipelago connect <host> <port> <slot> [password]`: connect to a running Archipelago server
- `/archipelago disconnect`: disconnect from the currently connected Archipelago server
- `/ap <message>`: send a message through Archipelago

## Wynntils Integration

Wynnpelago uses Wynntils to provide a few convenience features while playing Archipelago.
- **World Map**: Holding control overlays regions on the map (M). 
Regions in red are not accessible to the player.
- **Territory Map**: The guild map (J) will show all regions and what regions they connect to. 
  - The randomizer will ensure the entire game is beatable by staying within territories connected to Ragni.