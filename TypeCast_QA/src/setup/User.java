package setup;

/*
 * Abstract class "User" since a person or a legal entity can be referred to as users.
 */

public abstract class User {

	private String firstName;
	private String lastName;
	private String email;
	private Credentials credentials;

	public User(String firstName, String lastName, String email, Credentials credentials) {
		this.firstName = firstName;
		this.lastName = lastName;
		this.email = email;
		this.credentials = credentials;
	}

	public abstract void login(Credentials credentials);

	public String getFirstName() {
		return firstName;
	}

	public void setFirstName(String firstName) {
		this.firstName = firstName;
	}

	public String getLastName() {
		return lastName;
	}

	public void setLastName(String lastName) {
		this.lastName = lastName;
	}

	public String getEmail() {
		return email;
	}

	public void setEmail(String email) {
		this.email = email;
	}

	public Credentials getCredentials() {
		return credentials;
	}

	public void setCredentials(Credentials credentials) {
		this.credentials = credentials;
	}

}
