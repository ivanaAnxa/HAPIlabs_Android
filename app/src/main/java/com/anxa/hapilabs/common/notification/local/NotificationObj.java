package com.anxa.hapilabs.common.notification.local;

import java.util.UUID;

public class NotificationObj {

	public String userID;
	
	public String alertId;
	public int category;
	public String label;
	public String startTime;
	public String endTime;
	public boolean isRemind;
	public String message;
	public int ratingsCount;
	
	
	/*new tags used in SM*/
	public int daysofweek; //1-7
	public int alert_type; //1 = daily 2 = weekly 
	
	public NotificationObj(){
		UniqueIDgen();
	}
	
	
	
	public void UniqueIDgen() {   
	  UUID uniqueKey = UUID.randomUUID(); 
	  alertId = uniqueKey.toString();
	}  
	
	
}
