package com.gm910.occentmod.api.util;

import java.lang.reflect.Constructor;
import java.lang.reflect.Field;
import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;
import java.util.Arrays;
import java.util.Objects;

import javax.annotation.Nullable;

public class ModReflect {

	/**
	 * IF ANY ARGUMENTS FOR CONSTRUCTOR ARE NULL, use this method Constructs object
	 * from given class with given arguments without the hassle of error checking
	 * 
	 * @param clazz  class to find constructor from
	 * @param clargs classes used for constructor arguments
	 * @param args   arguments for constructor
	 */
	@SuppressWarnings("unchecked")
	public static <T> T construct(Class<T> clazz, Class<?>[] clargs, Object[] args) {

		Object toConstruct = null;
		try {
			Constructor<?> con = clazz.getDeclaredConstructor(clargs);
			con.setAccessible(true);
			toConstruct = con.newInstance(args);
		} catch (InstantiationException | IllegalAccessException | NoSuchMethodException | SecurityException e) {
			throw new RuntimeException(e);
		} catch (InvocationTargetException e) {
			throw new RuntimeException(e);
		}
		return (T) toConstruct;
	}

	/**
	 * ONLY USE if no arguments are null Constructs object from given arguments
	 * 
	 * @param clazz class to construct from
	 * @param args  the arguments for the constructor
	 * @return
	 */
	public static <T> T construct(Class<T> clazz, Object... args) {
		Class<?>[] typelist = new Class<?>[args.length];
		for (int i = 0; i < typelist.length; i++) {
			Objects.requireNonNull(args[i], Arrays.toString(args) + " must not have null elements");
			typelist[i] = args[i].getClass();
		}
		return construct(clazz, typelist, args);
	}

	/**
	 * IF ANY ARGUMENTS FOR METHOD ARE NULL, use this method Calls given method
	 * 
	 * @param clazz     class to find method from
	 * @param returnt   class to get return type from, may be null if returntype is
	 *                  void--try not to use 'void.class'
	 * @param name      name of method
	 * @param obfusname obfuscated name of method (can be null)
	 * @param obj       optional object to call method on
	 * @param clargs    classes used for method arguments
	 * @param args      arguments for method
	 */
	@SuppressWarnings("unchecked")
	public static <T> T run(Class<?> clazz, Class<T> returnt, String name, String obfusname, Object obj,
			Class<?>[] clargs, Object[] args) {

		Object toConstruct = null;
		Method method = null;
		if (obfusname != null) {
			try {
				method = clazz.getDeclaredMethod(obfusname, clargs);
			} catch (NoSuchMethodException | SecurityException e1) {

			}
		}
		if (method == null) {
			try {
				method = clazz.getDeclaredMethod(name, clargs);
				method.setAccessible(true);
				toConstruct = method.invoke(obj, args);
			} catch (IllegalAccessException | NoSuchMethodException | SecurityException e) {
				return null;
			} catch (InvocationTargetException e) {
				throw new RuntimeException(e);
			}
		}
		if (returnt == Void.class || returnt == null) {
			return null;
		}
		return (T) toConstruct;
	}

	/**
	 * IF ANY ARGUMENTS FOR METHOD ARE NULL, don't use this method, use the other
	 * overload version Calls given method
	 * 
	 * @param clazz   class to find method from
	 * @param returnt class to get return type from, may be null if returntype is
	 *                void--try not to use 'void.class'
	 * @param name    name of method
	 * @param obj     optional object to call method on
	 * @param args    arguments for method
	 */
	public static <T> T run(Class<?> clazz, Class<T> returnt, String name, String obfusname, Object obj,
			Object... args) {
		Class<?>[] typelist = new Class<?>[args.length];
		for (int i = 0; i < typelist.length; i++) {
			Objects.requireNonNull(args[i], Arrays.toString(args) + " must not have null elements");
			typelist[i] = args[i].getClass();
		}
		return run(clazz, returnt, name, obfusname, obj, typelist, args);
	}

	/**
	 * Sets field to given value
	 * 
	 * @param clazz      class to use to set field
	 * @param fieldclass class of field itself
	 * @param name       name of field
	 * @param withfield  the object that has the field in it, can be null if static
	 * @param value      value to put in field
	 * @returns the exception thrown when attempting to set the field, or null if it
	 *          worked
	 */
	public static <T> Throwable setField(Class<?> clazz, Class<T> fieldclass, String name, String obfusname,
			@Nullable Object withfield, T value) {
		Field field = null;
		if (obfusname != null) {
			try {
				field = clazz.getDeclaredField(obfusname);
			} catch (NoSuchFieldException | SecurityException e) {
			}
		}
		if (field == null) {
			try {
				field = clazz.getDeclaredField(name);
				field.setAccessible(true);
				field.set(withfield, value);
			} catch (Throwable e) {
				return e;
			}
		}
		return null;
	}

	/**
	 * Gets the value of the given field
	 * 
	 * @param clazz      the class the field is in
	 * @param fieldClass the class of the field itself
	 * @param name       the name of the field
	 * @param withfield  the object with this field
	 * @return the field value or null if no value/it didn't work
	 */
	@SuppressWarnings("unchecked")
	public static <T> T getField(Class<?> clazz, Class<T> fieldClass, String name, String obfusname,
			@Nullable Object withfield) {

		Field field = null;
		if (obfusname != null) {
			try {
				field = clazz.getDeclaredField(obfusname);
			} catch (NoSuchFieldException | SecurityException e) {
			}
		}
		if (field == null) {
			try {
				field = clazz.getDeclaredField(name);
				field.setAccessible(true);
				return (T) field.get(withfield);
			} catch (IllegalArgumentException | IllegalAccessException | NoSuchFieldException | SecurityException
					| ClassCastException e) {

			}
		}
		return null;
	}

	/**
	 * Checks if b is an instanceof the class represented by clazz (or alternatively
	 * the first generic type parameter X). Works for generics
	 * 
	 * @param <X>
	 * @param b
	 * @param clazz
	 * @return
	 */
	@SuppressWarnings("unchecked")
	public static <X> boolean instanceOf(Object b, @Nullable Class<X> clazz) {
		try {
			X r = (X) b;
			return true;
		} catch (ClassCastException e) {

			// Lists.newArrayList());
			return false;
		}
	}

	public static Class<?> getSubclass(Class<?> clazz, String simpleName, String obfusname) {

		Class<?>[] classes = clazz.getDeclaredClasses();
		for (Class<?> clazz2 : classes) {

			if (clazz2.getName().equals(obfusname) || clazz2.getSimpleName().equals(simpleName)) {
				return clazz2;
			}
		}
		return null;
	}

}
