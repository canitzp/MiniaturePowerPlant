# Miniature Power Plant (MPP)

### Support:

| Version | Modloader | Status |
|:-------:|:---------:|:------:|
| 1.20.6  | NeoForge  |   ✅    |
| 1.20.4  | NeoForge  |   ✅    |
| 1.20.2  | NeoForge  |   ❌    |
| 1.20.1  |   Forge   |   ✅    |
| 1.19.x  |   Forge   |   ❌    |
| 1.18.x  |   Forge   |   ❌    |

Since 1.20.2, NeoForge is the supported modloader.

## About
Miniature Power Plant was created to solve a simple issue. Diverse energy production without fancy factories.

You just have to craft and place the carrier, which can hold up to three modules and three upgrades, as well as 20kFE. By adding the modules, you can create a simple power plant.

Any module has it pros and cons, for example the solar modules works best in direct sunlight, but is affected by bad weather.
But to find out al effects, there is a simple statistics view, where every energy production and every penalty is written down.

## Planned
- upgrades for existing modules (temperature, water)
- new module that "eats" grass and leaves to generate energy

## Wiki
### Carrier

#### Block
The carrier is a block which provides the base for any power generation module.
It can be waterlogged, but there is currently no benefit to do.

![](https://github.com/canitzp/MiniaturePowerPlant/blob/main/wiki/carrier_block_empty.png?raw=true)

#### GUI
On interaction, it opens its interface, to put modules in.

![](https://github.com/canitzp/MiniaturePowerPlant/blob/main/wiki/carrier_gui_empty.png?raw=true)

The interface consists on five parts (from top to bottom):

- Upper [modules](#modules) (eg: solar module) and [upgrades](#upgrades)
- Center [modules](#modules) (eg: temperature module) and [upgrades](#upgrades)
- Bottom [modules](#modules) and [upgrades](#upgrades)
- Energy meter and battery slot
- Player inventory

##### Statistics meter
Under every module/upgrade slot there is a bar, which shows statistics about the module and upgrade.

![](https://github.com/canitzp/MiniaturePowerPlant/blob/main/wiki/carrier_gui_statistics_bar.png?raw=true)

In this picture you can see the current rate, at which the module is depleting.
A rate of 100% is normal and the module depletes at its default level.
Upgrades can help to decrease this, but also increase this rate.

Below it the energy production, their penalties and boosts are listed.
All of those can have more than one entry and they may vary, by time, position or any other outside factor.

The visible bar show the depletion rate from 0% to 150%.
0% to 50% is green, 51% to 100% is yellow and everything above 100% is red.
A depletion rate of above 150% is possible, but not displayed within the bar.

##### Battery slot
The battery slot is able to extend the capacity of the Carrier.

The carrier tries to pump its own energy into the item, as soon and fast as possible.

The slot can only be populated with items, able to handle energy.
That doesn't mean that the item can actually receive energy, so be careful what you put in there.

##### Energy meter
The energy meter displays the current energy stored by the carrier or any item inside the battery slot.

On mouse hover it shows the exact amount of energy stored, for itself and any battery, the energy generation per tick and the wasted energy for the last tick.

The energy bar itself show the combined stored energy for the carrier and the optional battery.

![](https://github.com/canitzp/MiniaturePowerPlant/blob/main/wiki/carrier_gui_energy.png?raw=true)

##### Energy and Inventory IO
With cables or pipes from other mods it is possible to insert and extract items and extract the energy.

If the carrier has some stored energy left, it tries to push it in all directions and after that fills an optionally inserted battery.
Also if the optional battery has stored energy, it also tries to transfer it to surrounding blocks.

You can push items into the carrier, with the same rules as inside the gui.
Extracting is much more strict and so it is only possible to extract the three modules, if they are fully depleted.
The extraction of upgrades and the battery is not permitted.

### Modules
Modules are items that can be put into their corresponding [carrier](#gui) slot.
They are the main part of the energy production and they vary by functions.
Every module has a maximum depletion and a default depletion rate.
When in use, the module depletes by their rate per tick.

For any Class 1 module, the max depletion is 100K with a depletion rate of 1 per tick.
So this module would last 5000 seconds or roughly 83 minutes (real time).
The Class 1 solar module produces up to 10FE per tick and so it can produce up to 1MFE within its lifespan.
These calculation only apply when no upgrade is used.

|           Module            |      Slot      | Class | Max depletion | Depletion rate | Base Energy production |
|:---------------------------:|:--------------:|:-----:|:-------------:|:--------------:|:----------------------:|
|       [Solar](#solar)       |      Top       |   1   |     100K      |      1.0       |      0 - 10 FE/t       |
| [Temperature](#temperature) | Center, Bottom |   1   |     100K      |      1.0       |      0 - 20 FE/t       |
|       [Water](#water)       | Center, Bottom |   1   |     100K      |      1.0       |  0 - 8 FE/t per side   |
|        [Wind](#wind)        | Center, Bottom |   1   |     100K      |      1.0       |        10 FE/t         |


#### Solar
The solar modules are generating their energy from the sun.
But this can't be implemented, so I choose the easy way and let them produce their energy by the skylight the carrier block is receiving.
The skylight depends on multiple factors, like time of the day, obstructions above or weather conditions.
It uses the same algorithm as the daylight detector, but the value is multiplied by 0.67, to reduce the maximal output to 10FE/t (instead of 15).

The module can append penalties, caused by natural factors.
The most common one is rain which decreases the energy production by 50%, while a thunderstorm decreases the production by 85%.

There are multiple tiers of solar modules, with different depletion level and energy outputs.

#### Temperature
The temperature module is generating energy out of nothing, well not nothing but it seems like it would.
The energy production depends on the biome the carrier block is in.
Minecraft associated a temperature value with every biome, which is multiplied by ten to get the energy generated.

This module appends a 50% penalty, whenever it is raining at the carrier block position

#### Water
The water modules generated energy from the flow of nearby water.
To get the best result you need to put level 4 flowing water on all sides.

The water level is the stage of flowing water. placing down a water block creates a level 8 water,
with every block in the  flowing direction, the level is decreased by one.

Here is a list of the energy generated by which water level (Class 1 module):

| Water Level | Energy generated (FE/t) |
|:-----------:|:-----------------------:|
|    1 & 7    |            2            |
|    2 & 6    |            4            |
|    3 & 5    |            6            |
|      4      |            8            |
|      8      |            1            |

There are no penalties for this kind of module.

#### Wind
The wind module generates energy based on the carriers y-position, the world height and the surrounding blocks.
It base production is 10FE/t at y=128. Every positional increase or decrease, also increases or decreases the energy output.

The module has a 7x7 radius, where it checks for full-blocks and if it finds one, it adds a 10% penalty per block.

### Upgrades
Upgrades are items that can be put into their corresponding [carrier](#carrier) slot.
These are optional, but recommended to increase energy generation or decrease module depletion.
Normally an upgrade only affects the module it is placed nearby, but some upgrades have effects on others as well.
If so it is specified in their description.

#### Eco
Eco upgrades are good for reducing the depletion rate and so increasing the lifespan of a module, but they also reduce the energy generation.
The Eco+ upgrade decreases the depletion of all other modules as well as its own.

| Type | Own Depletion | Others Depletion | Energy Reduction |
|:----:|:-------------:|:----------------:|:----------------:|
| Eco  |      10%      |        -         |        5%        |
| Eco+ |      20%      |       10%        |       7.5%       |