package user;

/*
 * A type of user - "Person".
 */

import setup.Credentials;
import setup.User;

public class Person extends User {

	public Person(String firstName, String lastName, String email, Credentials credentials) {
		super(firstName, lastName, email, credentials);
	}

	@Override
	public void login(Credentials credentials) {
		// TODO Auto-generated method stub

	}

	@Override
	public String toString() {
		String person = "Person: " + getFirstName() + " " + getLastName();
		return person;
	}

}
