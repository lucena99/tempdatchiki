package ru.psv4.tempdatchiki.security;

public class Role {
	public static final String RECIPIENT = "recipient";
	// This role implicitly allows access to all views.
	public static final String ADMIN = "admin";

	private Role() {
		// Static methods and fields only
	}

	public static String[] getAllRoles() {
		return new String[] { RECIPIENT, ADMIN };
	}

}
