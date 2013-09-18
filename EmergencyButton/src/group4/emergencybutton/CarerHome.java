package group4.emergencybutton;

import com.parse.Parse;
import com.parse.ParseInstallation;
import com.parse.ParseUser;
import com.parse.PushService;

import android.os.Bundle;
import android.app.Activity;
import android.content.Intent;
import android.view.Menu;
import android.widget.ArrayAdapter;
import android.widget.ListView;

public class CarerHome extends Activity {
	
	ListView careeList ;

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
        
     // Get ListView object from xml
        careeList = (ListView) findViewById(R.id.CareeList);
        
        String[] values = new String[] { "Ben | Last Check-In: 1 hour 5 minutes ago", 
                "Tom | Last Check-In: 3 minutes ago",
               };
        

       ArrayAdapter<String> adapter = new ArrayAdapter<String>(this,
       android.R.layout.simple_list_item_1, android.R.id.text1, values);

        // Assign adapter to ListView
       careeList.setAdapter(adapter); 

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
