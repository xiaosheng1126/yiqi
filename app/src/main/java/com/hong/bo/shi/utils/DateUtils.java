package com.hong.bo.shi.utils;

import android.support.annotation.NonNull;

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Date;
import java.util.Locale;

public class DateUtils {

	/**
	 * yyyy-MM-dd
	 */
	public final static String FORMAT_DATE = "yyyy-MM-dd";

	/**
	 * yyyy-MM-dd HH:mm:ss
	 */
	public final static String FORMAT_DATETIME = "yyyy-MM-dd HH:mm:ss";

	public final static String TIME = "HH:mm:ss";
	/**
	 * HH:mm:
	 */
	public final static String IME_HHCMMCSS = "HH:mm";
	public final static String IME_MMDD = "MM-dd";


	/**
	 * 中国的星期格式
	 */
	private final static String[] chineseWeekNames = { "星期日", "星期一", "星期二",
			"星期三", "星期四", "星期五", "星期六" };


	/**
	 * 将Date转换成为固定格式的string
	 *
	 * @param source
	 *            date 对象
	 * @param format
	 *            要格式化得字符串
	 * @return
	 */
	public static String dateToString(Date source, String format) {
		if (source == null || format == null) {
			return "";
		}
		String tmpString = "";
		SimpleDateFormat simpleDateFormat = new SimpleDateFormat(format, Locale.CHINESE);
		try {
			tmpString = simpleDateFormat.format(source);
		} catch (Exception e) {
		}
		return tmpString;

	}

	public static String getTime(long time){
		Calendar cal = Calendar.getInstance();
		cal.setTime(new Date(time));
		return dateToString(cal.getTime(), TIME);
	}

	public static String getFormatDate(long timeStamp){
		if(timeStamp <= 0){
			timeStamp = System.currentTimeMillis();
		}
		return dateToString(new Date(timeStamp), FORMAT_DATETIME);
	}

	/**
	 * 获取简单的时间
	 * @param timeStamp
	 * @return
	 */
	public static String getFormatSimpleDate(long timeStamp){
		if(timeStamp <= 0){
			timeStamp = System.currentTimeMillis();
		}
		Calendar currentCal = Calendar.getInstance();
		Calendar cal = Calendar.getInstance();
		cal.setTime(new Date(timeStamp));
		if(isSameDay(currentCal, cal)){//同一天
			return dateToString(cal.getTime(), IME_HHCMMCSS);
		}
		if(isYesterday(currentCal, cal)){
			return "昨天 " + dateToString(cal.getTime(), IME_HHCMMCSS);
		}
		if(isSameWeek(currentCal, cal)){
			return chineseWeekNames[cal.get(Calendar.DAY_OF_WEEK)] + " " +
					dateToString(cal.getTime(), IME_HHCMMCSS);
		}
		return dateToString(cal.getTime(), FORMAT_DATE);
	}

	/**
	 * 是不是同一天
	 * @return
     */
	private static boolean isSameDay(@NonNull Calendar currentCal, @NonNull Calendar calendar){
		if(isSameMonth(currentCal, calendar)){
			return currentCal.get(Calendar.DAY_OF_MONTH) == calendar.get(Calendar.DAY_OF_MONTH);
		}else{
			return false;
		}
	}

	/**
	 * 是不是昨天
	 * @return
	 */
	private static boolean isYesterday(@NonNull Calendar currentCal, @NonNull Calendar calendar){
		if(isSameMonth(currentCal, calendar)){
			return currentCal.get(Calendar.DAY_OF_MONTH) - calendar.get(Calendar.DAY_OF_MONTH) == 1;
		}else{
			return false;
		}
	}

	/**
	 * 是不是同一个星期
	 * @return
	 */
	private static boolean isSameWeek(@NonNull Calendar currentCal, @NonNull Calendar calendar){
		if(isSameYear(currentCal, calendar)) {
			return currentCal.get(Calendar.WEEK_OF_YEAR) == calendar.get(Calendar.WEEK_OF_YEAR);
		}else{
			return false;
		}
	}

	/**
	 * 是不是同一个月
	 * @return
	 */
	private static boolean isSameMonth(@NonNull Calendar currentCal, @NonNull Calendar calendar){
		if(isSameYear(currentCal, calendar)){
			return currentCal.get(Calendar.MONTH) == calendar.get(Calendar.MONTH);
		}else{
			return false;
		}
	}

	/**
	 * 是不是同一年
	 * @return
	 */
	private static boolean isSameYear(@NonNull Calendar currentCal, @NonNull Calendar calendar){
		return currentCal.get(Calendar.YEAR) == calendar.get(Calendar.YEAR);
	}


	public static long stringToLong(String date){
		SimpleDateFormat format = new SimpleDateFormat(FORMAT_DATETIME);
		try {
			Date parse = format.parse(date);
			return parse.getTime();
		} catch (ParseException e) {
			e.printStackTrace();
		}
		return 0;
	}

}
