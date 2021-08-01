package de.ambertation.wunderreich.mixin.client;


import de.ambertation.wunderreich.gui.CycleTradesButton;
import de.ambertation.wunderreich.interfaces.IMerchantMenu;
import de.ambertation.wunderreich.network.CycleTradesMessage;
import net.minecraft.client.gui.screens.Screen;
import net.minecraft.client.gui.screens.inventory.AbstractContainerScreen;
import net.minecraft.client.gui.screens.inventory.MerchantScreen;
import net.minecraft.network.chat.Component;
import net.minecraft.world.entity.player.Inventory;
import net.minecraft.world.inventory.MerchantMenu;
import org.spongepowered.asm.mixin.Final;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Shadow;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

@Mixin(MerchantScreen.class)
public abstract class MerchantScreenMixin extends AbstractContainerScreen<MerchantMenu>  {
	public MerchantScreenMixin(MerchantMenu abstractContainerMenu, Inventory inventory, Component component) {
		super(abstractContainerMenu, inventory, component);
	}
	
	@Shadow @Final private static int MERCHANT_MENU_PART_X;
	
	@Inject(method="init", at=@At("TAIL"))
	public void wunderreich_onInit(CallbackInfo ci){
		MerchantScreen merchantScreen = (MerchantScreen)(Object)this;
		final int left = (this.width - this.imageWidth) / 2;
		final int top = (this.height - this.imageHeight) / 2;
		
		CycleTradesButton button = new CycleTradesButton(left + MERCHANT_MENU_PART_X + 8, top + 8, b -> {
			CycleTradesMessage.send();
		}, merchantScreen);
		
		this.addRenderableWidget(button);
	}
}
