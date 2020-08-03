package com.gm910.occentmod.api.networking.messages;

import java.util.List;
import java.util.function.Supplier;

import javax.annotation.Nullable;

import com.gm910.occentmod.api.networking.TaskEvent;

import net.minecraft.client.Minecraft;
import net.minecraft.entity.Entity;
import net.minecraft.entity.player.ServerPlayerEntity;
import net.minecraft.nbt.CompoundNBT;
import net.minecraft.network.NetworkManager;
import net.minecraft.network.PacketBuffer;
import net.minecraft.world.chunk.Chunk;
import net.minecraft.world.dimension.DimensionType;
import net.minecraftforge.common.MinecraftForge;
import net.minecraftforge.fml.LogicalSide;
import net.minecraftforge.fml.network.NetworkEvent;
import net.minecraftforge.fml.network.PacketDistributor;

public class Networking {

	public static void sendToServer(TaskMessage msg) {
		ModChannels.INSTANCE.sendToServer(msg);
	}

	public static void sendToPlayer(TaskMessage msg, ServerPlayerEntity play) {
		ModChannels.INSTANCE.send(PacketDistributor.PLAYER.with(() -> play), msg);
	}

	/**
	 * Send to all tracking this chunk
	 * 
	 * @param msg
	 * @param play
	 */
	public static void sendToChunk(TaskMessage msg, Chunk play) {
		ModChannels.INSTANCE.send(PacketDistributor.TRACKING_CHUNK.with(() -> play), msg);
	}

	/**
	 * Sends to all clients
	 * 
	 * @param msg
	 */
	public static void sendToAll(TaskMessage msg) {
		ModChannels.INSTANCE.send(PacketDistributor.ALL.noArg(), msg);
	}

	/**
	 * Sends to all in dimension
	 * 
	 * @param msg
	 * @param dim
	 */
	public static void sendToDim(TaskMessage msg, DimensionType dim) {
		ModChannels.INSTANCE.send(PacketDistributor.DIMENSION.with(() -> dim), msg);
	}

	/**
	 * Sends to all in the area specified by the x, y, z, dimension, and radius
	 * 
	 * @param msg
	 * @param x
	 * @param y
	 * @param z
	 * @param dim
	 * @param radius
	 * @param excluded
	 */
	public static void sendNear(TaskMessage msg, double x, double y, double z, DimensionType dim, int radius,
			@Nullable ServerPlayerEntity excluded) {
		ModChannels.INSTANCE.send(
				PacketDistributor.NEAR.with(() -> new PacketDistributor.TargetPoint(excluded, x, y, z, radius, dim)),
				msg);
	}

	/**
	 * Sends to the network managers in the list
	 * 
	 * @param msg
	 * @param ls
	 */
	public static void sendToNMList(TaskMessage msg, List<NetworkManager> ls) {
		ModChannels.INSTANCE.send(PacketDistributor.NMLIST.with(() -> ls), msg);
	}

	/**
	 * Sends to all tracking this entity
	 * 
	 * @param msg
	 * @param en
	 */
	public static void sendToTracking(TaskMessage msg, Entity en) {
		ModChannels.INSTANCE.send(PacketDistributor.TRACKING_ENTITY.with(() -> en), msg);
	}

	/**
	 * Sends to all tracking this player and the player itself
	 * 
	 * @param msg
	 * @param en
	 */
	public static void sendToTrackingAndSelf(TaskMessage msg, ServerPlayerEntity en) {
		ModChannels.INSTANCE.send(PacketDistributor.TRACKING_ENTITY_AND_SELF.with(() -> en), msg);
	}

	public static void sendToServer(ModTask msg) {
		ModChannels.INSTANCE.sendToServer(new TaskMessage(msg));
	}

	public static void sendToPlayer(ModTask msg, ServerPlayerEntity play) {
		ModChannels.INSTANCE.send(PacketDistributor.PLAYER.with(() -> play), new TaskMessage(msg));
	}

	/**
	 * Send to all tracking this chunk
	 * 
	 * @param msg
	 * @param play
	 */
	public static void sendToChunk(ModTask msg, Chunk play) {
		ModChannels.INSTANCE.send(PacketDistributor.TRACKING_CHUNK.with(() -> play), new TaskMessage(msg));
	}

	/**
	 * Sends to all clients
	 * 
	 * @param msg
	 */
	public static void sendToAll(ModTask msg) {
		ModChannels.INSTANCE.send(PacketDistributor.ALL.noArg(), new TaskMessage(msg));
	}

	/**
	 * Sends to all in dimension
	 * 
	 * @param msg
	 * @param dim
	 */
	public static void sendToDim(ModTask msg, DimensionType dim) {
		ModChannels.INSTANCE.send(PacketDistributor.DIMENSION.with(() -> dim), new TaskMessage(msg));
	}

	/**
	 * Sends to all in the area specified by the x, y, z, dimension, and radius
	 * 
	 * @param msg
	 * @param x
	 * @param y
	 * @param z
	 * @param dim
	 * @param radius
	 * @param excluded
	 */
	public static void sendNear(ModTask msg, double x, double y, double z, DimensionType dim, int radius,
			@Nullable ServerPlayerEntity excluded) {
		ModChannels.INSTANCE.send(
				PacketDistributor.NEAR.with(() -> new PacketDistributor.TargetPoint(excluded, x, y, z, radius, dim)),
				new TaskMessage(msg));
	}

	/**
	 * Sends to the network managers in the list
	 * 
	 * @param msg
	 * @param ls
	 */
	public static void sendToNMList(ModTask msg, List<NetworkManager> ls) {
		ModChannels.INSTANCE.send(PacketDistributor.NMLIST.with(() -> ls), new TaskMessage(msg));
	}

	/**
	 * Sends to all tracking this entity
	 * 
	 * @param msg
	 * @param en
	 */
	public static void sendToTracking(ModTask msg, Entity en) {
		ModChannels.INSTANCE.send(PacketDistributor.TRACKING_ENTITY.with(() -> en), new TaskMessage(msg));
	}

	/**
	 * Sends to all tracking this player and the player itself
	 * 
	 * @param msg
	 * @param en
	 */
	public static void sendToTrackingAndSelf(ModTask msg, ServerPlayerEntity en) {
		ModChannels.INSTANCE.send(PacketDistributor.TRACKING_ENTITY_AND_SELF.with(() -> en), new TaskMessage(msg));
	}

	public static TaskMessage task(ModTask task) {
		return new TaskMessage(task);
	}

	public static class TaskMessage {

		private CompoundNBT nbt = null;

		public TaskMessage(ModTask data) {
			this.nbt = ModTask.writeToNBT(data);
		}

		public TaskMessage(CompoundNBT data) {
			this.nbt = data;
		}

		public void encode(PacketBuffer buf) {
			// System.out.println("Encoding message " + ModTask.getFromNBT(nbt));
			if (nbt != null && !nbt.isEmpty())
				buf.writeCompoundTag(nbt);
		}

		public void handle(Supplier<NetworkEvent.Context> context) {
			NetworkEvent.Context ctxt = context.get();
			LogicalSide from = ctxt.getDirection().getOriginationSide();
			LogicalSide to = ctxt.getDirection().getReceptionSide();

			ModTask tasque = ModTask.getFromNBT(nbt);
			if (tasque == null) {
				System.out.println("Could not decode task sent from " + from + " in format " + nbt);
			} else {

				if (tasque.isLClient() && to.isClient() || tasque.isLServer() && to.isServer()) {
					// System.out.println("Handling task sent from " + from + " on " + to + ": " +
					// tasque);
					TaskEvent e = tasque.createEvent();
					if (e.getSource() != null) {

						e.getTask().setWorldRef(e.getSource().getServerWorld());
						e.getTask().setSender(e.getSource());
					} else {
						e.getTask().setWorldRef(Minecraft.getInstance().world);

					}
					e.setSource(ctxt.getSender());
					ctxt.enqueueWork(() -> {
						if (!MinecraftForge.EVENT_BUS.post(e)) {
							tasque.run();
						}

					});
				} else {
					String s = "Suppressed task " + tasque + " because it was sent to wrong side";
					System.out.println(s);
					ctxt.enqueueWork(() -> {
						System.out.println(s);
					});
				}
			}
		}

		public static TaskMessage fromBuffer(PacketBuffer buf) {
			return new TaskMessage(buf.readCompoundTag());
		}

	}
}
