# [Diegetic](https://modrinth.com/mod/diegetic/version/YdsiXhQd)

![Clocktime Example](https://cdn.modrinth.com/data/6V343pDj/images/c8d5564eb088d15a4289dd9aa844cae5b2caacf2.png)

## Features
Diegetic adds several in-game means of measuring information that is otherwise only reasonably obtainable outside of the game and with "meta-gaming". The current systems added are:
   - [Clock](https://minecraft.wiki/w/Clock): shows time (configurably IRL time)
   - [Compass](https://minecraft.wiki/w/Compass): shows coordinates
   - [Recovery Compass](https://minecraft.wiki/w/Recovery_Compass): shows relative coordinates
   - [Slimeball](https://minecraft.wiki/w/Slimeball): shows if you're in a [slime-chunk](https://minecraft.wiki/w/Slime#Slime_chunks)

The items used to measure information may be subject to change.

---

 This mod has configurations, through gamerules, for all added features!
 ## GameRules
 ```md
"diegeticClockDisplaysTime": Clocks can display the [world] time.
"diegeticClockUsesServerTime": If clocks are enabled, clocks will display the server's (IRL) time.
"diegeticCompassCoordinates": Compasses can provide absolute coordinates.
"diegeticLodestoneCompassRelativeCoordinates": Compasses associated with lodestones will provide relative coordinates to their lodestone.
"diegeticRecoveryCompassRelativeCoordinates": Recovery-compasses will provide relate coordinates to last known death location.
"diegeticSlimeChunkChecking": Slimeballs can inform if one's current chunk is a slime chunk.
```
Operators can update these with "/gamerule <name> <true/false>".

---

The shaders used in [the gallery](https://modrinth.com/mod/diegetic/gallery) are [BSL](https://modrinth.com/shader/bsl-shaders) by [CaptTatsu](https://modrinth.com/user/CaptTatsu).