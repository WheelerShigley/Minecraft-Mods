# Charged
![Player Trap](https://cdn.modrinth.com/data/5cQnQD5m/images/ff93df48541fea0bb49c24f2c74b789e39dfc557.png)
Charged Creepers killing players will (configurably) make them drop their head, (configurably, again) with their skin's texture.

---

![Overworld Drops](https://cdn.modrinth.com/data/5cQnQD5m/images/9aca6970b7173803316826a638a8c68d84e2c9ee.png)
![Wither Skeleton Drops](https://cdn.modrinth.com/data/5cQnQD5m/images/7a12eb15fbbfb8d1e5c0e504db9fee19b4b5c76f.png)
![Player Heads](https://cdn.modrinth.com/data/5cQnQD5m/images/03caee9fe9d277c76f77e27e510cf2ac6f016228.png)
Very many entities heads (configurably) can be dropped.

---
![Washing](https://cdn.modrinth.com/data/5cQnQD5m/images/e2d46df05aa37a326dce11e993594767209c82a2.png)
Player heads with skins can be washed into default skins.

---
## Configurations
```md
# When enabled, dropped player-heads will use the victim's texture; otherwise, it will be the default (Steve) texture.
PlayerHeadsUseSkins: true

# When enabled, player-head textures can be washed off with water, in a Cauldron, to the default (Steve) texture.
PlayerHeadTextureWashing: true

# Set some maximum amount of heads dropped by an individual Charged Creeper's explosion.
# When negative, there will be no limit!
MaximumDropsPerChargedCreeper: -1

# When enabled, players will be included as entities which drop heads.
enablePlayerHeadDrops: true
```