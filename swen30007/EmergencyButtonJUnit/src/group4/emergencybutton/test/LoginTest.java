package group4.emergencybutton.test;

import group4.emergencybutton.Login;
import group4.emergencybutton.R;
import android.test.ActivityInstrumentationTestCase2;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ListView;
import android.widget.TextView;

public class LoginTest extends
		ActivityInstrumentationTestCase2<Login> {
	
    private Login activity;
	
	public LoginTest() {
		super("group4.emergencybutton", Login.class);
	}
	
	@Override
	protected void setUp() throws Exception {
		super.setUp();
		activity = this.getActivity();
	}
	
	public void testLoginStuff() {
		EditText usernameBox = (EditText) activity.findViewById(R.id.edit_username);
		assertNotNull(usernameBox);
		EditText passwordBox = (EditText) activity.findViewById(R.id.edit_password);
		assertNotNull(passwordBox);
	}
}
