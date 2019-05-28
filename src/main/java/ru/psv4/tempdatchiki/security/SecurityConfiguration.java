package ru.psv4.tempdatchiki.security;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.config.ConfigurableBeanFactory;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.Scope;
import org.springframework.core.annotation.Order;
import org.springframework.security.config.annotation.authentication.builders.AuthenticationManagerBuilder;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.builders.WebSecurity;
import org.springframework.security.config.annotation.web.configuration.EnableWebSecurity;
import org.springframework.security.config.annotation.web.configuration.WebSecurityConfigurerAdapter;
import org.springframework.security.config.http.SessionCreationPolicy;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.security.web.authentication.SavedRequestAwareAuthenticationSuccessHandler;
import ru.psv4.tempdatchiki.backend.service.UserService;
import ru.psv4.tempdatchiki.backend.data.User;
import ru.psv4.tempdatchiki.utils.TdConst;

/**
 * Configures spring security, doing the following:
 * <li>Bypass security checks for static META-INF.resources,</li>
 * <li>Restrict access to the application, allowing only logged in users,</li>
 * <li>Set up the login form,</li>
 * <li>Configures the {@link UserDetailsServiceImpl}.</li>

 */
@EnableWebSecurity
public class SecurityConfiguration {

	private static final String LOGIN_PROCESSING_URL = "/login";
	private static final String LOGIN_FAILURE_URL = "/login?error";
	private static final String LOGIN_URL = "/login";
	private static final String LOGOUT_SUCCESS_URL = "/" + TdConst.PAGE_RECIPIENTS;

	private final UserDetailsService userDetailsService;

	@Autowired
	private PasswordEncoder passwordEncoder;
	
	@Autowired
	public SecurityConfiguration(UserDetailsService userDetailsService) {
		this.userDetailsService = userDetailsService;
	}

	/**
	 * The password encoder to use when encrypting passwords.
	 */
	@Bean
	public PasswordEncoder passwordEncoder() {
		return new BCryptPasswordEncoder();
	}

	@Bean
	@Scope(ConfigurableBeanFactory.SCOPE_PROTOTYPE)
	public CurrentUser currentUser(UserService userService) {
		final String login = SecurityUtils.getLogin();
		User user =
			login != null ? userService.findByLoginIgnoreCase(login).orElse(null) :
				null;
		return () -> user;
	}

	/**
	 * Registers our UserDetailsService and the password encoder to be used on login attempts.
	 */
	@Autowired
	public  void configureGlobal(AuthenticationManagerBuilder auth) {
		try {
			auth.userDetailsService(userDetailsService).passwordEncoder(passwordEncoder);
		} catch (Exception e) {
			throw new RuntimeException(e);
		}
	}

	@Configuration
	@Order(1)
	public static class RestapiWebSecurityConfig extends WebSecurityConfigurerAdapter {
		@Override
		protected void configure(HttpSecurity http) throws Exception {
				http
					.sessionManagement().sessionCreationPolicy(SessionCreationPolicy.STATELESS)
					.and()
					.antMatcher("/restapi/**")
					.antMatcher("/swagger/**")
					.authorizeRequests()
						.anyRequest().hasAuthority(Role.RESTAPI)
						.and()
					.httpBasic();
		}
	}

	@Configuration
	@Order(2)
	public static class AppWebSecurityConfig extends WebSecurityConfigurerAdapter {
		@Override
		protected void configure(HttpSecurity http) throws Exception {
			// Not using Spring CSRF here to be able to use plain HTML for the login page
			http.csrf().disable()
					// Register our CustomRequestCache, that saves unauthorized access attempts, so
					// the user is redirected after login.
					.requestCache().requestCache(new CustomRequestCache())

					// Restrict access to our application.
					.and().authorizeRequests()

					// Allow all flow internal requests.
					.requestMatchers(SecurityUtils::isFrameworkInternalRequest).permitAll()

					// Allow all requests by logged in users.
					.anyRequest().hasAnyAuthority(Role.getAppRoles())

					// Configure the login page.
					.and().formLogin().loginPage(LOGIN_URL).permitAll().loginProcessingUrl(LOGIN_PROCESSING_URL)
					.failureUrl(LOGIN_FAILURE_URL)

					// Register the success handler that redirects users to the page they last tried
					// to access
					.successHandler(new SavedRequestAwareAuthenticationSuccessHandler())

					// Configure logout
					.and().logout().logoutSuccessUrl(LOGOUT_SUCCESS_URL);
		}

		/**
		 * Allows access to static META-INF.resources, bypassing Spring security.
		 */
		@Override
		public void configure(WebSecurity web) throws Exception {
			web.ignoring().antMatchers(
					// Vaadin Flow static META-INF.resources
					"/VAADIN/**",

					// the standard favicon URI
					"/favicon.ico",

					// the robots exclusion standard
					"/robots.txt",

					// web application manifest
					"/manifest.webmanifest",
					"/sw.js",
					"/offline-page.html",

					// icons and images
					"/icons/**",
					"/images/**",

					// (development mode) static META-INF.resources
					"/frontend/**",

					// (development mode) webjars
					"/webjars/**",

					// (development mode) H2 debugging console
					"/h2-console/**",

					// (production mode) static META-INF.resources
					"/static.frontend-es5/**", "/static.frontend-es6/**",
					"/index.html",

					"/sw.js");
		}
	}
}
