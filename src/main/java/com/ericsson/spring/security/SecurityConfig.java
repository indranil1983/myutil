package com.ericsson.spring.security;

import java.util.Arrays;

import javax.sql.DataSource;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.AuthenticationProvider;
import org.springframework.security.authentication.ProviderManager;
import org.springframework.security.config.annotation.authentication.builders.AuthenticationManagerBuilder;
import org.springframework.security.config.annotation.authentication.configuration.EnableGlobalAuthentication;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.configuration.EnableWebSecurity;
import org.springframework.security.config.annotation.web.configuration.WebSecurityConfigurerAdapter;
import org.springframework.security.ldap.authentication.ad.ActiveDirectoryLdapAuthenticationProvider;

import com.ericsson.spring.util.AppresourceUtil;

@Configuration
@EnableWebSecurity
@EnableGlobalAuthentication
public class SecurityConfig extends WebSecurityConfigurerAdapter 
{
	private static final Logger logger = LoggerFactory.getLogger(SecurityConfig.class);
	
		
	 @Autowired
	 CustomSuccessHandler customSuccessHandler;
	 
	 @Value("${LDAP_DOMAIN}")
	 private String DOMAIN;

	 @Value("${LDAP_URL}")
     private String URL;
	
	 @Value("${app.env}")
	private String environment;
	
	
	
	

	@Override
	protected void configure(HttpSecurity http) throws Exception {

	  http.authorizeRequests()
		.antMatchers("/production").access("hasAuthority('EBI_OPS') or hasAuthority('EBI_ADMIN')")
		.antMatchers("/lab").access("hasAuthority('EBI_DEV') or hasAuthority('EBI_ADMIN') or hasAuthority('EBI_LAB')")
		.antMatchers("/rest/**").access("hasAuthority('EBI_DEV') or hasAuthority('EBI_ADMIN') or hasAuthority('EBI_LAB') or hasAuthority('EBI_OPS')")
		.and().formLogin().loginPage("/login").loginProcessingUrl("/j_spring_security_check").successHandler(customSuccessHandler)
		.usernameParameter("username").passwordParameter("password").failureUrl("/login?denied=true")
		.and().logout().logoutUrl("/j_spring_security_logout").logoutSuccessUrl("/login?logout=true");
	  
	  
	  http
      .antMatcher("/**")
      .csrf().disable()
      .httpBasic();
	  
	  http
		.headers()
			.frameOptions().sameOrigin()
			.httpStrictTransportSecurity().disable();

	  http.sessionManagement().maximumSessions(1);
	  
	}
	
	@Override
    protected void configure(AuthenticationManagerBuilder authManagerBuilder) throws Exception {
         
		

		if(AppresourceUtil.isProduction(environment))
		{
			authManagerBuilder.authenticationProvider(activeDirectoryLdapAuthenticationProvider()).userDetailsService(userDetailsService());
			logger.debug("Production Environment ");
			logger.debug(userDetailsService().toString());
		}
		else{
			authManagerBuilder.inMemoryAuthentication().withUser("prod").password("prod").roles("EBI_OPS").authorities("EBI_OPS").and().
			  withUser("admin").password("admin").roles("EBI_ADMIN").authorities("EBI_ADMIN").and().
			  withUser("lab").password("lab").roles("EBI_LAB").authorities("EBI_LAB");
		}
		
    }

    @Bean
    public AuthenticationManager authenticationManager() throws Exception{
    	if(AppresourceUtil.isProduction(environment))
		{
    		return new ProviderManager(Arrays.asList(activeDirectoryLdapAuthenticationProvider()));
		}
    	else return super.authenticationManager();
    }
    @Bean
    public AuthenticationProvider activeDirectoryLdapAuthenticationProvider() {
    	AuthenticationProvider provider = new CustomActiveDirectoryLdapAuthenticationProvider(DOMAIN, URL);
         return provider;
    }
	
	@Autowired
	public void configAuthentication(AuthenticationManagerBuilder auth) throws Exception {

	 /* auth.jdbcAuthentication().dataSource(dataSource)
		.usersByUsernameQuery(
			"select username,password, enabled from users where username=?")
*/		/*auth.jdbcAuthentication().dataSource(dataSource).authoritiesByUsernameQuery(
			"select username, role from user_roles where username=?");*/
	}
	
	
	
	
}
