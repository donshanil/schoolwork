package group4.emergencybutton;

import java.util.ArrayList;
import java.util.List;

import com.parse.FindCallback;
import com.parse.GetCallback;
import com.parse.Parse;
import com.parse.ParseInstallation;
import com.parse.ParseObject;
import com.parse.ParseUser;
import com.parse.PushService;
import com.parse.ParseQuery;
import com.parse.ParseException;

import android.os.Bundle;
import android.app.Activity;
import android.content.Intent;
import android.util.Log;
import android.view.Menu;
import android.widget.ArrayAdapter;
import android.widget.ListView;
import android.widget.TextView;

public class CarerHome extends Activity {
	
	ListView careeList;

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_carer_home);
		   
        ParseUser currentUser = ParseUser.getCurrentUser();
        String usertype = currentUser.get("Type").toString();
        if ((currentUser != null) && (usertype.equals("Carer"))) {
          // do stuff with the user
          // Save the current Installation to Parse.
          PushService.setDefaultPushCallback(this, CarerHome.class);
          ParseInstallation.getCurrentInstallation().saveInBackground();
          PushService.subscribe(this, "Carers", CarerHome.class);
	        
	        //Get carees for current carer
	               
	        ArrayList<String> usernames = new ArrayList<String>();
	        ArrayList<ParseObject> carees = new ArrayList<ParseObject>();
	        ArrayList<String> values = new ArrayList<String>();
	        
	        ParseQuery<ParseObject> query = ParseQuery.getQuery("CarerCarees");
	        query.whereEqualTo("Carer", currentUser.getUsername());
	        List<ParseObject> carerCarees = null;
			try {
				carerCarees = query.find();
			} catch (ParseException e1) {
				e1.printStackTrace();
			}


	        if (carerCarees != null) {
	           	for (ParseObject object : carerCarees) {
	               	for (Object username : (ArrayList) object.get("Carees")) {
		        		usernames.add(username.toString());
		        	}
	           	}
	        }
	        
	        if (usernames != null) {
	            for (String username : usernames) {
	            	//Get the user from parse, and get their last check-in, and put it into the values array.
	            	ParseQuery<ParseUser> userQuery = ParseUser.getQuery();
	            	userQuery.whereEqualTo("username", username);
	                try {
	                	//TODO: Below isn't working...
	                	List<ParseUser> users = userQuery.find();
	                	for (ParseObject caree : users) {
	                		carees.add(caree);
	                	}
					} catch (ParseException e) {
						e.printStackTrace();
					}
	            }
	        } 
	        
	        if (carees != null) {
	        	ParseObject checkIn = null;
	            for (ParseObject caree : carees) {
		        	ParseQuery<ParseObject> getQuery = ParseQuery.getQuery("Check_In");

					try {
						checkIn = getQuery.get(caree.getString("lastCheckInId"));
			        	values.add(checkIn.getString("Name") + " | last Check-In: " + checkIn.getCreatedAt().toString());
					} catch (ParseException e1) {
						e1.printStackTrace();
					}
	            }
	        } 
	               
	        // Get ListView object from xml
	        careeList = (ListView) findViewById(R.id.CareeList);
            
           ArrayAdapter<String> adapter = new ArrayAdapter<String>(this,
           android.R.layout.simple_list_item_1, android.R.id.text1, values);

            // Assign adapter to ListView
           careeList.setAdapter(adapter); 
           
           String firstName = currentUser.getString("firstName");
       	   String lastName = currentUser.getString("lastName");
           final TextView textViewToChange = (TextView) findViewById(R.id.loggedCarer);
           textViewToChange.setText("You are logged in as - " + firstName + " " + lastName);
           
        } else {
            // show the signup or login screen
	    	Intent intent = new Intent(this, Login.class);
	    	startActivity(intent);
        }


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
