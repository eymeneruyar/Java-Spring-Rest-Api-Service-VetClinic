package vetcilinicservice.Services;

import org.springframework.security.core.Authentication;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.security.web.authentication.WebAuthenticationDetails;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.context.request.RequestContextHolder;
import org.springframework.web.context.request.ServletRequestAttributes;
import vetcilinicservice.Entities.Role;
import vetcilinicservice.Entities.User;
import vetcilinicservice.Entities.AccountActivities;
import vetcilinicservice.Repositories._jpa.AccountActivityRepository;
import vetcilinicservice.Repositories._jpa.UserRepository;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.security.web.authentication.logout.LogoutSuccessHandler;
import org.springframework.security.web.authentication.logout.SimpleUrlLogoutSuccessHandler;
import vetcilinicservice.Utils.Util;

import javax.naming.AuthenticationException;
import javax.servlet.http.HttpServletRequest;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.ArrayList;
import java.util.Collection;
import java.util.List;
import java.util.Optional;

@Service
@Transactional
public class UserService extends SimpleUrlLogoutSuccessHandler implements UserDetailsService, LogoutSuccessHandler{

    final UserRepository uRepo;
    final AccountActivityRepository aRepo;
    public UserService(UserRepository uRepo, AccountActivityRepository aRepo) {
        this.uRepo = uRepo;
        this.aRepo = aRepo;
    }

    //Security Login
    @Override
    public UserDetails loadUserByUsername(String email) {
        System.out.println("Email: " + email);
        UserDetails userDetails = null;
        Optional<User> oUser = uRepo.findByuEmailEqualsIgnoreCase(email);
        if(oUser.isPresent()){
            User us = oUser.get();
            userDetails = new org.springframework.security.core.userdetails.User(
                    us.getUEmail(),
                    us.getUPassword(),
                    us.isEnabled(),
                    us.isTokenExpired(),
                    true,
                    true,
                    getAuthorities(us.getRoles()) );
        }
        else{
            throw new UsernameNotFoundException("Kullanıcı adı ya da şifre hatalı!");
        }
        return userDetails;
    }

    private static List<GrantedAuthority> getAuthorities (List<Role> roles) {
        List<GrantedAuthority> authorities = new ArrayList<>();
        for (Role role : roles) {
            authorities.add(new SimpleGrantedAuthority(role.getRName()));
        }
        return authorities;
    }

    public User register(User us) throws AuthenticationException {

        if(!Util.isEmail(us.getUEmail())){
            throw new AuthenticationException("Bu mail formatı hatalı!");
        }

        //Kullanıcılar bölümünden güncelleme işlemi yapabilmek için kapattım.
        /*Optional<User> uOpt = uRepo.findByuEmailEqualsIgnoreCase(us.getUEmail());
        if(uOpt.isPresent()){
            throw new AuthenticationException("Bu kullanıcı daha önce kayıtlı");
        }*/

        us.setUPassword(encoder().encode(us.getUPassword()));

        return uRepo.saveAndFlush(us);
    }

    public PasswordEncoder encoder() {
        return new BCryptPasswordEncoder();
    }

    public AccountActivities info(){

        AccountActivities accountInfo = new AccountActivities();
        Authentication auth = SecurityContextHolder.getContext().getAuthentication();

        String email = auth.getName();
        if( email != null){
            Optional <User> oUser = uRepo.findByuEmailEqualsIgnoreCase(email);
            if(oUser.isPresent()){
                HttpServletRequest request = ((ServletRequestAttributes) RequestContextHolder.getRequestAttributes()).getRequest();
                User user = oUser.get();
                WebAuthenticationDetails details = (WebAuthenticationDetails) auth.getDetails();
                String roles = " ";
                String name = user.getUName();
                String surname = user.getUSurname();
                String sessionId = request.getSession().getId();
                String ip = request.getRemoteAddr();
                String url = String.valueOf(request.getRequestURL());
                String imageFile = user.getUProfileImage();
                Collection<? extends GrantedAuthority> role = auth.getAuthorities();
                roles = role + roles;

                LocalDateTime myDateObj = LocalDateTime.now();
                DateTimeFormatter myFormatObj = DateTimeFormatter.ofPattern("dd-MM-yyyy HH:mm:ss");
                String date = myDateObj.format(myFormatObj);

                accountInfo = new AccountActivities(name,surname,email,sessionId,ip,roles,url,date);
                accountInfo.setImageFile(imageFile);
                aRepo.save(accountInfo);

            }
        }

        return accountInfo;

    }

}
