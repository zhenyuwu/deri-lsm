package ssc.utils;

public class ThreadUtil {
	
	public static void sleepForSomeTime(long time){
		try {
			Thread.sleep(time);
		} catch (InterruptedException e) {
			e.printStackTrace();
		}
	}
	
	public static void sleepForDays(int day){
		sleepForSomeTime(day * 24 * 60 * 60 * 1000);
	}
	
	public static void sleepForHours(int hour){
		sleepForSomeTime(hour * 60 * 60 * 1000);
	}
	
	public static void sleepForMinutes(int minute){
		sleepForSomeTime(minute * 60 * 1000);
	}
	
	public static void sleepForSeconds(int second){
		sleepForSomeTime(second * 1000);
	}
}
