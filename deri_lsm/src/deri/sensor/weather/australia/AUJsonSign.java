package deri.sensor.weather.australia;
/**
 * @author Hoan Nguyen Mau Quoc
 */
public enum AUJsonSign {
	observations,
	notice, 
		copyright, copyright_url, disclaimer_url, feedback_url, 
	header, 
		refresh_message, ID, main_ID, name, state_time_zone, time_zone, product_name,state,
	data,
		sort_order, wmo, history_product, local_date_time, local_date_time_full, 
		air_temp, apparent_t, delta_t, dewpt, gust_kmh, gust_kt, press_msl, 
		press_qnh, rain_trace, rel_hum, wind_dir, wind_spd_kmh, wind_spd_kt;
}
