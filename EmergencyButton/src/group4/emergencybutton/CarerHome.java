package group4.emergencybutton;

import com.parse.Parse;
import com.parse.ParseInstallation;
import com.parse.ParseUser;
import com.parse.PushService;

import android.os.Bundle;
import android.app.Activity;
import android.content.Intent;
import android.view.Menu;

public class CarerHome extends Activity {

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_carer_home);
		
        // Initialize Parse
        Parse.initialize(this, "MVq1PZGssKpeTZAWjqvgljViUY1FE1WtyZiuVDTa", "TUfSe1TeyJNCQAtTrUXuM7Rpeyv25wvoRhun9LkR");
        
        ParseUser currentUser = ParseUser.getCurrentUser();
        String usertype = currentUser.get("Type").toString();
        if ((currentUser != null) && (usertype.equals("Carer"))) {
          // do stuff with the user
        } else {
            // show the signup or login screen
	    	Intent intent = new Intent(this, Login.class);
	    	startActivity(intent);
        }
        
        // Save the current Installation to Parse.
        PushService.setDefaultPushCallback(this, CarerHome.class);
        ParseInstallation.getCurrentInstallation().saveInBackground();
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

}
