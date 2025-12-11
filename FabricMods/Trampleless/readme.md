# [Charged](https://modrinth.com/mod/charged/version/1.0.2_1.21.11)
---

![Player Trap](https://cdn.modrinth.com/data/5cQnQD5m/images/ff93df48541fea0bb49c24f2c74b789e39dfc557.png)
![Player Heads](https://cdn.modrinth.com/data/5cQnQD5m/images/03caee9fe9d277c76f77e27e510cf2ac6f016228.png)
Charged Creepers killing players will (configurably) make them drop their head, (configurably, again) with their skin's texture.

![Wither Skeleton Drops](https://cdn.modrinth.com/data/5cQnQD5m/images/7a12eb15fbbfb8d1e5c0e504db9fee19b4b5c76f.png)
Very many entities heads (configurably) can be dropped.

---

![Washing](https://cdn.modrinth.com/data/5cQnQD5m/images/e2d46df05aa37a326dce11e993594767209c82a2.png)
Player heads with skins can be washed into default skins.
This feature has a client/server sync feature that will prevent temporary, client-side ghost-blocks [of placed heads] appearing at all.

---

## [Game Rules](https://minecraft.wiki/w/Game_rule)

| Name (1.21+) | Name (-1.21) | Default Value | Description |
| ------------ | ------------ | ------------- | ----------- |
| player_head_drop | playerHeadDrop | true | Players killed by Charged-Creepers may drop a head. |
| player_head_drop_textures | playerHeadDropTextures  | true | When player drop heads, they will use their players' texture. |
| player_head_texture_washing | playerHeadDropTextures  | true | Textured player heads may be untextured with a Cauldron of water. |
| maximum_head_drop_count | maxHeadDropCount  | -1 | Maximum heads drop from a single Charged-Creepers' kills; when negative, limits are disabled. |

These can be modified with the gamerule command, ex:
``/gamerule charged:maximum_head_drop_count 1``
.

---

![Icon](https://cdn.modrinth.com/data/5cQnQD5m/images/0f66ff3776943aada9c93668917d6e28c6c8dac6.png)
