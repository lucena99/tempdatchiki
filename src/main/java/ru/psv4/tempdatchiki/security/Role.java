package ru.psv4.tempdatchiki.security;

public class Role {
	public static final String RECIPIENT = "recipient";
	// This role implicitly allows access to all views.
	public static final String ADMIN = "admin";

	public static final String RESTAPI = "restapi";

	private Role() {}

	public static String[] getAppRoles() {
		return new String[] { RECIPIENT, ADMIN };
	}
}
