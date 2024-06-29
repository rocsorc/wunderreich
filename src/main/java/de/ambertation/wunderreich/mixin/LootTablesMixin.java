package de.ambertation.wunderreich.mixin;

import de.ambertation.wunderreich.loot.LootTableJsonBuilder;

import net.minecraft.core.registries.Registries;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.server.ReloadableServerRegistries;
import net.minecraft.server.packs.resources.ResourceManager;
import net.minecraft.world.level.storage.loot.LootDataType;

import com.google.gson.Gson;
import com.google.gson.JsonElement;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.ModifyArg;

import java.util.Map;


@Mixin(value = ReloadableServerRegistries.class, priority = 200)
public class LootTablesMixin {
    @ModifyArg(method = "method_58279", at = @At(value = "INVOKE", target = "Lnet/minecraft/server/packs/resources/SimpleJsonResourceReloadListener;scanDirectory(Lnet/minecraft/server/packs/resources/ResourceManager;Ljava/lang/String;Lcom/google/gson/Gson;Ljava/util/Map;)V"))
    private static Map<ResourceLocation, JsonElement> wunderreich_injectLootTables(
            ResourceManager resourceManager,
            String lootPath,
            Gson gson,
            Map<ResourceLocation, JsonElement> map
    ) {
        final String tablePath = Registries.elementsDirPath(LootDataType.TABLE.registryKey());
        if (tablePath.equals(lootPath)) {
            LootTableJsonBuilder
                    .getAllBlocks()
                    .filter(e -> !map.containsKey(e.id()))
                    .forEach(e -> map.put(e.id(), e.json().get()));
        }

        return map;
    }
}
