package org.zju.ese.mediaservice;

import java.util.List;

import android.net.Uri;
import android.os.Bundle;
import android.os.RemoteException;
import android.app.Activity;
import android.app.ActivityManager;
import android.content.ComponentName;
import android.content.Context;
import android.content.Intent;
import android.view.Menu;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Switch;
import android.widget.TextView;

public class MediaServiceMonitor extends Activity {
	MediaPlayerServiceConnection conn = new MediaPlayerServiceConnection();
	Button openButton;
	Button closeButton;
	EditText editText;
	TextView statusText;
    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_media_service_monitor);
        editText = (EditText)findViewById(R.id.editText);
        
        openButton = (Button)findViewById(R.id.open_button);
        closeButton = (Button)findViewById(R.id.close_button);
        statusText = (TextView)findViewById(R.id.statusView);
        if(isServiceRunning(this,"org.zju.ese.mediacontrol.MediaService"))
        	statusText.setText("已开启");
        else
        	statusText.setText("已关闭");
        
//        Intent i = new Intent();
//		i.setClassName("com.android.music","com.android.music.MediaPlaybackService");
//        this.bindService(i, conn, Context.BIND_AUTO_CREATE);

        openButton.setOnClickListener(new OnClickListener(){

			@Override
			public void onClick(View v) {
				// TODO Auto-generated method stub
				statusText.setText("已开启");
		        
		        Intent intent = new Intent(MediaServiceMonitor.this, MediaService.class);
				intent.putExtra("port",Integer.parseInt(editText.getText().toString()));
				startService(intent);
			}
        });
        
        closeButton.setOnClickListener(new OnClickListener(){

			@Override
			public void onClick(View v) {
				// TODO Auto-generated method stub
				statusText.setText("已关闭");
				Intent intent = new Intent(MediaServiceMonitor.this, MediaService.class);
				intent.putExtra("port",Integer.parseInt(editText.getText().toString()));
		        stopService(intent);
//				try {
//					conn.mService.openFile("/mnt/sdcard/ftp/花火.mp3");
//					conn.mService.play();
//				} catch (RemoteException e) {
//					// TODO Auto-generated catch block
//					e.printStackTrace();
//				}
				
//				Intent it = new Intent(Intent.ACTION_VIEW);
//				it.setDataAndType(Uri.parse("/mnt/sdcard/ftp/花火.mp3"), "audio/mp3");
//				it.setComponent(new ComponentName("com.android.music","com.android.music.MediaPlaybackActivity"));
//				startActivity(it);
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
