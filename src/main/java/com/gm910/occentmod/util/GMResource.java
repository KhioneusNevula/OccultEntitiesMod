package com.gm910.occentmod.util;

import java.io.FileNotFoundException;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.UnsupportedEncodingException;
import java.util.ArrayList;
import java.util.List;
import java.util.Scanner;

import com.gm910.occentmod.OccultEntities;

import net.minecraft.util.ResourceLocation;

public class GMResource {

	public static String[] getNames(String filename) {
		List<String> names = new ArrayList<>();
		
		try (InputStream stream = GMResource.class.getClassLoader().getResourceAsStream("assets/"+OccultEntities.MODID+ "/namelists/"+filename+".txt");
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
		}	catch (NullPointerException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		System.out.println("Names not found for " + filename);
		return new String[] {"Names not found"};
	}
	
	public static InputStreamReader getResource(ResourceLocation loc) {
		
		try {
			return (new InputStreamReader(GMResource.class.getClassLoader()
			        .getResourceAsStream("assets/"+OccultEntities.MODID+ "/" + loc.getPath()), "UTF-8"));
		} catch (UnsupportedEncodingException e) {
			return null;
		}
	}
}
