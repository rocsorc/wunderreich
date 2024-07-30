package de.ambertation.wunderreich.recipes;

import de.ambertation.wunderreich.Wunderreich;
import de.ambertation.wunderreich.config.Configs;
import de.ambertation.wunderreich.gui.whisperer.WhisperContainer;
import de.ambertation.wunderreich.gui.whisperer.WhisperRule;
import de.ambertation.wunderreich.registries.WunderreichBlocks;
import de.ambertation.wunderreich.registries.WunderreichRecipes;
import de.ambertation.wunderreich.registries.WunderreichRules;
import de.ambertation.wunderreich.utils.ItemUtil;

import com.mojang.serialization.Codec;
import com.mojang.serialization.JsonOps;
import com.mojang.serialization.MapCodec;
import com.mojang.serialization.codecs.RecordCodecBuilder;
import net.minecraft.client.Minecraft;
import net.minecraft.core.Holder;
import net.minecraft.core.HolderLookup;
import net.minecraft.core.NonNullList;
import net.minecraft.core.Registry;
import net.minecraft.core.registries.BuiltInRegistries;
import net.minecraft.core.registries.Registries;
import net.minecraft.network.RegistryFriendlyByteBuf;
import net.minecraft.network.codec.StreamCodec;
import net.minecraft.resources.RegistryOps;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.tags.EnchantmentTags;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.crafting.Ingredient;
import net.minecraft.world.item.crafting.Recipe;
import net.minecraft.world.item.crafting.RecipeSerializer;
import net.minecraft.world.item.crafting.RecipeType;
import net.minecraft.world.item.enchantment.Enchantment;
import net.minecraft.world.level.Level;

import net.fabricmc.api.EnvType;
import net.fabricmc.api.Environment;

import com.google.gson.JsonElement;

import java.util.Comparator;
import java.util.LinkedList;
import java.util.List;
import java.util.Objects;
import java.util.stream.Collectors;
import org.jetbrains.annotations.ApiStatus;
import org.jetbrains.annotations.NotNull;

public class ImprinterRecipe extends WhisperRule implements Recipe<WhisperContainer.Input> {
    public static final int COST_A_SLOT = 0;
    public static final int COST_B_SLOT = 1;
    private static final List<ImprinterRecipe> RECIPES = new LinkedList<>();
    //    private static List<ImprinterRecipe> RECIPES_UI_SORTED = new LinkedList<>();
    public final ResourceLocation id;

    private ImprinterRecipe(
            ResourceLocation id,
            Holder<Enchantment> enchantment,
            Ingredient inputA,
            Ingredient inputB,
            int baseXP
    ) {
        super(enchantment, inputA, inputB, baseXP);
        this.id = id;
    }

    private ImprinterRecipe(
            ResourceLocation id,
            Holder<Enchantment> enchantment,
            Ingredient inputA,
            Ingredient inputB,
            ItemStack output,
            int baseXP,
            ItemStack type
    ) {
        super(enchantment, inputA, inputB, output, baseXP, type);
        this.id = id;
    }

    private ImprinterRecipe(Holder<Enchantment> e) {
        super(e);

        this.id = makeID(e);
    }

    @NotNull
    private static ResourceLocation makeID(Holder<Enchantment> e) {
        return Wunderreich.ID(Type.ID.getPath() + "/" + e.unwrapKey().orElseThrow().location().getPath());
    }

    public static List<ImprinterRecipe> getRecipes() {
        return RECIPES;
    }

    public static List<ImprinterRecipe> getUISortedRecipes() {
        return RECIPES
                .stream()
                .sorted(Comparator.comparing(a -> a.getCategory() + ":" + a.getName()))
                .collect(Collectors.toList());
    }

    private static void resortRecipes() {
//        RECIPES_UI_SORTED = RECIPES
//                .stream()
//                .sorted(Comparator.comparing(a -> a.getCategory() + ":" + a.getName()))
//                .collect(Collectors.toList());
    }

    @ApiStatus.Internal
    public static void register() {
        Registry.register(BuiltInRegistries.RECIPE_SERIALIZER, Serializer.ID, Serializer.INSTANCE);
        Registry.register(BuiltInRegistries.RECIPE_TYPE, Type.ID, Type.INSTANCE);
    }

    public static HolderLookup.Provider REGISTRY_PROVIDER_OR_NULL = null;

    @ApiStatus.Internal
    public static void registerForLevel() {
        if (Minecraft.getInstance() == null) return;
        if (Minecraft.getInstance().level == null) return;
        registerForLevel(Minecraft.getInstance().level.registryAccess());
    }

    public static void registerForLevel(HolderLookup.Provider provider) {
        if (provider == null || provider == REGISTRY_PROVIDER_OR_NULL) return;
        REGISTRY_PROVIDER_OR_NULL = provider;
        RECIPES.clear();

        if (WunderreichRules.Whispers.allowLibrarianSelection()) {
            List<Holder<Enchantment>> enchants = new LinkedList<>();
            final var enchantments = provider.lookup(Registries.ENCHANTMENT).orElse(null);
            if (enchantments != null) {
                enchantments.listElements()
                            .filter(e -> e.is(EnchantmentTags.TRADEABLE))
                            .forEach(e -> {
                                ResourceLocation ID = makeID(e);
                                if (Configs.RECIPE_CONFIG.newBooleanFor(ID.getPath(), ID).get())
                                    enchants.add(e);
                            });

                enchants.sort(Comparator.comparing(a -> WhisperRule.getFullname(a)
                                                                   .getString()));

                enchants.forEach(e -> {
                    ImprinterRecipe r = new ImprinterRecipe(e);
                    RECIPES.add(r);
                    if (REGISTRY_PROVIDER_OR_NULL == null) {
                        Wunderreich.LOGGER.error("Registry provider is null. Can not create Imprinter Recipes.");
                        return;
                    }
                    RegistryOps<JsonElement> registryOps = REGISTRY_PROVIDER_OR_NULL.createSerializationContext(JsonOps.INSTANCE);
                    WunderreichRecipes.RECIPES.put(
                            r.id,
                            Serializer.CODEC.codec()
                                            .encodeStart(registryOps, r)
                                            .getOrThrow()
                    );
                });
            }
        }

        resortRecipes();
    }

    @Override
    public ItemStack getToastSymbol() {
        return new ItemStack(WunderreichBlocks.WHISPER_IMPRINTER);
    }

    @Override
    public NonNullList<Ingredient> getIngredients() {
        return NonNullList.of(Ingredient.EMPTY, inputA, inputB);
    }


    @Override
    public boolean matches(WhisperContainer.Input inv, Level level) {
        if (inv.size() < 2) return false;
        return this.inputA.test(inv.getItem(COST_A_SLOT)) && this.inputB.test(inv.getItem(COST_B_SLOT)) ||
                this.inputA.test(inv.getItem(COST_B_SLOT)) && this.inputB.test(inv.getItem(COST_A_SLOT));
    }

    @Override
    public ItemStack assemble(WhisperContainer.Input recipeInput, HolderLookup.Provider provider) {
        return this.output.copy();
    }

    @Override
    @Environment(EnvType.CLIENT)
    public boolean canCraftInDimensions(int width, int height) {
        return true;
    }

    @Override
    public @NotNull ItemStack getResultItem(HolderLookup.Provider registryAccess) {
        return this.output;
    }


    @Override
    public @NotNull RecipeSerializer<?> getSerializer() {
        return Serializer.INSTANCE;
    }

    @Override
    public @NotNull RecipeType<?> getType() {
        return Type.INSTANCE;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (!(o instanceof ImprinterRecipe)) return false;
        ImprinterRecipe that = (ImprinterRecipe) o;
        return Objects.equals(id, that.id);
    }

    @Override
    public int hashCode() {
        return Objects.hash(id);
    }

    public static class Type implements RecipeType<ImprinterRecipe> {
        public static final ResourceLocation ID = Wunderreich.ID("imprinter");
        public static final RecipeType<ImprinterRecipe> INSTANCE = new Type();

        Type() {
        }

        @Override
        public String toString() {
            return ID.toString();
        }
    }

    private static class Serializer implements RecipeSerializer<ImprinterRecipe> {
        public static final MapCodec<ImprinterRecipe> CODEC = RecordCodecBuilder.mapCodec(instance -> instance.group(
                ResourceLocation.CODEC.fieldOf("id").forGetter(r -> r.id),
                Enchantment.CODEC.fieldOf("enchantment").forGetter(r -> r.enchantment),
                ItemUtil.CODEC_INGREDIENT_WITH_NBT.fieldOf("inputA").forGetter(r -> r.inputA),
                ItemUtil.CODEC_INGREDIENT_WITH_NBT.fieldOf("inputB").forGetter(r -> r.inputB),
                ItemUtil.CODEC_ITEM_STACK_WITH_NBT.fieldOf("output").forGetter(r -> r.output),
                Codec.INT.fieldOf("baseXP").forGetter(r -> r.baseXP),
                ItemStack.CODEC.fieldOf("type").forGetter(r -> r.type)
        ).apply(instance, ImprinterRecipe::new));
        public static final StreamCodec<RegistryFriendlyByteBuf, ImprinterRecipe> STREAM_CODEC = StreamCodec.of(ImprinterRecipe.Serializer::toNetwork, ImprinterRecipe.Serializer::fromNetwork);

        public final static ResourceLocation ID = Type.ID;
        public final static Serializer INSTANCE = new Serializer();

        @Override
        public @NotNull MapCodec<ImprinterRecipe> codec() {
            return CODEC;
        }


        @Override
        public StreamCodec<RegistryFriendlyByteBuf, ImprinterRecipe> streamCodec() {
            return STREAM_CODEC;
        }

        public static @NotNull ImprinterRecipe fromNetwork(RegistryFriendlyByteBuf packetBuffer) {
            ResourceLocation id = packetBuffer.readResourceLocation();
            Holder<Enchantment> e = Enchantment.STREAM_CODEC.decode(packetBuffer);
            Ingredient inputA = Ingredient.CONTENTS_STREAM_CODEC.decode(packetBuffer);
            Ingredient inputB = Ingredient.CONTENTS_STREAM_CODEC.decode(packetBuffer);
            ItemStack output = ItemStack.STREAM_CODEC.decode(packetBuffer);
            int baseXP = packetBuffer.readVarInt();
            ItemStack type = ItemStack.STREAM_CODEC.decode(packetBuffer);

            return new ImprinterRecipe(id, e, inputA, inputB, output, baseXP, type);
        }


        public static void toNetwork(RegistryFriendlyByteBuf packetBuffer, ImprinterRecipe recipe) {
            packetBuffer.writeResourceLocation(recipe.id);
            Enchantment.STREAM_CODEC.encode(packetBuffer, recipe.enchantment);
            Ingredient.CONTENTS_STREAM_CODEC.encode(packetBuffer, recipe.inputA);
            Ingredient.CONTENTS_STREAM_CODEC.encode(packetBuffer, recipe.inputB);
            ItemStack.STREAM_CODEC.encode(packetBuffer, recipe.output);
            packetBuffer.writeVarInt(recipe.baseXP);
            ItemStack.STREAM_CODEC.encode(packetBuffer, recipe.type);
        }
    }
}
