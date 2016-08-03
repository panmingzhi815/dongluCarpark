package com.donglu.carpark.yun;

import java.io.Serializable;

public class CarparkYunConfig implements Serializable{
	/**
	 * 
	 */
	private static final long serialVersionUID = 2645865659512835663L;
	public final String configFileName = "CarparkYunConfig.properties";
	public static CarparkYunConfig instance;
	
	private String company="";
	private String area="";
	private String companyCode="";
	private String areaCode="";
	private String url="";
	private Boolean autoStartServer=false;
	
	
	private CarparkYunConfig(){
//		saveConfig();
	}
	
	public static CarparkYunConfig getInstance(){
		if(instance == null){
			instance = new CarparkYunConfig();
		}
//		instance.loadConfig();
		
		return instance;
	}
	
//	public void saveConfig(){
//		try (FileOutputStream fos = new FileOutputStream(configFileName, false);PrintWriter out = new PrintWriter(fos);){
//			out.println(String.format("#云服务停车场信息配置  %s", new Date()));
//			out.println("#物业名称");
//			out.println(String.format("%s=%s", "company", getCompany()));
//			out.println("#停车场名称");
//			out.println(String.format("%s=%s", "area", getArea()));
//			out.println();
//			out.flush();
//		} catch (Exception e) {
//			e.printStackTrace();
//		}
//	}
//	
//	private void loadConfig() {
//		Path path = Paths.get(configFileName);
//		if(!Files.exists(path)){
//			try {
//				Files.createFile(path);
//			} catch (IOException e) {
//				e.printStackTrace();
//			}
//		}
//        try (FileInputStream fileInputStream = new FileInputStream(configFileName);){
//        	Properties preferenceStore = new Properties();
//            preferenceStore.load(fileInputStream);
//            this.company=preferenceStore.getProperty("company", company);
//            this.area=preferenceStore.getProperty("company", area);
//        } catch (IOException e) {
//            e.printStackTrace();
//        } 
//	}

	

	public static void main(String[] args) {
//		Path parent = Paths.get(System.getProperty("user.dir")).getParent().getParent();
//		System.out.println(parent.toString() + File.separator + "database" + File.separator);
	}

	public String getCompany() {
		return company;
	}

	public void setCompany(String company) {
		this.company = company;
//		saveConfig();
	}

	public String getArea() {
		return area;
	}

	public void setArea(String area) {
		this.area = area;
//		saveConfig();
	}

	public Boolean getAutoStartServer() {
		return autoStartServer;
	}

	public void setAutoStartServer(Boolean autoStartServer) {
		this.autoStartServer = autoStartServer;
	}

	public String getCompanyCode() {
		return companyCode;
	}

	public void setCompanyCode(String companyCode) {
		this.companyCode = companyCode;
	}

	public String getAreaCode() {
		return areaCode;
	}

	public void setAreaCode(String areaCode) {
		this.areaCode = areaCode;
	}

	public String getUrl() {
		return url;
	}

	public void setUrl(String url) {
		this.url = url;
	}
}
