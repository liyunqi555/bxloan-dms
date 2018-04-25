package com.coamctech.bxloan.dms.utils;

import java.io.BufferedInputStream;
import java.io.BufferedOutputStream;
import java.io.BufferedReader;
import java.io.DataOutputStream;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.OutputStream;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import java.util.Random;
import java.util.Vector;

/**
 * 本类是文件工具类，主要包括对文件的各种操作、readFromFile方法的功能是从文件中读数据；isFileExists方法的功能是 判断文件是否存在；
 * deleteFile方法的功能是删除文件；fileToVector方法的功能是将文件中内容转换城vecotr；vectorToFile方法的功能是将vector转换成文件；
 * 
 */
public class FileUtil {
	private DataOutputStream dos;
	private static final int BUFFER_SIZE = 16 * 1024;

	/**
	 * 向文件中写数据
	 * 
	 * @param fileName
	 *            文件的名称
	 * @param dataLine
	 *            写入的字符串
	 * @param isAppendMode
	 *            如果为 true，则将字节写入文件末尾处，而不是写入文件开始处
	 * @param isNewLine
	 *            是否从新的行开始写入。
	 * @return boolean 是否操作成功（若操作成功返回true，否则返回false）
	 */
	public boolean writeToFile(String fileName, String dataLine,
			boolean isAppendMode, boolean isNewLine) {
		if (isNewLine) {
			dataLine = "\n" + dataLine;
		}
		try {
			File outFile = new File(fileName);
			if (isAppendMode) {
				dos = new DataOutputStream(new FileOutputStream(fileName, true));
			} else {
				dos = new DataOutputStream(new FileOutputStream(outFile));
			}
			dos.writeBytes(dataLine);
			dos.close();
		} catch (FileNotFoundException ex) {
			return (false);
		} catch (IOException ex) {
			return (false);
		}
		return (true);
	}

	/**
	 * 从文件中读数据
	 * 
	 * @param fileName
	 *            文件的名称
	 * @return String 从文件中读出的字符串
	 */
	public String readFromFile(String fileName) {
		String DataLine = "";
		try {
			File inFile = new File(fileName);
			BufferedReader br = new BufferedReader(new InputStreamReader(
					new FileInputStream(inFile)));
			InputStream i = new FileInputStream(inFile);
			BufferedInputStream buffered = new BufferedInputStream(i);
			
			DataLine = br.readLine();
			br.close();
		} catch (FileNotFoundException ex) {
			return (null);
		} catch (IOException ex) {
			return (null);
		}
		return (DataLine);
	}

	/**
	 * 判断文件是否存在
	 * 
	 * @param fileName
	 *            文件的名称
	 * @return boolean 是否存在 （若存在返回true，否则返回false）
	 */
	public boolean isFileExists(String fileName) {
		File file = new File(fileName);
		return file.exists();
	}

	/**
	 * 将文件中内容转换成vecotr
	 * 
	 * @param fileName
	 *            文件的名称
	 * @return Vector 文件中内容转换后的容器
	 */
	public Vector fileToVector(String fileName) {
		Vector v = new Vector();
		String inputLine;
		try {
			File inFile = new File(fileName);
			BufferedReader br = new BufferedReader(new InputStreamReader(
					new FileInputStream(inFile)));
			while ((inputLine = br.readLine()) != null) {
				v.addElement(inputLine.trim());
			}
			br.close();
		} catch (FileNotFoundException ex) {
			//
		} catch (IOException ex) {
			//
		}
		return (v);
	}

	/**
	 * 将vector转换成文件
	 * 
	 * @param v
	 *            被转换的vector
	 * @param fileName
	 *            文件的名称
	 */
	public void vectorToFile(Vector v, String fileName) {
		for (int i = 0; i < v.size(); i++) {
			writeToFile(fileName, (String) v.elementAt(i), true, true);
		}
	}

	/**
	 * 文件拷贝
	 * 
	 * @param src
	 *            源文件
	 * @param dst
	 *            目标文件
	 * @return
	 */
	public static boolean copyFile(File src, File dst) {
		boolean result = false;
		InputStream in = null;
		OutputStream out = null;
		try {
			in = new BufferedInputStream(new FileInputStream(src), BUFFER_SIZE);
			out = new BufferedOutputStream(new FileOutputStream(dst),
					BUFFER_SIZE);
			byte[] buffer = new byte[BUFFER_SIZE];
			int len = 0;
			while ((len = in.read(buffer)) > 0) {
				out.write(buffer, 0, len);
			}
			result = true;
		} catch (Exception e) {
			e.printStackTrace();
			result = false;
		} finally {
			if (null != in) {
				try {
					in.close();
				} catch (IOException e) {
					e.printStackTrace();
				}
			}
			if (null != out) {
				try {
					out.close();
				} catch (IOException e) {
					e.printStackTrace();
				}
			}
		}
		return result;
	}

	/**
	 * 文件拷贝
	 * 
	 * @param src
	 *            源文件
	 * @param dst
	 *            目标文件
	 * @return
	 */
	public static boolean copyFile(String src, String dst) {
		File srcfile = new File(src);
		File dstfile = new File(dst);
		return copyFile(srcfile, dstfile);
	}

	/**
	 * 随机得到文件名称格式：yyyyMMddHHmmss+六位随机数字
	 * 
	 * @return
	 */
	public static String getRandomFileName(String fileName) {
		SimpleDateFormat sf = new SimpleDateFormat("yyyyMMddHHmmss");
		Date date = new Date();
		Random random = new Random();
		String fileStr = sf.format(date) + random.nextInt(10000);
		int i = fileName.lastIndexOf('.');
		String str = fileName.substring(i + 1);
		return fileStr + "." + str;
	}

	/**
	 * 根据指定路径得到该路径下面所有文件
	 * 
	 * @param filePath
	 * @return
	 */
	public static List<String> getFileNames(String filePath) {
		List<String> fileNames = new ArrayList<String>();
		File f = new File(filePath);
		if (f.isDirectory()) {
			File[] files = f.listFiles();
			for (File file : files) {
				String fileName = file.getName();
				fileNames.add(fileName);
			}
		}
		return fileNames;
	}

	// 根据规则，生成固定格式的文件名
	public static String getFileName(String originalFileName) {
		java.util.Date fileDate = new java.util.Date();
		java.text.SimpleDateFormat myDateFormat = new java.text.SimpleDateFormat(
				"yyyyMMddHHmmss");
		Random random = new Random();

		String realName = originalFileName;
		int ind = realName.lastIndexOf(".");
		String ext = realName.substring(ind).trim().toLowerCase();
		String fileName = (new Integer(random.nextInt(1000000))).toString()
				+ "_";
		fileName += myDateFormat.format(fileDate) + ext;

		return fileName;

	}

	/**
	 * 删除文件，可以是单个文件或文件夹
	 * 
	 * @param fileName
	 *            待删除的文件名
	 * @return 文件删除成功返回true,否则返回false
	 */
	public static boolean delete(String fileName) {
		File file = new File(fileName);
		if (!file.exists()) {
			System.out.println("删除文件失败：" + fileName + "文件不存在");
			return false;
		} else {
			if (file.isFile()) {

				return deleteFile(fileName);
			} else {
				return deleteDirectory(fileName);
			}
		}
	}

	/**
	 * 删除单个文件
	 * 
	 * @param fileName
	 *            被删除文件的文件名
	 * @return 单个文件删除成功返回true,否则返回false
	 */
	public static boolean deleteFile(String fileName) {
		File file = new File(fileName);
		if (file.isFile() && file.exists()) {
			file.delete();
			System.out.println("删除单个文件" + fileName + "成功！");
			return true;
		} else {
			System.out.println("删除单个文件" + fileName + "失败！");
			return false;
		}
	}

	/**
	 * 删除目录（文件夹）以及目录下的文件
	 * 
	 * @param dir
	 *            被删除目录的文件路径
	 * @return 目录删除成功返回true,否则返回false
	 */
	public static boolean deleteDirectory(String dir) {
		// 如果dir不以文件分隔符结尾，自动添加文件分隔符
		if (!dir.endsWith(File.separator)) {
			dir = dir + File.separator;
		}
		File dirFile = new File(dir);
		// 如果dir对应的文件不存在，或者不是一个目录，则退出
		if (!dirFile.exists() || !dirFile.isDirectory()) {
			System.out.println("删除目录失败" + dir + "目录不存在！");
			return false;
		}
		boolean flag = true;
		// 删除文件夹下的所有文件(包括子目录)
		File[] files = dirFile.listFiles();
		for (int i = 0; i < files.length; i++) {
			// 删除子文件
			if (files[i].isFile()) {
				flag = deleteFile(files[i].getAbsolutePath());
				if (!flag) {
					break;
				}
			}
			// 删除子目录
			else {
				flag = deleteDirectory(files[i].getAbsolutePath());
				if (!flag) {
					break;
				}
			}
		}

		if (!flag) {
			System.out.println("删除目录失败");
			return false;
		}

		// 删除当前目录
		if (dirFile.delete()) {
			System.out.println("删除目录" + dir + "成功！");
			return true;
		} else {
			System.out.println("删除目录" + dir + "失败！");
			return false;
		}
	}
}