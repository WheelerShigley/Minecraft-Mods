# [Configurable Sponges](https://modrinth.com/mod/configurable-sponges/version/1.0.0_1.21.5-1.21.6)

Customize [Sponge's](https://minecraft.wiki/w/Sponge)/[Wet-Sponge's](https://minecraft.wiki/w/Sponge#Wet_Sponge) range!
![Ocean Clearing](https://cdn.modrinth.com/data/n6uiUUfG/images/b3405dac928af3a6cf29af2f1d2ceea1a92c5616.png)
``/gamerule spongeRange 30``

Warning: setting ``spongeRange`` beyond ~100 will likely take a (very?) long time to process.

Allow any liquid ([Water](https://minecraft.wiki/w/Water), [Lava](https://minecraft.wiki/w/Lava), [Powder-Snow](https://minecraft.wiki/w/Powder_Snow)) to be cleared via Sponges/Wet-Sponges.
![Underground Clearing](https://cdn.modrinth.com/data/n6uiUUfG/images/3b26bfe83985be4b33bfb5179c2ee125d48c11f4.png)
![Nether Clearing](https://cdn.modrinth.com/data/n6uiUUfG/images/879bf5a1da1394db2fbbc87ab28d376025e9b164.png)
``/gamerule wetSpongeLava true``

### Extra
- Some water-logged blocks do not get un-waterlogged from sponges (ex. Rail); this may be subject to change.
- Having both Sponges and Wet-Sponges enabled for a flowing liquid may allow players to create many block-updates often.

---

## [Gamerules](https://minecraft.wiki/w/Game_rule)

| Name | Default Value |
| ---- | ------------- |
| spongeRange | 6 |
| spongeWater | true |
| spongeLava | false |
| spongePowderedSnow | false |
| wetSpongeWater | false |
| wetSpongeLava | false |
| wetSpongePowderedSnow | false |

example usage: ``/gamerule wetSpongeLava true``
