# [Diegetic](https://modrinth.com/mod/diegetic/version/0.8.3_1.21.11)

![Clocktime Example](https://cdn.modrinth.com/data/6V343pDj/images/c8d5564eb088d15a4289dd9aa844cae5b2caacf2.png)

## Features
Diegetic adds several in-game means of measuring information that is otherwise only reasonably obtainable outside of the game and with "meta-gaming". The current systems added are:
   - [Clock](https://minecraft.wiki/w/Clock): shows time (configurably IRL time)
   - [Compass](https://minecraft.wiki/w/Compass): shows coordinates
   - [Recovery Compass](https://minecraft.wiki/w/Recovery_Compass): shows relative coordinates
   - [Slimeball](https://minecraft.wiki/w/Slimeball): shows if you're in a [slime-chunk](https://minecraft.wiki/w/Slime#Slime_chunks)

The items used to measure information may be subject to change.

---

## [Game Rules](https://minecraft.wiki/w/Game_rule)

| Name (1.21+) | Name (-1.21) | Description |
| ------------ | ------------ | ----------- |
| clock_displays_time | diegeticClockDisplaysTime | Clocks can display the [world] time. |
| clock_uses_server_time | diegeticClockUsesServerTime | If clocks are enabled, clocks will display the server's (IRL) time. |
| compass_coordinates | diegetic- CompassCoordinates | Compasses can provide absolute coordinates. |
| lodestone_compass_relative_coordinates | diegeticLodestone- CompassRelativeCoordinates | Compasses associated with lodestones will provide relative coordinates to their lodestone. |
| recovery_compass_relative_coordinates | diegeticRecovery- CompassRelativeCoordinates | Recovery-compasses will provide relate coordinates to last known death location. |
| slime_chunk_checking | diegeticSlimeChunkChecking | Slimeballs can inform if one's current chunk is a slime chunk. |

By default, all of features are enabled (their gamerules are true).
These can be modified with the gamerule command, ex:
``/gamerule diegetic:slime_chunk_checking false``
.

---

The shaders used in [the gallery](https://modrinth.com/mod/diegetic/gallery) are [BSL](https://modrinth.com/shader/bsl-shaders) by [CaptTatsu](https://modrinth.com/user/CaptTatsu).