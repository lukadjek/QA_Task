package user;

/*
 * A type of user - "Company".
 */

import setup.Credentials;
import setup.User;

public class Company extends User {

	public Company(String firstName, String lastName, String email, Credentials credentials) {
		super(firstName, lastName, email, credentials);
		// TODO Auto-generated constructor stub
	}

	@Override
	public void login(Credentials credentials) {
		// TODO Auto-generated method stub

	}

	@Override
	public String toString() {
		String company = "Company: " + getFirstName() + " " + getLastName();
		return company;
	}

}
