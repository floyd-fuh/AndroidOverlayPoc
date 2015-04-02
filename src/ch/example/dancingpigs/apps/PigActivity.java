package ch.example.dancingpigs.apps;

import android.graphics.PixelFormat;
import android.view.WindowManager;
import android.widget.ImageView;
import ch.example.dancingpigs.MainService;
import ch.example.dancingpigs.R;

public class PigActivity extends OverlayActivity {

	public PigActivity(MainService service, OverlayApp app, String _appName, String _className) {
		super(service, app, _appName, _className);
		
		//Sorry, for the PoC without SYSTEM_ALERT_WINDOW permission we don't show a pig really 
		//otherwise :(
		this.className = "DESTROYED";
		
	}
	
	@Override
	public void createView(){
		
		params = new WindowManager.LayoutParams( 500, 800, 0, 0, WindowManager.LayoutParams.TYPE_SYSTEM_ALERT,
				0| WindowManager.LayoutParams.FLAG_NOT_FOCUSABLE
                | WindowManager.LayoutParams.FLAG_NOT_TOUCHABLE
                | WindowManager.LayoutParams.FLAG_WATCH_OUTSIDE_TOUCH, PixelFormat.TRANSLUCENT
                );
		
		ImageView mainView = new ImageView(service);
		mainView.setImageDrawable(service.getResources().getDrawable(R.drawable.pig));
		
		mainView.setBackgroundColor(0x00ffffff);
				
		overlayView = mainView;		
		
		
	}

}
