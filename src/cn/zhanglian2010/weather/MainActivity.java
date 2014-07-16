package cn.zhanglian2010.weather;

import java.io.BufferedReader;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.net.HttpURLConnection;
import java.net.URL;

import android.app.Activity;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.view.Menu;
import android.widget.TextView;
import cn.zhanglian2010.weather.bean.Weather;
import cn.zhanglian2010.weather.bean.WeatherInfo;

import com.google.gson.Gson;

public class MainActivity extends Activity {

	private TextView location;
	
	private TextView weather;
	
	private TextView other;
	
	private static final String WEATHER_URL = "http://m.weather.com.cn/atad/101040300.html";
	
	
	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_main);
		
		location = (TextView) findViewById(R.id.locationId);
		weather = (TextView) findViewById(R.id.weatherId);
		other = (TextView) findViewById(R.id.nextId);
		
		asyncFetchData();
		
	}
	
	
	private Handler handler = new Handler() {

		public void handleMessage(Message msg) {
			Weather w = (Weather) msg.obj;
			WeatherInfo info = w.getWeatherinfo();
			
			location.setText(info.getCity() + "|" + info.getDate_y());
			weather.setText(info.getWeather1() + "|" + info.getTemp1() + "|" + info.getWind1());
			other.setText(info.getWeather2() + "|" + info.getTemp2() + "|" + info.getWind2());
			
		};
	};
	
	
	private void asyncFetchData(){
		new Thread(){
			@Override
			public void run() {
				super.run();
				
				Weather w = fetchWeatherInfo();
				
				Message msg = handler.obtainMessage();
				msg.obj = w;
				
				handler.sendMessage(msg);
			}
		}.start();
	}
	
	
	private Weather fetchWeatherInfo(){
		Weather w = null;
		try{
			URL url = new URL(WEATHER_URL);
			HttpURLConnection urlConn = (HttpURLConnection) url.openConnection();
			InputStream inputStream = urlConn.getInputStream();
			
			StringBuilder builder = new StringBuilder();
			
			BufferedReader reader = new BufferedReader(
					new InputStreamReader(inputStream));
			
			String str = null;
			while( (str = reader.readLine()) != null){
				builder.append(str);
			}
			
			Gson gson = new Gson();
			w = gson.fromJson(builder.toString(), Weather.class);
			
		}catch(Exception e){
			e.printStackTrace();
		}
		
		return w;
	}

	@Override
	public boolean onCreateOptionsMenu(Menu menu) {
		getMenuInflater().inflate(R.menu.main, menu);
		return true;
	}

}
