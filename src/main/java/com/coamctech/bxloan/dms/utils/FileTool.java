package com.coamctech.bxloan.dms.utils;

import java.io.BufferedInputStream;
import java.io.BufferedOutputStream;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.LineNumberReader;
import java.io.OutputStream;
import java.text.MessageFormat;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.zip.ZipEntry;
import java.util.zip.ZipOutputStream;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class FileTool {
	private static Logger logger=LoggerFactory.getLogger(FileTool.class);

    public static void writeZip(String filePath,String zipTargetPath) throws IOException {
        OutputStream os = new BufferedOutputStream( new FileOutputStream( zipTargetPath ) );
        ZipOutputStream zos = new ZipOutputStream( os );
        byte[] buf = new byte[8192];
        int len;
        File file = new File( filePath );
        ZipEntry ze = new ZipEntry( file.getName() );
        zos.putNextEntry( ze );
        BufferedInputStream bis = new BufferedInputStream( new FileInputStream(file) );
        while ( ( len = bis.read( buf ) ) > 0 ) {
            zos.write( buf, 0, len );
        }
        zos.closeEntry();
        zos.close();
    }
   
    public static void copyFile(List<Object[]> filePaths,String targetPath,String path) throws IOException {
        if(targetPath!=null && (!"".equals(targetPath))){
        	File targetPathFile = new File(targetPath);
        	if(!targetPathFile.isDirectory()){
        		targetPathFile.mkdirs();
        	}
        }
        Map<String,Integer> sameNameCountMap=new HashMap<String, Integer>();
        for (int i = 0; i < filePaths.size(); i++) {
        	Object[] fileInfo = filePaths.get(i);
        	String fileName = (String) fileInfo[1];//xxx.xx
        	String name = fileName.substring(0,fileName.lastIndexOf("."));//获取不带后缀的文件名 xxx
        	String suffix = fileName.substring(fileName.lastIndexOf("."));//获取后缀以便重组文件名 .xx
        	String newfileName=fileName;
        	if(sameNameCountMap.containsKey(fileName)){
        		int count=sameNameCountMap.get(fileName);
        		count++;//自定增加1
        		sameNameCountMap.put(fileName, count);
        		newfileName=MessageFormat.format("{0}({1}){2}", name,count,suffix);
        	}else{
        		sameNameCountMap.put(fileName, 1);
        	}
        	logger.info(i+"--------File path:{}",targetPath+newfileName);
        	logger.info("......."+path+(String)fileInfo[2]);
        	FileInputStream in = new java.io.FileInputStream(path+(String)fileInfo[2]);  
    		FileOutputStream out = new FileOutputStream(targetPath+newfileName);  
    		byte[] bt = new byte[1024];  
    		int count;  
    		while ((count = in.read(bt)) > 0) { 
    			out.write(bt, 0, count);  
    		} 
    		in.close();  
    		out.close();
    		logger.info("the end of a write method");
        }
    }
    
    /** 
     * 将存放在sourceFilePath目录下的源文件，打包成fileName名称的zip文件，并存放到zipFilePath路径下 
     * @param sourceFilePath :待压缩的文件路径 
     * @param zipFilePath :压缩后存放路径 
     * @param fileName :压缩后文件的名称 
     * @return 
     */  
    public static boolean fileToZip(String sourceFilePath,String zipFilePath,String fileName){  
        boolean flag = false;  
        File sourceFile = new File(sourceFilePath);  
        FileInputStream fis = null;  
        BufferedInputStream bis = null;  
        FileOutputStream fos = null;  
        ZipOutputStream zos = null;  
         
        if(sourceFile.exists() == false){  
        	logger.error("待压缩的文件目录："+sourceFilePath+"不存在.");  
        }else{  
            try {  
                File zipFile = new File(zipFilePath + "/" + fileName +".zip");  
                if(zipFile.exists()){
                	logger.info(zipFilePath + "目录下存在名字为:" + fileName +".zip" +"打包文件.");  
                }else{  
                    File[] sourceFiles = sourceFile.listFiles();  
                    if(null == sourceFiles || sourceFiles.length<1){  
                    	logger.error("待压缩的文件目录：" + sourceFilePath + "里面不存在文件，无需压缩.");  
                    }else{  
                        fos = new FileOutputStream(zipFile);  
                        zos = new ZipOutputStream(new BufferedOutputStream(fos));  
                        byte[] bufs = new byte[1024*10];  
                        for(int i=0;i<sourceFiles.length;i++){  
                            //创建ZIP实体，并添加进压缩包  
                            ZipEntry zipEntry = new ZipEntry(sourceFiles[i].getName());  
                            System.out.println("zipEntry.getName="+zipEntry.getName());
                            zos.putNextEntry(zipEntry);  
                            //读取待压缩的文件并写进压缩包里  
                            fis = new FileInputStream(sourceFiles[i]);  
                            bis = new BufferedInputStream(fis, 1024*10);  
                            int read = 0;  
                            while((read=bis.read(bufs, 0, 1024*10)) != -1){  
                                zos.write(bufs,0,read);  
                            }  
                        }  
                        flag = true;  
                    }  
                }  
            } catch (FileNotFoundException e) {  
                e.printStackTrace();  
                throw new RuntimeException(e);  
            } catch (IOException e) {  
                e.printStackTrace();  
                throw new RuntimeException(e);  
            } finally{  
                //关闭流  
                try {  
                    if(null != bis) bis.close();  
                    if(null != zos) zos.close();  
                } catch (IOException e) {  
                    e.printStackTrace();  
                    throw new RuntimeException(e);  
                }  
            }  
        }  
        return flag;  
    }
    
    /** 
     * 将存放在sourceFilePath目录下的源文件，打包成fileName名称的zip文件，并存放到zipFilePath路径下 
     * @param shellPath : shell脚本存放路径
     * @param sourceFilePath :待压缩的文件路径 
     * @param zipFilePath :压缩后存放路径 
     * @param fileName :压缩后文件的名称 
     * @return 
     */  
    public static String fileToZipByShell(String shellPath,String sourceFilePath,String zipFilePath,String fileName){  
        String rsStr = "0";  
        File sourceFile = new File(sourceFilePath);  
         
        if(sourceFile.exists() == false){  
        	logger.error("待压缩的文件目录："+sourceFilePath+"不存在.");  
            rsStr = "0";
        }else{  
            try {
            	Process pcs = Runtime.getRuntime().exec("sh "+shellPath+" "+zipFilePath+" "+fileName);
            	InputStreamReader ir = new InputStreamReader(pcs.getInputStream());
            	LineNumberReader input = new LineNumberReader(ir);
            	String line = null;    
            	while ((line = input.readLine()) != null){
            		System.out.println(line);
            	}
            	if(null != input){    
            		input.close();    
        		}
            	if(null != ir){    
            		ir.close();    
            	}
            	int extValue = pcs.waitFor(); //返回码 1 表示正常退出0表示异常退出
            	logger.info("extValue=="+extValue);
            	//rsStr = ""+extValue;
            	File tempFile = new File (zipFilePath+fileName+".zip");
            	if(tempFile!=null&&tempFile.isFile()){
            		rsStr = "1";
            	}
            	
            }catch (IOException e) { 
            	rsStr = "0";
                e.printStackTrace();  
                throw new RuntimeException(e);  
            } catch (InterruptedException e) {
            	rsStr = "0";
				e.printStackTrace();
			}
        }  
        return rsStr;  
    } 
    
    public static void  delDir(String filepath) throws IOException {
    	File f = new File(filepath);//定义文件路径
    	if(f.exists() && f.isDirectory()){//判断是文件还是目录  
    		if(f.listFiles().length==0){//若目录下没有文件则直接删除  
    			f.delete();  
    		}else{//若有则把文件放进数组，并判断是否有下级目录  
    			File delFile[]=f.listFiles();  
    			int i =f.listFiles().length;  
    			for(int j=0;j<i;j++){  
    				if(delFile[j].isDirectory()){  
    					delDir(delFile[j].getAbsolutePath());//递归调用del方法并取得子目录路径  
    				}  
    				delFile[j].delete();//删除文件  
    			}  
    		}
    		f.delete();
    	}   
    }
    /**
     * 删除文件或目录
     * @param path
     */
    public static void deleteAllFilesOfDir(File path) {  
        if (!path.exists())  
            return;  
        if (path.isFile()) {  
            path.delete();  
            return;  
        }  
        File[] files = path.listFiles();  
        for (int i = 0; i < files.length; i++) {  
            deleteAllFilesOfDir(files[i]);  
        }  
        path.delete();  
    }
}
