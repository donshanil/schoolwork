package group4.emergencybutton;

import com.parse.Parse;
import com.parse.ParseObject;
import com.parse.ParsePush;
import com.parse.ParseUser;

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
		
		Parse.initialize(this, "MVq1PZGssKpeTZAWjqvgljViUY1FE1WtyZiuVDTa", "TUfSe1TeyJNCQAtTrUXuM7Rpeyv25wvoRhun9LkR");
		
        ParseUser currentUser = ParseUser.getCurrentUser();
        String usertype = currentUser.get("Type").toString();
        if ((currentUser != null) && (usertype.equals("Caree"))) {
          // do stuff with the user
        } else {
          // show the signup or login screen
	    	Intent intent = new Intent(this, Login.class);
	    	startActivity(intent);
        }
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
