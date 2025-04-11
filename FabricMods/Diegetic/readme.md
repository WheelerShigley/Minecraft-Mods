# [Diegetic](https://modrinth.com/mod/diegetic/version/0.6.3)

![Clocktime Example](https://cdn.modrinth.com/data/6V343pDj/images/c8d5564eb088d15a4289dd9aa844cae5b2caacf2.png)

## Features
Diegetic adds several in-game means of measuring information that is otherwise only reasonably obtainable outside of the game and with "meta-gaming". The current systems added are:
   - [Clock](https://minecraft.wiki/w/Clock): shows time (configurably IRL time)
   - [Compass](https://minecraft.wiki/w/Compass): shows coordinates
   - [Recovery Compass](https://minecraft.wiki/w/Recovery_Compass): shows relative coordinates
   - [Slimeball](https://minecraft.wiki/w/Slimeball): shows if you're in a [slime-chunk](https://minecraft.wiki/w/Slime#Slime_chunks)

The tools used to measure information may be subject to change.

To reload your configurations, run "/diegetic reload" (requires op).

---

 This mod has configuration for most added features.
 ## diegetic.properties
 ```
# Clocks can display the [world] time.
clock: true
# If clocks are enabled, clocks will display the server's (IRL) time.
clock_real: false

# Compasses can provide absolute coordinates.
compass: true
# Compasses associated with lodestones will provide relative coordinates to their lodestone.
lodestone_compass: true

# Slimeballs can inform if one's current chunk is a slime chunk.
slime: true
```

---

The shaders used in [the gallery](https://modrinth.com/mod/diegetic/gallery) are [BSL](https://modrinth.com/shader/bsl-shaders) by [CaptTatsu](https://modrinth.com/user/CaptTatsu).