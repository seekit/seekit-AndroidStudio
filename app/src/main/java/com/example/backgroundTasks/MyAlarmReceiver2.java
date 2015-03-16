package com.example.backgroundTasks;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.util.Log;

public class MyAlarmReceiver2 extends BroadcastReceiver{
	 public static final int REQUEST_CODE = 12345;
	  public static final String ACTION = "com.codepath.example.servicesdemo.alarm";
	  
	  
	// Triggered by the Alarm periodically (starts the service to run task)
	  @Override
	  public void onReceive(Context context, Intent intent) {
		
		

		String json=intent.getStringExtra("json");
	    Intent i = new Intent(context, MyAlarmTestService2.class);
	    i.putExtra("json", json);

	    context.startService(i);
	  }

}
