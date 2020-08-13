package com.gm910.occentmod.blocks;

import java.util.Random;

import net.minecraft.block.Block;
import net.minecraft.block.BlockRenderType;
import net.minecraft.block.BlockState;
import net.minecraft.block.DirectionalBlock;
import net.minecraft.block.IBeaconBeamColorProvider;
import net.minecraft.block.SoundType;
import net.minecraft.block.material.Material;
import net.minecraft.block.material.PushReaction;
import net.minecraft.entity.Entity;
import net.minecraft.item.DyeColor;
import net.minecraft.state.DirectionProperty;
import net.minecraft.state.StateContainer;
import net.minecraft.util.Direction;
import net.minecraft.util.Mirror;
import net.minecraft.util.Rotation;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.Vec3d;
import net.minecraft.world.IBlockReader;
import net.minecraft.world.World;
import net.minecraftforge.api.distmarker.Dist;
import net.minecraftforge.api.distmarker.OnlyIn;

public class MindJellyBlock extends ModBlock implements IBeaconBeamColorProvider {
	public static final DirectionProperty FACING = DirectionalBlock.FACING;

	public MindJellyBlock() {
		super(Properties.create(Material.CLAY).lightValue(1).noDrops().sound(SoundType.SLIME).doesNotBlockMovement()
				.notSolid());
		this.setDefaultState(this.stateContainer.getBaseState().with(FACING, Direction.NORTH));
	}

	@OnlyIn(Dist.CLIENT)
	public boolean isSideInvisible(BlockState state, BlockState adjacentBlockState, Direction side) {
		return adjacentBlockState.getBlock() == this ? true : super.isSideInvisible(state, adjacentBlockState, side);
	}

	@Override
	public boolean isTransparent(BlockState state) {
		// TODO Auto-generated method stub
		return true;
	}

	@Override
	public BlockRenderType getRenderType(BlockState state) {
		// TODO Auto-generated method stub
		return BlockRenderType.MODEL;
	}

	public boolean isNormalCube(BlockState state, IBlockReader worldIn, BlockPos pos) {
		return false;
	}

	@OnlyIn(Dist.CLIENT)
	public float getAmbientOcclusionLightValue(BlockState state, IBlockReader worldIn, BlockPos pos) {
		return 1.0F;
	}

	public boolean propagatesSkylightDown(BlockState state, IBlockReader reader, BlockPos pos) {
		return true;
	}

	public void onEntityCollision(BlockState state, World worldIn, BlockPos pos, Entity entityIn) {
		entityIn.setMotionMultiplier(state, new Vec3d(0.25D, (double) 0.05F, 0.25D));
	}

	public PushReaction getPushReaction(BlockState state) {
		return PushReaction.NORMAL;
	}

	@Override
	public boolean isStickyBlock(BlockState state) {
		return true;
	}

	public BlockState rotate(BlockState state, Rotation rot) {
		return state.with(FACING, rot.rotate(state.get(FACING)));
	}

	public BlockState mirror(BlockState state, Mirror mirrorIn) {
		return state.rotate(mirrorIn.toRotation(state.get(FACING)));
	}

	public void fillStateContainer(StateContainer.Builder<Block, BlockState> builder) {
		builder.add(FACING);
	}

	/*@Override
	public BlockState updatePostPlacement(BlockState stateIn, Direction facing, BlockState facingState, IWorld worldIn,
			BlockPos currentPos, BlockPos facingPos) {
		return stateIn.with(FACING,
				Direction.values()[new Random(currentPos.getX() + currentPos.getY() + currentPos.getZ())
						.nextInt(Direction.values().length)]);
	}*/

	@Override
	public DyeColor getColor() {
		DyeColor[] d = { DyeColor.GRAY, DyeColor.LIGHT_GRAY, DyeColor.WHITE, DyeColor.LIGHT_BLUE };
		return d[new Random().nextInt(d.length)];
	}

}
