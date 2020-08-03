package com.gm910.occentmod.api.util;

import java.util.Collection;
import java.util.HashSet;
import java.util.Iterator;
import java.util.Objects;
import java.util.Set;
import java.util.Spliterator;
import java.util.function.Consumer;
import java.util.function.Function;
import java.util.function.Predicate;
import java.util.stream.Collectors;
import java.util.stream.Stream;

public class ParallelSet<M, Y> extends HashSet<M> {

	/**
	 * 
	 */
	private static final long serialVersionUID = 8087285438096366195L;

	private Set<Y> delegate;
	private Function<Y, M> yoursToMine;
	private Function<M, Y> mineToYours;

	public ParallelSet(Set<Y> delegate, Function<Y, M> yoursToMine, Function<M, Y> mineToYours) {
		this.delegate = delegate;
		this.yoursToMine = yoursToMine;
		this.mineToYours = mineToYours;
	}

	@Override
	public int size() {
		// TODO Auto-generated method stub
		return delegate.size();
	}

	@Override
	public boolean isEmpty() {
		// TODO Auto-generated method stub
		return delegate.isEmpty();
	}

	@Override
	public boolean contains(Object o) {
		// TODO Auto-generated method stub
		return delegate.contains(toYoursOrConvert(o));
	}

	public Object toYoursOrConvert(Object o) {
		if (ModReflect.<M>instanceOf(o, null)) {
			return this.mineToYours((M) o);
		} else if (ModReflect.<Collection<M>>instanceOf(o, null)) {
			return ((Collection<M>) o).stream().map(mineToYours).collect(Collectors.toList());
		} else {
			return o;
		}
	}

	public Set<M> convertWholeSet() {
		return this.delegate.stream().map(yoursToMine).collect(Collectors.toSet());
	}

	public Set<Y> getDelegate() {
		return delegate;
	}

	public Function<M, Y> getMineToYours() {
		return mineToYours;
	}

	public Function<Y, M> getYoursToMine() {
		return yoursToMine;
	}

	protected Y mineToYours(M mine) {
		return mineToYours(mine);
	}

	protected M yoursToMine(Y yours) {
		return yoursToMine(yours);
	}

	@Override
	public Iterator<M> iterator() {
		return delegate.stream().map(yoursToMine).collect(Collectors.toSet()).iterator();
	}

	@Override
	public Object[] toArray() {
		return this.convertWholeSet().toArray();
	}

	@Override
	public <T> T[] toArray(T[] a) {
		return this.convertWholeSet().toArray(a);
	}

	@Override
	public boolean add(M e) {
		return delegate.add(mineToYours(e));
	}

	@Override
	public boolean remove(Object o) {
		return delegate.remove(toYoursOrConvert(o));
	}

	@Override
	public boolean containsAll(Collection<?> c) {
		return delegate.containsAll((Collection<?>) toYoursOrConvert(c));
	}

	@Override
	public boolean addAll(Collection<? extends M> c) {
		// TODO Auto-generated method stub
		return delegate.addAll(c.stream().map(mineToYours).collect(Collectors.toList()));
	}

	@Override
	public boolean retainAll(Collection<?> c) {
		return delegate.retainAll((Collection<?>) toYoursOrConvert(c));
	}

	@Override
	public boolean removeAll(Collection<?> c) {
		return delegate.removeAll((Collection<?>) toYoursOrConvert(c));
	}

	@Override
	public void clear() {
		this.delegate.clear();
	}

	@Override
	public void forEach(Consumer<? super M> action) {
		Objects.requireNonNull(action);
		for (M t : this) {
			action.accept(t);
			delegate.remove(t);
			delegate.add(mineToYours(t));
		}
	}

	public void delegateForEach(Consumer<? super Y> action) {
		delegate.forEach(action);
	}

	@Override
	public Spliterator<M> spliterator() {
		// TODO Auto-generated method stub
		return this.convertWholeSet().spliterator();
	}

	@Override
	public Stream<M> parallelStream() {
		// TODO Auto-generated method stub
		return this.convertWholeSet().parallelStream();
	}

	@Override
	public boolean removeIf(Predicate<? super M> filter) {
		// TODO Auto-generated method stub
		return delegate.removeIf((e) -> filter.test(yoursToMine(e)));
	}

	@Override
	public Stream<M> stream() {
		// TODO Auto-generated method stub
		return this.convertWholeSet().stream();
	}

}
