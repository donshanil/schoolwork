package group4.emergencybutton;

import com.parse.Parse;

import android.os.Bundle;
import android.app.Activity;
import android.view.Menu;

public class CarerHome extends Activity {

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_carer_home);
		
        // Initialize Parse
        Parse.initialize(this, "MVq1PZGssKpeTZAWjqvgljViUY1FE1WtyZiuVDTa", "TUfSe1TeyJNCQAtTrUXuM7Rpeyv25wvoRhun9LkR");
	}

	@Override
	public boolean onCreateOptionsMenu(Menu menu) {
		return false;
	}

}
