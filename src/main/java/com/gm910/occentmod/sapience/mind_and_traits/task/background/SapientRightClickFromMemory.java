package com.gm910.occentmod.sapience.mind_and_traits.task.background;

import java.util.List;
import java.util.Map;
import java.util.Optional;
import java.util.Set;
import java.util.stream.Collectors;

import com.gm910.occentmod.api.util.ModReflect;
import com.gm910.occentmod.api.util.ServerPos;
import com.gm910.occentmod.capabilities.citizeninfo.SapientInfo;
import com.gm910.occentmod.init.GMDeserialize;
import com.gm910.occentmod.sapience.mind_and_traits.memory.memories.CauseEffectMemory;
import com.gm910.occentmod.sapience.mind_and_traits.memory.memories.CauseEffectMemory.Certainty;
import com.gm910.occentmod.sapience.mind_and_traits.memory.memories.MemoryOfDeed;
import com.gm910.occentmod.sapience.mind_and_traits.memory.memories.MemoryOfSerializable;
import com.gm910.occentmod.sapience.mind_and_traits.occurrence.OccurrenceEffect.Connotation;
import com.gm910.occentmod.sapience.mind_and_traits.occurrence.OccurrenceType;
import com.gm910.occentmod.sapience.mind_and_traits.occurrence.deeds.ExistAtLocationDeed;
import com.gm910.occentmod.sapience.mind_and_traits.occurrence.deeds.SapientDeed;
import com.gm910.occentmod.sapience.mind_and_traits.personality.PersonalityTrait;
import com.gm910.occentmod.sapience.mind_and_traits.personality.PersonalityTrait.TraitLevel;
import com.gm910.occentmod.sapience.mind_and_traits.relationship.SapientIdentity;
import com.gm910.occentmod.sapience.mind_and_traits.task.Necessity;
import com.gm910.occentmod.sapience.mind_and_traits.task.SapientTask;
import com.gm910.occentmod.sapience.mind_and_traits.task.SapientWalkTarget;
import com.gm910.occentmod.sapience.mind_and_traits.task.TaskType;
import com.google.common.collect.ImmutableMap;
import com.google.common.collect.Streams;
import com.mojang.datafixers.util.Pair;

import net.minecraft.entity.CreatureEntity;
import net.minecraft.entity.LivingEntity;
import net.minecraft.entity.MobEntity;
import net.minecraft.entity.ai.attributes.AbstractAttributeMap;
import net.minecraft.entity.ai.attributes.AttributeModifier;
import net.minecraft.entity.ai.attributes.AttributeModifier.Operation;
import net.minecraft.entity.ai.attributes.IAttribute;
import net.minecraft.entity.ai.attributes.IAttributeInstance;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.item.ItemUseContext;
import net.minecraft.potion.EffectInstance;
import net.minecraft.util.CombatEntry;
import net.minecraft.util.CombatTracker;
import net.minecraft.util.Hand;
import net.minecraft.util.math.BlockRayTraceResult;
import net.minecraft.util.math.Vec3d;
import net.minecraft.world.server.ServerWorld;
import net.minecraftforge.common.util.FakePlayer;
import net.minecraftforge.common.util.FakePlayerFactory;

public class SapientRightClickFromMemory extends SapientTask<CreatureEntity> {

	public SapientRightClickFromMemory() {
		super(CreatureEntity.class, ImmutableMap.of());
		this.addContext(Context.BACKGROUND);
	}

	public boolean shouldExecute(ServerWorld worldIn, CreatureEntity owner) {
		boolean b = SapientInfo.get(owner).getKnowledge().getValueModule(GMDeserialize.ITEM_USE_CONTEXT, false)
				.isPresent() && (owner.getAttribute(PlayerEntity.REACH_DISTANCE) != null);

		MemoryOfSerializable<ItemUseContext, ?> con = SapientInfo.get(owner).getKnowledge()
				.getMemoryModule(GMDeserialize.ITEM_USE_CONTEXT, false);
		if (b && SapientInfo.get(owner).getKnowledge().getValueModule(GMDeserialize.ITEM_USE_CONTEXT, false).get()
				.getPos().distanceSq(owner.getPosition()) > Math
						.pow(owner.getAttribute(PlayerEntity.REACH_DISTANCE).getValue(), 2)) {
			SapientInfo.get(owner).getKnowledge().setValueModule(
					new SapientWalkTarget(con.getValue().getPos(), owner.getAIMoveSpeed(), 1, con.getNecessity()));
		}
		return b;
	}

	public boolean hasTheGuts(ServerWorld worldIn, CreatureEntity entity, SapientWalkTarget target) {
		Set<Pair<Connotation, ServerPos>> avoid = locationsToAvoid(entity).stream()
				.filter((loc) -> loc.getSecond().getDimension() == entity.dimension).collect(Collectors.toSet());
		TraitLevel para = PersonalityTrait.PARANOIA.getWeightedRandomReaction(
				SapientInfo.get(entity).getPersonality().getTrait(PersonalityTrait.PARANOIA));
		TraitLevel brav = PersonalityTrait.BRAVERY
				.getWeightedRandomReaction(SapientInfo.get(entity).getPersonality().getTrait(PersonalityTrait.BRAVERY));

		for (Pair<Connotation, ServerPos> avoidPos : avoid) {

			if (para != TraitLevel.EXCEPTIONAL_LOW
					&& target.getTarget().getPos().distanceTo(new Vec3d(avoidPos.getSecond()))
							- target.getDistance() <= para.id + 1
					&& (brav.isLessThan(TraitLevel.HIGH, true)
							? (target.getNecessity() == Necessity.NECESSARY ? brav == TraitLevel.EXCEPTIONAL_LOW
									: (target.getNecessity() == Necessity.PREFERABLE
											? brav.isLessThan(TraitLevel.HIGH_AVERAGE, false)
											: (avoidPos.getFirst() == Connotation.FATAL
													? brav != TraitLevel.EXCEPTIONAL_HIGH
													: true)))
							: (avoidPos.getFirst() == Connotation.FATAL ? brav != TraitLevel.EXCEPTIONAL_HIGH
									: true))) {
				return false;
			}
		}
		return true;
	}

	protected boolean shouldContinueExecuting(ServerWorld worldIn, CreatureEntity entityIn, long gameTimeIn) {
		Optional<SapientWalkTarget> optional = Optional.ofNullable(SapientInfo.get(entityIn).getKnowledge()
				.getValueModule(GMDeserialize.SAPIENT_WALK_TARGET, false).get());
		boolean b = optional.isPresent() && this.hasTheGuts(worldIn, entityIn, optional.get());
		if (optional.isPresent() && !this.hasTheGuts(worldIn, entityIn, optional.get())) {

		}
		return b;
	}

	protected void resetTask(ServerWorld worldIn, CreatureEntity entityIn, long gameTimeIn) {
	}

	protected void startExecuting(ServerWorld worldIn, CreatureEntity entityIn, long gameTimeIn) {

		FakePlayer play = FakePlayerFactory.get(worldIn, SapientInfo.get(entityIn).getProfile());
		play.setLocationAndAngles(entityIn.getPosX(), entityIn.getPosY(), entityIn.getPosZ(),
				entityIn.getRotationYawHead(), entityIn.rotationPitch);
		play.setHeldItem(Hand.MAIN_HAND, entityIn.getHeldItemMainhand());
		play.setHeldItem(Hand.OFF_HAND, entityIn.getHeldItemOffhand());
		Map<IAttribute, IAttributeInstance> persAttrs = ModReflect.getField(AbstractAttributeMap.class, Map.class,
				"attributes", "field_111154_a", entityIn.getAttributes());
		for (IAttribute a : persAttrs.keySet()) {
			if (play.getAttribute(a) == null) {
				play.getAttributes().registerAttribute(a);
			}
			play.getAttribute(a).applyModifier(new AttributeModifier("sync",
					-play.getAttribute(a).getValue() + entityIn.getAttribute(a).getValue(), Operation.ADDITION));
		}
		play.getActiveItemStack().onItemUse(
				SapientInfo.get(entityIn).getKnowledge().getValueModule(GMDeserialize.ITEM_USE_CONTEXT, true).get());
		ItemUseContext ctxt = SapientInfo.get(entityIn).getKnowledge()
				.getValueModule(GMDeserialize.ITEM_USE_CONTEXT, false).get();

		BlockRayTraceResult resultIn = ModReflect.getField(ItemUseContext.class, BlockRayTraceResult.class,
				"rayTraceResult", "field_221535_d", ctxt);
		worldIn.getBlockState(ctxt.getPos()).onBlockActivated(worldIn, play, play.getActiveHand(), resultIn);
		for (EffectInstance effect : play.getActivePotionEffects()) {
			entityIn.addPotionEffect(effect);
		}
		List<CombatEntry> combats = ModReflect.getField(CombatTracker.class, List.class, "combatEntries",
				"field_94556_a", play.getCombatTracker());
		if (!combats.isEmpty()) {
			for (CombatEntry combat : combats) {
				ModReflect.run(LivingEntity.class, Void.class, "damageEntity", "func_70665_d", entityIn,
						combat.getDamageSrc(), combat.getDamageAmount());
			}
		}
		Map<IAttribute, IAttributeInstance> attributes = ModReflect.getField(AbstractAttributeMap.class, Map.class,
				"attributes", "field_111154_a", play.getAttributes());
		for (IAttribute attr : attributes.keySet()) {
			if (entityIn.getAttribute(attr) != null) {
				entityIn.getAttribute(attr)
						.applyModifier(new AttributeModifier("sync",
								-entityIn.getAttribute(attr).getValue() + play.getAttribute(attr).getValue(),
								Operation.ADDITION));

			}
		}
	}

	public Set<Pair<Connotation, ServerPos>> locationsToAvoid(CreatureEntity en) {
		SapientInfo<? extends MobEntity> info = SapientInfo.get(en);
		Set<MemoryOfDeed<?>> mems = info.getKnowledge()
				.getByPredicate((e) -> e instanceof MemoryOfDeed
						&& (((MemoryOfDeed<?>) e).getDeed().getType() == OccurrenceType.EXIST_AT_LOCATION
								|| ((MemoryOfDeed<?>) e).getDeed().getType() == OccurrenceType.RIGHT_CLICK_AT_LOCATION)
						&& ((MemoryOfDeed<?>) e).getOpinion().harmful())
				.stream().map((e) -> (MemoryOfDeed<?>) e).collect(Collectors.toSet());
		Set<CauseEffectMemory<?>> cemems = info.getKnowledge().getByPredicate((e) -> e instanceof CauseEffectMemory
				&& (((CauseEffectMemory<?>) e).getCause().getType() == OccurrenceType.EXIST_AT_LOCATION
						|| ((CauseEffectMemory<?>) e).getCause().getType() == OccurrenceType.RIGHT_CLICK_AT_LOCATION)
				&& ((CauseEffectMemory<?>) e).getConnotation().getEffect(info.getIdentity()).harmful()
				&& ((CauseEffectMemory<?>) e).getCertainty().compareCertainty(Certainty.ALMOST_CERTAIN) >= 0
				&& ((CauseEffectMemory<?>) e).getConnotation().getEffect(info.getIdentity()).harmful()).stream()
				.map((e) -> (CauseEffectMemory<?>) e).collect(Collectors.toSet());
		return Streams
				.concat(mems.stream().map((d) -> d)
						.map((e) -> Pair.of(e.getOpinion(), ((ExistAtLocationDeed) e.getDeed()).getLocation())),
						cemems.stream()
								.map((d) -> Pair.of(d.getEffect().getEffect().getEffect(info.getIdentity()),
										(ExistAtLocationDeed) d.getCause()))
								.map((l) -> Pair.of(l.getFirst(), l.getSecond().getLocation())))
				.collect(Collectors.toSet());
	}

	protected void updateTask(ServerWorld worldIn, CreatureEntity owner, long gameTime) {

	}

	@Override
	public SapientDeed getDeed(SapientIdentity doer) {
		return null;
	}

	@Override
	public TaskType<CreatureEntity, ? extends SapientTask<CreatureEntity>> getType() {
		return null;
	}
}