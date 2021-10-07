package vetcilinicservice.Config;

import org.springframework.context.annotation.Configuration;
import org.springframework.security.config.annotation.authentication.builders.AuthenticationManagerBuilder;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.builders.WebSecurity;
import org.springframework.security.config.annotation.web.configuration.WebSecurityConfigurerAdapter;
import vetcilinicservice.Services.UserService;

@Configuration
public class ServiceSecurityConfig extends WebSecurityConfigurerAdapter {

    final UserService uService;
    public ServiceSecurityConfig(UserService uService) {
        this.uService = uService;
    }

    //sql -> jpa query ile user denetimi yapılacak.
    @Override
    protected void configure(AuthenticationManagerBuilder auth) throws Exception {
        auth.userDetailsService(uService).passwordEncoder(uService.encoder());
    }

    //Rollere göre giriş izni sağlamaktadır.
    @Override
    protected void configure(HttpSecurity http) throws Exception {
        http
                .httpBasic()
                .and()
                .authorizeRequests()
                .antMatchers("/home/**").hasAnyRole("SECRETARY","DOCTOR","ADMIN")
                .antMatchers("/customer/**").hasAnyRole("SECRETARY","DOCTOR")
                .antMatchers("/patient/**").hasAnyRole("SECRETARY","DOCTOR")
                .antMatchers("/treatment/**").hasAnyRole("SECRETARY","DOCTOR")
                .antMatchers("/calendar/**").hasAnyRole("SECRETARY","DOCTOR")
                .antMatchers("/sales/**").hasAnyRole("SECRETARY","DOCTOR")
                .antMatchers("/buying/**").hasAnyRole("SECRETARY","DOCTOR")
                .antMatchers("/laboratory/**").hasAnyRole("SECRETARY","DOCTOR")
                .antMatchers("/payActions/**").hasAnyRole("SECRETARY","DOCTOR")
                .antMatchers("/productDefinition/**").hasAnyRole("SECRETARY","DOCTOR")
                .antMatchers("/vaccineDefinition/**").hasAnyRole("SECRETARY","DOCTOR")
                .antMatchers("/supplier/**").hasAnyRole("SECRETARY","DOCTOR")
                .antMatchers("/agenda/**").hasAnyRole("SECRETARY","DOCTOR","ADMIN")
                .antMatchers("/storage/**").hasAnyRole("SECRETARY","DOCTOR")
                .antMatchers("/account/**").hasAnyRole("SECRETARY","DOCTOR","ADMIN")
                .antMatchers("/cat/**").hasAnyRole("SECRETARY","DOCTOR","ADMIN")
                .antMatchers("/settingsUsers/**").hasRole("ADMIN")
                .antMatchers("/register/**").permitAll()
                .antMatchers("/admin/**").permitAll()
                .and()
                .csrf().disable()
                .formLogin().disable()
                .logout().logoutSuccessUrl("/admin/logout").invalidateHttpSession(true);
        http.headers().frameOptions().disable();
    }

    @Override
    public void configure(WebSecurity web) throws Exception {

        web.ignoring().antMatchers(
                "/v2/api-docs",
                "/configuration/ui",
                "/swagger-resources/**",
                "/configuration/security",
                "/swagger-ui.html",
                "/webjars/**");

    }

}
