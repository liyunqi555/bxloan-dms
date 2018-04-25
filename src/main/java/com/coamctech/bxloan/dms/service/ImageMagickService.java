package com.coamctech.bxloan.dms.service;

import java.io.IOException;
import java.util.Properties;

import org.im4java.core.ConvertCmd;
import org.im4java.core.IM4JavaException;
import org.im4java.core.IMOperation;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import com.coamctech.bxloan.dms.conf.DmsConfig;
import com.coamctech.bxloan.dms.utils.ImageUtils;

@Service
public class ImageMagickService {
	
	protected Logger logger = LoggerFactory.getLogger(getClass());
	
	@Autowired
	private DmsConfig dmsConfig;
	
	/**
	 * 原始图片处理
	 * @param width
	 * @param height
	 * @param file
	 */
	public void dealImage(String oldFilePath){
		this.makeThuImage(oldFilePath);
		this.makeMidImage(oldFilePath);
	}
	
	/**
	 * 生成缩中图
	 * @param oldFilePath
	 */
	private void makeMidImage(String oldFilePath){
		synchronized (this) {
			String fileLastName = oldFilePath.substring(oldFilePath.indexOf("."));//文件后缀名
			String tempStr = oldFilePath.substring(0, oldFilePath.lastIndexOf("."));
			String midFilePath = tempStr+"_mid"+fileLastName;
			logger.info("midFilePath=={}",midFilePath);
			String compressMethod = dmsConfig.getCompressMethod();
			if("IM4JAVA".equals(compressMethod)){
				IMOperation operationMid = new IMOperation();
				operationMid.addImage(oldFilePath);
				operationMid.resize(300, 300);  
				operationMid.addImage(midFilePath);
				ConvertCmd cmd = new ConvertCmd();
				cmd.setSearchPath("E:\\Program Files\\ImageMagick-7.0.7-Q16");  //Windows需要设置，Linux不需要
				try {
					cmd.run(operationMid);
					logger.info("resize midFilePath=={} successs!",midFilePath);
				} catch (IOException | InterruptedException | IM4JavaException e) {
					e.printStackTrace();
					logger.info("resize midFilePath=={} fail!",midFilePath);
				}
			}else if("BufferedImage".equals(compressMethod)){
				try {
					ImageUtils.scale(oldFilePath, midFilePath, 300,300);
					logger.info("resize midFilePath=={} successs!",midFilePath);
				} catch (IOException e) {
					e.printStackTrace();
					logger.info("resize midFilePath=={} fail!",midFilePath);
				}
			}
			
			
		}
		
	}
	
	/**
	 * 生成缩略图
	 * @param oldFilePath
	 */
	private void makeThuImage(String oldFilePath){
		synchronized (this) {
			String fileLastName = oldFilePath.substring(oldFilePath.indexOf("."));//文件后缀名
			String tempStr = oldFilePath.substring(0, oldFilePath.lastIndexOf("."));
			String thuFilePath = tempStr+"_thu"+fileLastName;
			logger.info("thuFilePath=={}",thuFilePath);
			String compressMethod = dmsConfig.getCompressMethod();
			if("IM4JAVA".equals(compressMethod)){
				IMOperation operationThu = new IMOperation();
				operationThu.addImage(oldFilePath);
				operationThu.resize(100, 100);  
				operationThu.addImage(thuFilePath);
				ConvertCmd cmd = new ConvertCmd();
				if(isOSWindows()){
					cmd.setSearchPath("E:\\Program Files\\ImageMagick-7.0.7-Q16");  //Windows需要设置，Linux不需要
				}
				try {
					cmd.run(operationThu);
					logger.info("resize thuFilePath=={} successs!",thuFilePath);
				} catch (IOException | InterruptedException | IM4JavaException e) {
					e.printStackTrace();
					logger.info("resize thuFilePath=={} fail!",thuFilePath);
				}
			}else if("BufferedImage".equals(compressMethod)){
				try {
					ImageUtils.scale(oldFilePath, thuFilePath, 100,100);
					logger.info("resize thuFilePath=={} successs!",thuFilePath);
				} catch (IOException e) {
					e.printStackTrace();
					logger.info("resize thuFilePath=={} fail!",thuFilePath);
				}
			}
			
			
		}
		
	}
	
	
	/**
	 * 判断当前操作系统是否为windows
	 */
	public static boolean isOSWindows() {
        Properties prop = System.getProperties();
        String os = prop.getProperty("os.name");
        if (os != null && os.toLowerCase().startsWith("win")) {
            return true;
        } else {
            return false;
        }
    } 
	

}
