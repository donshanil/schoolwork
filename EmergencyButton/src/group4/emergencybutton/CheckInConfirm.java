package group4.emergencybutton;

import android.os.Bundle;
import android.app.Activity;
import android.view.Menu;
import android.view.View;
import android.content.Intent;

public class CheckInConfirm extends Activity {
	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_check_in_confirm);
	}
		
	@Override
	public boolean onCreateOptionsMenu(Menu menu) {
		return false;
	}
	
	@Override
	public void onBackPressed() {
		Intent intent = new Intent(this, CareeHome.class);
		startActivity(intent);
	}
	
	public void backButton(View view) {
		onBackPressed();
	}
	
}
