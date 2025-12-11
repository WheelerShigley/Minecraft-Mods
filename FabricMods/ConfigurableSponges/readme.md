# [Configurable Sponges](https://modrinth.com/mod/configurable-sponges/version/1.0.0_1.21.11)

Customize [Sponge's](https://minecraft.wiki/w/Sponge)/[Wet-Sponge's](https://minecraft.wiki/w/Sponge#Wet_Sponge) range!
![Ocean Clearing](https://cdn.modrinth.com/data/n6uiUUfG/images/b3405dac928af3a6cf29af2f1d2ceea1a92c5616.png)
``/gamerule configurable_sponges:sponges_range 30``

Allow any liquid ([Water](https://minecraft.wiki/w/Water), [Lava](https://minecraft.wiki/w/Lava), [Powder-Snow](https://minecraft.wiki/w/Powder_Snow)) to be cleared via Sponges/Wet-Sponges.
![Underground Clearing](https://cdn.modrinth.com/data/n6uiUUfG/images/3b26bfe83985be4b33bfb5179c2ee125d48c11f4.png)
![Nether Clearing](https://cdn.modrinth.com/data/n6uiUUfG/images/879bf5a1da1394db2fbbc87ab28d376025e9b164.png)
``/gamerule wetSpongeLava true``

### Extra
- Some water-logged blocks do not get un-waterlogged from sponges (ex. Rail); this may be subject to change.
- Having both Sponges and Wet-Sponges enabled for a flowing liquid may allow players to create many block-updates often.

---

## [Gamerules](https://minecraft.wiki/w/Game_rule)

| Name (1.21+) | Name (-1.21) | Default Value |
| ------------ | ------------ | ------------- |
| sponges_range | spongeRange | 6 |
| sponge_absorbs_water | spongeWater | true |
| sponge_absorbs_lava | spongeLava | false |
| sponge_absorbs_powdered_snow | spongePowderedSnow | false |
| wet_sponge_absorbs_water | wetSpongeWater | false |
| wet_sponge_absorbs_lava | wetSpongeLava | false |
| wet_sponge_absorbs_powdered_snow | wetSpongePowderedSnow | false |

example usage: ``/gamerule configurable_sponges:sponges_range 16 true``

Warning:
setting ``sponges_range`` beyond ~100 will likely take a (very?) long time to process and may time-out (crash) the server.
