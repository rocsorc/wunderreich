/**
 * This class is adapted from "Easy Villagers"
 */
package de.ambertation.wunderreich.gui;

import de.ambertation.wunderreich.network.CycleTradesMessage;
import de.ambertation.wunderreich.registries.WunderreichRules;

import com.mojang.blaze3d.systems.RenderSystem;
import com.mojang.math.Axis;
import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.GuiGraphics;
import net.minecraft.client.gui.components.Button;
import net.minecraft.client.gui.screens.inventory.AbstractContainerScreen;
import net.minecraft.client.gui.screens.inventory.MerchantScreen;
import net.minecraft.core.Holder;
import net.minecraft.core.component.DataComponents;
import net.minecraft.network.chat.Component;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.inventory.MerchantMenu;
import net.minecraft.world.item.Items;
import net.minecraft.world.item.enchantment.Enchantment;
import net.minecraft.world.item.enchantment.ItemEnchantments;
import net.minecraft.world.item.trading.MerchantOffer;
import net.minecraft.world.item.trading.MerchantOffers;

import it.unimi.dsi.fastutil.objects.Object2IntMap;

import java.util.ArrayList;
import java.util.List;
import java.util.Optional;
import org.jetbrains.annotations.NotNull;

public class CycleTradesButton extends Button {
    private static final ResourceLocation PAGE_FORWARD_HIGHLIGHTED_SPRITE = ResourceLocation.withDefaultNamespace("widget/page_forward_highlighted");
    private static final ResourceLocation PAGE_FORWARD_SPRITE = ResourceLocation.withDefaultNamespace("widget/page_forward");

    public static final int WIDTH = 23;
    private static final int HALF_HEIGHT = 13;
    public static final int HEIGHT = HALF_HEIGHT * 2;

    private final MerchantScreen screen;
    private final MerchantMenu menu;
    private boolean canUse;

    public CycleTradesButton(int x, int y, OnPress pressable, MerchantScreen screen, MerchantMenu menu) {
        super(x, y, WIDTH, HEIGHT, Component.empty(), pressable, Button.DEFAULT_NARRATION);
        this.screen = screen;
        this.menu = menu;
    }

    @NotNull
    public static CycleTradesButton getCycleTradesButton(
            AbstractContainerScreen<MerchantMenu> merchantScreenMixin,
            int imageWidth,
            int imageHeight,
            MerchantScreen merchantScreen,
            MerchantMenu menu
    ) {
        final int left = (merchantScreenMixin.width - imageWidth) / 2;
        final int top = (merchantScreenMixin.height - imageHeight) / 2;

        CycleTradesButton button = new CycleTradesButton(left - CycleTradesButton.WIDTH - 2, top + 2, b -> {
            CycleTradesMessage.send();
        }, merchantScreen, menu);

        if (WunderreichRules.Whispers.cyclingNeedsWhisperer()) {
            button.canUse = CycleTradesMessage.containsWhisperer(Minecraft.getInstance().player) != null;
            button.active = button.canUse;
            button.visible = button.canUse;
        } else {
            button.canUse = true;
        }

        return button;
    }

    @Override
    public void renderWidget(GuiGraphics guiGraphics, int mouseX, int mouseY, float partialTicks) {
        visible = canUse && screen.getMenu().showProgressBar() && screen.getMenu().getTraderXp() <= 0;

        RenderSystem.setShaderColor(1F, 1F, 1F, 1F);
        ResourceLocation arrowLocation = (this.isHovered()
                ? PAGE_FORWARD_HIGHLIGHTED_SPRITE
                : PAGE_FORWARD_SPRITE);

        guiGraphics.blitSprite(arrowLocation, this.getX(), getY() + HALF_HEIGHT - 3, 23, 13);

        guiGraphics.pose().pushPose();
        guiGraphics.pose().rotateAround(Axis.ZP.rotationDegrees(180), getX(), getY(), 0);
        guiGraphics.blitSprite(arrowLocation, getX() - WIDTH, getY() - HALF_HEIGHT, 23, 13);
        guiGraphics.pose().popPose();


        if (isHovered) {
            List<Component> components = new ArrayList<>(2);
            components.add(Component.translatable("tooltip.wunderreich.cycle_trades"));
            MerchantOffers offers = this.menu.getOffers();
            for (MerchantOffer offer : offers) {
                if (offer.getResult().is(Items.ENCHANTED_BOOK)) {
                    final var result = offer.getResult();
                    final ItemEnchantments enchantments = result.get(DataComponents.STORED_ENCHANTMENTS);

                    for (Object2IntMap.Entry<Holder<Enchantment>> entry : enchantments.entrySet()) {
                        final Holder<Enchantment> e = entry.getKey();
                        final int level = enchantments.getLevel(e);
                        components.add(Enchantment.getFullname(e, level));
                    }
                }
            }

            guiGraphics.renderTooltip(Minecraft.getInstance().font, components, Optional.empty(), mouseX, mouseY);
        }


    }
}
