package live.karyl.security;

import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.security.config.annotation.method.configuration.EnableMethodSecurity;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.configuration.EnableWebSecurity;
import org.springframework.security.core.userdetails.User;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.provisioning.InMemoryUserDetailsManager;
import org.springframework.security.web.SecurityFilterChain;

@Configuration
@EnableWebSecurity
@EnableMethodSecurity
public class SecurityConfig {

	@Bean
	public SecurityFilterChain securityFilterChain(HttpSecurity http) throws Exception {
		http
				.authorizeHttpRequests()
				.requestMatchers("/login").permitAll()
				.anyRequest().authenticated()
				.and()
				.formLogin((form) -> {
							try {
								form
										.loginPage("/login")
										.loginProcessingUrl("/login")
										.defaultSuccessUrl("/", true)
										.permitAll()
										.and()
										.rememberMe()
										.key("remember-me-key")
										.tokenValiditySeconds(86400);
							} catch (Exception e) {
								throw new RuntimeException(e);
							}
						}
				)
				.logout().permitAll()
				.and().csrf().disable();
		return http.build();
	}

	@Bean
	public InMemoryUserDetailsManager userDetailsManager() {
		UserDetails user = User.withDefaultPasswordEncoder()
				.username("admin")
				.password("@@admin@@")
				.roles("USER")
				.build();
		return new InMemoryUserDetailsManager(user);
	}

}
