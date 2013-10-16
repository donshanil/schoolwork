package group4.emergencybutton;

import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import java.util.Set;

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
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.widget.AdapterView;
import android.widget.AdapterView.OnItemClickListener;
import android.widget.ArrayAdapter;
import android.widget.ListView;
import android.widget.TextView;
import android.widget.Toast;

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
	        
	        //Get carees for current carer
	        
	        ArrayList<String> usernames = new ArrayList<String>();
	        final ArrayList<ParseObject> carees = new ArrayList<ParseObject>();
	        ArrayList<String> values = new ArrayList<String>();
	        
	    	ParseQuery<ParseUser> userQuery = ParseUser.getQuery();
	    	userQuery.whereEqualTo("carer", currentUser.get("username").toString());
	    	
	        try {
	        	List<ParseUser> users = userQuery.find();
	        	for (ParseObject caree1 : users) {
	        		
	        		ParseUser currentCaree = (ParseUser) caree1;
	        			        		
	        		PushService.subscribe(this, currentCaree.get("username").toString(), CarerHome.class);
	        		
	        		usernames.add(currentCaree.get("username").toString());
	        	}
			} catch (ParseException e) {
				e.printStackTrace();
			}
	        
	        if (usernames != null) {
	            for (String username : usernames) {
	            	//Get the user from parse, and get their last check-in, and put it into the values array.
	            	ParseQuery<ParseUser> userQuery1 = ParseUser.getQuery();
	            	userQuery1.whereEqualTo("username", username);
	                try {
	                	//TODO: Below isn't working...
	                	List<ParseUser> users = userQuery1.find();
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
						Date currentDate = new Date();
						
						long millis = (currentDate.getTime() - checkIn.getCreatedAt().getTime());						
						long second = (millis / 1000) % 60;
						long minute = (millis / (1000 * 60)) % 60;
						long hour = (millis / (1000 * 60 * 60)) % 24;
						
						String lastCheckIn = String.format("%02d hours, %02d minutes, %02d seconds ago" +
								"", hour, minute, second);

			        	values.add("Name: " + checkIn.getString("Name") + "\nLast Check-In: " + lastCheckIn);
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
           
           careeList.setOnItemClickListener(new OnItemClickListener() {

			@Override
			public void onItemClick(AdapterView<?> parent, View view, int position,
					long id) {
				final String careeuser = carees.get(position).getString("username");
	            Intent intent = new Intent(CarerHome.this, ManageCaree.class);
	            intent.putExtra("caree_username",careeuser);
	            startActivity(intent);
				
			}
			
           });
           
           
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
        MenuInflater menuInflater = getMenuInflater();
        menuInflater.inflate(R.menu.carer_home, menu);
        return true;
    }
	
    @Override
    public boolean onOptionsItemSelected(MenuItem item)
    {
         
        switch (item.getItemId())
        {
        case R.id.log_out:
        	
        	Set<String> setOfAllSubscriptions = PushService.getSubscriptions(this);
        	
        	for (String sub : setOfAllSubscriptions) {
        		PushService.unsubscribe(this, sub);
    		}
    
        	
            Intent intent1 = new Intent(this, Login.class);
            startActivity(intent1);
            return true;
 
        default:
            return super.onOptionsItemSelected(item);
        }
    }    
    
	@Override
	public void onBackPressed() {
		// Don't Support "Back"
	}    

}
