package com.TominoCZ.FBP.handler;

import com.TominoCZ.FBP.particle.FBPParticleManager;

import net.minecraft.client.Minecraft;
import net.minecraft.client.entity.EntityPlayerSP;
import net.minecraft.client.particle.ParticleDigging.Factory;
import net.minecraftforge.event.entity.EntityJoinWorldEvent;
import net.minecraftforge.fml.common.eventhandler.SubscribeEvent;

public class FBPEventHandler {
	@SubscribeEvent
	public void onEntityJoinWorldEvent(EntityJoinWorldEvent e) {
		if (e.getEntity() instanceof EntityPlayerSP)
			Minecraft.getMinecraft().effectRenderer = new FBPParticleManager(e.getWorld(),
					Minecraft.getMinecraft().getTextureManager(), new Factory());
	}
}