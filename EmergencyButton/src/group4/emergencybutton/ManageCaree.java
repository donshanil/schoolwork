package group4.emergencybutton;

import java.util.Date;
import java.util.List;
import java.util.Set;

import com.parse.ParseException;
import com.parse.ParseObject;
import com.parse.ParsePush;
import com.parse.ParseQuery;
import com.parse.ParseUser;
import com.parse.PushService;

import android.os.Bundle;
import android.app.Activity;
import android.content.Intent;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.widget.Button;
import android.widget.TextView;
import android.widget.Toast;

public class ManageCaree extends Activity {
	
	ParseObject caree_obj;

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
		String status1 = "Alarm";
		String ack_status = "Not Yet Acknowledged";
		Boolean activated = false;
		
		try {
			ParseObject alarm = alarmQuery.getFirst();
			
			caree_obj = alarm;
			
			activated = alarm.getBoolean("Activated");
			
			Date currentDate = new Date();
			
			long millis = (currentDate.getTime() - alarm.getCreatedAt().getTime());						
			long second = (millis / 1000) % 60;
			long minute = (millis / (1000 * 60)) % 60;
			long hour = (millis / (1000 * 60 * 60)) % 24;
			
			time = String.format("%02d hours, %02d minutes, %02d seconds ago" +
					"", hour, minute, second);
			
			Boolean is_ack = caree_obj.getBoolean("acknowledged");
			if (is_ack){
				ack_status = "Acknowledged";
			}
			
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
				
				caree_obj = check;
				
				Date currentDate = new Date();
			
				long millis = (currentDate.getTime() - check.getCreatedAt().getTime());						
				long second = (millis / 1000) % 60;
				long minute = (millis / (1000 * 60)) % 60;
				long hour = (millis / (1000 * 60 * 60)) % 24;
				
				time = String.format("%02d hours, %02d minutes, %02d seconds ago" +
						"", hour, minute, second);
				
				Boolean is_ack = caree_obj.getBoolean("acknowledged");
				if (is_ack){
					ack_status = "Acknowledged";
				}
				
				
			} catch (ParseException e) {
				e.printStackTrace();
			}
			
			status = "Checked-In";
			status1 = "Check In";
		}
		

		
        Button button = (Button) findViewById(R.id.acknowledge_button);
        button.setText("Respond to " + status1);
		
        final TextView textViewToChange = (TextView) findViewById(R.id.currentCaree);
        textViewToChange.setText(caree + "'s Status: " + caree + " - " + status + " " + time);
        
        final TextView is_acknowledged = (TextView) findViewById(R.id.is_acknowledged);
        is_acknowledged.setText(ack_status);
        
		
	}
	
    public void acknowledge(View view) throws ParseException{
    	
    	caree_obj.put("acknowledged", true);
    	caree_obj.save();
    	Boolean ack = caree_obj.getBoolean("acknowledged");
    	
    	String ackstr = "False";
    	
    	if (ack){
    		ackstr = "True";
    	}
    	
        Toast.makeText(ManageCaree.this, "Acknowledged!", Toast.LENGTH_LONG).show();
        
	    Intent intent = getIntent();
	    finish();
	    startActivity(intent);
        
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
