package group4.emergencybutton;

import com.parse.ParseObject;
import com.parse.ParsePush;

import android.os.Bundle;
import android.app.Activity;
import android.view.Menu;
import android.view.View;
import android.content.Intent;

public class HandleAlert extends Activity {

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_handle_alert);
	}
		
	@Override
	public boolean onCreateOptionsMenu(Menu menu) {
		return false;
	}
	
	@Override
	public void onBackPressed() {
		// Don't Support "Back"
	}
	
	// Cancel Alert and Bring user back to home
	public void cancelAlert(View view){
		// Tell Server to Cancel Alarm
        ParseObject testObject = new ParseObject("Alarm");
        testObject.put("Name", "Harlan Wade");
        testObject.put("Activated", false);        
        testObject.saveInBackground();
        
        // Notify Carers on Cancel
        ParsePush push = new ParsePush();
        push.setChannel("Carers");
        push.setMessage("Harlan Wade has cancelled his/her alarm.");
        push.sendInBackground();
        
		Intent intent = new Intent(this, CareeHome.class);
		startActivity(intent);
	}

}
