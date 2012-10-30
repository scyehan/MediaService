package org.zju.ese.mediaservice;

import org.apache.mina.core.service.IoHandlerAdapter;
import org.apache.mina.core.session.IoSession;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.zju.ese.model.RequestMessage;

import android.os.Handler;

public class RequestHandle extends IoHandlerAdapter {
	private final static Logger logger = LoggerFactory
			   .getLogger(RequestHandle.class);
	Handler handler;
	
	public RequestHandle(Handler handler) {
		super();
		this.handler = handler;
	}
	@Override
	 public void sessionOpened(IoSession session) throws Exception {
		//System.out.println(11);
	 }
	 @Override
	 public void messageReceived(IoSession session, Object message)
	   throws Exception {
		 //System.out.println("收到信息�? + message.toString());
		 if(message instanceof RequestMessage)
		 {
			 RequestMessage request = (RequestMessage)message;
			 handler.obtainMessage(request.getCommand(), request.getPath()).sendToTarget();
		 }
	 }
	 @Override
	 public void messageSent(IoSession session, Object message) throws Exception {
		 //System.out.println("发�?信息�? + message.toString());
	 }
	 
	 @Override
	 public void exceptionCaught( IoSession session, Throwable cause ) throws Exception
	 {
		 logger.error(cause.getMessage());
	        cause.printStackTrace();
	        session.close(true);
	 }
}
