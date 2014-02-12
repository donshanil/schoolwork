package group4.emergencybutton.test;

import group4.emergencybutton.CareeHome;
import group4.emergencybutton.R;
import android.test.ActivityInstrumentationTestCase2;
import android.widget.Button;
import android.widget.TextView;

public class CareeHomeTest extends
		ActivityInstrumentationTestCase2<CareeHome> {
	
    private CareeHome activity;
	
	public CareeHomeTest() {
		super("group4.emergencybutton", CareeHome.class);
	}
	
	@Override
	protected void setUp() throws Exception {
		super.setUp();
		activity = this.getActivity();
	}
	
	public void testValidateButtonsAndText() {
		//Check if the help text is the correct string resource, and check if the button exists.
		Button emergencyButton = (Button) activity.findViewById(R.id.emergency_button);
		assertNotNull(emergencyButton);
		String emergencyString = activity.getString(R.string.emergency_button);
		assertEquals(emergencyString, emergencyButton.getText().toString());
		Button checkInButton = (Button) activity.findViewById(R.id.check_in_button);
		assertNotNull(checkInButton);
		String checkInString = activity.getString(R.string.check_in_button);
		assertEquals(checkInString, checkInButton.getText().toString());
		TextView loggedInCareeText = (TextView) activity.findViewById(R.id.loggedCaree);
		assertNotNull(loggedInCareeText);
	}
}
