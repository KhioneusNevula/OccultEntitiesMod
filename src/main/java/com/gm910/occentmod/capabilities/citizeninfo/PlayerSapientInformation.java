package com.gm910.occentmod.capabilities.citizeninfo;

import com.gm910.occentmod.api.language.NamePhonemicHelper.PhonemeWord;
import com.gm910.occentmod.capabilities.formshifting.Formshift;
import com.gm910.occentmod.empires.gods.citinfo.EmptyNeeds;
import com.gm910.occentmod.sapience.mind_and_traits.emotions.Emotions;
import com.gm910.occentmod.sapience.mind_and_traits.genetics.Genetics;
import com.gm910.occentmod.sapience.mind_and_traits.genetics.Race;
import com.gm910.occentmod.sapience.mind_and_traits.memory.Memories;
import com.gm910.occentmod.sapience.mind_and_traits.personality.Personality;
import com.gm910.occentmod.sapience.mind_and_traits.relationship.Genealogy;
import com.gm910.occentmod.sapience.mind_and_traits.relationship.Relationships;
import com.gm910.occentmod.sapience.mind_and_traits.relationship.SapientIdentity.DynamicSapientIdentity;
import com.gm910.occentmod.sapience.mind_and_traits.religion.Religion;
import com.gm910.occentmod.sapience.mind_and_traits.skills.Skills;
import com.mojang.authlib.GameProfile;

import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.inventory.IInventory;
import net.minecraft.nbt.CompoundNBT;
import net.minecraft.world.server.ServerWorld;

public class PlayerSapientInformation extends SapientInfo<PlayerEntity> {

	@Override
	public CompoundNBT serializeNBT() {
		return new CompoundNBT();
	}

	@Override
	public void deserializeNBT(CompoundNBT nbt) {

	}

	@Override
	public IInventory getInventory() {
		return this.$getOwner().inventory;
	}

	@Override
	public void onCreation() {
		System.out.println("Sapient information init for "
				+ (this.$getOwner() != null ? this.$getOwner().getDisplayName().getFormattedText() : this.$getOwner()));
		this.profile = this.$getOwner().getGameProfile();
		this.autonomy = new PlayerEmptyAutonomy(this.$getOwner());
		System.out.println("Sapient autonomy " + autonomy);
		this.emotions = new Emotions() {
			@Override
			public void tick() {
			}

		};
		this.genetics = new Genetics<PlayerEntity>(PlayerEntity.class) {
		};
		this.identity = new DynamicSapientIdentity(Formshift.get(this.$getOwner()).getForm(),
				this.$getOwner().getUniqueID(), new PhonemeWord(this.$getOwner().getGameProfile().getName()),
				new Genealogy(), Race.HUMAN, null, (ServerWorld) this.$getOwner().world);
		knowledge = new Memories<PlayerEntity>(this.$getOwner()) {
			@Override
			public void tick() {
				// super.tick();
			}
		};
		this.needs = new EmptyNeeds<PlayerEntity>(this.$getOwner());
		this.skills = new Skills();
		this.personality = new Personality<PlayerEntity>(this.$getOwner()) {
			@Override
			public void tick() {
			}
		};
		this.relationships = new Relationships(this.$getOwner());
		this.religion = new Religion<PlayerEntity>(this.$getOwner()) {

		};
		System.out.println("Sapient info end init " + this);
	}

	@Override
	public GameProfile getProfile() {
		return this.$getOwner().getGameProfile();
	}

	@Override
	public PlayerEntity getPlayerDelegate() {
		return this.$getOwner();
	}

}
