package com.dl.bomber;

import android.os.Bundle;
import android.app.Activity;
import android.content.pm.ActivityInfo;
import android.view.Menu;
import android.view.Window;
import android.view.WindowManager;

public class Main extends Activity {

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
    	//强制为横屏
    	setRequestedOrientation(ActivityInfo.SCREEN_ORIENTATION_LANDSCAPE);
        //全屏
  		requestWindowFeature(Window.FEATURE_NO_TITLE); 
  		getWindow().setFlags(WindowManager.LayoutParams.FLAG_FULLSCREEN ,  
  		              WindowManager.LayoutParams.FLAG_FULLSCREEN);
  		
  		GameView gameView = new GameView(this);        
        setContentView(gameView);
    }
//
//    @Override
//    public boolean onCreateOptionsMenu(Menu menu) {
//        getMenuInflater().inflate(R.menu.activity_main, menu);
//        return true;
//    }
}
