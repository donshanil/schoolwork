package group4.emergencybutton;

import java.util.Set;

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
		
        final TextView textViewToChange = (TextView) findViewById(R.id.currentCaree);
        textViewToChange.setText("Caree - " + caree);
		
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
