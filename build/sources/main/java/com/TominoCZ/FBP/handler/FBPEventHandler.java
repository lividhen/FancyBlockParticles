package com.TominoCZ.FBP.handler;

import com.TominoCZ.FBP.FBP;
import com.TominoCZ.FBP.block.FBPAnimationDummyBlock;
import com.TominoCZ.FBP.node.BlockNode;
import com.TominoCZ.FBP.particle.FBPAnimationParticle;
import com.TominoCZ.FBP.particle.FBPParticleManager;
import com.TominoCZ.FBP.renderer.FBPEntityRenderer;

import net.minecraft.block.Block;
import net.minecraft.block.BlockCommandBlock;
import net.minecraft.block.BlockFalling;
import net.minecraft.block.BlockFence;
import net.minecraft.block.BlockSlab;
import net.minecraft.block.BlockSlab.EnumBlockHalf;
import net.minecraft.block.BlockSnow;
import net.minecraft.block.BlockStructure;
import net.minecraft.block.BlockTorch;
import net.minecraft.block.properties.IProperty;
import net.minecraft.block.state.IBlockState;
import net.minecraft.client.Minecraft;
import net.minecraft.client.entity.EntityPlayerSP;
import net.minecraft.client.multiplayer.WorldClient;
import net.minecraft.client.particle.ParticleDigging.Factory;
import net.minecraft.entity.Entity;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.init.Blocks;
import net.minecraft.item.Item;
import net.minecraft.item.ItemBlock;
import net.minecraft.item.ItemSlab;
import net.minecraft.item.ItemStack;
import net.minecraft.util.EnumActionResult;
import net.minecraft.util.EnumFacing;
import net.minecraft.util.EnumHand;
import net.minecraft.util.SoundCategory;
import net.minecraft.util.SoundEvent;
import net.minecraft.util.math.AxisAlignedBB;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.MathHelper;
import net.minecraft.util.math.Vec3d;
import net.minecraft.world.GameType;
import net.minecraft.world.IWorldEventListener;
import net.minecraft.world.World;
import net.minecraftforge.event.entity.EntityJoinWorldEvent;
import net.minecraftforge.event.entity.player.PlayerInteractEvent.RightClickBlock;
import net.minecraftforge.event.world.BlockEvent;
import net.minecraftforge.fml.common.eventhandler.Event.Result;
import net.minecraftforge.fml.common.eventhandler.SubscribeEvent;
import net.minecraftforge.fml.relauncher.ReflectionHelper;
import net.minecraftforge.fml.relauncher.Side;
import net.minecraftforge.fml.relauncher.SideOnly;

public class FBPEventHandler {
	Minecraft mc;

	BlockPos lastPos;
	
	public FBPEventHandler() {
		mc = Minecraft.getMinecraft();
		/*
		IWorldEventListener listener = new IWorldEventListener() {
			
			@Override
			public void spawnParticle(int particleID, boolean ignoreRange, double xCoord, double yCoord, double zCoord,
					double xSpeed, double ySpeed, double zSpeed, int... parameters) {
				// TODO Auto-generated method stub
				
			}
			
			@Override
			public void sendBlockBreakProgress(int breakerId, BlockPos pos, int progress) {
				// TODO Auto-generated method stub
				
			}
			
			@Override
			public void playSoundToAllNearExcept(EntityPlayer player, SoundEvent soundIn, SoundCategory category, double x,
					double y, double z, float volume, float pitch) {
				// TODO Auto-generated method stub
				
			}
			
			@Override
			public void playRecord(SoundEvent soundIn, BlockPos pos) {
				// TODO Auto-generated method stub
				
			}
			
			@Override
			public void playEvent(EntityPlayer player, int type, BlockPos blockPosIn, int data) {
				// TODO Auto-generated method stub
				
			}
			
			@Override
			public void onEntityRemoved(Entity entityIn) {
				// TODO Auto-generated method stub
				
			}
			
			@Override
			public void onEntityAdded(Entity entityIn) {
				// TODO Auto-generated method stub
				
			}
			
			@Override
			public void notifyLightSet(BlockPos pos) {
				// TODO Auto-generated method stub
				
			}
			
			@Override
			public void notifyBlockUpdate(World worldIn, BlockPos pos, IBlockState oldState, IBlockState newState, int flags) {
				// TODO Auto-generated method stub
				//TODO try?
			}
			
			@Override
			public void markBlockRangeForRenderUpdate(int x1, int y1, int z1, int x2, int y2, int z2) {
				// TODO Auto-generated method stub
				
			}
			
			@Override
			public void broadcastSound(int soundID, BlockPos pos, int data) {
				// TODO Auto-generated method stub
				
			}
		};
	*/
	}

	@SideOnly(Side.CLIENT)
	@SubscribeEvent
	public void onEntityJoinWorldEvent(EntityJoinWorldEvent e) {
		if (e.getEntity() instanceof EntityPlayerSP) {
			mc.effectRenderer = new FBPParticleManager(e.getWorld(), mc.getTextureManager(), new Factory());

			mc.entityRenderer = new FBPEntityRenderer(mc, mc.getResourceManager());
		}
	}

	@SideOnly(Side.CLIENT)
	@SubscribeEvent
	public void onPlayerPlaceBlockEvent(BlockEvent.PlaceEvent e) {
		IBlockState bs = e.getPlacedBlock();
		Block placed = bs.getBlock();

		if (placed == FBP.FBPBlock)
			e.setCanceled(true);
	}

	@SubscribeEvent
	public void onInteractionEvent(RightClickBlock e) {
		if (!FBP.enabled)
			return;

		EnumFacing facing = e.getFace();
		EnumHand hand = e.getHand();

		EntityPlayer plr = e.getEntityPlayer();

		ItemStack itemStack = e.getItemStack();
		World w = e.getWorld();
		BlockPos pos = e.getPos().offset(facing);

		Vec3d vec = e.getHitVec();

		Block b = null;
		IBlockState clickedBlockState = w.getBlockState(pos.offset(facing.getOpposite()));

		if (w instanceof WorldClient && itemStack != null) {
			b = Block.getBlockFromItem(itemStack.getItem());

			if (b instanceof BlockSlab
					&& w.getBlockState(pos.offset(facing.getOpposite())).getBlock() instanceof FBPAnimationDummyBlock)
				return;

			if (b != null && canBlockBePlaced(plr, w, e.getPos(), facing, hand, e.getUseBlock(), e.getUseItem(),
					e.getItemStack(), vec)) {

				if (b == FBP.FBPBlock) {
					mc.thePlayer.inventory.deleteStack(itemStack);
					mc.thePlayer.inventory.markDirty();
					e.setCanceled(true);
					return;
				}

				if (!FBP.fancyPlaceAnim)
					return;

				int itemBlockMeta = ((ItemBlock) Item.getItemFromBlock(b)).getMetadata(itemStack.getMetadata());

				float f = (float) (vec.xCoord - (double) pos.getX());
				float f1 = (float) (vec.yCoord - (double) pos.getY());
				float f2 = (float) (vec.zCoord - (double) pos.getZ());

				IBlockState stateForPlacement = b.getStateForPlacement(w, pos, facing, f, f1, f2, itemBlockMeta, plr,
						itemStack);

				IBlockState stateAtPos = w.getBlockState(pos);
				if (stateAtPos.getBlock() == FBP.FBPBlock) {
					if (FBP.FBPBlock.blockNodes.containsKey(pos.offset(facing.getOpposite()))) {
						BlockNode n = FBP.FBPBlock.blockNodes.get(pos.offset(facing.getOpposite()));
						stateAtPos = n.state;
					}
				}

				boolean becomesDoubleSlab = false;
				boolean isSlabAtPos = stateAtPos.getBlock() instanceof BlockSlab;

				if (b instanceof BlockSlab || (isSlabAtPos && b instanceof BlockSlab)) {
					ItemSlab is = (ItemSlab) Item.getItemFromBlock(b);

					BlockSlab toPlace = ((BlockSlab) b);

					IProperty<?> iproperty = ((BlockSlab) b).getVariantProperty();

					BlockSlab singleSlab = null;
					BlockSlab doubleSlab = null;

					try {
						singleSlab = (BlockSlab) ReflectionHelper.findField(ItemSlab.class,

								"field_150949_c", "singleSlab").get(is);
						doubleSlab = (BlockSlab) ReflectionHelper
								.findField(ItemSlab.class, "field_179226_c", "doubleSlab").get(is);
						EnumBlockHalf half;

						if (isSlabAtPos) {
							half = stateAtPos.getValue(BlockSlab.HALF);

							if (stateAtPos.getValue(iproperty) == stateForPlacement.getValue(iproperty)
									&& ((half == EnumBlockHalf.TOP && f1 < 0.5)
											|| half == EnumBlockHalf.BOTTOM && f1 > 0.5)) {
								stateForPlacement = doubleSlab.getStateFromMeta(itemBlockMeta);

								b = stateForPlacement.getBlock();

								becomesDoubleSlab = true;
							}
						} else {
							half = clickedBlockState.getValue(BlockSlab.HALF);
							if (clickedBlockState.getValue(iproperty) == stateForPlacement.getValue(iproperty)
									&& ((facing == EnumFacing.DOWN && half == EnumBlockHalf.TOP)
											|| (facing == EnumFacing.UP && half == EnumBlockHalf.BOTTOM))) {
								stateForPlacement = doubleSlab.getStateFromMeta(itemBlockMeta);

								b = stateForPlacement.getBlock();
								pos = pos.offset(facing.getOpposite());

								becomesDoubleSlab = true;
							}
						}
					} catch (Exception ex) {

					}
				}

				AxisAlignedBB bb1 = stateForPlacement.getBoundingBox(w, pos).offset(pos);
				AxisAlignedBB bb2 = plr.getEntityBoundingBox();

				if (b instanceof BlockFalling) {
					BlockFalling bf = (BlockFalling) b;
					if (bf.canFallThrough(w.getBlockState(pos.offset(EnumFacing.DOWN))))
						return;
				} else if (b instanceof BlockTorch && clickedBlockState.getBlock() instanceof FBPAnimationDummyBlock) {
					if (stateAtPos.getBlock() instanceof BlockTorch) {
						for (EnumFacing fc : EnumFacing.VALUES) {
							BlockPos p = pos.offset(fc);
							Block bl = w.getBlockState(p).getBlock();

							boolean canBePlaced = false;

							if (!(bl instanceof FBPAnimationDummyBlock)) {
								if (bl.isSideSolid(bl.getDefaultState(), w, pos, fc)) {
									canBePlaced = true;
								}
							}

							if (!canBePlaced)
								return;
						}
					}
				}

				if (stateForPlacement.getBlock().canReplace(w, pos.offset(facing.getOpposite()), facing, itemStack)) {
					try {
						if (stateForPlacement.getBlock() != clickedBlockState.getBlock()
								&& clickedBlockState.getBlock().isReplaceable(w, pos.offset(facing.getOpposite()))) {
							pos = pos.offset(facing.getOpposite());
						}
					} catch (Throwable t) {

					}
				}
				
				stateForPlacement = stateForPlacement.getActualState(w, pos);
/*
				IBlockState snowLayer = null;
				boolean snowLayerChanges = false;

				

				if (b == Blocks.SNOW_LAYER) {
					boolean layerInFront = stateAtPos.getBlock() == Blocks.SNOW_LAYER;
					boolean layerClicked = clickedBlockState.getBlock() == Blocks.SNOW_LAYER;
					
						IProperty<Integer> iproperty = BlockSnow.LAYERS;
						int l = snowLayer.getValue(iproperty);

						if (l < 8) {
							
							
							
						}
							boolean canEditFromSide = l == 1;

							if (!canEditFromSide && facing != EnumFacing.UP) {
								if (stateAtPos.getBlock() != Blocks.SNOW_LAYER
										&& !stateAtPos.getBlock().isReplaceable(w, pos)
										|| (snowLayer != clickedBlockState && snowLayer.getBlock() != Blocks.SNOW_LAYER
												&& (snowLayer.getValue(iproperty) == 8)))
									return;
							}

							if (snowLayer == stateAtPos || clickedBlockState.getBlock().isReplaceable(w, pos.offset(facing.getOpposite()))) {
								stateForPlacement = stateAtPos.withProperty(iproperty, ++l);
								snowLayerChanges = true;
							} else if (facing == EnumFacing.UP) {
								stateForPlacement = snowLayer.withProperty(iproperty, ++l);
								snowLayerChanges = true;
							}

							if ((snowLayer == clickedBlockState 
									&& facing == EnumFacing.UP)
									|| (!clickedBlockState.getBlock().isReplaceable(w, pos.offset(facing.getOpposite()))
									&& snowLayer == stateAtPos))
								pos = pos.offset(facing.getOpposite()); // edit the block clicked
						
					}
				}
				}*/
				long seed = MathHelper.getPositionRandom(pos);

				if ((b.canPlaceBlockAt(w, pos) || becomesDoubleSlab) && !bb1.intersectsWith(bb2)
						&& FBP.canBlockBeAnimated(stateForPlacement.getBlock())) {
					FBPAnimationParticle p = new FBPAnimationParticle(mc.theWorld, pos.getX() + 0.5f, pos.getY() + 0.5f,
							pos.getZ() + 0.5f, stateForPlacement, e.getEntityPlayer(), seed);

					mc.effectRenderer.addEffect(p);
				}
			}
		}

	}

	boolean canBlockBePlaced(EntityPlayer plr, World w, BlockPos pos, EnumFacing fc, EnumHand hand, Result getUseBlock,
			Result getUseItem, ItemStack stack, Vec3d vec) {
		float f = (float) (vec.xCoord - (double) pos.getX());
		float f1 = (float) (vec.yCoord - (double) pos.getY());
		float f2 = (float) (vec.zCoord - (double) pos.getZ());
		boolean flag = false;
		EnumActionResult result = EnumActionResult.PASS;

		if (mc.playerController.getCurrentGameType() != GameType.SPECTATOR) {
			Item item = stack == null ? null : stack.getItem();
			EnumActionResult ret = item == null ? EnumActionResult.PASS
					: item.onItemUseFirst(stack, plr, w, pos, fc, f, f1, f2, hand);
			if (ret != EnumActionResult.PASS)
				return false;

			BlockNode node = FBP.FBPBlock.blockNodes.get(pos);

			IBlockState iblockstate = w.getBlockState(pos);

			if (iblockstate.getBlock() == FBP.FBPBlock) {
				if (node != null)
					iblockstate = node.state;
			}

			boolean bypass = true;
			for (ItemStack s : new ItemStack[] { plr.getHeldItemMainhand(), plr.getHeldItemOffhand() })
				bypass = bypass && (s == null || s.getItem().doesSneakBypassUse(s, w, pos, plr));

			if ((!plr.isSneaking() || bypass || getUseBlock == Result.ALLOW)) {
				if (getUseBlock != net.minecraftforge.fml.common.eventhandler.Event.Result.DENY)
					flag = iblockstate.getBlock().onBlockActivated(w, pos, iblockstate, plr, hand, stack, fc, f, f1,
							f2);

				if (flag)
					return (iblockstate.getBlock() instanceof BlockFence
							|| iblockstate.getBlock() instanceof BlockStructure);
			}

			if (!flag && stack != null && stack.getItem() instanceof ItemBlock) {
				ItemBlock itemblock = (ItemBlock) stack.getItem();

				if (!itemblock.canPlaceBlockOnSide(w, pos, fc, plr, stack))
					return false;
			}
		}

		if (stack != null && !flag && mc.playerController.getCurrentGameType() != GameType.SPECTATOR
				|| getUseItem == Result.ALLOW) {
			if (stack.getItem() instanceof ItemBlock && !plr.canUseCommandBlock()) {
				Block block = ((ItemBlock) stack.getItem()).getBlock();

				if (block instanceof BlockCommandBlock || block instanceof BlockStructure) {
					return false;
				}
			}

			if (mc.playerController.getCurrentGameType().isCreative()) {
				if (result == EnumActionResult.FAIL)
					return false;
			}
		}
		return true;
	}
}