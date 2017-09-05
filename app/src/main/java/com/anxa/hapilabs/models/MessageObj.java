package com.anxa.hapilabs.models;


public class MessageObj {

private String message_id;

private String message_string;


private MESSAGE_TYPE type;


public static enum MESSAGE_TYPE {
	
	SUCCESS(1), FAILED(2);
	
		 private final int value;
		    private MESSAGE_TYPE(int value) {
		        this.value = value;
		    }

		    public int getValue() {
		        return value;
		    }
	};

	

public static MESSAGE_TYPE getMESSAGETYPEValue(int value) {
	MESSAGE_TYPE returnvalue = MESSAGE_TYPE.SUCCESS; // default returnvalue
	 for (final MESSAGE_TYPE type : MESSAGE_TYPE.values()) {
	        if (type.value == value) {
	        	returnvalue = type;
	            break;
	        }
	    }
	 return returnvalue;
}

	public MessageObj()
	{
		
	}


	public String getMessage_string() {
		return message_string;
	}


	public void setMessage_string(String message_string) {
		this.message_string = message_string;
	}


	public String getMessage_id() {
		return message_id;
	}


	public void setMessage_id(String message_id) {
		this.message_id = message_id;
	}


	


	public MESSAGE_TYPE getType() {
		return type;
	}


	public void setType(MESSAGE_TYPE type) {
		this.type = type;
	}
	
}
