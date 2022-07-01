package run;

/*
 * Please read below:
 * "org.openqa.selenium.WebDriverException: unknown error: unexpected command response"
 * and
 * "org.openqa.selenium.WebDriverException: unknown error: cannot determine loading status"
 * were error messages I got almost every time when running the code, which made it impossible to check my code (assertions)
 * 110% so I was writing this code more or less with no possibility of checking... These errors have nothing to do with my code, but rather with Google Chrome 
 * (please take a look at: https://github.com/SeleniumHQ/selenium/issues/10799 and https://www.globalnerdy.com/2022/06/24/why-your-selenium-chromedriver-chrome-setup-stopped-working/).
 * I have Google Chrome version 103 and cannot downgrade to version 102... also, updating Google Chrome is currently not
 * possible also since I am using an older MacOs version and thus cannot get new Google Chrome versions...
 * I hope you will take this into consideration when reviewing the code/running it.
 */

import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertNotEquals;
import static org.junit.Assert.assertTrue;
import static org.junit.jupiter.api.Assertions.assertEquals;

import java.util.LinkedList;
import java.util.List;
import java.util.concurrent.TimeUnit;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.MethodOrderer.OrderAnnotation;
import org.junit.jupiter.api.Order;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.TestMethodOrder;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.ValueSource;
import org.openqa.selenium.By;
import org.openqa.selenium.JavascriptExecutor;
import org.openqa.selenium.Keys;
import org.openqa.selenium.WebDriver;
import org.openqa.selenium.WebElement;
import org.openqa.selenium.chrome.ChromeDriver;
import org.openqa.selenium.support.ui.ExpectedConditions;
import org.openqa.selenium.support.ui.Select;
import org.openqa.selenium.support.ui.WebDriverWait;

import setup.Credentials;
import setup.User;
import user.Person;

@TestMethodOrder(OrderAnnotation.class)
public class TestCase {

	private String url = new String("https://www.links.hr/hr/register");
	private WebDriver driver;

	@BeforeAll
	static void setupClass() {

		// WebDriverManager.chromedriver().setup(); // setup either easier with
		// WebDriverManager class (external
		// import is needed) , or manually like I did below
		String userDirectory = System.getProperty("user.dir");
		System.setProperty("webdriver.chrome.driver", userDirectory.concat("/lib/chromedriver"));

	}

	@BeforeEach
	void setup() {

		driver = new ChromeDriver();
		driver.get(url);
		driver.manage().deleteAllCookies();
		driver.manage().window().maximize();
		// wait until all possibly hidden elements are
		// present to the driver
//		driver.manage().timeouts().implicitlyWait(200, TimeUnit.SECONDS); 
//		driver.manage().timeouts().pageLoadTimeout(200, TimeUnit.SECONDS);

		// remove cookie bar notification from HTML
		JavascriptExecutor js = null;
		if (driver instanceof JavascriptExecutor) {
			js = (JavascriptExecutor) driver;
		}
		js.executeScript("return document.getElementsByClassName('eu-cookie-bar-notification')[0].remove();");

	}

	@Test
	@Order(1)
	@DisplayName("TC-1: Verify URL")
	void validateUrl() {

		String expectedUrl = url;
		String actualUrl = driver.getCurrentUrl();

		assertEquals(expectedUrl, actualUrl);

	}

	@Test
	@Order(2)
	@DisplayName("TC-2: Verify form visibility")
	void validateFormPresence() {

		WebElement form = findByClass("registration-page");

		boolean formIsDisplayed = form.isDisplayed();

		assertTrue(formIsDisplayed);

	}

	@Test
	@Order(3)
	@DisplayName("TC-3: Verify “legal entity” checkbox is clickable")
	void validateLegalEntityCheckboxIsClickable() {

		WebElement legalEntityCheckbox = findByID("RegisterAsCompany");
		WebElement companyInfo = findByID("companyInfo");

		scroll(250);

		for (int i = 0; i < 2; i++) {

			checkboxCheck(legalEntityCheckbox, companyInfo);

		}

	}

	@Test
	@Order(4)
	@DisplayName("TC-4: Verify only one radio button at a time for “gender” is selectable")
	void validateGenderRadioButtonIsClickable() {

		WebElement genderMale = findByID("gender-male");
		WebElement genderFemale = findByID("gender-female");

		scroll(400);

		List<WebElement> elementsList = new LinkedList<WebElement>();
		elementsList = findMultipleByClassName("gender");

		for (int i = 0; i < elementsList.size(); i++) {
			radioButtonCheck(genderMale, genderFemale, i);
		}

	}

	@Test
	@Order(5)
	@DisplayName("TC-5: Verify “date” field range")
	void validateAllDateFieldsAreInRange() {

		// *** DATE ***
		WebElement dayDropdown = findByName("DateOfBirthDay");

		Select day = new Select(dayDropdown);

		// get all days
		List<WebElement> list_day = day.getOptions();

		// index 1 is the first day (number) since index 0 is "Dan"
		String firstDayNumber = list_day.get(1).getText();

		// assert days start from "1"
		assertEquals("1", firstDayNumber);

		// assert days end with "31"
		assertEquals("31", list_day.get(list_day.size() - 1).getText());

		// assert there are 32 days (31 plus "Dan")
		assertTrue(list_day.size() == 32);

		// *** MONTH ***
		WebElement monthDropdown = findByName("DateOfBirthMonth");

		Select month = new Select(monthDropdown);

		List<WebElement> list_month = month.getOptions();

		// index 1 is the first month since index 0 is "Mjesec"
		String firstMonth = list_month.get(1).getText();

		// assert months start from "siječanj"
		assertEquals("siječanj", firstMonth);

		// assert there are 13 months (12 plus "Mjesec")
		assertTrue(list_month.size() == 13);

		// *** YEAR ***
		WebElement yearDropdown = findByName("DateOfBirthYear");

		Select year = new Select(yearDropdown);

		// get all years
		List<WebElement> list_year = year.getOptions();

		// index 1 is the first year since index 0 is "Godine"
		String firstYear = list_year.get(1).getText();

		// assert years start from "1912"
		assertEquals("1912", firstYear);

		// assert years end with "2022"
		assertEquals("2022", list_year.get(list_year.size() - 1).getText());

		// assert there are 112 years (111 plus "Godine")
		assertTrue(list_year.size() == 112);

	}

	@ParameterizedTest
	@Order(6)
	@DisplayName("TC-6: Verify “email” must not be empty and must be in valid format")
	@ValueSource(strings = { " ", "@mailcom", "mail@com", "mail.com", "mail@c!om.com", "mail@mail.ch..",
			"myEmail@myEmail.de" })
	public void validateEmail(String someEmail) {

		WebElement emailField = findByName("Email");

		scroll(500);
		pause(2000);

		write(emailField, someEmail);

		emailField.sendKeys(Keys.ENTER);

		List<WebElement> listErrorMessages = findMultipleByClassName("field-validation-error");

		assertTrue(listErrorMessages.stream().anyMatch(item -> "Pogrešan e-mail".equals(item.getText())));

		if (someEmail.equals("myEmail@myEmail.de"))
			assertTrue(listErrorMessages.stream().anyMatch(item -> !"Pogrešan e-mail".equals(item.getText())));

	}

//	@Test					// I couldn't access the zip field(city field) since it's input field is hidden. No matter what I did, I always ended up with: "element not interactable" when inserting value in it
	@Order(7)
	@DisplayName("TC-7: Verify that the right “country” gets automatically selected based on “zip code” input")
	void validateCountryBasedOnZip() {

		// scroll(800);

		// this with email works perfect
//		WebElement emailll = findByXpath("//label[contains(.,'Elektronska pošta:')]//following::input[1]");
//		write(emailll, "123");

//		WebElement zipCode = driver.findElement(By.xpath("/html/body/div[5]/div[10]/div[4]/div[1]/form/div/div[3]/div[4]/div[2]/div[2]/input[1]"));

		// WebElement zipCode = findByXpath("//div[contains(@class,'inputs')]");

		// WebElement zipCode =
		// findByXpath("//div[contains(@class,'form-fields')]/div[1]/input[1]");

		// WebElement zipCode = findByXpath("//div[contains(@class,'form-fields') and
		// label[contains(text(), 'Poštanski broj:')]/input[1]");

		// WebElement zipCode = driver.findElement(By.xpath("//*[text()='Poštanski
		// broj:']"));

//		WebElement zipCode = driver.findElement(By.xpath("//label[contains(.,'Poštanski broj:')]/following::input[1]"));

//		write(zipCode, "88888");

		// div[contains(@class,'password-group')]//following::div//input[@type='phone']

//		WebElement zipCode = driver.findElement(By.xpath("//label[contains(.,'Poštanski broj:') ]//child::input[1]"));

//		WebElement zipCode1 = driver.findElement(By.xpath("//div[contains(@class,'inputs left') and label[contains(text(), 'Poštanski broj:')]]//child::input"));

//		write(zipCode1, "88888");

		// label[contains(text(), 'Delete group')]//ancestor::div//input

		// WebElement zipCode = findByXpath("//label[contains(text(), 'Poštanski
		// broj:')]");

//		System.out.println(zipCode1.getAttribute("name"));

		// write(zipCode, "88370");

//		pause(1000);

		// zipCode.sendKeys(Keys.ENTER);

		// WebElement city =
		// findByXpath("/html/body/div[5]/div[10]/div[4]/div[1]/form/div/div[3]/div[4]/div[2]/div[3]/input[1]");

	}

	@Test
	@Order(8)
	@DisplayName("TC-8: Verify “news” checkbox is clickable")
	void validateNewsCheckboxIsClickable() {

		scroll(1200);

		WebElement newsCheckbox = findByID("Newsletter");

		for (int i = 0; i < 2; i++) {

			boolean isChecked = newsCheckbox.isSelected();

			pause(500);

			if (i == 0) {
				assertFalse(isChecked);
				click(newsCheckbox, 200);
			} else if (i == 1) {
				assertTrue(isChecked);
				click(newsCheckbox, 200);
			}

		}

	}

	@ParameterizedTest
	@Order(9)
	@DisplayName("TC-9: Verify “OIB” as legal entity")
	@ValueSource(strings = { "1234567899", "123456789999", "abcdefghijk", "12345678999" })
	void validateOIB(String OIBs) {

		scroll(250);

		WebElement legalEntityCheckbox = findByID("RegisterAsCompany");

		click(legalEntityCheckbox, 200);

		WebElement oib = findByName("CompanyOIB");

		write(oib, OIBs);

		pause(1500);

		Pattern pattern = Pattern.compile("^(\\d{11})$");
		Matcher matcher = pattern.matcher(oib.getAttribute("value"));
		boolean allDigits = matcher.find();

		List<WebElement> listErrorMessages = findMultipleByClassName("field-validation-error");

		if (oib.getAttribute("value").length() != 11 || allDigits == false)
			assertTrue(listErrorMessages.stream().anyMatch(item -> "Neispravan OIB".equals(item.getText())));

		else if (oib.getAttribute("value").length() == 11 && allDigits == true)

			assertFalse(listErrorMessages.stream().anyMatch(item -> "Neispravan OIB".equals(item.getText())));
	}

	@Test
	@Order(10)
	@DisplayName("TC-10: Verify “register” button is visible and clickable")
	void validateLoginButtonIsVisibleAndClickable() {

		scroll(1400);

		WebElement register = findByID("register-button");
		assertTrue(register.isDisplayed() && register.isEnabled());

	}

	@Test
	@Order(11)
	@DisplayName("TC-11: Verify “password” and “confirm password” must be equal")
	void validatePasswordandConfirmPasswordMustBeEqual() {

		scroll(1400);

		WebElement password = findByID("Password");
		WebElement confirmPassword = findByID("ConfirmPassword");

		Credentials credentials = new Credentials("hello123", "hello123");

		write(password, credentials.getPassword());
		write(confirmPassword, credentials.getConfirmPassword());

		pause(1000);

		String passwordValue = password.getAttribute("value");
		String confirmPasswordValue = confirmPassword.getAttribute("value");

		List<WebElement> listErrorMessages = findMultipleByClassName("field-validation-error");

		// assert password and confirm password are the same
		assertEquals(passwordValue, confirmPasswordValue);

		// assert no error message for password is displayed
		assertFalse(listErrorMessages.stream()
				.anyMatch(item -> "Lozinka i potvrda lozinke se ne podudaraju".equals(item.getText())));

		password.clear();
		confirmPassword.clear();

		credentials.setPassword("New Password");
		credentials.setConfirmPassword("NeW Password");

		write(password, credentials.getPassword());
		write(confirmPassword, credentials.getConfirmPassword());

		passwordValue = password.getAttribute("value");
		confirmPasswordValue = confirmPassword.getAttribute("value");

		pause(1000);

		listErrorMessages = findMultipleByClassName("field-validation-error");

		// assert password and confirm password are not the same
		assertNotEquals(passwordValue, confirmPasswordValue);

		// assert an error message for password is displayed
		assertTrue(listErrorMessages.stream()
				.anyMatch(item -> "Lozinka i potvrda lozinke se ne podudaraju.".equals(item.getText())));

	}

	@Test
	@Order(12)
	@DisplayName("TC-12: Verify “password” must have 6 characters at least")
	void validatePasswordMustHaveMin6Characters() {

		scroll(1400);

		WebElement password = findByID("Password");

		Credentials credentials = new Credentials("12345", "");

		write(password, credentials.getPassword());

		password.sendKeys(Keys.ENTER);

		pause(1000);

		List<WebElement> listErrorMessages = findMultipleByClassName("field-validation-error");

		assertTrue(listErrorMessages.stream()
				.anyMatch(item -> "Lozinka treba imati najmanje 6 znakova.".equals(item.getText())));

		password.clear();

		credentials.setPassword("123456");

		write(password, credentials.getPassword());

		pause(1000);

		assertFalse(listErrorMessages.stream()
				.anyMatch(item -> "Lozinka treba imati najmanje 6 znakova.".equals(item.getText())));

	}

	@Test
	@Order(13)
	@DisplayName("TC-13: Verify valid registration")
	void validateValidRegistration() {

		User aPerson = new Person("James", "Bond", "jisom23799@lankew.com",
				new Credentials("password123", "password123"));

		WebElement name = findByID("FirstName");
		WebElement lastName = findByID("LastName");
		WebElement email = findByID("Email");
		WebElement password = findByID("Password");
		WebElement confirmPassword = findByID("ConfirmPassword");
		WebElement register = findByID("register-button");

		scroll(700);

		write(name, aPerson.getFirstName());
		write(lastName, aPerson.getLastName());
		write(email, aPerson.getEmail());
		write(password, aPerson.getCredentials().getPassword());
		write(confirmPassword, aPerson.getCredentials().getConfirmPassword());

		scroll(1600);

		pause(1000);

		click(register, 2000);

		assertNotEquals("URLs should not be the same since registration should be successful.", url,
				driver.getCurrentUrl());

	}

	@Test
	@Order(14)
	@DisplayName("TC-14: Verify all required fields")
	void validateRequiredFields() {

		WebElement register = findByID("register-button");

		scroll(1400);

		click(register, 2000);

		List<WebElement> listErrorMessages = findMultipleByClassName("field-validation-error");

		for (WebElement errorMessage : listErrorMessages) {
			System.out.println(errorMessage.getAttribute("value"));
		}

		assertEquals(url, driver.getCurrentUrl());

	}

	private void radioButtonCheck(WebElement genderMale, WebElement genderFemale, int iteration) {

		switch (iteration) {
		case 0:
			click(genderMale, 1500);
			assertFalse(genderFemale.isSelected());
			break;
		case 1:
			click(genderFemale, 1500);
			assertFalse(genderMale.isSelected());
			break;
		default:
			System.out.println("There might be somehow more than 2 genders, please check the form.");
			break;
		}

	}

	// checkboxCheck
	private void checkboxCheck(WebElement checkbox, WebElement companyInfo) {

		if (checkbox.getAttribute("checked") == null) {
			String expectedDisplayAttribute = "none";
			String actualDisplayAttribute = companyInfo.getCssValue("display");

			assertTrue(actualDisplayAttribute.contains(expectedDisplayAttribute));
			assertFalse(companyInfo.isDisplayed());

			click(checkbox, 2000);

		} else if (checkbox.getAttribute("checked") != null) {
			String expectedDisplayAttribute = "block";
			String actualDisplayAttribute = companyInfo.getCssValue("display");

			assertTrue(actualDisplayAttribute.contains(expectedDisplayAttribute));
			assertTrue(companyInfo.isDisplayed());

			click(checkbox, 2000);
		}
	}

	// a method so scroll the page
	private void scroll(int value) {
		JavascriptExecutor executor = (JavascriptExecutor) driver;
		executor.executeScript("scroll(0," + value + ");");
		pause(1000);
	}

	// a method to find element by ID
	private WebElement findByID(String id) {
		return driver.findElement(By.id(id));
	}

	// a method to find element by class
	private WebElement findByClass(String className) {
		return driver.findElement(By.className(className));
	}

	// a method to find element by name
	private WebElement findByName(String name) {
		return driver.findElement(By.name(name));
	}

	// a method to find element by xpath
	private WebElement findByXpath(String xpath) {
		return driver.findElement(By.xpath(xpath));
	}

	// a method to find multiple elements by class
	private List<WebElement> findMultipleByClassName(String name) {
		return driver.findElements(By.className(name));
	}

	// a method to add text to element
	private void write(WebElement element, String keysToSend) {
		element.sendKeys(keysToSend);
	}

	// a method to perform a simple and slowed down click on element
	private void click(WebElement element, int timeOutInSeconds) {

		WebDriverWait wait = new WebDriverWait(driver, timeOutInSeconds);
		wait.until(ExpectedConditions.elementToBeClickable(element));
		element.click();
		pause(timeOutInSeconds);

	}

	// a method to pause the execution for a set amount of time
	private void pause(Integer milliseconds) {
		try {
			TimeUnit.MILLISECONDS.sleep(milliseconds);
		} catch (InterruptedException e) {
			e.printStackTrace();
		}
	}

	// after each test close the chrome driver (page)
	@AfterEach
	void teardown() {
		driver.close();
	}

}
