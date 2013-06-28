package deri.sensor.utils;

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Date;
import org.joda.time.*;
import org.joda.time.format.*;
/**
 * @author Hoan Nguyen Mau Quoc
 */
public class DateUtil {
	
	/**
	 * @param time the format should be like:"yyyyMMddHHmmss"
	 * @return the Date
	 */
	public static Date fullFormatDigits2Date(String time){
		Date date = null;
		if(time.matches("\\d{14}")){
			String format = "yyyyMMddHHmmss";
			SimpleDateFormat sdf = new SimpleDateFormat(format);
			try {
				date = sdf.parse(time);
			} catch (ParseException e) {
				e.printStackTrace();
			}
		}
		return date;
	}
	
	/**
	 * parse the time to string under the format.
	 * @param time
	 * @param format
	 * @return
	 */
	public static String date2FormatString(Date time, String format){
		String result = "";
		SimpleDateFormat sdf = new SimpleDateFormat(format);
		try {
			result = sdf.format(time);
		} catch (Exception e) {
			e.printStackTrace();
		}
		return result;
	}
	
	public static Date RFC822Section5Format_to_Date(String time){
		String format = "EEE, dd MMM yyyy hh:mm a";
		Date date = string2Date(time,format);
		return date;
	}
	
	public static Date RFC822WUnderGroundFormat_to_date(String time){
		String format = "EEE, dd MMM yyyy hh:mm:ss";
		Date date = string2Date(time,format);
		return date;
	}
		
	public static boolean isRFC822WUnderGroundFormat(String time){
		String regex = "EEE, dd MMM yyyy hh:mm:ss";
		SimpleDateFormat sdf = new SimpleDateFormat(regex);
		try {
			sdf.parse(time);
		} catch (ParseException e) {
			return false;
		}
		return true;
	}
	
	public static String date2StandardString(Date time){
		return date2FormatString(time,"yyyy-MM-dd'T'HH:mm:ss");
	}
	
	public static Date standardString2Date(String time){
		return string2Date(time,"yyyy-MM-dd'T'HH:mm:ss");
	}
	
	public static String getYearMonthDay(Date date){
		Calendar calendar = Calendar.getInstance();
		calendar.setTime(date);
		int year = calendar.get(Calendar.YEAR);
		int month = calendar.get(Calendar.MONTH) + 1;
		String month_str = (month < 10 ? ("0" + month) : month + "");
		int day = calendar.get(Calendar.DAY_OF_MONTH);
		String day_str = (day < 10 ?("0" + day) : "" + day);
		return year + "-" + month_str + "-" + day_str;
	}
	
	public static Date string2Date(String time, String regex){
		Date date = null;
		SimpleDateFormat sdf = new SimpleDateFormat(regex);
		try {
			date = sdf.parse(time);
		} catch (ParseException e) {
			e.printStackTrace();
		}
		return date;
	}

	/**
	 * time1 < time2 + days
	 * @param time1
	 * @param time2
	 * @param days
	 * @return
	 */
	public static boolean isBefore_day(Date time1, Date time2, int days){
		if(time2 == null){
			return false;
		}else if(time1 == null){
			return true;
		}
		boolean result = false;
		Calendar calendar1 = Calendar.getInstance();
		calendar1.setTime(time1);
		Calendar calendar2 = Calendar.getInstance();
		calendar2.setTime(time2);
		calendar2.add(Calendar.DAY_OF_MONTH, days);
		if(calendar1.before(calendar2)){
			result = true;
		}
		return result;
	}
	
	public static boolean isBefore(Date time1, Date time2){
		return isBefore_day(time1,time2,0);
	}
	
	/**
	 * time1 < time2 - 7_days
	 * @param time1
	 * @param time2
	 * @return
	 */
	public static boolean isBeforeOneWeek(Date time1, Date time2){
		return isBefore_day(time1,time2,-7);
	}
	
	public static void main(String[] args) {
		String time = "2012-05-26T09:24:49+07:00";
		Date date = DateUtil.string2Date(time,"yyyy-MM-dd'T'HH:mm:ss");
		System.out.println(date.toString());		
//		DateTimeFormatter parser = ISODateTimeFormat.dateTime();
//        DateTime dt = parser.parseDateTime(time);
//        
//        DateTimeFormatter formatter = DateTimeFormat.mediumDateTime();
//        System.out.println(dt.toString());
	}
	
}
