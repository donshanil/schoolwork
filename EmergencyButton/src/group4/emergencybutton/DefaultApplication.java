package group4.emergencybutton;

import com.parse.Parse;

import android.app.Application;
import android.content.Intent;

public class DefaultApplication extends Application {

    @Override
    public void onCreate() {
        super.onCreate();
		Parse.initialize(this, "MVq1PZGssKpeTZAWjqvgljViUY1FE1WtyZiuVDTa", "TUfSe1TeyJNCQAtTrUXuM7Rpeyv25wvoRhun9LkR");
    }
	
}
