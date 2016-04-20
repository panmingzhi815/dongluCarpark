package com.donglu.carpark.service;

public interface ImageServiceI {
	String saveImageInServer(byte[] image,String imageName);
	byte[] getImage(String imageName);
}
