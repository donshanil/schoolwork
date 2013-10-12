package group4.emergencybutton;

import android.os.Bundle;
import android.app.Activity;
import android.content.Intent;
import android.view.Menu;
import android.view.View;
import android.widget.TextView;

import com.parse.Parse;
import com.parse.ParseInstallation;
import com.parse.ParseObject;
import com.parse.ParsePush;
import com.parse.ParseUser;
import com.parse.PushService;

public class CareeHome extends Activity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_caree_home);
        
        // Initialize Parse
        Parse.initialize(this, "MVq1PZGssKpeTZAWjqvgljViUY1FE1WtyZiuVDTa", "TUfSe1TeyJNCQAtTrUXuM7Rpeyv25wvoRhun9LkR");
        
        ParseUser currentUser = ParseUser.getCurrentUser();
        String usertype = currentUser.get("Type").toString();
        if ((currentUser != null) && (usertype.equals("Caree"))) {
          // do stuff with the user
        } else {
          // show the Signup or login screen
	    	Intent intent = new Intent(this, Login.class);
	    	startActivity(intent);
        }
        
        TextView textView = (TextView) findViewById(R.id.loggedCaree);
        textView.setText("You are logged in as - Caree "+currentUser.get("username").toString());
        
        PushService.setDefaultPushCallback(this, CareeHome.class);
        ParseInstallation.getCurrentInstallation().saveInBackground();
        PushService.subscribe(this, "Carees", CareeHome.class);
        //ParseAnalytics.trackAppOpened(getIntent()); // For Tracking
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
        
        Intent intent = new Intent(this, CheckInConfirm.class);
        startActivity(intent);
        
    }
    
}
