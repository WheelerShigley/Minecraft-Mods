package me.wheelershigley.tradesmaxxing.mixins;

import com.google.common.collect.ImmutableMap;
import com.google.common.collect.Maps;
import it.unimi.dsi.fastutil.ints.Int2ObjectMap;
import me.wheelershigley.tradesmaxxing.TradeMaps;
import net.minecraft.util.Util;
import net.minecraft.village.TradeOffers;
import net.minecraft.village.VillagerProfession;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Shadow;

import java.util.Map;

@Mixin(value = TradeOffers.class, priority = 800)
public class TradeOffersMixin {
    @Shadow
    private static Int2ObjectMap<TradeOffers.Factory[]> copyToFastUtilMap(ImmutableMap<Integer, TradeOffers.Factory[]> map) { return null; }

    @Shadow
    public static final Map<VillagerProfession, Int2ObjectMap<TradeOffers.Factory[]>> PROFESSION_TO_LEVELED_TRADE;
    static {
        PROFESSION_TO_LEVELED_TRADE = Util.make(
            Maps.newHashMap(), (map) -> {
                map.put(VillagerProfession.ARMORER,         copyToFastUtilMap(TradeMaps.ARMORORTRADES)      );
                map.put(VillagerProfession.BUTCHER,         copyToFastUtilMap(TradeMaps.BUTCHERTRADES)      );
                map.put(VillagerProfession.CARTOGRAPHER,    copyToFastUtilMap(TradeMaps.CARTOGRAPHERTRADES) );
                map.put(VillagerProfession.CLERIC,          copyToFastUtilMap(TradeMaps.CLERICTRADES)       );
                map.put(VillagerProfession.FARMER,          copyToFastUtilMap(TradeMaps.FARMERTRADES)       );
                map.put(VillagerProfession.FISHERMAN,       copyToFastUtilMap(TradeMaps.FISHERMANTRADES)    );
                map.put(VillagerProfession.FLETCHER,        copyToFastUtilMap(TradeMaps.FLETCHERTRADES)     );
                map.put(VillagerProfession.LEATHERWORKER,   copyToFastUtilMap(TradeMaps.LEATHERWORKERTRADES));
                map.put(VillagerProfession.LIBRARIAN,       copyToFastUtilMap(TradeMaps.LIBRARIANTRADES)    );
                map.put(VillagerProfession.MASON,           copyToFastUtilMap(TradeMaps.MASONTRADES)        );
                map.put(VillagerProfession.SHEPHERD,        copyToFastUtilMap(TradeMaps.SHEPHERDTRADES)     );
                map.put(VillagerProfession.TOOLSMITH,       copyToFastUtilMap(TradeMaps.TOOLSMITHTRADES)    );
                map.put(VillagerProfession.WEAPONSMITH,     copyToFastUtilMap(TradeMaps.WEAPONSMITHTRADES)  );
                }
        );
    }
}
