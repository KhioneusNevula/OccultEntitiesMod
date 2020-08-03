package com.gm910.occentmod.entities.wizard.tasks.wizard.jobs;

import java.util.Map;
import java.util.Optional;

import com.gm910.occentmod.api.networking.messages.Networking;
import com.gm910.occentmod.api.networking.messages.types.TaskParticles;
import com.gm910.occentmod.api.util.GMHelper;
import com.gm910.occentmod.entities.wizard.WizardEntity;
import com.gm910.occentmod.entities.wizard.WizardJob;

import net.minecraft.entity.ai.brain.memory.MemoryModuleType;
import net.minecraft.particles.ParticleTypes;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.GlobalPos;
import net.minecraft.world.server.ServerWorld;

public class WizardJobTypes {

	@FunctionalInterface
	public static interface JobConsumer {
		public void accept(ServerWorld world, WizardEntity entity, long long1);
	}

	public static final JobConsumer JOBLESS = (world, entity, long1) -> {
	};

	public static final JobConsumer CUPID = (world, entity, long1) -> {
		Optional<GlobalPos> jobSite = entity.getBrain().getMemory(MemoryModuleType.JOB_SITE);
		jobSite.ifPresent((site) -> {
			// PointOfInterestType poit =
			// world.getPointOfInterestManager().getType(site.getPos()).orElse(null);
			BlockPos pos = site.getPos();
			for (int i = 0; i < world.rand.nextInt(100); i++) {
				Networking.sendToAll(new TaskParticles(ParticleTypes.HEART,
						pos.getX() + 0.5 + world.rand.nextDouble() - world.rand.nextDouble(),
						pos.getY() + world.rand.nextDouble() * 3,
						pos.getZ() + 0.5 + world.rand.nextDouble() - world.rand.nextDouble(), world.dimension.getType(),
						world.rand.nextDouble() * 10 - world.rand.nextDouble() * 5,
						world.rand.nextDouble() * 10 - world.rand.nextDouble() * 5,
						world.rand.nextDouble() * 10 - world.rand.nextDouble() * 5, false, false, false));
			}
		});
	};

	public static final Map<WizardJob, JobConsumer> JOB_STYLES = GMHelper.createHashMap((map) -> {
		map.put(WizardJob.JOBLESS, JOBLESS);
	});
}
