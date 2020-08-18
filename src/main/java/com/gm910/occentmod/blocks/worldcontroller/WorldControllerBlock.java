package com.gm910.occentmod.blocks.worldcontroller;

import java.util.Random;
import java.util.concurrent.atomic.AtomicBoolean;
import java.util.function.BiFunction;

import javax.annotation.Nullable;

import com.gm910.occentmod.api.util.BlockInfo;
import com.gm910.occentmod.blocks.ModBlock;

import net.minecraft.block.Block;
import net.minecraft.block.BlockState;
import net.minecraft.block.Blocks;
import net.minecraft.block.material.Material;
import net.minecraft.client.Minecraft;
import net.minecraft.entity.Entity;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.fluid.IFluidState;
import net.minecraft.item.Item;
import net.minecraft.item.ItemStack;
import net.minecraft.item.ItemUseContext;
import net.minecraft.tileentity.TileEntity;
import net.minecraft.util.ActionResultType;
import net.minecraft.util.Hand;
import net.minecraft.util.math.AxisAlignedBB;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.BlockRayTraceResult;
import net.minecraft.util.math.RayTraceResult;
import net.minecraft.util.math.Vec3d;
import net.minecraft.util.math.shapes.IBooleanFunction;
import net.minecraft.util.math.shapes.ISelectionContext;
import net.minecraft.util.math.shapes.VoxelShape;
import net.minecraft.util.math.shapes.VoxelShapes;
import net.minecraft.world.GameType;
import net.minecraft.world.IBlockReader;
import net.minecraft.world.TickPriority;
import net.minecraft.world.World;
import net.minecraft.world.server.ServerWorld;
import net.minecraftforge.common.util.FakePlayer;

/**
 * Copied from Small Units mod
 * 
 * @author borah
 *
 */
public class WorldControllerBlock extends ModBlock {

	public WorldControllerBlock() {
		super(Block.Properties.create(Material.ROCK).notSolid().noDrops());
	}

	@Override
	public VoxelShape getCollisionShape(BlockState state, IBlockReader worldIn, BlockPos pos,
			ISelectionContext context) {
		VoxelShape shape = Block.makeCuboidShape(0, 0, 0, 0, 0, 0);
		if (context != null) {
			Entity entity = context.getEntity();
			AxisAlignedBB bb1 = null;
			WorldControllerTileEntity te = (WorldControllerTileEntity) worldIn.getTileEntity(pos);
			if (entity != null && te != null) {
				bb1 = entity.getCollisionBox(entity);
				if (bb1 == null)
					bb1 = entity.getCollisionBoundingBox();
				if (bb1 == null)
					bb1 = entity.getBoundingBox();
				bb1 = bb1.offset(new Vec3d(pos).scale(-1));
				float expand = 0.2125f;
				expand = Math.max(expand, entity.getCollisionBorderSize() * expand);
				bb1 = bb1.grow(expand, expand, expand);
				bb1 = bb1.grow(entity.getCollisionBorderSize());
			}
			AxisAlignedBB finalBb = bb1;
			if (te != null) {
				World cWorld = te.getContainedWorld();
				for (BlockPos p : te.unitHashMap.keySet()) {
					BlockInfo u = te.unitHashMap.get(p);
					VoxelShape shape1 = u.getState().getCollisionShape(cWorld, p);
					AtomicBoolean collides = new AtomicBoolean(false);
					if (entity != null)
						shape1.toBoundingBoxList().forEach((b) -> {
							if (b != null) {
								b = new AxisAlignedBB(b.minX / te.upb, b.minY / te.upb, b.minZ / te.upb,
										b.maxX / te.upb, b.maxY / te.upb, b.maxZ / te.upb).offset(
												p.getX() / (float) te.upb, p.getY() / (float) te.upb,
												p.getZ() / (float) te.upb);
								if (checkCollision.apply(b, finalBb))
									collides.set(true);
							}
						});
					else
						collides.set(true);
					if (!shape1.isEmpty() && collides.get() && !u.getState().isAir())
						if (te.upb == 0)
							te.upb = 4;
					for (AxisAlignedBB bb : shape1.toBoundingBoxList())
						if (entity != null) {
							bb = new AxisAlignedBB(bb.minX / te.upb, bb.minY / te.upb, bb.minZ / te.upb,
									bb.maxX / te.upb, bb.maxY / te.upb, bb.maxZ / te.upb).offset(
											p.getX() / (float) te.upb, p.getY() / (float) te.upb,
											p.getZ() / (float) te.upb);
							if (checkCollision.apply(bb, bb1) || (bb.intersects(bb1) || bb.contains(bb1.getCenter())
									|| bb1.contains(bb.getCenter()) || context.func_216378_a(shape1, pos, true)))
								shape = VoxelShapes.combine(shape, VoxelShapes.create(bb), IBooleanFunction.OR);
						} else
							shape = VoxelShapes.combine(shape,
									VoxelShapes
											.create(bb.minX / te.upb, bb.minY / te.upb, bb.minZ / te.upb,
													bb.maxX / te.upb, bb.maxY / te.upb, bb.maxZ / te.upb)
											.withOffset(p.getX() / (float) te.upb, p.getY() / (float) te.upb,
													p.getZ() / (float) te.upb),
									IBooleanFunction.OR);
				}
			}
		} else {
			WorldControllerTileEntity te = (WorldControllerTileEntity) worldIn.getTileEntity(pos);
			if (te != null) {
				World cWorld = (World) te.getContainedWorld();
				for (BlockPos p : te.unitHashMap.keySet()) {
					BlockInfo u = te.unitHashMap.get(p);
					if (te.upb == 0)
						te.upb = 4;
					VoxelShape shape1 = u.getState().getCollisionShape(cWorld,
							new BlockPos(p.getX(), p.getY(), p.getZ()));
					if (!shape1.isEmpty() && !u.getState().isAir())
						for (AxisAlignedBB bb : shape1.toBoundingBoxList())
							shape = VoxelShapes.combine(shape,
									VoxelShapes
											.create(bb.minX / te.upb, bb.minY / te.upb, bb.minZ / te.upb,
													bb.maxX / te.upb, bb.maxY / te.upb, bb.maxZ / te.upb)
											.withOffset(p.getX() / (float) te.upb, p.getY() / (float) te.upb,
													p.getZ() / (float) te.upb),
									IBooleanFunction.OR);
				}
			}
		}
		return shape;
	}

	private static final BiFunction<AxisAlignedBB, AxisAlignedBB, Boolean> checkCollision = (a, b) -> b.intersects(a);

	@Override
	public VoxelShape getRaytraceShape(BlockState state, IBlockReader worldIn, BlockPos pos) {
		try {
			return getSelectedShape(worldIn, pos, new ISelectionContext() {
				@Override
				public boolean func_225581_b_() {
					return false;
				}

				@Override
				public boolean func_216378_a(VoxelShape shape, BlockPos pos, boolean p_216378_3_) {
					return false;
				}

				@Override
				public boolean hasItem(Item itemIn) {
					return false;
				}

				@Nullable
				@Override
				public Entity getEntity() {
					return Minecraft.getInstance().renderViewEntity;
				}
			});
		} catch (Throwable err) {
			VoxelShape shape = Block.makeCuboidShape(0, 0, 0, 0, 0, 0);
			try {
				WorldControllerTileEntity te = (WorldControllerTileEntity) worldIn.getTileEntity(pos);
				World cWorld = te.getContainedWorld();
				for (BlockPos p : te.unitHashMap.keySet()) {
					BlockInfo u = te.unitHashMap.get(p);
					for (AxisAlignedBB bb : u.getState().getShape(cWorld, new BlockPos(p.getX(), p.getY(), p.getZ()))
							.toBoundingBoxList()) {
						if (te.upb == 0) {
							te.upb = 4;
						}
						shape = VoxelShapes.combine(shape,
								VoxelShapes
										.create(bb.minX / te.upb, bb.minY / te.upb, bb.minZ / te.upb, bb.maxX / te.upb,
												bb.maxY / te.upb, bb.maxZ / te.upb)
										.withOffset(p.getX() / (float) te.upb, p.getY() / (float) te.upb,
												p.getZ() / (float) te.upb),
								IBooleanFunction.OR);
					}
				}
			} catch (Throwable ignored) {
			}
			if (shape.isEmpty()) {
				return VoxelShapes.create(0, 0, 0, 1, 1, 1);
			}
			return shape;
		}
	}

	@Override
	public VoxelShape getShape(BlockState state, IBlockReader worldIn, BlockPos pos, ISelectionContext context) {
		return getSelectedShape(worldIn, pos, context);
	}

	public VoxelShape getSelectedShape(IBlockReader worldIn, BlockPos pos, ISelectionContext context) {
		if (context.getEntity() != null)
			try {
				VoxelShape returnVal = Block.makeCuboidShape(0, 0, 0, 0.01, 0.01, 0.01);
				double distanceBest = 999999;
				Vec3d start = context.getEntity().getEyePosition(0);
				Vec3d stop = start.add(context.getEntity().getLookVec().scale(9));
				WorldControllerTileEntity te = (WorldControllerTileEntity) worldIn.getTileEntity(pos);
				if (te != null) {
					for (BlockPos p : te.unitHashMap.keySet()) {
						BlockInfo u = te.unitHashMap.get(p);
						try {
							double bestDist = 999999;
							VoxelShape shape = null;
							for (AxisAlignedBB bb : u.getState()
									.getShape(worldIn, new BlockPos(p.getX(), p.getY(), p.getZ()))
									.toBoundingBoxList()) {
								try {
									if (te.upb == 0) {
										te.upb = 4;
									}
									AxisAlignedBB newBox = new AxisAlignedBB(bb.minX / te.upb, bb.minY / te.upb,
											bb.minZ / te.upb, bb.maxX / te.upb, bb.maxY / te.upb, bb.maxZ / te.upb)
													.offset(p.getX() / (float) te.upb, p.getY() / (float) te.upb,
															p.getZ() / (float) te.upb);
									if (shape == null) {
										shape = VoxelShapes.create(newBox);
									} else {
										shape = VoxelShapes.combine(shape, VoxelShapes.create(newBox),
												IBooleanFunction.OR);
									}
									if (newBox.offset(pos).rayTrace(start, stop).isPresent()) {
										double thisDist = newBox.offset(pos).rayTrace(start, stop).get()
												.distanceTo(start);
										if (thisDist < bestDist) {
											bestDist = thisDist;
										}
									}
								} catch (Exception ignored) {
								}
							}
							if (shape != null && !shape.isEmpty()) {
								if (bestDist < distanceBest) {
									returnVal = shape;
									distanceBest = bestDist;
								}
							}
						} catch (Exception ignored) {
						}
					}
					if (returnVal.isEmpty()) {
						returnVal = VoxelShapes.combine(returnVal, Block.makeCuboidShape(0, 0, 0, 0.01, 0.01, 0.01),
								IBooleanFunction.OR);
					}
					return returnVal;

				}
			} catch (Exception ignored) {
			}
		return Block.makeCuboidShape(0, 0, 0, 0.01, 0.01, 0.01);
	}

	@Override
	public VoxelShape getRenderShape(BlockState state, IBlockReader worldIn, BlockPos pos) {
		return Block.makeCuboidShape(0, 0, 0, 0, 0, 0);
	}

	@Override
	public void onEntityCollision(BlockState state, World worldIn, BlockPos pos, Entity entityIn) {
		super.onEntityCollision(state, worldIn, pos, entityIn);
	}

	@Override
	public int getLightValue(BlockState state, IBlockReader world, BlockPos pos) {
		return 0;
	}

	@Override
	public ItemStack getPickBlock(BlockState state, RayTraceResult target, IBlockReader world, BlockPos pos,
			PlayerEntity player) {
		// ItemStack value = new ItemStack(Deferred.UNITITEM.get());
		// value.getOrCreateTag().put("BlockEntityTag",
		// world.getTileEntity(pos).serializeNBT());
		return ItemStack.EMPTY;
	}

	@Override
	public void tick(BlockState state, ServerWorld worldIn, BlockPos pos, Random rand) {
		super.tick(state, worldIn, pos,
				rand);/*
						if (worldIn.getTileEntity(pos) != null && worldIn.getTileEntity(pos) instanceof SmallerUnitsTileEntity)
						((SmallerUnitsTileEntity) worldIn.getTileEntity(pos)).containedDimType.tick(worldIn);*/
		worldIn.notifyBlockUpdate(pos, state, state, 0);
		worldIn.getPendingBlockTicks().scheduleTick(pos, this, 1, TickPriority.EXTREMELY_HIGH);
	}

	@Override
	public void onBlockAdded(BlockState state, World worldIn, BlockPos pos, BlockState oldState, boolean isMoving) {
		worldIn.getPendingBlockTicks().scheduleTick(pos, this, 1, TickPriority.EXTREMELY_HIGH);
		super.onBlockAdded(state, worldIn, pos, oldState, isMoving);
	}

	@Override
	public ActionResultType onBlockActivated(BlockState state, World worldIn, BlockPos pos, PlayerEntity player,
			Hand handIn, BlockRayTraceResult hit) {
		if (worldIn.isRemote)
			return ActionResultType.SUCCESS;
		try {
			WorldControllerTileEntity te = (WorldControllerTileEntity) worldIn.getTileEntity(pos);
			Vec3d blockpos = hit.getHitVec().subtract(new Vec3d(pos)).scale(te.upb);
			BlockState heldState = Block.getBlockFromItem(player.getHeldItem(handIn).getItem()).getDefaultState();
			if (!hit.getFace().getDirectionVec().toString().contains("-"))
				if (blockpos.getY() % 1 == 0)
					blockpos = blockpos.subtract(0, 1, 0);
				else if (blockpos.getX() % 1 == 0)
					blockpos = blockpos.subtract(1, 0, 0);
				else if (blockpos.getZ() % 1 == 0)
					blockpos = blockpos.subtract(0, 0, 1);
			BlockPos loc = new BlockPos(blockpos);
			try {
				heldState = Block.getBlockFromItem(player.getHeldItem(handIn).getItem()).getStateForPlacement(heldState,
						hit.getFace(), heldState, te.getContainedWorld(), new BlockPos(blockpos),
						loc.offset(hit.getFace()), handIn);
			} catch (Throwable ignored) {
			}
			try {
				BlockState clickedState = te.getContainedWorld().getBlockState(loc);
				VoxelShape shape = clickedState.getShape(te.getContainedWorld(), loc);
				if (!shape.isEmpty()) {
					if (clickedState.getBlock()
							.onBlockActivated(clickedState, te.getContainedWorld(), loc, player, handIn, hit)
							.equals(ActionResultType.PASS)) {
						if (!Block.getBlockFromItem(player.getHeldItem(handIn).getItem()).getDefaultState()
								.equals(Blocks.AIR.getDefaultState())) {
							loc = loc.offset(hit.getFace());
							FakePlayer fakePlayer = new FakePlayer((ServerWorld) te.getContainedWorld(),
									player.getGameProfile());
							fakePlayer.setPositionAndRotation(player.getPosX() - pos.getX(),
									player.getPosY() - pos.getY(), player.getPosZ() - pos.getZ(), player.rotationYaw,
									player.rotationPitch);
							fakePlayer.setRotationYawHead(player.getRotationYawHead());
							fakePlayer.setHeldItem(handIn, player.getHeldItem(handIn).copy());
							if (player.isCreative()) {
								fakePlayer.setGameType(GameType.CREATIVE);
							}
							Vec3d start = player.getEyePosition(0).subtract(player.getLookVec().scale(1))
									.subtract(new Vec3d(pos));
							Vec3d stop = start.add(player.getLookVec().scale(10));
							VoxelShape newShape = null;
							for (AxisAlignedBB bb : shape.toBoundingBoxList()) {
								if (newShape == null) {
									newShape = VoxelShapes.create(bb.shrink(te.upb).offset(loc));
								} else {
									newShape = VoxelShapes.combine(newShape,
											VoxelShapes.create(bb.shrink(te.upb).offset(loc)), IBooleanFunction.OR);
								}
							}
							BlockRayTraceResult result = (newShape.rayTrace(start.scale(te.upb), stop.scale(te.upb),
									loc.offset(hit.getFace().getOpposite())));
//							System.out.println(result);
//							System.out.println(start);
//							System.out.println(stop);
//							System.out.println(loc.offset(hit.getFace().getOpposite()));
							if (result != null) {
								try {
									result = result.withFace(hit.getFace());
									fakePlayer.getHeldItem(handIn)
											.onItemUse(new ItemUseContext(fakePlayer, handIn, result));
								} catch (Throwable err) {
									StringBuilder stack = new StringBuilder(
											"\n" + err.toString() + "(" + err.getMessage() + ")");
									for (StackTraceElement element : err.getStackTrace())
										stack.append(element.toString()).append("\n");
									System.out.println(stack.toString());
									System.out.println(result);
								}
							}
							if (te.getContainedWorld().getBlockState(loc).equals(Blocks.AIR.getDefaultState())) {
								te.getContainedWorld().setBlockState(loc, heldState);
							}
						}
					}
				}
			} catch (Throwable err) {
				if (!Block.getBlockFromItem(player.getHeldItem(handIn).getItem()).getDefaultState()
						.equals(Blocks.AIR.getDefaultState())) {
					BlockState clickedState = te.getContainedWorld()
							.getBlockState(loc.offset(hit.getFace().getOpposite()));
					VoxelShape shape = clickedState.getShape(te.getContainedWorld(),
							loc.offset(hit.getFace().getOpposite()));
					Vec3d start = player.getEyePosition(0).subtract(new Vec3d(pos));
					Vec3d stop = start.add(player.getLookVec().scale(9));
					BlockRayTraceResult result = (shape.rayTrace(start, stop, loc));
					try {
						if (result != null)
							player.getHeldItem(handIn).onItemUse(new ItemUseContext(player, handIn, result));
						else
							te.getContainedWorld().setBlockState(loc, heldState.updatePostPlacement(hit.getFace(),
									heldState, te.getContainedWorld(), loc, loc), 0);
					} catch (Throwable ignored) {
						StringBuilder stack = new StringBuilder("\n" + err.toString() + "(" + err.getMessage() + ")");
						for (StackTraceElement element : err.getStackTrace())
							stack.append(element.toString()).append("\n");
						System.out.println(stack.toString());
					}
				}
				StringBuilder stack = new StringBuilder("\n" + err.toString() + "(" + err.getMessage() + ")");
				for (StackTraceElement element : err.getStackTrace())
					stack.append(element.toString()).append("\n");
				System.out.println(stack.toString());
			}
			try {
				TileEntity tileEntity = te.getContainedWorld().getBlockState(loc)
						.createTileEntity(te.getContainedWorld());
				if (tileEntity != null) {
					if (player.getHeldItem(handIn).getOrCreateTag().contains("BlockEntityTag")) {
						tileEntity.read(player.getHeldItem(handIn).getOrCreateTag().getCompound("BlockEntityTag"));
						tileEntity.setPos(loc);
					}
					te.getContainedWorld().setTileEntity(loc, tileEntity);
				}
			} catch (Throwable err2) {
			}
			te.markDirty();
			worldIn.notifyBlockUpdate(pos, state, state, 0);
		} catch (Throwable err) {
			StringBuilder stack = new StringBuilder("\n" + err.toString() + "(" + err.getMessage() + ")");
			for (StackTraceElement element : err.getStackTrace())
				stack.append(element.toString()).append("\n");
			System.out.println(stack.toString());
		}
		return ActionResultType.SUCCESS;
	}

	@Override
	public boolean removedByPlayer(BlockState state, World world, BlockPos pos, PlayerEntity player,
			boolean willHarvest, IFluidState fluid) {
		if (world.isRemote) {
			return false;
		}
		try {
			WorldControllerTileEntity te = (WorldControllerTileEntity) world.getTileEntity(pos);
			BlockRayTraceResult hit = this.getRaytraceShape(state, world, pos).rayTrace(
					player.getEyePosition(0).subtract(player.getLookVec()),
					player.getEyePosition(0).add(player.getLookVec().scale(8)), pos);
			Vec3d blockpos = hit.getHitVec().subtract(new Vec3d(pos)).scale(te.upb);
			if (!hit.getFace().getDirectionVec().toString().contains("-")) {
				if (blockpos.getY() % 1 == 0) {
					blockpos = blockpos.subtract(0, 1, 0);
				} else if (blockpos.getX() % 1 == 0) {
					blockpos = blockpos.subtract(1, 0, 0);
				} else if (blockpos.getZ() % 1 == 0) {
					blockpos = blockpos.subtract(0, 0, 1);
				}
			}
			BlockPos loc = new BlockPos(blockpos);
			te.getContainedWorld().setBlockState(loc, Blocks.AIR.getDefaultState(), 0);
			world.notifyBlockUpdate(pos, state, state, 0);
		} catch (Throwable ignored) {
		}
		return false;

	}

	@Nullable
	@Override
	public TileEntity createTileEntity(BlockState state, IBlockReader world) {
		return new WorldControllerTileEntity();
	}
}
