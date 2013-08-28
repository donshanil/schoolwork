package group4.emergencybutton;

import android.os.Bundle;
import android.app.Activity;
import android.content.Intent;
import android.view.Menu;
import android.view.View;
import com.parse.Parse;
import com.parse.ParseAnalytics;
import com.parse.ParseObject;

public class MainActivity extends Activity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
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
    	// Stuff to send alarm to server
        Parse.initialize(this, "MVq1PZGssKpeTZAWjqvgljViUY1FE1WtyZiuVDTa", "TUfSe1TeyJNCQAtTrUXuM7Rpeyv25wvoRhun9LkR"); 
        ParseAnalytics.trackAppOpened(getIntent());
        ParseObject testObject = new ParseObject("TestObject");
        testObject.put("foo", "bar");
        testObject.saveInBackground();
        
    	Intent intent = new Intent(this, HandleAlert.class);
    	startActivity(intent);
    }
    
    public void checkIn(View view){
    	Intent intent = new Intent(this, MainActivity.class);
    	startActivity(intent);
    }
    
}
