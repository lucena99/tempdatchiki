package ru.psv4.tempdatchiki;

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
import org.springframework.security.web.authentication.SimpleUrlAuthenticationSuccessHandler;
import org.springframework.web.util.UrlPathHelper;
import ru.psv4.tempdatchiki.backend.data.User;
import ru.psv4.tempdatchiki.backend.service.UserService;
import ru.psv4.tempdatchiki.security.*;
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

	public static final String STARTUP_PAGE = "/app/controllers";
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
	@Order(2)
	public static class RestapiWebSecurityConfig extends WebSecurityConfigurerAdapter {
		@Override
		protected void configure(HttpSecurity http) throws Exception {
			http.csrf().disable()
			// the ant matcher is what limits the scope of this configuration.
					.antMatcher("/restapi/**").authorizeRequests()
					.antMatchers("/restapi/**").hasAnyAuthority(Role.RESTAPI, Role.ADMIN)
					.and()
					.httpBasic();
		}
	}

	@Configuration
	@Order(3)
	public static class RestapiWebSecurityConfig2 extends WebSecurityConfigurerAdapter {
		@Override
		protected void configure(HttpSecurity http) throws Exception {
			http.csrf().disable()
			// the ant matcher is what limits the scope of this configuration.
					.antMatcher("/swagger-ui.html/**").authorizeRequests()
					.antMatchers("/swagger-ui.html/**").hasAuthority(Role.ADMIN)
					.and().httpBasic();
		}
	}

	@Configuration
	@Order(4)
	public static class AppWebSecurityConfig extends WebSecurityConfigurerAdapter {
		public AppWebSecurityConfig() {
			super(false);
		}
		@Override
		protected void configure(HttpSecurity http) throws Exception {
			SavedRequestAwareAuthenticationSuccessHandler successHandler =
					new SavedRequestAwareAuthenticationSuccessHandler();
			successHandler.setDefaultTargetUrl(STARTUP_PAGE);

			// Not using Spring CSRF here to be able to use plain HTML for the login page
			http.csrf().disable()
					// Register our CustomRequestCache, that saves unauthorized access attempts, so
					// the user is redirected after login.
					.requestCache().requestCache(new CustomRequestCache())

//					// Allow all flow internal requests.
					.and().authorizeRequests()
					.requestMatchers(SecurityUtils::isFrameworkInternalRequest).permitAll()

					.and()
					.authorizeRequests()
					.antMatchers("/app/**").hasAnyAuthority(Role.getUIRoles())

					.and().formLogin().loginPage("/app/login").permitAll().loginProcessingUrl("/app/login")
					.failureUrl("/app/login?error")

					// Register the success handler that redirects users to the page they last tried
					// to access
					.successHandler(successHandler)

					// Configure logout
					.and().logout().logoutSuccessUrl(STARTUP_PAGE);
		}

		@Override
		public void configure(WebSecurity web) throws Exception {
			web.ignoring().antMatchers(
					// Vaadin Flow static META-INF.resources
					"/app/VAADIN/**",

					// the standard favicon URI
					"/app/favicon.ico",

					// the robots exclusion standard
					"/app/robots.txt",

					// web application manifest
					"/app/manifest.webmanifest",
					"/app/sw.js",
					"/app/offline-page.html",

					// icons and images
					"/app/icons/**",
					"/app/images/**",

					// (development mode) static META-INF.resources
					"/app/frontend/**",

					// (development mode) webjars
					"/app/webjars/**",

					// (development mode) H2 debugging console
					"/app/h2-console/**",

					// (production mode) static META-INF.resources
					"/app/static.frontend-es5/**", "/app/static.frontend-es6/**");
		}
	}
}
