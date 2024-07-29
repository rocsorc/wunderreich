package de.ambertation.wunderreich.mixin;

import de.ambertation.wunderreich.interfaces.AbstractVillagerAccessor;
import de.ambertation.wunderreich.network.CycleTradesMessage;

import net.minecraft.world.entity.npc.Villager;
import net.minecraft.world.entity.npc.VillagerData;
import net.minecraft.world.entity.npc.VillagerTrades;
import net.minecraft.world.flag.FeatureFlags;
import net.minecraft.world.item.trading.MerchantOffers;

import it.unimi.dsi.fastutil.ints.Int2ObjectMap;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

@Mixin(value = Villager.class, priority = 100)
public class VillagerMixin {

    protected void wunderreich_updateTradesProxy() {
        Villager self = (Villager) (Object) this;
        AbstractVillagerAccessor acc = (AbstractVillagerAccessor) this;

        boolean found;
        MerchantOffers merchantOffers;
        VillagerTrades.ItemListing[] itemListings;
        int maxCount = 1000;
        do {
            //TODO: [MC Update] Check for changes in base Method
            //-------------------------------------
            VillagerData villagerData = self.getVillagerData();
            Int2ObjectMap<VillagerTrades.ItemListing[]> int2ObjectMap;
            if (self.level().enabledFeatures().contains(FeatureFlags.TRADE_REBALANCE)) {
                Int2ObjectMap<VillagerTrades.ItemListing[]> int2ObjectMap2 = VillagerTrades.EXPERIMENTAL_TRADES
                        .get(villagerData.getProfession());
                int2ObjectMap = int2ObjectMap2 != null
                        ? int2ObjectMap2
                        : VillagerTrades.TRADES.get(villagerData.getProfession());
            } else {
                int2ObjectMap = VillagerTrades.TRADES.get(villagerData.getProfession());
            }
            if (int2ObjectMap == null || int2ObjectMap.isEmpty()) {
                return;
            }
            itemListings = int2ObjectMap.get(villagerData.getLevel());
            if (itemListings == null) {
                return;
            }
            merchantOffers = self.getOffers();
            acc.wunderreich_addOffersFromItemListings(merchantOffers, itemListings, 2);
            //-------------------------------------

            found = CycleTradesMessage.hasSelectedTrades(self, merchantOffers);
            if (!found) {
                self.setOffers(new MerchantOffers());
                maxCount--;
            }
        } while (!found && maxCount > 0);
    }

    @Inject(method = "updateTrades", at = @At(value = "HEAD"), cancellable = true)
    void wunderreich_updateTrades(CallbackInfo ci) {
        Villager self = (Villager) (Object) this;
        if (CycleTradesMessage.canSelectTrades(self)) {
            wunderreich_updateTradesProxy();
            ci.cancel();
        }
    }
}
