package group4.emergencybutton;

public class NameValidator {

	public static boolean validateName(String firstname, String lastname)
	{
		//validate first name
		if(firstname.length() < 1)
		{
			return false;
		}
		
		if(lastname.length() <1)
		{
			return false;
		}
		return true;
	}
}
