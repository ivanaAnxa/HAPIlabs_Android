package com.anxa.hapilabs.common.handlers.reader;



import android.os.Handler;
import android.os.Message;

public abstract class JsonDefaultResponseHandler extends Handler{

	protected Handler handler;
	protected String resultCode;
	private String resultMessage;
	protected Object responseObj;
	
	protected boolean isError = false;
	StringBuilder builder;
	
	public static final int START = 1;
	public static final int COMPLETED = 2;
	public static final int ERROR = 3;
	
	
	
	public JsonDefaultResponseHandler(Handler handler){
		this.handler = handler;
	}
	
	
	public abstract void start();
	
	public abstract void start(String strJson);
	
	public abstract void start(String strJson,String id);
	
	public Object getResponseObj(){
		  return responseObj;
	}
	
	public void setResponseObj(Object responseObj){
		this.responseObj = responseObj;
	}
	public void setResultCode(String resultCode){
		this.resultCode = resultCode;
	}
	
	public void setResultMessage(String resultMessage){
		this.resultMessage = resultMessage;
	}
	
	public String getResultCode(){
		return resultCode;
	}
	
	public String getResultMessage(){
		return resultMessage;
	}
	
	
	@Override
    public void handleMessage(Message msg)
    {
		 super.handleMessage(msg);
    }
	

	
	
}
