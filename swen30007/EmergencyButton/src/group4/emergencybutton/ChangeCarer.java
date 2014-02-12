package group4.emergencybutton;

import java.util.ArrayList;
import java.util.List;
import java.util.Set;

import com.parse.ParseException;
import com.parse.ParseObject;
import com.parse.ParseQuery;
import com.parse.ParseUser;
import com.parse.PushService;
import com.parse.SaveCallback;

import android.os.Bundle;
import android.app.Activity;
import android.content.Intent;
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

public class ChangeCarer extends Activity {
	
	ListView careeList;

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_change_carer);
		
        final ParseUser currentUser = ParseUser.getCurrentUser();
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
    	
    	String uname;
    	
        try {
        	List<ParseUser> users = userQuery1.find();
        	for (ParseObject carer1 : users) {
        		
        		ParseUser listCarer = (ParseUser) carer1;
        		uname = listCarer.getString("username");
                //firstName = listCarer.getString("firstName");
            	//lastName = listCarer.getString("lastName");
            	values.add(uname);
            	
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
		
		careeList.setOnItemClickListener(new OnItemClickListener(){

			@Override
			public void onItemClick(AdapterView<?> parent, View view, int position,
					long id) {
				
				final String newCarer = ((TextView) view).getText().toString();
				
				currentUser.put("carer", newCarer);
				currentUser.saveInBackground(new SaveCallback() {
					
					@Override
					public void done(ParseException e) {
					    Toast.makeText(getApplicationContext(),
								(newCarer + " set as current Carer."), Toast.LENGTH_SHORT).show();
					    Intent intent = getIntent();
					    finish();
					    startActivity(intent);
						
					}
				});
			}
		});
	}

	@Override
	public boolean onCreateOptionsMenu(Menu menu) {
        MenuInflater menuInflater = getMenuInflater();
        menuInflater.inflate(R.menu.change_carer, menu);
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

}
