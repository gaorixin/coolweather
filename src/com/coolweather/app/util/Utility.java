package com.coolweather.app.util;
import java.text.SimpleDateFormat;
import java.util.Locale;

import org.json.JSONException;
import org.json.JSONObject;

import android.content.Context;
import android.content.SharedPreferences;
import android.preference.PreferenceManager;
import android.text.TextUtils;
import android.util.Log;

import com.coolweather.app.model.City;
import com.coolweather.app.model.CoolWeatherDB;
import com.coolweather.app.model.County;
import com.coolweather.app.model.Province;

public class Utility {

	public synchronized static boolean handleProvincesResponse(CoolWeatherDB coolWeatherDB, String response) {
		
		if (!TextUtils.isEmpty(response)) {
			String[] allProvinces = response.split(",");//服务器返回的文本信息为：01|北京，02|上海，03|天津，04|重庆，05|黑龙江...
			if (allProvinces != null && allProvinces.length >0) {
			    for (String p : allProvinces) {
				    String[] array = p.split("\\|");
				    Province province =new Province();
				    province.setProvinceCode(array[0]);
				    province.setProvinceName(array[1]);
				    coolWeatherDB.saveProvince(province);
			    }
			    return true;
		    }
		
	    }
	    return false;
	}
	
	public static boolean handleCitiesResponse(CoolWeatherDB coolWeatherDB, String response, int provinceId) {
		
		if (!TextUtils.isEmpty(response)) {
			String[] allCities = response.split(",");//服务器返回的文本信息为：1901|南京，1902|无锡，1903|镇江，1904|苏州，1905|南通...
			if (allCities != null && allCities.length >0) {
				for (String c : allCities) {
					String[] array = c.split("\\|");
					City city =new City();
					city.setCityCode(array[0]);
					city.setCityName(array[1]);
					city.setProvinceId(provinceId);
					coolWeatherDB.saveCity(city);
				}
				return true;
			}
			
		}
		return false;
	}
	
	public static boolean handleCountiesResponse(CoolWeatherDB coolWeatherDB, String response, int cityId) {
		
		if (!TextUtils.isEmpty(response)) {
			String[] allCounties = response.split(",");//服务器返回的文本信息为：190401|苏州，190402|常熟，190403|张家港，190404|昆山，190405|吴县东山...
			if (allCounties != null && allCounties.length >0) {
				for (String c : allCounties) {
					String[] array = c.split("\\|");
					County county  =new County();
					county.setCountyCode(array[0]);
					county.setCountyName(array[1]);
					county.setCityId(cityId);
					coolWeatherDB.saveCounty(county);
				}
				return true;
			}
			
		}
		return false;
	}
	
//	解析服务器返回的JSON数据，并将解析出的数据存储到本地
	
	public static void handleWeatherResponse(Context context,String response){
		try {
			JSONObject jsonObject = new JSONObject(response);
			JSONObject weatherInfo = jsonObject.getJSONObject("weatherinfo");
			String cityName = weatherInfo.getString("city");
			String weatherCode = weatherInfo.getString("cityid");
			String temp1 = weatherInfo.getString("temp1");
			String temp2 = weatherInfo.getString("temp2");
			String weatherDesp = weatherInfo.getString("weather");
			String publishTime = weatherInfo.getString("ptime");
			saveWeatherInfo(context, cityName, weatherCode, temp1, temp2, weatherDesp, publishTime);
			Log.d("Utility_weahterinfo", temp1 + temp2);
						
		} catch (JSONException e) {
			// TODO: handle exception
			e.printStackTrace();
		}
	}
	
	public static void saveWeatherInfo (Context context,String cityName,String weatherCode,String temp1,String temp2,String weatherDesp,String publishTime){
		SimpleDateFormat sdf = new SimpleDateFormat("yyyy年M月d日", Locale.CHINA);
		SharedPreferences.Editor editor = PreferenceManager.getDefaultSharedPreferences(context).edit();
		editor.putBoolean("city_selected", true);
		editor.putString("city_name", cityName);
		editor.putString("weather_code", weatherCode);
		editor.putString("temp1", temp1);
		editor.putString("temp2", temp2);
		editor.putString("weather_desp", weatherDesp);
		editor.putString("publish_time", publishTime);
		editor.putString("current_date", sdf.format(new java.util.Date()));
		editor.commit();
	}
}
