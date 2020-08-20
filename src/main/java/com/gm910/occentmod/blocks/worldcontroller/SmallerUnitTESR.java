package com.gm910.occentmod.blocks.worldcontroller;

import java.util.ArrayList;
import java.util.Random;

import com.gm910.occentmod.api.util.BlockInfo;
import com.mojang.blaze3d.matrix.MatrixStack;
import com.mojang.blaze3d.systems.RenderSystem;
import com.mojang.blaze3d.vertex.IVertexBuilder;

import net.minecraft.block.BlockRenderType;
import net.minecraft.block.BlockState;
import net.minecraft.client.Minecraft;
import net.minecraft.client.renderer.IRenderTypeBuffer;
import net.minecraft.client.renderer.LightTexture;
import net.minecraft.client.renderer.Matrix4f;
import net.minecraft.client.renderer.RenderType;
import net.minecraft.client.renderer.RenderTypeLookup;
import net.minecraft.client.renderer.WorldRenderer;
import net.minecraft.client.renderer.model.BakedQuad;
import net.minecraft.client.renderer.model.IBakedModel;
import net.minecraft.client.renderer.tileentity.TileEntityRenderer;
import net.minecraft.client.renderer.tileentity.TileEntityRendererDispatcher;
import net.minecraft.crash.ReportedException;
import net.minecraft.item.Item;
import net.minecraft.item.ItemStack;
import net.minecraft.tileentity.TileEntity;
import net.minecraft.util.Direction;
import net.minecraft.util.math.AxisAlignedBB;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.BlockRayTraceResult;
import net.minecraft.util.math.Vec3d;
import net.minecraft.util.math.shapes.VoxelShape;
import net.minecraft.world.LightType;

public class SmallerUnitTESR extends TileEntityRenderer<WorldControllerTileEntity> {
	private static SmallerUnitTESR INSTANCE;

	public SmallerUnitTESR(TileEntityRendererDispatcher rendererDispatcherIn) {
		super(rendererDispatcherIn);
		INSTANCE = this;
	}

	public static SmallerUnitTESR getINSTANCE() {
		return INSTANCE;
	}

	@Override
	public void render(WorldControllerTileEntity tileEntityIn, float partialTicks, MatrixStack matrixStackIn,
			IRenderTypeBuffer bufferIn, int combinedLightIn, int combinedOverlayIn) {
		if (true) {
			matrixStackIn.push();
			int sc = tileEntityIn.serializeNBT().getInt("upb");
			if (sc == 0) {
				sc = 4;
			}
			int packedlight = combinedLightIn;
			if (tileEntityIn.hasWorld()) {
				packedlight = ((tileEntityIn.getWorld().getLightFor(LightType.SKY, tileEntityIn.getPos()) << 20)
						| (tileEntityIn.getWorld().getLightFor(LightType.BLOCK, tileEntityIn.getPos()) << 4));
			}
			matrixStackIn.scale(1f / sc, 1f / sc, 1f / sc);
			for (BlockPos pos : tileEntityIn.unitHashMap.keySet()) {
				BlockInfo unit = tileEntityIn.unitHashMap.get(pos);
				matrixStackIn.push();
				BlockState state = unit.getState();
				matrixStackIn.translate(pos.getX(), pos.getY(), pos.getZ());
				int light = tileEntityIn.hasWorld() ? LightTexture.packLight(
						tileEntityIn.getContainedWorld()
								.getLightValue(new BlockPos(pos.getX(), pos.getY(), pos.getZ())),
						tileEntityIn.getContainedWorld().getSkylightSubtracted()) : combinedLightIn;
				if (state.getRenderType().equals(BlockRenderType.MODEL)
						|| state.getRenderType().equals(BlockRenderType.ENTITYBLOCK_ANIMATED)
						|| state.getRenderType().equals(BlockRenderType.INVISIBLE))
					if (tileEntityIn.getContainedWorld()
							.getTileEntity(new BlockPos(pos.getX(), pos.getY(), pos.getZ())) != null) {
						matrixStackIn.push();
						try {
							TileEntity renderTE = tileEntityIn.getContainedWorld()
									.getTileEntity(new BlockPos(pos.getX(), pos.getY(), pos.getZ()));
							renderTE.setWorldAndPos(tileEntityIn.getContainedWorld(),
									new BlockPos(pos.getX(), pos.getY(), pos.getZ()));
							if (TileEntityRendererDispatcher.instance.getRenderer(renderTE) != null)
								TileEntityRendererDispatcher.instance.getRenderer(renderTE).render(renderTE,
										partialTicks, matrixStackIn, bufferIn, light, combinedOverlayIn);
						} catch (NullPointerException ignored) {
						} catch (Throwable err) {
							StringBuilder errmsg = new StringBuilder("\n");
							errmsg.append(err.toString()).append('\n');
							for (StackTraceElement element : err.getStackTrace())
								errmsg.append(element.toString()).append('\n');
							System.out.println(errmsg.toString());
						}
						matrixStackIn.pop();
					}
				try {
					if (state.getRenderType().equals(BlockRenderType.MODEL)) {
						ArrayList<BakedQuad> qds = new ArrayList<>();
						IBakedModel mdl = Minecraft.getInstance().getBlockRendererDispatcher().getBlockModelShapes()
								.getModel(state);
						for (Direction dir : Direction.values())
							if ((!tileEntityIn.getContainedWorld()
									.getBlockState(new BlockPos(pos.getX(), pos.getY(), pos.getZ()).offset(dir))
									.isSolidSide(tileEntityIn.getContainedWorld(),
											new BlockPos(pos.getX(), pos.getY(), pos.getZ()).offset(dir), dir))
									|| (!(RenderTypeLookup
											.getRenderType(tileEntityIn.getContainedWorld().getBlockState(
													new BlockPos(pos.getX(), pos.getY(), pos.getZ()).offset(dir)))
											.equals(RenderType.getSolid())))) {
								if (RenderTypeLookup.getRenderType(state).equals(
										(RenderTypeLookup.getRenderType(tileEntityIn.getContainedWorld().getBlockState(
												new BlockPos(pos.getX(), pos.getY(), pos.getZ()).offset(dir)))))) {
									if (!state.equals(tileEntityIn.getContainedWorld().getBlockState(
											new BlockPos(pos.getX(), pos.getY(), pos.getZ()).offset(dir)))) {
										if (RenderTypeLookup.getRenderType(state).equals(RenderType.getTranslucent())
												&& tileEntityIn.getContainedWorld()
														.getBlockState(new BlockPos(pos.getX(), pos.getY(), pos.getZ())
																.offset(dir))
														.isSolidSide(tileEntityIn.getContainedWorld(),
																new BlockPos(pos.getX(), pos.getY(), pos.getZ())
																		.offset(dir),
																dir)) {
										} else {
											qds.addAll(mdl.getQuads(state, dir, new Random(
													new BlockPos(pos.getX(), pos.getY(), pos.getZ()).toLong())));
										}
									}
								} else {
									qds.addAll(mdl.getQuads(state, dir,
											new Random(new BlockPos(pos.getX(), pos.getY(), pos.getZ()).toLong())));
								}
							}
						qds.addAll(mdl.getQuads(state, null,
								new Random(new BlockPos(pos.getX(), pos.getY(), pos.getZ()).toLong())));
//						if (true||state.getShape(tileEntityIn.containedWorld,new BlockPos(pos.getX(),pos.getY(),pos.getZ())).equals(VoxelShapes.create(0,0,0,1,1,1))&&tileEntityIn.useManual) {
//							Minecraft.getInstance().getItemRenderer().renderQuads(matrixStackIn,bufferIn.getBuffer(RenderTypeLookup.getRenderType(state)),qds,new ItemStack(Item.getItemFromBlock(state.getBlock())),light, combinedOverlayIn);
						ItemStack itemStackIn = new ItemStack(Item.getItemFromBlock(state.getBlock()));
						boolean flag = !itemStackIn.isEmpty();
						MatrixStack.Entry matrixstack$entry = matrixStackIn.getLast();

						for (BakedQuad bakedquad : qds) {
							int i = -1;
							if (flag && bakedquad.hasTintIndex())
								i = Minecraft.getInstance().getBlockColors().getColor(state,
										tileEntityIn.getContainedWorld(),
										new BlockPos(pos.getX(), pos.getY(), pos.getZ()), bakedquad.getTintIndex());

							float f = (float) (i >> 16 & 255) / 255.0F;
							float f1 = (float) (i >> 8 & 255) / 255.0F;
							float f2 = (float) (i & 255) / 255.0F;
							bufferIn.getBuffer(RenderTypeLookup.getRenderType(state)).addVertexData(matrixstack$entry,
									bakedquad, f, f1, f2, light, combinedOverlayIn, true);
						}
//						}
						if (tileEntityIn.isEnchanted)
							Minecraft.getInstance().getBlockRendererDispatcher().renderModel(state,
									new BlockPos(pos.getX(), pos.getY(), pos.getZ()), tileEntityIn.getContainedWorld(),
									matrixStackIn, bufferIn.getBuffer(RenderType.getGlint()), true,
									new Random(new BlockPos(pos.getX(), pos.getY(), pos.getZ()).toLong()),
									net.minecraftforge.client.model.data.EmptyModelData.INSTANCE);
//						IFluidState fluidState=Blocks.WATER.getDefaultState().getFluidState();
//						Minecraft.getInstance().getBlockRendererDispatcher().renderFluid(new BlockPos(pos.getX(),pos.getY(),pos.getZ()),tileEntityIn.containedWorld,bufferIn.getBuffer(RenderTypeLookup.getRenderType(fluidState)),fluidState);
					}
				} catch (Throwable err) {
					Minecraft.getInstance().getBlockRendererDispatcher().renderBlock(state, matrixStackIn, bufferIn,
							packedlight, combinedOverlayIn);
					if (err instanceof ReportedException) {
						System.out.println(((ReportedException) err).getCrashReport().getCompleteReport());
					} else {
						StringBuilder errmsg = new StringBuilder("\n");
						errmsg.append(err.toString()).append('\n');
						for (StackTraceElement element : err.getStackTrace()) {
							errmsg.append(element.toString()).append('\n');
						}
						System.out.println(errmsg.toString());
					}
				}
				matrixStackIn.pop();
			}
			matrixStackIn.pop();
		}
		if (Minecraft.getInstance().gameSettings.showDebugInfo) {
			matrixStackIn.push();
			try {
				RenderSystem.enableDepthTest();
				VoxelShape shape = tileEntityIn.getBlockState().getBlock().getCollisionShape(
						tileEntityIn.getBlockState(), tileEntityIn.getWorld(), tileEntityIn.getPos(), null);
				for (AxisAlignedBB box : shape.toBoundingBoxList()) {
					WorldRenderer.drawBoundingBox(matrixStackIn, bufferIn.getBuffer(RenderType.getLines()), box, 1, 0,
							0, 1);
				}
				shape = tileEntityIn.getBlockState().getBlock().getShape(tileEntityIn.getBlockState(),
						tileEntityIn.getWorld(), tileEntityIn.getPos(), null);
				for (AxisAlignedBB box : shape.toBoundingBoxList()) {
					WorldRenderer.drawBoundingBox(matrixStackIn, bufferIn.getBuffer(RenderType.getLines()), box, 0, 0,
							1, 1);
				}
				shape = tileEntityIn.getBlockState().getBlock().getRaytraceShape(tileEntityIn.getBlockState(),
						tileEntityIn.getWorld(), tileEntityIn.getPos());
				for (AxisAlignedBB box : shape.toBoundingBoxList()) {
					WorldRenderer.drawBoundingBox(matrixStackIn, bufferIn.getBuffer(RenderType.getLines()), box, 0, 1,
							0, 1);
				}
				BlockRayTraceResult result = (shape.rayTrace(
						Minecraft.getInstance().player.getEyePosition(0), Minecraft.getInstance().player
								.getEyePosition(0).add(Minecraft.getInstance().player.getLookVec().scale(9)),
						tileEntityIn.getPos()));
				if (result != null) {
					float size = 0.001f;
					Vec3d hit = result.getHitVec();
					hit = hit.subtract(new Vec3d(tileEntityIn.getPos()));
					WorldRenderer.drawBoundingBox(matrixStackIn, bufferIn.getBuffer(RenderType.getLines()),
							new AxisAlignedBB(hit.x - size, hit.y - size, hit.z - size, hit.x + size, hit.y + size,
									hit.z + size),
							0, 0, 0, 1);
				}
				shape = tileEntityIn.getBlockState().getBlock().getRenderShape(tileEntityIn.getBlockState(),
						tileEntityIn.getWorld(), tileEntityIn.getPos());
				for (AxisAlignedBB box : shape.toBoundingBoxList()) {
					WorldRenderer.drawBoundingBox(matrixStackIn, bufferIn.getBuffer(RenderType.getLines()), box, 1, 0,
							1, 1);
				}
			} catch (Exception ignored) {
			}
			matrixStackIn.pop();
		}
	}

	private static void drawShape(MatrixStack matrixStackIn, IVertexBuilder bufferIn, VoxelShape shapeIn, double xIn,
			double yIn, double zIn, float red, float green, float blue, float alpha) {
		Matrix4f matrix4f = matrixStackIn.getLast().getMatrix();
		shapeIn.forEachEdge((p_230013_12_, p_230013_14_, p_230013_16_, p_230013_18_, p_230013_20_, p_230013_22_) -> {
			bufferIn.pos(matrix4f, (float) (p_230013_12_ + xIn), (float) (p_230013_14_ + yIn),
					(float) (p_230013_16_ + zIn)).color(red, green, blue, alpha).endVertex();
			bufferIn.pos(matrix4f, (float) (p_230013_18_ + xIn), (float) (p_230013_20_ + yIn),
					(float) (p_230013_22_ + zIn)).color(red, green, blue, alpha).endVertex();
		});
	}
}