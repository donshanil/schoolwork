package group4.emergencybutton;

import android.os.Bundle;
import android.app.Activity;
import android.view.Menu;

public class CarerHome extends Activity {

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_carer_home);
	}

	@Override
	public boolean onCreateOptionsMenu(Menu menu) {
		return false;
	}

}
