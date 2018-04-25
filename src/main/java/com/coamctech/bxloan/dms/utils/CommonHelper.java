package com.coamctech.bxloan.dms.utils;

import java.math.BigDecimal;
import java.math.RoundingMode;
import java.sql.Timestamp;
import java.text.DateFormat;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Arrays;
import java.util.Date;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Locale;
import java.util.Map;
import java.util.Set;

import org.apache.commons.lang3.StringUtils;
import org.springframework.format.number.CurrencyStyleFormatter;

/**
 * 通用辅助类
 * 
 * @author AcoreHeng
 */
public final class CommonHelper {
	private final static String EMPTY = "";
	/** 逗号分隔符 */
	public final static String SEPARATOR_COMMA = ",";
	/** 竖直线分隔符 */
	public final static String SEPARATOR_VERTICAL = "|";
	/**冒号分隔符*/
	public final static String SEPARATOR_COLON = ":";
	/** 换行分隔符 */
	public final static String NEW_LINE = "\n";
	public final static String DF_DATE_TIME = "yyyy-MM-dd HH:mm:ss";
	public final static String DF_DATE_SHORT_TIME = "yyyy-MM-dd HH:mm";
	public final static String DF_DATE = "yyyy-MM-dd";
	public final static String DF_TIME = "HH:mm:ss";
	public final static String DF_STYLE_YYYYMMDD = "yyyyMMdd";
	private static Map<String, DateFormat> DF_MAP;
	static {
		DF_MAP = new HashMap<String, DateFormat>();
		DF_MAP.put(DF_DATE, new SimpleDateFormat(DF_DATE));
		DF_MAP.put(DF_DATE_SHORT_TIME, new SimpleDateFormat(DF_DATE_SHORT_TIME));
		DF_MAP.put(DF_DATE_TIME, new SimpleDateFormat(DF_DATE_TIME));
	}
	public static String formatCurrency(BigDecimal currency){
		CurrencyStyleFormatter currencyFormatter = new CurrencyStyleFormatter();  
		currencyFormatter.setFractionDigits(0);//保留小数点后几位  
		currencyFormatter.setRoundingMode(RoundingMode.CEILING);//舍入模式（ceilling表示四舍五入）
		return currencyFormatter.print(currency, Locale.CHINA);
	}
	// TODO String
	/**
	 * 判断字符串不能是null、""、"null"(忽略大小写)
	 * 
	 * @param str
	 * @return
	 */
	public static boolean isNotNull(String str) {
		return StringUtils.isNoneBlank(str) && !"null".equalsIgnoreCase(str);
	}

	/**
	 * Object转String
	 * 
	 * @param obj
	 * @return
	 */
	public static String toStr(Object obj) {
		if (obj == null) {
			return null;
		}
		return String.valueOf(obj);
	}

	// TODO Long
	/**
	 * Object转Long
	 * 
	 * @param obj
	 * @return
	 */
	public static Long toLong(Object obj) {
		if (obj == null) {
			return null;
		}
		if (obj instanceof BigDecimal) {
			return ((BigDecimal) obj).longValue();
		}
		if (obj instanceof Long) {
			return (Long) obj;
		}
		return Long.parseLong(String.valueOf(obj));
	}
	// TODO Double
	/**
	 * Object转Double
	 * 
	 * @param obj
	 * @return
	 */
	public static Double toDouble(Object obj) {
		if (obj == null) {
			return null;
		}
		if (obj instanceof BigDecimal) {
			return ((BigDecimal) obj).doubleValue();
		}
		if (obj instanceof Double) {
			return (Double) obj;
		}
		return Double.parseDouble(String.valueOf(obj));
	}
	
	
	/**
	 * Object转Long
	 * @param obj
	 * @param defaultVal 默认值
	 * @return
	 */
	public static Long toLong(Object obj,Long defaultVal) {
		if (obj == null) {
			return defaultVal;
		}
		if (obj instanceof BigDecimal) {
			return ((BigDecimal) obj).longValue();
		}
		if (obj instanceof Long) {
			return (Long) obj;
		}
		return Long.parseLong(String.valueOf(obj));
	}

	/**
	 * Object转Int
	 * 
	 * @param obj
	 * @return
	 */
	public static Integer toInt(Object obj) {
		if (obj == null) {
			return null;
		}
		if (obj instanceof BigDecimal) {
			return ((BigDecimal) obj).intValue();
		}
		if (obj instanceof String) {
			return Integer.parseInt(String.valueOf(obj));
		}
		if (obj instanceof Long) {
			return ((Long) obj).intValue();
		}
		return (Integer) obj;
	}

	/**
	 * Object转Int
	 * 
	 * @param obj
	 * @param defaultVal 默认值
	 * @return
	 */
	public static Integer toInt(Object obj, int defaultVal) {
		Integer val = toInt(obj);
		return val != null ? val : defaultVal;
	}

	/**
	 * Object转BigDecimal
	 * 
	 * @param obj
	 * @return
	 */
	public static BigDecimal toBigDecimal(Object obj) {
		if (obj == null) {
			return null;
		}
		if (obj instanceof String) {
			String str = toStr(obj);
			if (StringUtils.isBlank(str)) {
				return null;
			}
			return new BigDecimal(toStr(obj));
		}
		if (obj instanceof Integer) {
			return new BigDecimal(toStr(obj));
		}
		if (obj instanceof Double) {
			return new BigDecimal(toStr(obj));
		}
		return (BigDecimal) obj;
	}
	/**
	 * @param obj
	 * @param defaultVal
	 * @return
	 */
	public static BigDecimal toBigDecimal(Object obj,BigDecimal defaultVal) {
		if (obj == null) {
			return defaultVal;
		}
		if (obj instanceof String) {
			String str = toStr(obj);
			if (StringUtils.isBlank(str)) {
				return defaultVal;
			}
			return new BigDecimal(toStr(obj));
		}
		if (obj instanceof Integer) {
			return new BigDecimal(toStr(obj));
		}
		return (BigDecimal) obj;
	}

	/**
	 * String转BigDecimal
	 * 
	 * @param str
	 * @return
	 */
	public static BigDecimal str2BigDecimal(String str) {
  		if (str == null||"".equals(str)) {
			return BigDecimal.ZERO;
		}
		if (str instanceof String) {
			return new BigDecimal(toStr(str));
		}
		return null;
	}

	/**
	 * Long转Str
	 * 
	 * @param str
	 *            如果为null
	 * @return 如果为null则返回""
	 */
	public static String long2StrIfEmpty(Long val) {
		if (val == null) {
			return EMPTY;
		}
		return String.valueOf(val);
	}
	
	/**
	 * @param bds
	 * @return
	 */
	public static BigDecimal sumOfBigDecimal(BigDecimal... bds){
		if(bds==null||bds.length==0){
			return null;
		}
		BigDecimal r_bd=BigDecimal.ZERO;
		if(bds.length>0){
			for (BigDecimal bd : bds) {
				if(bd!=null){
					r_bd=r_bd.add(bd);
				}
			}
		}
		return r_bd;
	}
	/**
	 * 求差
	 * @param bd
	 * @param bds
	 * @return
	 */
	public static BigDecimal diffOfBigDecimal(BigDecimal bd,BigDecimal... bds){
		if(bd==null||bds==null||bds.length==0){
			return bd;
		}
		BigDecimal r_bd=bd;
		if(bds.length>0){
			for (BigDecimal temp_bd : bds) {
				if(bd!=null){
					r_bd=r_bd.subtract(temp_bd);
				}
			}
		}
		return r_bd;
	}

	// TODO Date
	/**
	 * 获取系统当前时间
	 * 
	 * @return
	 */
	public static Date getNow() {
		return new Date();
	}
	/**
	 * 获取系统当前时间的字符串格式
	 * 
	 * @return
	 */
	public static String getNowStr(String format) {
		if (StringUtils.isBlank(format)) {
			return null;
		}
		DateFormat df = getDateFormat(format);
		return df.format(new Date());
	}
	
	/**
	 * 获取指定格式DateFormat
	 * 
	 * @param format
	 * @return
	 */
	public static DateFormat getDateFormat(String format) {
		if (StringUtils.isBlank(format)) {
			return null;
		}
		DateFormat df = DF_MAP.get(format);
		if (df == null) {
			df = new SimpleDateFormat(format);
		}
		return df;
	}
	

	/**
	 * 格式化日期为指定格式
	 * 
	 * @param date
	 * @param format
	 * @return
	 */
	public static Date formatDate(final Date date, String format) {
		if (date == null) {
			return null;
		}
		if (StringUtils.isBlank(format)) {
			return date;
		}
		Date temp_date = null;
		try {
			DateFormat df = getDateFormat(format);
			temp_date = df.parse(df.format(date));
		} catch (ParseException e) {
			// e.printStackTrace();
		}
		return temp_date;
	}

	/**
	 * 将日期转换为指定格式的字符串
	 * 
	 * @param date
	 * @param format
	 * @return
	 */
	public static String date2Str(Date date, String format) {
		if (date == null || StringUtils.isBlank(format)) {
			return null;
		}
		DateFormat df = getDateFormat(format);
		synchronized(df) {
			return df.format(date);
		}
	}

	/**
	 * 将日期字符串按照指定格式转换为date
	 * 
	 * @param dateStr
	 * @param format
	 * @return
	 */
	public static Date str2Date(String dateStr, String format) {
		if (StringUtils.isBlank(dateStr) || StringUtils.isBlank(format)) {
			return null;
		}
		try {
			DateFormat df = getDateFormat(format);
			synchronized(df) {
				return df.parse(dateStr);
			}
		} catch (ParseException e) {
			e.printStackTrace();
		}
		return null;
	}
	
	/**
	 * 将逗号隔开的字符串转为Set集合
	 * @param str
	 * @return
	 */
	public static Set<String> str2Set(String str){
		return new HashSet<String>(Arrays.asList(StringUtils.split(str,SEPARATOR_COMMA)));
	}
	/**
	 * @param str
	 * @return
	 */
	public static Set<Long> str2SetOfLong(String str){
		String[] strs=StringUtils.split(str,SEPARATOR_COMMA);
		Set<Long> set=new HashSet<Long>();
		if(strs!=null&&strs.length>0){
			for (String s : strs) {
				if(StringUtils.isNumeric(s)){
					set.add(toLong(s));
				}
			}
		}
		return set;
	}
	public static String set2Str(Set<String> set){
		return StringUtils.join(set.toArray(),SEPARATOR_COMMA);
	}
	/**
	 * 将逗号隔开的字符串转为List集合
	 * @param str
	 * @return
	 */
	public static List<String> str2List(String str){
		return Arrays.asList(StringUtils.split(str,SEPARATOR_COMMA));
	}
	/**
	 * 将list转换成字符隔开的字符串
	 * @author:gph
	 * @createTime:2015年5月26日
	 * @param list
	 * @return
	 */
	public static String list2Str(List<?> list, String separator){
		StringBuffer buf = new StringBuffer();
		if (list == null) {
			return null;
		}
		for (int i = 0; i < list.size(); i++) {
			if (i > 0) {
				buf.append(separator);
			}
			buf.append("'");
			buf.append(list.get(i));
			buf.append("'");
		}
		return buf.toString();
	}

	/**
	 * Object转Boolean
	 * 
	 * @param obj
	 * @return
	 */
	public static Boolean toBoolean(Object obj) {
		if (obj == null) {
			return null;
		}
		if (obj instanceof Boolean) {
			return (Boolean) obj;
		}
		return null;
	}
	
	public static Date toDate(Object obj) {
		if (obj == null) {
			return null;
		}
		if (obj instanceof Date) {
			return (Date) obj;
		}
		if (obj instanceof Timestamp) {
			return new Date(((Timestamp) obj).getTime());
		}
		return null;
	}

	/** 
	 * 将集合转换为逗号隔开的字符串
	 *
	 * @param List<String> 集合
	 * @return 逗号隔开的字符串
	 */
	public static String list2Str(List<String> list){
		return StringUtils.join(list.toArray(), SEPARATOR_COMMA);
	}
	
	
	/**
	 * 将带分隔字符串转为List集合
	 * @param str
	 * @return
	 */
	public static List<String> strToList(String str,String format){
		return Arrays.asList(StringUtils.split(str,format));
	}

	/**
	 * 友好时间格式返回
	 * @param date
	 * @return
	 */
	public static String friendlyDate(Date date) {

		long n = new Date().getTime();
		long e = date.getTime();

		if (n - e > 7 * 24 * 60 * 60 * 1000) {
			return new SimpleDateFormat("yyyy-MM-dd").format(date);
		}
		if (n - e > 24 * 60 * 60 * 1000) {
			return (long) Math.floor((n - e) * 1d / (24 * 60 * 60*1000) ) + "天前";
		}
		if (n - e > 60 * 60 * 1000) {
			return (long) Math.floor((n - e) * 1d / (60 * 60*1000) ) + "小时前";
		}
		if (n - e > 60 * 1000) {
			return (long) Math.floor((n - e) * 1d / 60000 ) + "分钟前";
		}
        if (n - e > 1 * 1000) {
            return "刚刚";
        }
		return new SimpleDateFormat("yyyy-MM-dd").format(date);
	}

    public static void main(String[] args) {
        System.out.println(friendlyDate(str2Date("2017-01-11 07:13:01","yyyy-MM-dd HH:mm:ss")));
    }
}
