package com.donglu.carpark.ui.view.img;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.Random;

public class PlateUtil {
	public static void main(String[] args) {
		String plate="粤BD021WW";
		List<String> list = replacePlateOne(plate);
		System.out.println(list.size());
		for (String string : list) {
			System.out.println(string);
		}
		list = replacePlateTwo(plate);
		System.out.println(list.size());
		for (String string : list) {
			System.out.println(string);
		}
		list = replacePlateThree(plate);
		System.out.println(list.size());
		for (String string : list) {
			System.out.println(string);
		}
		list = replacePlateFour(plate);
		System.out.println(list.size());
		for (String string : list) {
			System.out.println(string);
		}
		
	}
	
	public static List<String> replacePlate(String plate,int left) {
		List<String> list = new ArrayList<>();
		switch (left) {
		case 1:
			list = replacePlateOne(plate);
			break;
		case 2:
			list = replacePlateTwo(plate);
			break;
		case 3:
			list = replacePlateThree(plate);
			break;
		case 4:
			list = replacePlateFour(plate);
			break;
		default:
			list = Arrays.asList(plate);
			break;
		}
		return list;
	}
	/**
	 * @param plate
	 * @return
	 */
	public static List<String> replacePlateOne(String plate) {
		List<String> list=new ArrayList<>();
		for (int i = 0; i < plate.length(); i++) {
			char[] charArray = plate.toCharArray();
			charArray[i]='_';
			list.add(new String(charArray));
		}
		return list;
	}
	/**
	 * @param plate
	 * @return
	 */
	public static List<String> replacePlateTwo(String plate) {
		List<String> list=new ArrayList<>();
		for (int i = 0; i < plate.length()-1; i++) {
			for (int j = i+1; j < plate.length(); j++) {
				char[] charArray = plate.toCharArray();
				charArray[i]='_';
				charArray[j]='_';
				list.add(new String(charArray));
			}
		}
		return list;
	}
	/**
	 * @param plate
	 * @return
	 */
	public static List<String> replacePlateThree(String plate) {
		List<String> list=new ArrayList<>();
		for (int i = 0; i < plate.length()-2; i++) {
			for (int j = i+1; j < plate.length()-1; j++) {
				for (int j2 = j+1; j2 < plate.length(); j2++) {
					char[] charArray = plate.toCharArray();
					charArray[i]='_';
					charArray[j]='_';
					charArray[j2]='_';
					list.add(new String(charArray));
				}
			}
		}
		return list;
	}
	/**
	 * @param plate
	 * @return
	 */
	public static List<String> replacePlateFour(String plate) {
		List<String> list=new ArrayList<>();
		for (int i = 0; i < plate.length()-3; i++) {
			for (int j = i+1; j < plate.length()-2; j++) {
				for (int j2 = j+1; j2 < plate.length()-1; j2++) {
					for (int k = j2+1; k < plate.length(); k++) {
						char[] charArray = plate.toCharArray();
						charArray[i]='_';
						charArray[j]='_';
						charArray[j2]='_';
						charArray[k]='_';
						list.add(new String(charArray));
					}
				}
			}
		}
		return list;
	}
}
