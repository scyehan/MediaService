package org.zju.ese.mediaservice;

import android.content.ComponentName;
import android.content.ServiceConnection;
import android.os.IBinder;

import com.android.music.IMediaPlaybackService;

public class MediaPlayerServiceConnection implements ServiceConnection {
	public IMediaPlaybackService mService;
	
	public void onServiceConnected(ComponentName name, IBinder service) {
		// TODO Auto-generated method stub
		mService = IMediaPlaybackService.Stub.asInterface(service);
	}

	public void onServiceDisconnected(ComponentName name) {
		// TODO Auto-generated method stub

	}
}
