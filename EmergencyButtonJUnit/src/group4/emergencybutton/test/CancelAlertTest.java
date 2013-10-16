package group4.emergencybutton.test;

import java.util.ArrayList;
import java.util.List;


import group4.emergencybutton.CareeHome;
import group4.emergencybutton.HandleAlert;
import group4.emergencybutton.R;
import android.test.ActivityInstrumentationTestCase2;
import android.test.UiThreadTest;
import android.view.View;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.ListView;
import android.widget.TextView;
import android.app.Activity;
import android.app.Instrumentation;
import android.app.Instrumentation.ActivityMonitor;

public class CancelAlertTest extends
		ActivityInstrumentationTestCase2<HandleAlert> {
	
    private HandleAlert activity;
    private View view;
	
	public CancelAlertTest() {
		super("group4.emergencybutton", HandleAlert.class);
	}
	
	@Override
	protected void setUp() throws Exception {
		super.setUp();
		activity = this.getActivity();
		view = activity.findViewById(R.id.cancel_alert_button);
	}
//	
//	@UiThreadTest
//	public void testCancelAlertActivityChange() {
//		//Test if the activity change occurs
//		Instrumentation instrumentation = getInstrumentation();
//		ActivityMonitor monitor = instrumentation.addMonitor(CareeHome.class.getName(), null, false);
//	    Activity currentActivity = getInstrumentation().waitForMonitorWithTimeout(monitor, 5);
//	    //assertEquals(activity, currentActivity);
//		Button button = (Button) activity.findViewById(R.id.cancel_alert_button);
//		button.performClick();
//	    currentActivity = getInstrumentation().waitForMonitorWithTimeout(monitor, 5);
//	    currentActivity.
//	}
	
	public void testHelpTextAndButtonExist() {
		//Check if the help text is the correct string resource, and check if the button exists.
		Button cancelButton = (Button) activity.findViewById(R.id.cancel_alert_button);
		assertNotNull(cancelButton);
		String resourceString = activity.getString(R.string.handle_alert);
		TextView helpText = (TextView) activity.findViewById(R.id.help_text);
		assertEquals(resourceString, helpText.getText().toString());
	}
}
