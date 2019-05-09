package ru.psv4.tempdatchiki.security;

import ru.psv4.tempdatchiki.model.User;

@FunctionalInterface
public interface CurrentUser {
	User getUser();
}
