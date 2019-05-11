package ru.psv4.tempdatchiki.security;

import ru.psv4.tempdatchiki.backend.data.User;

@FunctionalInterface
public interface CurrentUser {
	User getUser();
}
