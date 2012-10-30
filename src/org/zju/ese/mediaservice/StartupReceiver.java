package org.zju.ese.mediaservice;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.widget.Toast;

public class StartupReceiver extends BroadcastReceiver {
	static final String ACTION = "android.intent.action.BOOT_COMPLETED";
	@Override
	public void onReceive(Context context, Intent intent) {
		// TODO Auto-generated method stub
		if (intent.getAction().equals(ACTION))
		{
			context.startService(new Intent(context, MediaService.class));
			Toast.makeText(context, "MediaService has started!",
					Toast.LENGTH_LONG).show();
		}
	}
}
