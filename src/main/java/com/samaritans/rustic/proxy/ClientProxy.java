package com.samaritans.rustic.proxy;

import javax.annotation.Nullable;

import com.samaritans.rustic.ModColorManager;
import com.samaritans.rustic.client.KeyHandler;
import com.samaritans.rustic.client.renderer.CabinetTileEntityRenderer;
import com.samaritans.rustic.client.renderer.CrushingTubTileEntityRenderer;
import com.samaritans.rustic.client.renderer.PotTileEntityRenderer;
import com.samaritans.rustic.tileentity.CabinetTileEntity;
import com.samaritans.rustic.tileentity.CrushingTubTileEntity;
import com.samaritans.rustic.tileentity.PotTileEntity;

import net.minecraft.client.Minecraft;
import net.minecraft.client.particle.Particle;
import net.minecraft.client.particle.ParticleManager;
import net.minecraft.crash.CrashReport;
import net.minecraft.crash.CrashReportCategory;
import net.minecraft.crash.ReportedException;
import net.minecraft.particles.IParticleData;
import net.minecraftforge.api.distmarker.Dist;
import net.minecraftforge.api.distmarker.OnlyIn;
import net.minecraftforge.fml.client.registry.ClientRegistry;
import net.minecraftforge.fml.event.lifecycle.FMLClientSetupEvent;
import net.minecraftforge.fml.javafmlmod.FMLJavaModLoadingContext;

@OnlyIn(Dist.CLIENT)
public class ClientProxy implements IProxy {

	public static Minecraft mc = null;
	
	public ClientProxy() {
		FMLJavaModLoadingContext.get().getModEventBus().addListener(this::clientSetup);
	}
	
	@Override
	public Dist getDist() {
		return Dist.CLIENT;
	}
	
	private void clientSetup(final FMLClientSetupEvent event) {
		mc = Minecraft.getInstance();
		
        ClientRegistry.bindTileEntitySpecialRenderer(CabinetTileEntity.class, new CabinetTileEntityRenderer<>());
        ClientRegistry.bindTileEntitySpecialRenderer(CrushingTubTileEntity.class, new CrushingTubTileEntityRenderer());
        ClientRegistry.bindTileEntitySpecialRenderer(PotTileEntity.class, new PotTileEntityRenderer());

        // TODO move this to a correct event
        ModColorManager.register();
    }

	@Override
	public void loadComplete() {
		KeyHandler.initKeyBinds();
	}
	
	@Nullable
	public static Particle spawnParticle(IParticleData particleData, double x, double y, double z, double velX, double velY, double velZ) {
		try {
			ParticleManager particles = mc.particles;
			if (particles != null)
				return particles.addParticle(particleData, x, y, z, velX, velY, velZ);
		} catch (Throwable throwable) {
			CrashReport crashreport = CrashReport.makeCrashReport(throwable, "Exception while adding particle");
			CrashReportCategory crashreportcategory = crashreport.makeCategory("Particle being added");
			crashreportcategory.addDetail("ID", particleData.getType().getRegistryName());
			crashreportcategory.addDetail("Parameters", particleData.getParameters());
			crashreportcategory.addDetail("Position", () -> CrashReportCategory.getCoordinateInfo(x, y, z));
			throw new ReportedException(crashreport);
		}
		return null;
	}

}
