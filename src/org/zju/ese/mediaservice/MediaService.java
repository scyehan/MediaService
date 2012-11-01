package org.zju.ese.mediaservice;

import java.io.File;
import java.io.IOException;
import java.net.InetSocketAddress;
import java.util.List;

import org.apache.ftpserver.FtpServer;
import org.apache.ftpserver.FtpServerFactory;
import org.apache.ftpserver.ftplet.FtpException;
import org.apache.ftpserver.listener.ListenerFactory;
import org.apache.ftpserver.usermanager.PropertiesUserManagerFactory;
import org.apache.mina.filter.codec.ProtocolCodecFilter;
import org.apache.mina.filter.codec.serialization.ObjectSerializationCodecFactory;
import org.apache.mina.transport.socket.SocketAcceptor;
import org.apache.mina.transport.socket.nio.NioSocketAcceptor;
import org.zju.ese.model.RequestMessage;

import com.android.music.IMediaPlaybackService;

import android.app.Service;
import android.content.ComponentName;
import android.content.Context;
import android.content.Intent;
import android.content.ServiceConnection;
import android.content.pm.PackageInfo;
import android.content.pm.PackageManager;
import android.content.pm.ResolveInfo;
import android.content.pm.PackageManager.NameNotFoundException;
import android.net.Uri;
import android.os.Environment;
import android.os.Handler;
import android.os.IBinder;
import android.os.Message;
import android.os.RemoteException;

public class MediaService extends Service {
	private MediaPlayerServiceConnection conn = new MediaPlayerServiceConnection();
	private SocketAcceptor acceptor;
	private FtpServer mFtpServer;
	int port;
	@Override
	public IBinder onBind(Intent intent) {
		// TODO Auto-generated method stub
		return null;
	}

	@Override
	public void onStart(android.content.Intent intent, int startId) {
		port = intent.getExtras().getInt("port");
		RequestHandle handler = new RequestHandle(this.handler);
		acceptor = new NioSocketAcceptor();
        acceptor.setHandler(handler);
        acceptor.getFilterChain().addLast("protocol", 
                new ProtocolCodecFilter(new ObjectSerializationCodecFactory()));
        
        try {
            acceptor.bind(new InetSocketAddress(port));
        } catch (IOException e) {
        	e.printStackTrace();
            System.exit(-1);
        }
        
        Intent i = new Intent();
		i.setClassName("com.android.music","com.android.music.MediaPlaybackService");
        this.bindService(i, conn, Context.BIND_AUTO_CREATE);
        
        startFtpServer();
	}
	
	@Override
	public void onDestroy() {
		super.onDestroy();
		
		if(null != mFtpServer) {
			mFtpServer.stop();
			mFtpServer = null;
		}
		
		acceptor.unbind();
	}
	
	private void startFtpServer() {
		FtpServerFactory serverFactory = new FtpServerFactory();

		ListenerFactory factory = new ListenerFactory();

		// set the port of the listener
		//int port = 2221;
		factory.setPort(port + 100);

		// replace the default listener
		serverFactory.addListener("default", factory.createListener());
		PropertiesUserManagerFactory userManagerFactory = new PropertiesUserManagerFactory();
		userManagerFactory.setFile(new File("/sdcard/users.properties"));
		
		serverFactory.setUserManager(userManagerFactory.createUserManager());
		// start the server
		FtpServer server = serverFactory.createServer();
		this.mFtpServer = server;
		try {
			server.start();
		} catch (FtpException e) {
			e.printStackTrace();
		}

	}
	
	private Handler handler = new Handler()
	{
		 @Override
	     public void handleMessage(Message msg) 
		 {
			 String path = (String)msg.obj;
			 try {
				switch(msg.what)
				{
				case RequestMessage.PLAY:
					conn.mService.play();
					break;
				case RequestMessage.PAUSE:
					conn.mService.pause();
					break;
				case RequestMessage.PREVIOUS:
					conn.mService.prev();
					break;
				case RequestMessage.NEXT:
					conn.mService.next();
					break;
				case RequestMessage.OPEN:
					openApp("com.android.music");
					break;
				case RequestMessage.OPENFILE:
					if(path != null)
					{
						if(!path.contains("/"))
							path = "/sdcard/ftp/"+ path + ".mp3";
						conn.mService.openFile(path);
						conn.mService.play();
						openApp("com.android.music");
					}
					
//					Intent it = new Intent(Intent.ACTION_VIEW);
//					it.setDataAndType(Uri.parse(path), "audio/mp3");
//					it.setComponent(new ComponentName("com.android.music","com.android.music.MediaPlaybackActivity"));
//					startActivity(it);
					break;
				case RequestMessage.PICTURE:
					if(path != null)
					{
						File file = new File(path);
			            Intent intent = new Intent(Intent.ACTION_VIEW);
			            intent.setDataAndType(Uri.fromFile(file), "image/*");
			            intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK);  
						startActivity(intent);  
					}
					break;
				}
			 } catch (RemoteException e) {
					// TODO Auto-generated catch block
					e.printStackTrace();
				}
		 }
	}; 
	
	public void openApp(String packageName) { 
        PackageManager packageManager = this.getPackageManager(); 
        PackageInfo pi = null;   
         
        try { 
        	pi = packageManager.getPackageInfo(packageName, 0); 
        } catch (NameNotFoundException e) { 
        } 
        Intent resolveIntent = new Intent(Intent.ACTION_MAIN, null); 
        resolveIntent.addCategory(Intent.CATEGORY_LAUNCHER); 
        resolveIntent.setPackage(pi.packageName); 
 
        List<ResolveInfo> apps = packageManager.queryIntentActivities(resolveIntent, 0); 
 
        ResolveInfo ri = apps.iterator().next(); 
        if (ri != null ) { 
            String className = ri.activityInfo.name; 
 
            Intent intent = new Intent(Intent.ACTION_MAIN); 
            intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK);  
            intent.addCategory(Intent.CATEGORY_LAUNCHER); 
 
            ComponentName cn = new ComponentName(packageName, className); 
 
            intent.setComponent(cn); 
            this.startActivity(intent); 
        } 
    }
	
}
