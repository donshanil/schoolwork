package group4.emergencybutton;

import com.parse.Parse;
import com.parse.ParseUser;

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
	}

	@Override
	public boolean onCreateOptionsMenu(Menu menu) {
		return false;
	}

}
