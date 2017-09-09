package com.TominoCZ.FBP.renderer;

import com.TominoCZ.FBP.FBP;
import com.TominoCZ.FBP.particle.FBPParticleRain;
import com.TominoCZ.FBP.particle.FBPParticleSnow;

import net.minecraft.client.Minecraft;
import net.minecraft.client.renderer.EntityRenderer;
import net.minecraft.client.resources.IResourceManager;
import net.minecraft.init.Blocks;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.MathHelper;
import net.minecraft.world.biome.Biome;

public class FBPEntityRenderer extends EntityRenderer {

	private Minecraft mc;

	long snowTick;
	long rainTick;

	public FBPEntityRenderer(Minecraft mcIn, IResourceManager resourceManagerIn) {
		super(mcIn, resourceManagerIn);
		mc = mcIn;
	}

	@Override
	public void updateRenderer() {
		super.updateRenderer();

		if (FBP.fancyWeather && mc.theWorld.isRaining()) {
			BlockPos.MutableBlockPos blockpos$mutableblockpos = new BlockPos.MutableBlockPos();
			Biome biome;

			double mX;
			double mZ;
			double mT;
			
			double offsetY;

			double angle;
			double radius;
			double X;
			double Z;

			int r;

			if (++snowTick >= 13) {
				r = 25;
				
				mX = mc.thePlayer.motionX * 26;
				mZ = mc.thePlayer.motionZ * 26;

				mT = (MathHelper.sqrt_double(mX * mX + mZ * mZ) / 25);

				offsetY = mc.thePlayer.motionY * 6;

				for (int i = 0; i < 25 * (2 - mc.gameSettings.particleSetting); i++) {
					// get random position within radius of a little over the player's render
					// distance
					angle = mc.theWorld.rand.nextDouble() * Math.PI * 2;
					radius = MathHelper.sqrt_double(mc.theWorld.rand.nextDouble()) * r;
					X = mc.thePlayer.posX + mX + radius * Math.cos(angle);
					Z = mc.thePlayer.posZ + mZ + radius * Math.sin(angle);

					// check if position is within a snow biome
					blockpos$mutableblockpos.setPos(X, 0, Z);
					biome = mc.theWorld.getBiome(blockpos$mutableblockpos);

					if (biome.getEnableSnow()) {
						mc.effectRenderer
								.addEffect(new FBPParticleSnow(mc.theWorld, X, mc.thePlayer.posY + 17 + offsetY, Z,
										FBP.random.nextDouble(-0.5, 0.5), FBP.random.nextDouble(0.25, 0.8) + mT * 1.5f,
										FBP.random.nextDouble(-0.5, 0.5), Blocks.SNOW.getDefaultState()));
					}
				}

				snowTick = (int) (mT * 2 + offsetY / 2);
			}

			if (++rainTick >= 8) {
				r = 40;
				
				mX = mc.thePlayer.motionX * 26;
				mZ = mc.thePlayer.motionZ * 26;

				mT = (MathHelper.sqrt_double(mX * mX + mZ * mZ) / 25);

				offsetY = mc.thePlayer.motionY * 6;

				for (int i = 0; i < 60 * (2 - mc.gameSettings.particleSetting); i++) {
					// get random position within radius of a little over the player's render
					// distance
					angle = mc.theWorld.rand.nextDouble() * Math.PI * 2;
					radius = MathHelper.sqrt_double(mc.theWorld.rand.nextDouble()) * r;
					X = mc.thePlayer.posX - 5 + mX + radius * Math.cos(angle);
					Z = mc.thePlayer.posZ - 5 + mZ + radius * Math.sin(angle);

					// check if position is NOT within a snow biome
					blockpos$mutableblockpos.setPos(X, 0, Z);
					biome = mc.theWorld.getBiome(blockpos$mutableblockpos);

					if (biome.canRain() && !biome.getEnableSnow()) {
						mc.effectRenderer.addEffect(new FBPParticleRain(mc.theWorld, X, mc.thePlayer.posY + 15, Z, 0.25,
								FBP.random.nextDouble(0.75, 2.25f) + mT / 2, 0.25, Blocks.SNOW.getDefaultState()));
					}
				}

				rainTick = (int) (mT * 2 + offsetY / 2);
			}
		}
	}

	@Override
	protected void renderRainSnow(float partialTicks) {
		if (!FBP.enabled || !FBP.fancyWeather)
			super.renderRainSnow(partialTicks);

		/*
		 * net.minecraftforge.client.IRenderHandler renderer =
		 * this.mc.theWorld.provider.getWeatherRenderer(); if (renderer != null) {
		 * renderer.render(partialTicks, this.mc.theWorld, mc); return; }
		 * 
		 * float f = this.mc.theWorld.getRainStrength(partialTicks);
		 * 
		 * if (f > 0.0F) { this.enableLightmap(); Entity entity =
		 * this.mc.getRenderViewEntity(); World world = this.mc.theWorld; int i =
		 * MathHelper.floor_double(entity.posX); int j =
		 * MathHelper.floor_double(entity.posY); int k =
		 * MathHelper.floor_double(entity.posZ); Tessellator tessellator =
		 * Tessellator.getInstance(); VertexBuffer vertexbuffer =
		 * tessellator.getBuffer(); GlStateManager.disableCull();
		 * GlStateManager.glNormal3f(0.0F, 1.0F, 0.0F); GlStateManager.enableBlend();
		 * GlStateManager.tryBlendFuncSeparate(GlStateManager.SourceFactor.SRC_ALPHA,
		 * GlStateManager.DestFactor.ONE_MINUS_SRC_ALPHA,
		 * GlStateManager.SourceFactor.ONE, GlStateManager.DestFactor.ZERO);
		 * GlStateManager.alphaFunc(516, 0.1F); double d0 = entity.lastTickPosX +
		 * (entity.posX - entity.lastTickPosX) * (double) partialTicks; double d1 =
		 * entity.lastTickPosY + (entity.posY - entity.lastTickPosY) * (double)
		 * partialTicks; double d2 = entity.lastTickPosZ + (entity.posZ -
		 * entity.lastTickPosZ) * (double) partialTicks; int l =
		 * MathHelper.floor_double(d1); int i1 = 5;
		 * 
		 * if (this.mc.gameSettings.fancyGraphics) { i1 = 10; }
		 * 
		 * float[] rainXCoords = new float[1024]; float[] rainYCoords = new float[1024];
		 * int rendererUpdateCount = 0; try { rainXCoords = (float[])
		 * getRainPosX.invokeExact((EntityRenderer) this); rainYCoords = (float[])
		 * getRainPosY.invokeExact((EntityRenderer) this); rendererUpdateCount = (int)
		 * getRendererUpdateCount.invokeExact((EntityRenderer) this); } catch (Throwable
		 * e) { e.printStackTrace(); }
		 * 
		 * int j1 = -1; float f1 = rendererUpdateCount + partialTicks;
		 * vertexbuffer.setTranslation(-d0, -d1, -d2); GlStateManager.color(1.0F, 1.0F,
		 * 1.0F, 1.0F); BlockPos.MutableBlockPos blockpos$mutableblockpos = new
		 * BlockPos.MutableBlockPos();
		 * 
		 * for (int k1 = k - i1; k1 <= k + i1; ++k1) { for (int l1 = i - i1; l1 <= i +
		 * i1; ++l1) { int i2 = (k1 - k + 16) * 32 + l1 - i + 16; double d3 =
		 * rainXCoords[i2] * 0.5D; double d4 = rainYCoords[i2] * 0.5D;
		 * blockpos$mutableblockpos.setPos(l1, 0, k1); Biome biome =
		 * world.getBiome(blockpos$mutableblockpos);
		 * 
		 * if (biome.canRain() || biome.getEnableSnow()) { int j2 =
		 * world.getPrecipitationHeight(blockpos$mutableblockpos).getY(); int k2 = j -
		 * i1; int l2 = j + i1;
		 * 
		 * if (k2 < j2) { k2 = j2; }
		 * 
		 * if (l2 < j2) { l2 = j2; }
		 * 
		 * int i3 = j2;
		 * 
		 * if (j2 < l) { i3 = l; }
		 * 
		 * if (k2 != l2) { world.rand.setSeed((long) (l1 * l1 * 3121 + l1 * 45238971 ^
		 * k1 * k1 * 418711 + k1 * 13761)); blockpos$mutableblockpos.setPos(l1, k2, k1);
		 * float f2 = biome.getFloatTemperature(blockpos$mutableblockpos);
		 * 
		 * if (world.getBiomeProvider().getTemperatureAtHeight(f2, j2) >= 0.15F) { if
		 * (j1 != 0) { if (j1 >= 0) { tessellator.draw(); }
		 * 
		 * j1 = 0; this.mc.getTextureManager().bindTexture(FBP.RAIN_TEXTURES);
		 * vertexbuffer.begin(7, DefaultVertexFormats.PARTICLE_POSITION_TEX_COLOR_LMAP);
		 * }
		 * 
		 * double d5 = -((double) (rendererUpdateCount + l1 * l1 * 3121 + l1 * 45238971
		 * + k1 * k1 * 418711 + k1 * 13761 & 31) + (double) partialTicks) / 32.0D (3.0D
		 * + world.rand.nextDouble()); double d6 = (double) ((float) l1 + 0.5F) -
		 * entity.posX; double d7 = (double) ((float) k1 + 0.5F) - entity.posZ; float f3
		 * = MathHelper.sqrt_double(d6 * d6 + d7 * d7) / (float) i1; float f4 = ((1.0F -
		 * f3 * f3) * 0.5F + 0.5F) * f; blockpos$mutableblockpos.setPos(l1, i3, k1); int
		 * j3 = world.getCombinedLight(blockpos$mutableblockpos, 0); int k3 = j3 >> 16 &
		 * 65535; int l3 = j3 & 65535; vertexbuffer.pos((double) l1 - d3 + 0.5D,
		 * (double) l2, (double) k1 - d4 + 0.5D) .tex(0.0D, (double) k2 * 0.25D +
		 * d5).color(1.0F, 1.0F, 1.0F, f4) .lightmap(k3, l3).endVertex();
		 * vertexbuffer.pos((double) l1 + d3 + 0.5D, (double) l2, (double) k1 + d4 +
		 * 0.5D) .tex(1.0D, (double) k2 * 0.25D + d5).color(1.0F, 1.0F, 1.0F, f4)
		 * .lightmap(k3, l3).endVertex(); vertexbuffer.pos((double) l1 + d3 + 0.5D,
		 * (double) k2, (double) k1 + d4 + 0.5D) .tex(1.0D, (double) l2 * 0.25D +
		 * d5).color(1.0F, 1.0F, 1.0F, f4) .lightmap(k3, l3).endVertex();
		 * vertexbuffer.pos((double) l1 - d3 + 0.5D, (double) k2, (double) k1 - d4 +
		 * 0.5D) .tex(0.0D, (double) l2 * 0.25D + d5).color(1.0F, 1.0F, 1.0F, f4)
		 * .lightmap(k3, l3).endVertex(); } else if (!FBP.enabled || !FBP.fancyWeather)
		 * { if (j1 != 1) { if (j1 >= 0) { tessellator.draw(); }
		 * 
		 * j1 = 1; this.mc.getTextureManager().bindTexture(FBP.SNOW_TEXTURES);
		 * vertexbuffer.begin(7, DefaultVertexFormats.PARTICLE_POSITION_TEX_COLOR_LMAP);
		 * }
		 * 
		 * double d8 = (double) (-((float) (rendererUpdateCount & 511) + partialTicks) /
		 * 512.0F); double d9 = world.rand.nextDouble() + (double) f1 * 0.01D * (double)
		 * ((float) world.rand.nextGaussian()); double d10 = world.rand.nextDouble() +
		 * (double) (f1 * (float) world.rand.nextGaussian()) * 0.001D; double d11 =
		 * (double) ((float) l1 + 0.5F) - entity.posX; double d12 = (double) ((float) k1
		 * + 0.5F) - entity.posZ; float f6 = MathHelper.sqrt_double(d11 * d11 + d12 *
		 * d12) / (float) i1; float f5 = ((1.0F - f6 * f6) * 0.3F + 0.5F) * f;
		 * blockpos$mutableblockpos.setPos(l1, i3, k1); int i4 =
		 * (world.getCombinedLight(blockpos$mutableblockpos, 0) * 3 + 15728880) / 4; int
		 * j4 = i4 >> 16 & 65535; int k4 = i4 & 65535; vertexbuffer.pos((double) l1 - d3
		 * + 0.5D, (double) l2, (double) k1 - d4 + 0.5D) .tex(0.0D + d9, (double) k2 *
		 * 0.25D + d8 + d10).color(1.0F, 1.0F, 1.0F, f5) .lightmap(j4, k4).endVertex();
		 * vertexbuffer.pos((double) l1 + d3 + 0.5D, (double) l2, (double) k1 + d4 +
		 * 0.5D) .tex(1.0D + d9, (double) k2 * 0.25D + d8 + d10).color(1.0F, 1.0F, 1.0F,
		 * f5) .lightmap(j4, k4).endVertex(); vertexbuffer.pos((double) l1 + d3 + 0.5D,
		 * (double) k2, (double) k1 + d4 + 0.5D) .tex(1.0D + d9, (double) l2 * 0.25D +
		 * d8 + d10).color(1.0F, 1.0F, 1.0F, f5) .lightmap(j4, k4).endVertex();
		 * vertexbuffer.pos((double) l1 - d3 + 0.5D, (double) k2, (double) k1 - d4 +
		 * 0.5D) .tex(0.0D + d9, (double) l2 * 0.25D + d8 + d10).color(1.0F, 1.0F, 1.0F,
		 * f5) .lightmap(j4, k4).endVertex(); } } } } }
		 * 
		 * if (j1 >= 0) { tessellator.draw(); }
		 * 
		 * vertexbuffer.setTranslation(0.0D, 0.0D, 0.0D); GlStateManager.enableCull();
		 * GlStateManager.disableBlend(); GlStateManager.alphaFunc(516, 0.1F);
		 * this.disableLightmap(); }
		 */
	}
}
