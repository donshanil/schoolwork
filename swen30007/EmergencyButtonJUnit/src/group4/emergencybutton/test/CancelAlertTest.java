package group4.emergencybutton.test;


import group4.emergencybutton.HandleAlert;
import group4.emergencybutton.R;
import android.test.ActivityInstrumentationTestCase2;
import android.widget.Button;
import android.widget.TextView;

public class CancelAlertTest extends
		ActivityInstrumentationTestCase2<HandleAlert> {
	
    private HandleAlert activity;
	
	public CancelAlertTest() {
		super("group4.emergencybutton", HandleAlert.class);
	}
	
	@Override
	protected void setUp() throws Exception {
		super.setUp();
		activity = this.getActivity();
	}
	
	public void testHelpTextAndButtonExist() {
		//Check if the help text is the correct string resource, and check if the button exists.
		Button cancelButton = (Button) activity.findViewById(R.id.cancel_alert_button);
		assertNotNull(cancelButton);
		String resourceString = activity.getString(R.string.handle_alert);
		TextView helpText = (TextView) activity.findViewById(R.id.help_text);
		assertEquals(resourceString, helpText.getText().toString());
	}
}
