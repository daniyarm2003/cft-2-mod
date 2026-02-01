package com.lildan42.cft.initialization;

import com.lildan42.cft.CFT2Mod;
import com.lildan42.cft.items.CFTRemovalWandItem;
import net.fabricmc.fabric.api.itemgroup.v1.ItemGroupEvents;
import net.minecraft.item.Item;
import net.minecraft.item.ItemGroup;
import net.minecraft.item.ItemGroups;
import net.minecraft.registry.Registries;
import net.minecraft.registry.Registry;
import net.minecraft.registry.RegistryKey;
import net.minecraft.registry.RegistryKeys;
import net.minecraft.util.Rarity;
import org.slf4j.Logger;

import java.util.List;
import java.util.function.Function;

public class CFT2ModItems implements CFT2Initializer {

    public static final CFTRemovalWandItem CFT_REMOVAL_WAND = registerItem("cft_removal_wand",
            CFTRemovalWandItem::new, new Item.Settings().fireproof().rarity(Rarity.RARE).maxCount(1).useCooldown(1.0F));

    private static <T extends Item> T registerItem(String name, Function<Item.Settings, T> itemFactory, Item.Settings settings) {
        RegistryKey<Item> itemKey = RegistryKey.of(RegistryKeys.ITEM, CFT2Mod.createModIdentifier(name));

        T item = itemFactory.apply(settings.registryKey(itemKey));
        Registry.register(Registries.ITEM, itemKey, item);

        return item;
    }

    private void registerCreativeTabItems(RegistryKey<ItemGroup> itemGroupKey, List<Item> items) {
        ItemGroupEvents.modifyEntriesEvent(itemGroupKey).register(
                itemGroup -> items.forEach(itemGroup::add));
    }

    @Override
    public String getInitializationStageName() {
        return "Item registration";
    }

    @Override
    public void initialize(Logger logger) {
        this.registerCreativeTabItems(ItemGroups.TOOLS, List.of(
                CFT_REMOVAL_WAND
        ));

        logger.info("Items have been added to creative tabs successfully");
    }
}
