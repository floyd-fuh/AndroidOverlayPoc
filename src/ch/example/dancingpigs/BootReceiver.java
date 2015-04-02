package ch.example.dancingpigs;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;

public class BootReceiver extends BroadcastReceiver {   

    @Override
    public void onReceive(Context context, Intent intent) {
    	Intent overlayServiceIntent = new Intent(context, MainService.class);
    	if(MainService.getInstance() == null) 
    		context.startService(overlayServiceIntent);
    }
}
