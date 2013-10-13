package group4.emergencybutton;

import java.util.ArrayList;
import java.util.List;

import com.parse.ParseException;
import com.parse.ParseObject;
import com.parse.ParseQuery;
import com.parse.ParseUser;

import android.os.Bundle;
import android.app.Activity;
import android.content.Intent;
import android.view.Menu;
import android.widget.ArrayAdapter;
import android.widget.ListView;
import android.widget.TextView;

public class ChangeCarer extends Activity {
	
	ListView careeList;

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_change_carer);
		
        ParseUser currentUser = ParseUser.getCurrentUser();
        String usertype = currentUser.get("Type").toString();
        if ((currentUser != null) && (usertype.equals("Caree"))) {        	
          //
        } else {
          // show the Signup or login screen
	    	Intent intent = new Intent(this, Login.class);
	    	startActivity(intent);
        }
        
    	String carer = currentUser.getString("carer");
    	
    	ParseQuery<ParseUser> userQuery = ParseUser.getQuery();
    	userQuery.whereEqualTo("username", carer);
    	
    	String firstName = "N/A";
    	String lastName = "N/A";
    	
        try {
        	List<ParseUser> users = userQuery.find();
        	for (ParseObject carer1 : users) {
        		
        		ParseUser currentCarer = (ParseUser) carer1;
        		
                firstName = currentCarer.getString("firstName");
            	lastName = currentCarer.getString("lastName");
        	}
		} catch (ParseException e) {
			e.printStackTrace();
		}
    	
    	TextView textViewToChange = (TextView) findViewById(R.id.currentCarer);
        textViewToChange.setText("Your current carer is " + firstName + " " + lastName);
        
    	ParseQuery<ParseUser> userQuery1 = ParseUser.getQuery();
    	userQuery1.whereEqualTo("Type", "Carer");

    	ArrayList<String> values = new ArrayList<String>();
    	
        try {
        	List<ParseUser> users = userQuery1.find();
        	for (ParseObject carer1 : users) {
        		
        		ParseUser listCarer = (ParseUser) carer1;        		
                firstName = listCarer.getString("firstName");
            	lastName = listCarer.getString("lastName");
            	values.add(firstName + " " + lastName);
            	
        	}
		} catch (ParseException e) {
			e.printStackTrace();
		}
        
		// Get ListView object from xml
		careeList = (ListView) findViewById(R.id.CarerList);
		 
		ArrayAdapter<String> adapter = new ArrayAdapter<String>(this,
		android.R.layout.simple_list_item_1, android.R.id.text1, values);
		
		 // Assign adapter to ListView
		careeList.setAdapter(adapter); 
        
	}

	@Override
	public boolean onCreateOptionsMenu(Menu menu) {
		// Inflate the menu; this adds items to the action bar if it is present.
		getMenuInflater().inflate(R.menu.change_carer, menu);
		return true;
	}

}
