package com.samaritans.rustic.proxy;

import com.samaritans.rustic.ModColorManager;
import com.samaritans.rustic.client.renderer.CabinetTileEntityRenderer;
import com.samaritans.rustic.client.renderer.CrushingTubTileEntityRenderer;
import com.samaritans.rustic.client.renderer.PotTileEntityRenderer;
import com.samaritans.rustic.tileentity.CabinetTileEntity;
import com.samaritans.rustic.tileentity.CrushingTubTileEntity;
import com.samaritans.rustic.tileentity.PotTileEntity;

import net.minecraftforge.api.distmarker.Dist;
import net.minecraftforge.api.distmarker.OnlyIn;
import net.minecraftforge.fml.client.registry.ClientRegistry;
import net.minecraftforge.fml.event.lifecycle.FMLClientSetupEvent;
import net.minecraftforge.fml.javafmlmod.FMLJavaModLoadingContext;

@OnlyIn(Dist.CLIENT)
public class ClientProxy implements IProxy {

	public ClientProxy() {
		FMLJavaModLoadingContext.get().getModEventBus().addListener(this::clientSetup);
	}
	
	@Override
	public Dist getDist() {
		return Dist.CLIENT;
	}
	
	private void clientSetup(final FMLClientSetupEvent event) {
        ClientRegistry.bindTileEntitySpecialRenderer(CabinetTileEntity.class, new CabinetTileEntityRenderer<>());
        ClientRegistry.bindTileEntitySpecialRenderer(CrushingTubTileEntity.class, new CrushingTubTileEntityRenderer());
        ClientRegistry.bindTileEntitySpecialRenderer(PotTileEntity.class, new PotTileEntityRenderer());

        // TODO move this to a correct event
        ModColorManager.register();
    }

}
