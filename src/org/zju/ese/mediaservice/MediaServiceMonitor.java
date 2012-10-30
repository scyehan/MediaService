package org.zju.ese.mediaservice;

import java.util.List;

import android.os.Bundle;
import android.app.Activity;
import android.app.ActivityManager;
import android.content.Context;
import android.content.Intent;
import android.view.Menu;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.EditText;
import android.widget.Switch;

public class MediaServiceMonitor extends Activity {
	Switch switchButton;
	EditText editText;
    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_media_service_monitor);
        switchButton = (Switch)findViewById(R.id.switch1);
        editText = (EditText)findViewById(R.id.editText);
        if(isServiceRunning(this,"org.zju.ese.mediaservice.MediaService"))
        	switchButton.setChecked(true);
        else
        	switchButton.setChecked(false);
        
        //Intent intent = new Intent(this, MediaService.class);
        //startService(intent);
        
        switchButton.setOnClickListener(new OnClickListener(){
			@Override
			public void onClick(View v) {
				// TODO Auto-generated method stub
				
				if(MediaServiceMonitor.this.switchButton.isChecked())
				{
					Intent intent = new Intent(MediaServiceMonitor.this, MediaService.class);
					intent.putExtra("port",Integer.parseInt(editText.getText().toString()));
					startService(intent);
				}
				else
				{
					Intent intent = new Intent(MediaServiceMonitor.this, MediaService.class);
					intent.putExtra("port",Integer.parseInt(editText.getText().toString()));
			        stopService(intent);
				}
			}
        	
        });
    }
    
    public static boolean isServiceRunning(Context mContext,String className) {
        boolean isRunning = false;
        ActivityManager activityManager = (ActivityManager)
        		mContext.getSystemService(Context.ACTIVITY_SERVICE); 
        List<ActivityManager.RunningServiceInfo> serviceList 
        = activityManager.getRunningServices(30);
       if (!(serviceList.size()>0)) {
            return false;
        }
        for (int i=0; i<serviceList.size(); i++) {
        	String name = serviceList.get(i).service.getClassName();
            if (name.equals(className) == true) {
                isRunning = true;
                break;
            }
        }
        return isRunning;
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.activity_media_service_monitor, menu);
        return true;
    }
}
