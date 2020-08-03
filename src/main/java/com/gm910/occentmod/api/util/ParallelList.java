package com.gm910.occentmod.api.util;

import java.util.ArrayList;
import java.util.Collection;
import java.util.Iterator;
import java.util.List;
import java.util.ListIterator;
import java.util.function.Function;

public class ParallelList<E, M> extends ArrayList<E> {

	/**
	 * 
	 */
	private static final long serialVersionUID = 6140992959408135924L;

	private Function<E, M> translatorEM;

	private Function<M, E> translatorME;

	private ArrayList<M> basis;

	/**
	 * 
	 * @param basis  this is the list that the ParallelList uses as a basis
	 * @param trans1 this is the function that is used to translate the elements of
	 *               this list to the other list
	 * @param trans2 this is the function translating the other way
	 */
	public ParallelList(ArrayList<M> basis, Function<E, M> trans1, Function<M, E> trans2) {
		translatorEM = trans1;
		translatorME = trans2;
		this.basis = basis;
	}

	public Function<E, M> getTranslator1() {
		return translatorEM;
	}

	public Function<M, E> getTranslator2() {
		return translatorME;
	}

	@Override
	public int size() {
		return basis.size();
	}

	@Override
	public boolean isEmpty() {
		return basis.isEmpty();
	}

	@Override
	public boolean contains(Object o) {
		return basis.contains(ModReflect.<E>instanceOf(o, null) ? translatorEM.apply((E) o) : o);
	}

	@Override
	public Iterator<E> iterator() {

		List<E> ls = new ArrayList<>();
		for (M m : basis) {
			ls.add(translatorME.apply(m));
		}

		return ls.iterator();
	}

	@Override
	public Object[] toArray() {

		Object[] ar = new Object[basis.size()];

		for (int i = 0; i < basis.size(); i++) {
			ar[i] = translatorME.apply(basis.get(i));
		}

		return ar;
	}

	public List<E> translateList() {
		List<E> ls = new ArrayList<>();
		for (int i = 0; i < basis.size(); i++) {
			ls.add(translatorME.apply(basis.get(i)));
		}
		return ls;
	}

	public Collection<E> translateCollectionME(Collection<? extends M> col) {
		List<E> ls = new ArrayList<>();
		Iterator<? extends M> iter = col.iterator();
		while (iter.hasNext()) {
			ls.add(translatorME.apply(iter.next()));
		}
		return ls;
	}

	public Collection<M> translateCollectionEM(Collection<? extends E> col) {
		List<M> ls = new ArrayList<>();
		Iterator<? extends E> iter = col.iterator();
		while (iter.hasNext()) {
			ls.add(translatorEM.apply(iter.next()));
		}
		return ls;
	}

	@Override
	public <T> T[] toArray(T[] a) {

		return translateList().toArray(a);
	}

	@Override
	public boolean add(E e) {
		return basis.add(translatorEM.apply(e));
	}

	@Override
	public boolean remove(Object o) {
		return basis.remove(ModReflect.<E>instanceOf(o, null) ? translatorEM.apply((E) o) : o);
	}

	@Override
	public boolean containsAll(Collection<?> c) {
		return basis.containsAll(
				ModReflect.<Collection<E>>instanceOf(c, null) ? this.translateCollectionEM((Collection<E>) c) : c);
	}

	@Override
	public boolean addAll(Collection<? extends E> c) {

		return basis.addAll(this.translateCollectionEM(c));
	}

	@Override
	public boolean addAll(int index, Collection<? extends E> c) {
		return basis.addAll(this.translateCollectionEM(c));
	}

	@Override
	public boolean removeAll(Collection<?> c) {
		return basis.removeAll(
				ModReflect.<Collection<E>>instanceOf(c, null) ? this.translateCollectionEM((Collection<E>) c) : c);
	}

	@Override
	public boolean retainAll(Collection<?> c) {
		return basis.retainAll(
				ModReflect.<Collection<E>>instanceOf(c, null) ? this.translateCollectionEM((Collection<E>) c) : c);
	}

	@Override
	public void clear() {
		basis.clear();
	}

	@Override
	public E get(int index) {

		return translatorME.apply(basis.get(index));
	}

	@Override
	public E set(int index, E element) {
		return translatorME.apply(basis.set(index, translatorEM.apply(element)));
	}

	@Override
	public void add(int index, E element) {
		basis.add(index, translatorEM.apply(element));
	}

	@Override
	public E remove(int index) {
		return translatorME.apply(basis.remove(index));
	}

	@Override
	public int indexOf(Object o) {
		return basis.indexOf(ModReflect.<E>instanceOf(o, null) ? translatorEM.apply((E) o) : o);
	}

	@Override
	public int lastIndexOf(Object o) {
		return basis.lastIndexOf(ModReflect.<E>instanceOf(o, null) ? translatorEM.apply((E) o) : o);
	}

	@Override
	public ListIterator<E> listIterator() {

		return this.translateList().listIterator();
	}

	@Override
	public ListIterator<E> listIterator(int index) {
		return this.translateList().listIterator(index);
	}

	@Override
	public List<E> subList(int fromIndex, int toIndex) {
		return this.translateList().subList(fromIndex, toIndex);
	}

}
