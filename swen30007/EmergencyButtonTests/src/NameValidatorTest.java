import junit.framework.TestCase;
import group4.emergencybutton.NameValidator;

//reference: http://java.dzone.com/articles/junit-tutorial-beginners
public class NameValidatorTest extends TestCase {
	
	private String firstname;
	private String lastname;
	
	protected void setUp() throws Exception
	{
		super.setUp();
		firstname = "Harlan";
		lastname = "Wade";
	}
	
	protected void tearDown() throws Exception
	{
		
		super.tearDown();
		firstname = "";
		lastname = "";
	
	}
	public void testInvalidFirstname ()
	{
		boolean result = false; //it should fail
		boolean tester = NameValidator.validateName("", lastname);
		assertEquals(result, tester);
	}
}
