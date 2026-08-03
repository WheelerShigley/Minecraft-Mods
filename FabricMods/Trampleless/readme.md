# [Trampleless](https://modrinth.com/mod/trampleless/version/1.2.2_26.1-26.2)
![Example Usage](https://cdn.modrinth.com/data/shxtVuPU/images/c843cdaa126a2429e0ed552ffd0b2e6fe843edfc.png)
Trampling [Farmland](https://minecraft.wiki/w/Farmland) can be (configurably) disabled!

## Feather-Falling prevents trampling farmland (configurable)!

Farmland can *only* be *trampled* by **[players](https://minecraft.wiki/w/Player)** *when* they
- lack [boots](https://minecraft.wiki/w/Boots) with [Feather-Falling](https://minecraft.wiki/w/Feather_Falling)

and by **[non-players](https://minecraft.wiki/w/Mob)** *when* they're
- at least [medium-sized](# "larger than 0.512m^3")
- [mobGriefing](https://minecraft.wiki/w/Commands/gamerule#Examples) is enabled
- lack [boots](https://minecraft.wiki/w/Boots) with [Feather-Falling](https://minecraft.wiki/w/Feather_Falling)
.

---

## Protect your fields!
![One of my friend's wheat-flats, "Dad Farm".](https://cdn.modrinth.com/data/shxtVuPU/images/d9b8e43232cf17e35f1795eb603fec79d597316b.png)

---

## [Game Rules](https://minecraft.wiki/w/Game_rule)

| Name (1.21+) | Name (-1.21) | Default Value | Description |
| ------------ | ------------ | ------------- | ----------- |
| farmland_trampling | farmlandTrampling | true | Farmland can be trampled. |
| feather_falling_trampling | featherFallingTrampling  | false | Farmland can be trampled even with Feather-Falling boots equiped. |

These can be modified with the gamerule command, ex:
``/gamerule trampleless:farmland_trampling false``
.
