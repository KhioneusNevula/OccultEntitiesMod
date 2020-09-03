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

public class ParallelSet<ThisClass, BaseClass> extends HashSet<ThisClass> {

	/**
	 * 
	 */
	private static final long serialVersionUID = 8087285438096366195L;

	private Set<BaseClass> delegate;
	private Function<BaseClass, ThisClass> yoursToMine;
	private Function<ThisClass, BaseClass> mineToYours;

	private Class<ThisClass> thisClass;
	private Class<BaseClass> baseClass;

	public ParallelSet(Class<ThisClass> thisClass, Class<BaseClass> baseClass, Set<BaseClass> delegate,
			Function<BaseClass, ThisClass> yoursToMine, Function<ThisClass, BaseClass> mineToYours) {
		this.delegate = delegate;
		this.yoursToMine = yoursToMine;
		this.mineToYours = mineToYours;
		this.thisClass = thisClass;
		this.baseClass = baseClass;
	}

	@Override
	public int size() {
		// TODO Auto-generated method stub
		return delegate.size();
	}

	public boolean isCollectionOfThisClass(Object col) {
		if (!(col instanceof Collection))
			return false;
		Collection<?> cop = (Collection<?>) col;
		if (cop.isEmpty()) {
			return true;
		}
		Class<?> toUse = cop.stream().findAny().get().getClass();
		if (toUse == this.thisClass) {
			return true;
		}
		return false;
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
		if (this.thisClass.isInstance(o)) {
			return this.mineToYours((ThisClass) o);
		} else if (isCollectionOfThisClass(o)) {
			return ((Collection<ThisClass>) o).stream().map(mineToYours).collect(Collectors.toList());
		} else {
			return o;
		}
	}

	public Set<ThisClass> convertWholeSet() {
		return this.delegate.stream().map(yoursToMine).collect(Collectors.toSet());
	}

	public Set<BaseClass> getDelegate() {
		return delegate;
	}

	public Function<ThisClass, BaseClass> getMineToYours() {
		return mineToYours;
	}

	public Function<BaseClass, ThisClass> getYoursToMine() {
		return yoursToMine;
	}

	protected BaseClass mineToYours(ThisClass mine) {
		return mineToYours(mine);
	}

	protected ThisClass yoursToMine(BaseClass yours) {
		return yoursToMine(yours);
	}

	@Override
	public Iterator<ThisClass> iterator() {
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
	public boolean add(ThisClass e) {
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
	public boolean addAll(Collection<? extends ThisClass> c) {
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
	public void forEach(Consumer<? super ThisClass> action) {
		Objects.requireNonNull(action);
		for (ThisClass t : this) {
			action.accept(t);
			delegate.remove(mineToYours(t));
			delegate.add(mineToYours(t));
		}
	}

	public void delegateForEach(Consumer<? super BaseClass> action) {
		delegate.forEach(action);
	}

	@Override
	public Spliterator<ThisClass> spliterator() {
		// TODO Auto-generated method stub
		return this.convertWholeSet().spliterator();
	}

	@Override
	public Stream<ThisClass> parallelStream() {
		// TODO Auto-generated method stub
		return this.convertWholeSet().parallelStream();
	}

	@Override
	public boolean removeIf(Predicate<? super ThisClass> filter) {
		// TODO Auto-generated method stub
		return delegate.removeIf((e) -> filter.test(yoursToMine(e)));
	}

	@Override
	public Stream<ThisClass> stream() {
		// TODO Auto-generated method stub
		return this.convertWholeSet().stream();
	}

}
