package group4.emergencybutton;

import android.os.Bundle;
import android.app.Activity;
import android.content.Intent;
import android.view.Menu;
import android.view.View;
import android.widget.TextView;

import com.parse.Parse;
import com.parse.ParseException;
import com.parse.ParseInstallation;
import com.parse.ParseObject;
import com.parse.ParsePush;
import com.parse.ParseUser;
import com.parse.PushService;

import java.util.Arrays;
import java.util.List;
import java.util.UUID;

public class CareeHome extends Activity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_caree_home);
        
        ParseUser currentUser = ParseUser.getCurrentUser();
        String usertype = currentUser.get("Type").toString();
    	String firstName = currentUser.getString("firstName");
    	String lastName = currentUser.getString("lastName");
        if ((currentUser != null) && (usertype.equals("Caree"))) {
          // do stuff with the user
        } else {
          // show the signup or login screen
	    	Intent intent = new Intent(this, Login.class);
	    	startActivity(intent);
        }
        
        PushService.setDefaultPushCallback(this, CareeHome.class);
        ParseInstallation.getCurrentInstallation().saveInBackground();
        PushService.subscribe(this, "Carees", CareeHome.class);
        
        final TextView textViewToChange = (TextView) findViewById(R.id.loggedCaree);
        textViewToChange.setText("You are logged in as - " + firstName + " " + lastName);
        
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
    	ParseUser currentUser = ParseUser.getCurrentUser();
    	String firstName = currentUser.getString("firstName");
    	String lastName = currentUser.getString("lastName");
    	
    	// Send Alarm to server
        ParseObject testObject = new ParseObject("Alarm");
        testObject.put("Name", firstName + " " + lastName);
        testObject.put("Activated", true);
        testObject.saveInBackground();
        
        // Notify Carers on Alarm
        ParsePush push = new ParsePush();
        push.setChannel("Carers");
        push.setMessage(firstName + " " + lastName + " needs HELP!!!");
        push.sendInBackground();
        
    	Intent intent = new Intent(this, HandleAlert.class);
    	startActivity(intent);
    }
    
    public void checkIn(View view) throws ParseException{
    	ParseUser currentUser = ParseUser.getCurrentUser();
    	String firstName = currentUser.getString("firstName");
    	String lastName = currentUser.getString("lastName");
    	
    	// Check In with database
    	ParseObject testObject = ParseObject.create("Check_In");
        testObject.put("Name", firstName + " " + lastName);
        testObject.save();
        testObject.fetch();
        currentUser.put("lastCheckInId", testObject.getObjectId());
        currentUser.saveInBackground();
        
        // Notify Carers on Check-In
        ParsePush push = new ParsePush();
        push.setChannel("Carers");
        push.setMessage(firstName + " " + lastName + " just checked in!");
        push.sendInBackground();
        
        Intent intent = new Intent(this, CheckInConfirm.class);
        startActivity(intent);
        
    }
    
}
