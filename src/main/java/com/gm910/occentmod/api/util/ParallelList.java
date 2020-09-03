package com.gm910.occentmod.api.util;

import java.util.ArrayList;
import java.util.Collection;
import java.util.Iterator;
import java.util.List;
import java.util.ListIterator;
import java.util.function.Function;

public class ParallelList<ThisClass, BasisClass> extends ArrayList<ThisClass> {

	/**
	 * 
	 */
	private static final long serialVersionUID = 6140992959408135924L;

	private Function<ThisClass, BasisClass> translatorEM;

	private Function<BasisClass, ThisClass> translatorME;

	private ArrayList<BasisClass> basis;

	private Class<ThisClass> thisClass;
	private Class<BasisClass> basisClass;

	/**
	 * 
	 * @param basis  this is the list that the ParallelList uses as a basis
	 * @param trans1 this is the function that is used to translate the elements of
	 *               this list to the other list
	 * @param trans2 this is the function translating the other way
	 */
	public ParallelList(Class<BasisClass> basisClass, Class<ThisClass> thisClass, ArrayList<BasisClass> basis,
			Function<ThisClass, BasisClass> trans1, Function<BasisClass, ThisClass> trans2) {
		translatorEM = trans1;
		translatorME = trans2;
		this.basis = basis;
		this.thisClass = thisClass;
		this.basisClass = basisClass;
	}

	public Class<BasisClass> getBasisClass() {
		return basisClass;
	}

	public Class<ThisClass> getThisClass() {
		return thisClass;
	}

	public Function<ThisClass, BasisClass> getTranslator1() {
		return translatorEM;
	}

	public Function<BasisClass, ThisClass> getTranslator2() {
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
		return basis.contains(ModReflect.<ThisClass>instanceOf(o, thisClass) ? translatorEM.apply((ThisClass) o) : o);
	}

	@Override
	public Iterator<ThisClass> iterator() {

		List<ThisClass> ls = new ArrayList<>();
		for (BasisClass m : basis) {
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

	public List<ThisClass> translateList() {
		List<ThisClass> ls = new ArrayList<>();
		for (int i = 0; i < basis.size(); i++) {
			ls.add(translatorME.apply(basis.get(i)));
		}
		return ls;
	}

	public Collection<ThisClass> translateCollectionME(Collection<? extends BasisClass> col) {
		List<ThisClass> ls = new ArrayList<>();
		Iterator<? extends BasisClass> iter = col.iterator();
		while (iter.hasNext()) {
			ls.add(translatorME.apply(iter.next()));
		}
		return ls;
	}

	public Collection<BasisClass> translateCollectionEM(Collection<? extends ThisClass> col) {
		List<BasisClass> ls = new ArrayList<>();
		Iterator<? extends ThisClass> iter = col.iterator();
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
	public boolean add(ThisClass e) {
		return basis.add(translatorEM.apply(e));
	}

	@Override
	public boolean remove(Object o) {
		return basis.remove(ModReflect.<ThisClass>instanceOf(o, thisClass) ? translatorEM.apply((ThisClass) o) : o);
	}

	@Override
	public boolean containsAll(Collection<?> c) {
		Collection<?> cop = c;
		if (c.isEmpty()) {
			return true;
		}
		Class<?> toUse = cop.stream().findAny().get().getClass();
		return basis.containsAll(toUse == thisClass ? this.translateCollectionEM((Collection<ThisClass>) c) : c);
	}

	public boolean isCollectionOfThisClass(Collection<?> col) {
		Collection<?> cop = col;
		if (col.isEmpty()) {
			return true;
		}
		Class<?> toUse = cop.stream().findAny().get().getClass();
		if (toUse == this.thisClass) {
			return true;
		}
		return false;
	}

	@Override
	public boolean addAll(Collection<? extends ThisClass> c) {

		return basis.addAll(this.translateCollectionEM(c));
	}

	@Override
	public boolean addAll(int index, Collection<? extends ThisClass> c) {
		return basis.addAll(this.translateCollectionEM(c));
	}

	@Override
	public boolean removeAll(Collection<?> c) {
		return basis.removeAll(isCollectionOfThisClass(c) ? this.translateCollectionEM((Collection<ThisClass>) c) : c);
	}

	@Override
	public boolean retainAll(Collection<?> c) {
		return basis.retainAll(isCollectionOfThisClass(c) ? this.translateCollectionEM((Collection<ThisClass>) c) : c);
	}

	@Override
	public void clear() {
		basis.clear();
	}

	@Override
	public ThisClass get(int index) {

		return translatorME.apply(basis.get(index));
	}

	@Override
	public ThisClass set(int index, ThisClass element) {
		return translatorME.apply(basis.set(index, translatorEM.apply(element)));
	}

	@Override
	public void add(int index, ThisClass element) {
		basis.add(index, translatorEM.apply(element));
	}

	@Override
	public ThisClass remove(int index) {
		return translatorME.apply(basis.remove(index));
	}

	@Override
	public int indexOf(Object o) {
		return basis.indexOf(ModReflect.<ThisClass>instanceOf(o, thisClass) ? translatorEM.apply((ThisClass) o) : o);
	}

	@Override
	public int lastIndexOf(Object o) {
		return basis
				.lastIndexOf(ModReflect.<ThisClass>instanceOf(o, thisClass) ? translatorEM.apply((ThisClass) o) : o);
	}

	@Override
	public ListIterator<ThisClass> listIterator() {

		return this.translateList().listIterator();
	}

	@Override
	public ListIterator<ThisClass> listIterator(int index) {
		return this.translateList().listIterator(index);
	}

	@Override
	public List<ThisClass> subList(int fromIndex, int toIndex) {
		return this.translateList().subList(fromIndex, toIndex);
	}

}
