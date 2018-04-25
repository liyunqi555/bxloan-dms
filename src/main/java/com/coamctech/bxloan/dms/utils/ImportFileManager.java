package com.coamctech.bxloan.dms.utils;

import java.io.File;
import java.util.HashMap;
import java.util.Iterator;
import java.util.Map;
import java.util.Map.Entry;

import org.apache.commons.lang3.StringUtils;

/**
 * 类描述：导入文件的管理器，导入的文件信息保存在内存中，
 * 每次导入都会检查一下所有的临时导入文件，如果文件的创建时间已超过10分钟。
 * 就将临时文件删除掉。
 * @author dfjk
 *
 */
public class ImportFileManager {

	private static Map<String, File> fileMapping = new HashMap<String, File>();
	
	/**
	 * 
	 * addFile
	 * 功能描述：添加一个临时文件
	 * 逻辑描述:
	 * @param fileId	临时文件ID，这个ID是sessionid + "#" + 创建时间
	 * @param file		临时文件对象
	 * @return 无
	 * @throws 异常类型  异常描述
	 * @since Ver 1.00
	 */
	public static void addFile(String fileId, File file) {
		if (StringUtils.isNotBlank(fileId) && file != null) {
			synchronized (fileMapping) {
				clearTimeoutFile();
				fileMapping.put(fileId, file);
			}
		}
	}
	
	/**
	 * 
	 * removeFile
	 * 功能描述：移除一个临时文件
	 * 逻辑描述:
	 * @param fileId	临时文件ID
	 * @return 返回值类型  返回值描述
	 * @throws 异常类型  异常描述
	 * @since Ver 1.00
	 */
	public static void removeFile(String fileId) {
		if (StringUtils.isBlank(fileId)) {
			return;
		}
		
		synchronized (fileMapping) {
			if (fileMapping.containsKey(fileId)) {
				fileMapping.remove(fileId);
			}
		}
	}
	
	/**
	 * 
	 * getFileByFileId
	 * 功能描述：根据FileId获得临时文件的File信息
	 * 逻辑描述:
	 * @param fileId	临时文件ID
	 * @return 有返回临时文件对象，否则返回null
	 * @throws 异常类型  异常描述
	 * @since Ver 1.00
	 */
	public static File getFileByFileId(String fileId) {
		if (StringUtils.isNotBlank(fileId)) {
			synchronized (fileMapping) {
				return fileMapping.get(fileId);
			}
		}
		
		return null;
	}
	
	/**
	 * 
	 * clearTimeoutFile
	 * 功能描述： 清理创建时间超过了10分钟的临时文件
	 * 逻辑描述:
	 * @param 参数名称  参数描述
	 * @return 返回值类型  返回值描述
	 * @throws 异常类型  异常描述
	 * @since Ver 1.00
	 */
	public static void clearTimeoutFile() {
		synchronized (fileMapping) {
			long currentTime = System.currentTimeMillis();
			long timeout = 1000*60*10;
			
			Iterator<Entry<String, File>> iter = fileMapping.entrySet().iterator();
			while (iter.hasNext()) {
				Entry<String, File> entry = iter.next();
				File file = entry.getValue();
				if (currentTime - file.lastModified() > timeout) {
					file.delete();
					iter.remove();
				}
			}
		}
	}
}
