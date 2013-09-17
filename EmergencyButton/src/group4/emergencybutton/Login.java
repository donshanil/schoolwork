package group4.emergencybutton;

import com.parse.LogInCallback;
import com.parse.Parse;
import com.parse.ParseException;
import com.parse.ParseUser;
import com.parse.SignUpCallback;

import android.os.Bundle;
import android.app.Activity;
import android.content.Intent;
import android.view.Menu;
import android.view.View;
import android.widget.EditText;

public class Login extends Activity {

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_login);
		
		Parse.initialize(this, "MVq1PZGssKpeTZAWjqvgljViUY1FE1WtyZiuVDTa", "TUfSe1TeyJNCQAtTrUXuM7Rpeyv25wvoRhun9LkR");
		
		ParseUser user = new ParseUser();
		user.setUsername("Carer");
		user.setPassword("CarerPass");
		user.setEmail("Carer@email.com");
		 
		// other fields can be set just like with ParseObject
		user.put("Type", "Carer");
		
		user.signUpInBackground(new SignUpCallback() {
			  public void done(ParseException e) {
			    if (e == null) {
			      // Hooray! Let them use the app now.
			    } else {
			      // Sign up didn't succeed. Look at the ParseException
			      // to figure out what went wrong
			    }
			  }
			});
		
		//logIn();
	}

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
    	// No Menu
        return false;
    }
    
	@Override
	public void onBackPressed() {
		// Don't Support "Back"
	}
	
	public void logIn(View view){
		
		EditText etuser = (EditText) findViewById(R.id.edit_username);
		EditText etpass = (EditText) findViewById(R.id.edit_password);

		String user = etuser.getText().toString();
		String pass = etpass.getText().toString();
		
		ParseUser.logInInBackground(user, pass, new LogInCallback() {
			  public void done(ParseUser user, ParseException e) {
			    if (user != null) {
			      // Hooray! The user is logged in.
			    	ParseUser currentuser = ParseUser.getCurrentUser();
			    	loggedIn(currentuser.get("Type").toString());
			    } else {
			      // Signup failed. Look at the ParseException to see what happened.
			    }
			  }
			});
	}
	
    private void loggedIn(String type){
    	
    	if (type.equals("Caree")){
	    	Intent intent = new Intent(this, CareeHome.class);
	    	startActivity(intent);
    	}
    	else{
	    	Intent intent = new Intent(this, CarerHome.class);
	    	startActivity(intent);
    	}
    }

}
