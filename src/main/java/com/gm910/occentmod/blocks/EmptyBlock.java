package com.gm910.occentmod.blocks;

import com.gm910.occentmod.init.TileInit;

import net.minecraft.block.AirBlock;
import net.minecraft.block.Block;
import net.minecraft.block.BlockRenderType;
import net.minecraft.block.BlockState;
import net.minecraft.block.IBucketPickupHandler;
import net.minecraft.block.ILiquidContainer;
import net.minecraft.block.IWaterLoggable;
import net.minecraft.block.material.Material;
import net.minecraft.block.material.MaterialColor;
import net.minecraft.fluid.Fluid;
import net.minecraft.fluid.FluidState;
import net.minecraft.fluid.Fluids;
import net.minecraft.fluid.IFluidState;
import net.minecraft.item.BlockItemUseContext;
import net.minecraft.state.BooleanProperty;
import net.minecraft.state.properties.BlockStateProperties;
import net.minecraft.tileentity.TileEntity;
import net.minecraft.util.Direction;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.shapes.ISelectionContext;
import net.minecraft.util.math.shapes.VoxelShape;
import net.minecraft.util.math.shapes.VoxelShapes;
import net.minecraft.world.IBlockReader;
import net.minecraft.world.IWorld;
import net.minecraft.world.IWorldReader;

public class EmptyBlock extends ModBlock implements IBucketPickupHandler, ILiquidContainer {

	
	public EmptyBlock() {
		super(Block.Properties.create(Material.AIR, MaterialColor.AIR).doesNotBlockMovement().noDrops());
		//this.addTileEntity(() -> TileInit.BRENNISTEINVAETTR.get(), null);
		
	}
	
	@Override
	public VoxelShape getShape(BlockState state, IBlockReader worldIn, BlockPos pos, ISelectionContext context) {
		// TODO Auto-generated method stub
		return  VoxelShapes.empty();
	}
	
	public BlockRenderType getRenderType(BlockState state) {
	      return BlockRenderType.INVISIBLE;
	   }
	
	@Override
	public boolean isReplaceable(BlockState p_225541_1_, Fluid p_225541_2_) {
		// TODO Auto-generated method stub
		return false;
	}
	
	@Override
	public boolean isReplaceable(BlockState state, BlockItemUseContext useContext) {
		// TODO Auto-generated method stub
		return false;
	}
	
	@Override
	public boolean isAir(BlockState state, IBlockReader world, BlockPos pos) {
		// TODO Auto-generated method stub
		return false;
	}
	
	@Override
	public boolean isAir(BlockState state) {
		// TODO Auto-generated method stub
		return false;
	}
	
	@Override
	public boolean isFlammable(BlockState state, IBlockReader world, BlockPos pos, Direction face) {
		// TODO Auto-generated method stub
		return false;
	}
	
	
	
	public boolean canContainFluid(IBlockReader worldIn, BlockPos pos, BlockState state, Fluid fluidIn) {
	      return true;
	   }

	   public boolean receiveFluid(IWorld worldIn, BlockPos pos, BlockState state, IFluidState fluidStateIn) {
	         if (!worldIn.isRemote()) {
	            worldIn.getPendingFluidTicks().scheduleTick(pos, fluidStateIn.getFluid(), fluidStateIn.getFluid().getTickRate(worldIn));
	         }

	         return true;
	   }

	   public Fluid pickupFluid(IWorld worldIn, BlockPos pos, BlockState state) {
		   
		   worldIn.getPendingFluidTicks().scheduleTick(pos, Fluids.EMPTY, Fluids.EMPTY.getFluid().getTickRate(worldIn));
		   return state.getFluidState().getFluid();
	      
	   }
	
	public static class EmptyTile extends TileEntity {
		
		public EmptyTile() {
			super(TileInit.BRENNISTEINVAETTR.get());
		}
		
	}

}
