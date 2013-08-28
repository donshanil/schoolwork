package group4.emergencybutton;

import android.os.Bundle;
import android.app.Activity;
import android.content.Intent;
import android.view.Menu;
import android.view.View;

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
    	Intent intent = new Intent(this, HandleAlert.class);
    	startActivity(intent);
    }
    
    public void checkIn(View view){
    	Intent intent = new Intent(this, MainActivity.class);
    	startActivity(intent);
    }
    
}
