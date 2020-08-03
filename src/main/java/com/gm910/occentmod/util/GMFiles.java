package com.gm910.occentmod.util;

import java.io.FileNotFoundException;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.UnsupportedEncodingException;
import java.util.ArrayList;
import java.util.List;
import java.util.Scanner;
import java.util.function.Predicate;

import com.gm910.occentmod.OccultEntities;
import com.google.common.base.Predicates;

import net.minecraft.util.ResourceLocation;

public class GMFiles {

	public static String[] getNames(String filename) {
		List<String> names = new ArrayList<>();

		try (InputStream stream = GMFiles.class.getClassLoader()
				.getResourceAsStream("assets/" + OccultEntities.MODID + "/namelists/" + filename + ".txt");
				InputStreamReader re = new InputStreamReader(stream, "UTF-8");
				Scanner scan = new Scanner(re)) {
			while (scan.hasNextLine()) {
				names.add(scan.nextLine());
			}
			return names.toArray(new String[0]);
		} catch (FileNotFoundException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		} catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		} catch (NullPointerException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		System.out.println("Names not found for " + filename);
		return new String[] { "Names not found" };
	}

	/**
	 * Gets the lines of the document which is at the given filepath in the ASSETS
	 * folder. The given lines will not include any which do not fit the given
	 * predicate
	 * 
	 * @param filepath
	 * @return
	 */
	public static String[] getLines(ResourceLocation filepath, Predicate<String> predicate) {
		List<String> names = new ArrayList<>();

		try (InputStreamReader re = getResource(filepath); Scanner scan = new Scanner(re)) {
			while (scan.hasNextLine()) {
				String line = scan.nextLine();
				if (predicate.test(line)) {
					names.add(line);
				}
			}
			return names.toArray(new String[0]);
		} catch (IOException e) {
			return new String[0];
		}
	}

	public static String[] getLines(ResourceLocation filepath) {
		return getLines(filepath, Predicates.alwaysTrue());
	}

	public static InputStreamReader getResource(ResourceLocation loc) {

		try {
			return (new InputStreamReader(GMFiles.class.getClassLoader()
					.getResourceAsStream("assets/" + loc.getNamespace() + "/" + loc.getPath()), "UTF-16"));
		} catch (UnsupportedEncodingException e) {
			return null;
		}
	}

	public static ResourceLocation rl(String path) {
		return new ResourceLocation(OccultEntities.MODID, path);
	}
}
