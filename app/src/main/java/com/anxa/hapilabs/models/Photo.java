package com.anxa.hapilabs.models;

import java.util.UUID;

import android.graphics.Bitmap;

public class Photo {

	
public String coreData_id;

public String photo_id;

public String tempId;

public Bitmap image;

public String photo_url_large;

public static PHOTO_STATUS getPHOTOSTATUSvalue(int value) {
	PHOTO_STATUS returnvalue = PHOTO_STATUS.ONGOING_UPLOADPHOTO; // default returnvalue
	 for (final PHOTO_STATUS type : PHOTO_STATUS.values()) {
	        if (type.value == value) {
	        	returnvalue = type;
	            break;
	        }
	    }
	 return returnvalue;
}
public static enum PHOTO_STATUS {
	
	ONGOING_UPLOADPHOTO(1), SYNC_UPLOADPHOTO(2), FAILED_UPLOADPHOTO(3);
	
	 private final int value;
	 
	    private PHOTO_STATUS(int value) {
	        this.value = value;
	    }

	    public int getValue() {
	        return value;
	    }
	    
	 
};


public PHOTO_STATUS state = PHOTO_STATUS.SYNC_UPLOADPHOTO ;//default value is sync

public void setId(String id) {
	this.photo_id = id;
}


  //generate a temp photo id
  public void UniqueIDgen() {  
  	
	  UUID uniqueKey = UUID.randomUUID(); 
	  setId(uniqueKey.toString());
	  
	}  
	
	
	public Photo()
	{
		UniqueIDgen();
	}
}
