package com.gm910.occentmod.api.functionalinterfaces;

public class ModFunc {

	@FunctionalInterface
	public interface TriFunction<A, B, C, R> {
		public R apply(A a, B b, C c);
	}
	
	@FunctionalInterface
	public interface TetraFunction<A, B, C, D, R> {
		public R apply(A a, B b, C c, D d);
	}
	
	@FunctionalInterface
	public interface PentaFunction<A, B, C, D, E, R> {
		public R apply(A a, B b, C c, D d, E e);
	}
	
	@FunctionalInterface
	public interface HexaFunction<A, B, C, D, E, F, R> {
		public R apply(A a, B b, C c, D d, E e, F f);
	}
	
	@FunctionalInterface
	public interface HeptaFunction<A, B, C, D, E, F, G, R> {
		public R apply(A a, B b, C c, D d, E e, F f, G g);
	}
	
	@FunctionalInterface
	public interface OctaFunction<A, B, C, D, E, F, G, H, R> {
		public R apply(A a, B b, C c, D d, E e, F f, H h);
	}
	
	@FunctionalInterface
	public interface EnneaFunction<A, B, C, D, E, F, G, H, I, R> {
		public R apply(A a, B b, C c, D d, E e, F f, H h, I i);
	}
	
	//Consumers
	@FunctionalInterface
	public interface TetraConsumer<A, B, C, D> {
		public void accept(A a, B b, C c, D d);
	}
	
	@FunctionalInterface
	public interface PentaConsumer<A, B, C, D, E> {
		public void accept(A a, B b, C c, D d, E e);
	}
	
	@FunctionalInterface
	public interface HexaConsumer<A, B, C, D, E, F> {
		public void accept(A a, B b, C c, D d, E e, F f);
	}
	
	@FunctionalInterface
	public interface HeptaConsumer<A, B, C, D, E, F, G> {
		public void accept(A a, B b, C c, D d, E e, F f, G g);
	}
	
	@FunctionalInterface
	public interface OctaConsumer<A, B, C, D, E, F, G, H> {
		public void accept(A a, B b, C c, D d, E e, F f, H h);
	}
	
	@FunctionalInterface
	public interface EnneaConsumer<A, B, C, D, E, F, G, H, I> {
		public void accept(A a, B b, C c, D d, E e, F f, H h, I i);
	}
}
