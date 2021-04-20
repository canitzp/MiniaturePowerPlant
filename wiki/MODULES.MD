# Modules
Modules are items that can be put into their corresponding [carrier](CARRIER.MD#gui) slot.
They are the main part of the energy production and they vary by functions.
Every module has a maximal depletion and a default depletion rate.
When in use, the module depletes by their rate per tick.

For any Class 1 module, the max depletion is 100K with a depletion rate of 1 per tick.
So this module would last 5000 seconds or roughly 83 minutes (real time).
The Class 1 solar module produces up to 10FE per tick and so it can produce up to 1MFE within its lifespan.
These calculation only apply when no upgrade is used.

## Overview

| Module            | Slot     | Class | Max depletion     | Depletion rate    | Energy production |
| :---------------: | :------: | :---: | :---------------: | :---------------: | :---------------: |
| [Solar](#solar)   | Top      | 1     | 100K              | 1.0               | 0 - 10 FE/t       |
| Temperature       | Center, Bottom   | 1     | 100K              | 1.0               | 0 - 20 FE/t       |


### Solar
The solar modules are generating their energy from the sun.
But this can't be implemented, so I choose the easy way and let them produce their energy by the skylight the carrier block is receiving.
The skylight depends on multiple factors, like time of the day and obstructions above.

The module can append penalties, cause nature factors.
The most common one is rain which decreases the energy production by 50%, while a thunderstorm decreases the production by 85%.

### Temperature
The temperature module is generating energy out of nothing, well not nothing but it seems like it would.
The energy production depends on the biome the carrier block is in.
Minecraft associated a temperature value with every biome, which is multiplied by ten to get the energy generated.

This module appends a 50% penalty, whenever it is raining at the carrier block position