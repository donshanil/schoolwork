package group4.emergencybutton;

import android.os.Bundle;
import android.app.Activity;
import android.content.Intent;
import android.view.Menu;
import android.view.View;
import com.parse.Parse;
import com.parse.ParseAnalytics;
import com.parse.ParseInstallation;
import com.parse.ParseObject;
import com.parse.ParsePush;
import com.parse.PushService;

public class MainActivity extends Activity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        
        // Initialize Parse
        Parse.initialize(this, "MVq1PZGssKpeTZAWjqvgljViUY1FE1WtyZiuVDTa", "TUfSe1TeyJNCQAtTrUXuM7Rpeyv25wvoRhun9LkR");
        
        PushService.setDefaultPushCallback(this, MainActivity.class);
        ParseInstallation.getCurrentInstallation().saveInBackground();
        // ParseAnalytics.trackAppOpened(getIntent()); // For Tracking
        
        // Push Service (As Carer) (Not Final, just for demo)
        PushService.subscribe(this, "Carers", CarerHome.class);
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
    	// No Menu
        return false;
    }
    
	@Override
	public void onBackPressed() {
		// Don't Support "Back"
	}    
	
    public void initiateAlert(View view){
    	// Send Alarm to server
        ParseObject testObject = new ParseObject("Alarm");
        testObject.put("Name", "Harlan Wade");
        testObject.put("Activated", true);
        testObject.saveInBackground();
        
        // Notify Carers on Alarm
        ParsePush push = new ParsePush();
        push.setChannel("Carers");
        push.setMessage("Harlan Wade needs HELP!!!");
        push.sendInBackground();
        
    	Intent intent = new Intent(this, HandleAlert.class);
    	startActivity(intent);
    }
    
    public void checkIn(View view){
    	// Check In with database
        ParseObject testObject = new ParseObject("Check_In");
        testObject.put("Name", "Harlan Wade");
        testObject.saveInBackground();
        
        // Notify Carers on Check-In
        ParsePush push = new ParsePush();
        push.setChannel("Carers");
        push.setMessage("Harlan Wade just checked in!");
        push.sendInBackground();
        
        /*
    	Intent intent = new Intent(this, MainActivity.class);
    	startActivity(intent);
    	*/
    }
    
}
