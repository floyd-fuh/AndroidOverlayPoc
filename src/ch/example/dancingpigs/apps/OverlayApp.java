package ch.example.dancingpigs.apps;

import ch.example.dancingpigs.MainService;
import android.util.Log;

public class OverlayApp{
	
	protected OverlayActivity[] activities;
	protected MainService service;
	private String packageName;

	public OverlayApp(MainService service, String _packageName, String _className){
		super();
		this.service = service;
		this.packageName = _packageName;
		activities = new OverlayActivity[]{new OverlayActivity(service, this, _packageName, _className), new OverlayActivityAndroid5(service, this, _packageName, _className)};

	}
	
	public void createViews() {
		for(OverlayActivity activity : activities){
			activity.createView();
		}
	}

	public OverlayActivity getActivityForClassName(String className) {
		for(OverlayActivity activity : activities){
			if(activity.getClassName().equalsIgnoreCase(className)){
				return activity;
			}
		}
		Log.e(this.getClass().getName(), "Couldn't find "+className);
		return null;
			
	}
	
	public OverlayActivity[] getActivities() {
		return this.activities;
	}
	
	public String getPackageName(){
		return packageName;
	}


}
