package com.example.backgroundTasks;

import android.app.Activity;
import android.app.IntentService;
import android.content.Intent;
import android.os.Bundle;
import android.os.ResultReceiver;
import android.util.Log;

public class MyTestService extends IntentService {

	public MyTestService() {
		super("Testeando Servcio");
		Log.d("MyTestService","MyTestService");
		// TODO Auto-generated constructor stub
	}

	 @Override
	  protected void onHandleIntent(Intent intent) {
		 Log.d("MyTestService","onHandledIntent");
	    // Extract the receiver passed into the service
	    ResultReceiver rec = intent.getParcelableExtra("receiver");
	    // Extract additional values from the bundle
	    String val = intent.getStringExtra("foo");
	    // To send a message to the Activity, create a pass a Bundle
	    Bundle bundle = new Bundle();
	    bundle.putString("resultValue", "My Result Value. Passed in: " + val);
	    // Here we call send passing a resultCode and the bundle of extras
	    rec.send(Activity.RESULT_OK, bundle);
	 
	  }


}
