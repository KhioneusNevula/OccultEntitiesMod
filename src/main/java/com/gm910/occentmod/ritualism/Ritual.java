package com.gm910.occentmod.ritualism;

import java.util.HashMap;
import java.util.HashSet;
import java.util.Map;
import java.util.Set;
import java.util.UUID;

import com.google.common.collect.Maps;
import com.google.common.collect.Sets;
import com.mojang.datafixers.Dynamic;
import com.mojang.datafixers.types.DynamicOps;

import net.minecraft.nbt.CompoundNBT;
import net.minecraft.util.IDynamicSerializable;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.MathHelper;
import net.minecraft.world.server.ServerWorld;
import net.minecraftforge.common.capabilities.CapabilityProvider;

public class Ritual extends CapabilityProvider<Ritual> implements IDynamicSerializable {

	private ServerWorld worldOfOrigin;

	private UUID owner;

	private BlockPos centerBlock;

	private Map<BlockPos, RitualNode> nodes = new HashMap<>();

	private Set<RitualNode> nextNodes = Sets.newHashSet();

	private Set<RitualNode> previousNodes = Sets.newHashSet();

	private Map<RitualNode, CompoundNBT> transferredData = Maps.newHashMap();

	private Thread initThread;

	private UUID id = MathHelper.getRandomUUID();

	public Ritual(ServerWorld world, BlockPos centerBlock) {
		this(world);
		this.centerBlock = centerBlock;
	}

	public Ritual(ServerWorld world) {
		super(Ritual.class);
		this.worldOfOrigin = world;
		this.initThread = new Thread(this::initializeMethod);
		this.gatherCapabilities();
	}

	public <T> void deserialize(Dynamic<T> dyn) {

	}

	@Override
	public <T> T serialize(DynamicOps<T> ops) {
		return null;
	}

	public void initialize() {
		initThread.start();

	}

	public Thread getInitThread() {
		return initThread;
	}

	public void setInitThread(Thread initThread) {
		this.initThread = initThread;
	}

	public void reinitialize() {
		this.initThread = new Thread(this::initializeMethod);
		initThread.start();
	}

	private void initializeMethod() {

		Set<BlockPos> nextPositions = new HashSet<>();
		do {

		} while (!nextPositions.isEmpty());
	}

	public void tick() {

	}

	public void setOwner(UUID owner) {
		this.owner = owner;
	}

	public UUID getOwner() {
		return owner;
	}

	public UUID getId() {
		return id;
	}

	public void setCenterBlock(BlockPos centerBlock) {
		this.centerBlock = centerBlock;
	}

	public void setId(UUID id) {
		this.id = id;
	}

	public ServerWorld getWorldOfOrigin() {
		return worldOfOrigin;
	}

	/**
	 * whether the ritual shape determiner should add this block to its position map
	 * 
	 * @param pos
	 * @return
	 */
	public boolean isAppropriate(BlockPos pos) {

		return true;// TODO
	}

	public RitualNode getNode(BlockPos pos) {
		return nodes.get(pos);
	}

	/**
	 * Gets a node from the actual world as opposed to the internal ritual map
	 * 
	 * @param pos
	 * @return
	 */
	public RitualNode getNodeFromWorld(BlockPos pos) {
		return null;// TODO this.worldOfOrigin.getTileEntity(pos)..pos.
	}

	public BlockPos getPosition(RitualNode node) {
		for (BlockPos pos : this.nodes.keySet()) {
			if (nodes.get(pos).equals(node)) {
				return pos;
			}
		}
		return null;
	}

	public RitualNode setNode(BlockPos pos, RitualNode node) {
		return nodes.put(pos, node);
	}

	public RitualNode removeNode(BlockPos pos) {
		return nodes.remove(pos);
	}

}
