package ch.example.dancingpigs;

import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;

public class MainActivity extends Activity {
	
    @Override
    protected void onCreate(Bundle savedInstanceState) {    
        super.onCreate(savedInstanceState);
		setContentView(R.layout.main);
        startUpService(); 
        setTextView();
    }
    
    @Override
    protected void onStart() {    
        super.onStart();
		setContentView(R.layout.main);
        setTextView();
    }
    
    private void startUpService(){
        Intent intent = new Intent(this, MainService.class);
        if(MainService.getInstance() == null){
            startService(intent);
        }
    }
    
    private void setTextView(){
    }
    
}