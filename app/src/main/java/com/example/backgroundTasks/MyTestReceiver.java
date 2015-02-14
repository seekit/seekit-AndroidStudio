package com.example.backgroundTasks;
import android.os.Bundle;
import android.os.Handler;
import android.os.ResultReceiver;
import android.util.Log;


public class MyTestReceiver extends ResultReceiver{
	private Receiver receiver;
	
	public MyTestReceiver(Handler handler) {
		super(handler);
		Log.d("MyTestReciever","MyTestReciever");
		// TODO Auto-generated constructor stub
	}
	 // Setter for assigning the receiver
	  public void setReceiver(Receiver receiver) {
	      this.receiver = receiver;
	      Log.d("MyTestReciever","setReciever");
	  }

	  // Defines our event interface for communication
	  public interface Receiver {
	      public void onReceiveResult(int resultCode, Bundle resultData);
	  }

	  // Delegate method which passes the result to the receiver if the receiver has been assigned
	  @Override
	  protected void onReceiveResult(int resultCode, Bundle resultData) {
	      if (receiver != null) {
	        receiver.onReceiveResult(resultCode, resultData);
	      }
	  }

}
