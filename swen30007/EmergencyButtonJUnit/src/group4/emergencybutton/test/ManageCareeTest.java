package group4.emergencybutton.test;

import group4.emergencybutton.ManageCaree;
import group4.emergencybutton.R;
import android.test.ActivityInstrumentationTestCase2;
import android.widget.Button;
import android.widget.ListView;
import android.widget.TextView;

public class ManageCareeTest extends
		ActivityInstrumentationTestCase2<ManageCaree> {
	
    private ManageCaree activity;
	
	public ManageCareeTest() {
		super("group4.emergencybutton", ManageCaree.class);
	}
	
	@Override
	protected void setUp() throws Exception {
		super.setUp();
		activity = this.getActivity();
	}
	
	public void testCareeResources() {
		TextView currentCareeText = (TextView) activity.findViewById(R.id.currentCaree);
		assertNotNull(currentCareeText);
		Button acknowledgeButton = (Button) activity.findViewById(R.id.acknowledge_button);
		assertNotNull(acknowledgeButton);
	}
}
