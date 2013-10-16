package group4.emergencybutton;

import java.util.Date;
import java.util.List;
import java.util.Set;

import com.parse.ParseException;
import com.parse.ParseObject;
import com.parse.ParseQuery;
import com.parse.ParseUser;
import com.parse.PushService;

import android.os.Bundle;
import android.app.Activity;
import android.content.Intent;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.widget.TextView;

public class ManageCaree extends Activity {

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_manage_caree);
		
		Intent intent = getIntent();
		String caree = intent.getStringExtra("caree_username");
		
		ParseQuery<ParseObject> alarmQuery = ParseQuery.getQuery("Alarm");
		
		alarmQuery.whereEqualTo("username", caree);
		alarmQuery.orderByDescending("createdAt");
		
		String time = "";
		String status = "Needs Help";
		Boolean activated = false;
		
		try {
			ParseObject alarm = alarmQuery.getFirst();
			
			activated = alarm.getBoolean("Activated");
			
			Date currentDate = new Date();
			
			long millis = (currentDate.getTime() - alarm.getCreatedAt().getTime());						
			long second = (millis / 1000) % 60;
			long minute = (millis / (1000 * 60)) % 60;
			long hour = (millis / (1000 * 60 * 60)) % 24;
			
			time = String.format("%02d hours, %02d minutes, %02d seconds ago" +
					"", hour, minute, second);
			
		} catch (ParseException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		
		if (!(activated)){
			ParseQuery<ParseObject> checkQuery = ParseQuery.getQuery("Check_In");
			checkQuery.whereEqualTo("username",caree);
			checkQuery.orderByDescending("createdAt");
			
			try {
				ParseObject check = checkQuery.getFirst();
				
				Date currentDate = new Date();
				
				long millis = (currentDate.getTime() - check.getCreatedAt().getTime());						
				long second = (millis / 1000) % 60;
				long minute = (millis / (1000 * 60)) % 60;
				long hour = (millis / (1000 * 60 * 60)) % 24;
				
				time = String.format("%02d hours, %02d minutes, %02d seconds ago" +
						"", hour, minute, second);
				
			} catch (ParseException e) {
				e.printStackTrace();
			}
			
			status = "Checked-In";
		}
		

		
        final TextView textViewToChange = (TextView) findViewById(R.id.currentCaree);
        textViewToChange.setText(caree + "'s Status:" + caree + " " + status + " " + time);
		
	}

	@Override
	public boolean onCreateOptionsMenu(Menu menu) {
        MenuInflater menuInflater = getMenuInflater();
        menuInflater.inflate(R.menu.manage_caree, menu);
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
