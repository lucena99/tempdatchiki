package ru.psv4.tempdatchiki.security;

import org.springframework.context.annotation.Primary;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.stereotype.Service;
import ru.psv4.tempdatchiki.beans.UserService;
import ru.psv4.tempdatchiki.model.User;

import javax.annotation.Resource;
import java.util.Collections;
import java.util.Optional;

/**
 * Implements the {@link UserDetailsService}.
 * 
 * This implementation searches for {@link User} entities by the login
 * supplied in the login screen.
 */
@Service
@Primary
public class UserDetailsServiceImpl implements UserDetailsService {

	@Resource
	private UserService userService;

	/**
	 *
	 * Recovers the {@link User} from the database using the e-mail address supplied
	 * in the login screen. If the user is found, returns a
	 * {@link org.springframework.security.core.userdetails.User}.
	 *
	 * @param username User's e-mail address
	 * 
	 */
	@Override
	public UserDetails loadUserByUsername(String username) throws UsernameNotFoundException {
		Optional<User> userOp = userService.findByLoginIgnoreCase(username);
		if (!userOp.isPresent()) {
			throw new UsernameNotFoundException("Не найден пользователь с логином: " + username);
		} else {
			User user = userOp.get();
			return new org.springframework.security.core.userdetails.User(user.getLogin(), user.getPassword(),
					Collections.singletonList(new SimpleGrantedAuthority(user.getRole())));
		}
	}
}