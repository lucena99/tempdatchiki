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
		public RestapiWebSecurityConfig() {
			super(false);
		}
		@Override
		protected void configure(HttpSecurity http) throws Exception {
//				http
////					.sessionManagement().sessionCreationPolicy(SessionCreationPolicy.STATELESS)
//					.and()
//						.antMatchers("/restapi/**",
//			"/swagger-ui.html/**",
//			"/v2/api-docs",
//			"/swagger-resources",
//			"/configuration/security",
//			"/configuration/ui")
//						.authorizeRequests().
//						.hasAuthority("!!!")
//					.and()
//						.httpBasic();
			http.csrf().disable()
			// the ant matcher is what limits the scope of this configuration.
					.antMatcher("/restapi/**").authorizeRequests()
					.antMatchers("/restapi/**").hasAnyAuthority(Role.RESTAPI, Role.ADMIN)
					.and()
					.httpBasic();/*.and()
					.antMatcher("/swagger-ui.html/**").authorizeRequests()
					.antMatchers("/swagger-ui.html/**").hasAuthority(Role.RESTAPI)
					.and().httpBasic().and()
					.antMatcher("/v2/api-docs").authorizeRequests()
					.antMatchers("/v2/api-docs").hasAuthority(Role.RESTAPI)
					.and().httpBasic().and()
					.antMatcher("/swagger-resources").authorizeRequests()
					.antMatchers("/swagger-resources").hasAuthority(Role.RESTAPI)
					.and().httpBasic().and()
					.antMatcher("/configuration/security").authorizeRequests()
					.antMatchers("/configuration/security").hasAuthority(Role.RESTAPI)
					.and().httpBasic().and()
					.antMatcher("/configuration/ui").authorizeRequests()
					.antMatchers("/configuration/ui").hasAuthority(Role.RESTAPI)
					.and().httpBasic();*/

//			http
//					.sessionManagement().sessionCreationPolicy(SessionCreationPolicy.STATELESS)
//					.and()
//					.authorizeRequests()
//					.antMatchers("/restapi/**")
//					.hasAuthority("admin")
//					.and()spring
//					.httpBasic()
//			.and();
		}
	}

	@Configuration
	@Order(3)
	public static class RestapiWebSecurityConfig2 extends WebSecurityConfigurerAdapter {
		public RestapiWebSecurityConfig2() {
			super(false);
		}
		@Override
		protected void configure(HttpSecurity http) throws Exception {
//				http
////					.sessionManagement().sessionCreationPolicy(SessionCreationPolicy.STATELESS)
//					.and()
//						.antMatchers("/restapi/**",
//			"/swagger-ui.html/**",
//			"/v2/api-docs",
//			"/swagger-resources",
//			"/configuration/security",
//			"/configuration/ui")
//						.authorizeRequests().
//						.hasAuthority("!!!")
//					.and()
//						.httpBasic();
			http.csrf().disable()
			// the ant matcher is what limits the scope of this configuration.
					/*.antMatcher("/restapi/**").authorizeRequests()
					.antMatchers("/restapi/**").hasAuthority(Role.RESTAPI)
					.and().httpBasic().and()*/
					.antMatcher("/swagger-ui.html/**").authorizeRequests()
					.antMatchers("/swagger-ui.html/**").hasAuthority(Role.ADMIN)
					.and().httpBasic();/*.and()
					.antMatcher("/v2/api-docs").authorizeRequests()
					.antMatchers("/v2/api-docs").hasAuthority(Role.RESTAPI)
					.and().httpBasic().and()
					.antMatcher("/swagger-resources").authorizeRequests()
					.antMatchers("/swagger-resources").hasAuthority(Role.RESTAPI)
					.and().httpBasic().and()
					.antMatcher("/configuration/security").authorizeRequests()
					.antMatchers("/configuration/security").hasAuthority(Role.RESTAPI)
					.and().httpBasic().and()
					.antMatcher("/configuration/ui").authorizeRequests()
					.antMatchers("/configuration/ui").hasAuthority(Role.RESTAPI)
					.and().httpBasic();*/

//			http
//					.sessionManagement().sessionCreationPolicy(SessionCreationPolicy.STATELESS)
//					.and()
//					.authorizeRequests()
//					.antMatchers("/restapi/**")
//					.hasAuthority("admin")
//					.and()spring
//					.httpBasic()
//			.and();
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

					// Restrict access to our application.
//					.and().authorizeRequests()
//
//					// Allow all flow internal requests.
					.and().authorizeRequests()
					.requestMatchers(SecurityUtils::isFrameworkInternalRequest).permitAll()

					.and()
//					.antMatcher("/app/**")
//					.antMatcher("/frontend/**")
//					.antMatcher("/VAADIN/**")
//					.antMatcher("/images/**")
//					.antMatcher("/icons/**")
//					.antMatcher("/styles/**")
					.authorizeRequests()
					.antMatchers("/app/**").hasAnyAuthority(Role.getUIRoles())

//					.and().authorizeRequests()
//					// Allow all requests by logged in users.
//					.antMatchers("/app/**",
//							"/app/frontend/**","/frontend/**",
//							"/VAADIN/**", "/images/**", "/icons/**",
//							"/styles/**"
//							)
//					.hasAnyAuthority(Role.getUIRoles())



//					// Configure the login page.
//					.and().formLogin().loginPage(LOGIN_URL).permitAll().loginProcessingUrl(LOGIN_PROCESSING_URL)
//					.failureUrl(LOGIN_FAILURE_URL)
//
//					// Register the success handler that redirects users to the page they last tried
//					// to access
//					.successHandler(new AppSuccessHandler())
//
//					// Configure logout
//					.and().logout().logoutSuccessUrl(LOGOUT_SUCCESS_URL);

					.and().formLogin().loginPage("/app/login").permitAll().loginProcessingUrl("/app/login")
					.failureUrl("/app/login?error")

					// Register the success handler that redirects users to the page they last tried
					// to access
					.successHandler(successHandler)

					// Configure logout
					.and().logout().logoutSuccessUrl("/app/controllers");
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
//			.requestMatchers((r) -> {
//				return SecurityUtils.isFrameworkInternalRequest(r) &&
//						new UrlPathHelper().getPathWithinApplication(r).equals("/app/");
//			});
		}
//
//		/**
//		 * Allows access to static META-INF.resources, bypassing Spring security.
//		 */
//		@Override
//		public void configure(WebSecurity web) throws Exception {
//			web.ignoring().antMatchers(
//					// Vaadin Flow static META-INF.resources
//					"/VAADIN/**",
//
//					// the standard favicon URI
//					"/favicon.ico",
//
//					// the robots exclusion standard
//					"/robots.txt",
//
//					// web application manifest
//					"/manifest.webmanifest",
//					"/sw.js",
//					"/offline-page.html",
//
//					// icons and images
//					"/icons/**",
//					"/images/**",
//
//					// (development mode) static META-INF.resources
//					"/frontend/**",
//
//					// (development mode) webjars
//					"/webjars/**",
//
//					// (development mode) H2 debugging console
//					"/h2-console/**",
//
//					// (production mode) static META-INF.resources
//					"/static.frontend-es5/**", "/static.frontend-es6/**",
//					"/index.html");
//		}
	}

//	@Configuration
//	@Order(1)
	public static class GeneralWebSecurityConfig extends WebSecurityConfigurerAdapter {

		public GeneralWebSecurityConfig() {
			super(true);
		}

//		@Override
//		protected void configure(HttpSecurity http) throws Exception {
//			SavedRequestAwareAuthenticationSuccessHandler successHandler =
//					new SavedRequestAwareAuthenticationSuccessHandler();
//			successHandler.setDefaultTargetUrl("/app/recipients");
//			// Not using Spring CSRF here to be able to use plain HTML for the login page
//			http.csrf().disable()
//					// Register our CustomRequestCache, that saves unauthorized access attempts, so
//					// the user is redirected after login.
//					.requestCache().requestCache(new CustomRequestCache())
//
//					// Restrict access to our application.
//					.and().authorizeRequests().antMatchers("/app/recipients").hasAuthority(Role.ADMIN)
////
//					// Configure the login page.
//					.and().formLogin().loginPage("/app/login")./*permitAll().*/loginProcessingUrl("/app/login")
//					.failureUrl(LOGIN_FAILURE_URL)
//
//					// Register the success handler that redirects users to the page they last tried
//					// to access
//					.successHandler(successHandler)
//
//					// Configure logout
//					.and().logout().logoutSuccessUrl(LOGOUT_SUCCESS_URL);
//		}


		@Override
		protected void configure(HttpSecurity http) throws Exception {

		}

		/**
		 * Allows access to static META-INF.resources, bypassing Spring security.
		 */
		@Override
		public void configure(WebSecurity web) throws Exception {
//			web.ignoring().antMatchers(
//					// Vaadin Flow static META-INF.resources
//					"/app/VAADIN/**",
//
//					// the standard favicon URI
//					"/app/favicon.ico",
//
//					// the robots exclusion standard
//					"/app/robots.txt",
//
//					// web application manifest
//					"/app/manifest.webmanifest",
//					"/app/sw.js",
//					"/app/offline-page.html",
//
//					// icons and images
//					"/app/icons/**",
//					"/app/images/**",
//
//					// (development mode) static META-INF.resources
//					"/app/frontend/**",
//
//					// (development mode) webjars
//					"/app/webjars/**",
//
//					// (development mode) H2 debugging console
//					"/app/h2-console/**",
//
//					// (production mode) static META-INF.resources
//					"/app/static.frontend-es5/**", "/app/static.frontend-es6/**",
//					"/app/index.html");
		}
	}
}
