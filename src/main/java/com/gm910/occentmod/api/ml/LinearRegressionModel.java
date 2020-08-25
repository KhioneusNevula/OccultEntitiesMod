package com.gm910.occentmod.api.ml;

import java.util.Arrays;
import java.util.List;
import java.util.function.Function;

import com.google.common.collect.ImmutableMap;
import com.google.common.collect.Lists;
import com.mojang.datafixers.Dynamic;
import com.mojang.datafixers.types.DynamicOps;

import net.minecraft.util.IDynamicSerializable;

public class LinearRegressionModel implements Function<Double[], Double>, IDynamicSerializable {
	private final double[] thetaVector;

	LinearRegressionModel(double[] thetaVector) {
		this.thetaVector = Arrays.copyOf(thetaVector, thetaVector.length);
	}

	public static LinearRegressionModel deserialize(Dynamic<?> dyn) {
		return new LinearRegressionModel(dyn.get("thetaVector").asStream().mapToDouble((e) -> e.asDouble(0)).toArray());

	}

	@Override
	public <T> T serialize(DynamicOps<T> ops) {
		List<Double> ls = Lists.newArrayList();
		for (double d : thetaVector) {
			ls.add(d);
		}
		return ops.createMap(ImmutableMap.of(ops.createString("thetaVector"),
				ops.createList(ls.stream().map((d) -> ops.createDouble(d)))));
	}

	public Double apply(Double[] featureVector) {
		// for computational reasons the first element has to be 1.0
		assert featureVector[0] == 1.0;

		// simple, sequential implementation
		double prediction = 0;
		for (int j = 0; j < thetaVector.length; j++) {
			prediction += thetaVector[j] * featureVector[j];
		}
		return prediction;
	}

	public double[] getThetas() {
		return Arrays.copyOf(thetaVector, thetaVector.length);
	}
}