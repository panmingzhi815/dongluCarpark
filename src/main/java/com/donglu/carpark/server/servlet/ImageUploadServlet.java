package com.donglu.carpark.server.servlet;


import javax.servlet.ServletException;


import com.caucho.hessian.server.HessianServlet;
import com.donglu.carpark.service.CarparkDatabaseServiceProvider;
import com.donglu.carpark.service.ImageServiceI;
import com.google.inject.Inject;

public class ImageUploadServlet extends HessianServlet implements ImageServiceI {

    private static final long serialVersionUID = 884523916637749569L;

    @Inject
    private CarparkDatabaseServiceProvider sp;

	private ImageServiceI imageService;
    
    @Override
    public void init() throws ServletException {
    	imageService = sp.getImageService();
    }
	@Override
	public String saveImageInServer(byte[] image, String imageName) {
		return imageService.saveImageInServer(image, imageName);		
	}
	@Override
	public byte[] getImage(String imageName) {
		return imageService.getImage(imageName);
	}
	@Override
	public String getImagePath(String image) {
		return imageService.getImagePath(image);
	}

}
