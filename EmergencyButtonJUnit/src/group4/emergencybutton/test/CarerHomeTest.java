package group4.emergencybutton.test;

import group4.emergencybutton.CarerHome;
import group4.emergencybutton.R;
import android.test.ActivityInstrumentationTestCase2;
import android.widget.Button;
import android.widget.ListView;
import android.widget.TextView;

public class CarerHomeTest extends
		ActivityInstrumentationTestCase2<CarerHome> {
	
    private CarerHome activity;
	
	public CarerHomeTest() {
		super("group4.emergencybutton", CarerHome.class);
	}
	
	@Override
	protected void setUp() throws Exception {
		super.setUp();
		activity = this.getActivity();
	}
	
	public void testContents() {
		//Check if the help text is the correct string resource, and check if the button exists.
		TextView loggedInCarerText = (TextView) activity.findViewById(R.id.loggedCarer);
		assertNotNull(loggedInCarerText);
		ListView careeList = (ListView) activity.findViewById(R.id.CareeList);
		assertNotNull(careeList);
	}
}
