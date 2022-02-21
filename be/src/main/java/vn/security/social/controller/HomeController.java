package vn.security.social.controller;

import com.fasterxml.jackson.core.JsonParseException;
import com.google.api.client.googleapis.auth.oauth2.GoogleIdToken;
import com.google.api.client.googleapis.auth.oauth2.GoogleIdTokenVerifier;
import com.google.api.client.http.javanet.NetHttpTransport;
import com.google.api.client.json.jackson2.JacksonFactory;
import com.google.common.io.BaseEncoding;
import org.modelmapper.ModelMapper;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.BadCredentialsException;
import org.springframework.security.authentication.DisabledException;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.social.ExpiredAuthorizationException;
import org.springframework.social.InvalidAuthorizationException;
import org.springframework.social.facebook.api.Facebook;
import org.springframework.social.facebook.api.impl.FacebookTemplate;
import org.springframework.web.bind.annotation.*;
import vn.security.social.dto.AuthenticationRequest;
import vn.security.social.dto.AuthenticationResponse;
import vn.security.social.dto.TokenDTO;
import vn.security.social.dto.UserLoginDTO;
import vn.security.social.model.Account;
import vn.security.social.model.ERole;
import vn.security.social.model.Provider;
import vn.security.social.model.User;
import vn.security.social.service.IAccountService;
import vn.security.social.service.IUserService;
import vn.security.social.service.security.MyUserDetailsService;
import vn.security.social.util.JwtUtil;

import java.util.Collections;
import java.util.LinkedHashMap;

@RestController
@CrossOrigin
@RequestMapping("api")
public class HomeController {

    private AuthenticationManager authenticationManager;

    private MyUserDetailsService userDetailsService;

    private JwtUtil jwtUtil;

    private IUserService userService;

    private IAccountService accountService;

    private PasswordEncoder passwordEncoder;

    private ModelMapper modelMapper;

    public HomeController(AuthenticationManager authenticationManager, MyUserDetailsService userDetailsService,
                          JwtUtil jwtUtil, IUserService userService, IAccountService accountService, PasswordEncoder passwordEncoder,
                          ModelMapper modelMapper) {
        this.authenticationManager = authenticationManager;
        this.userDetailsService = userDetailsService;
        this.jwtUtil = jwtUtil;
        this.userService = userService;
        this.accountService = accountService;
        this.passwordEncoder = passwordEncoder;
        this.modelMapper = modelMapper;
    }

    // password user login with Facebook & Google = passwordSocial
    private final String passwordSocial = "social_facebook_&_gooogle_u093840932%@*&^#@!*kjewhj,ncoiweq}kqw'''asdsadqw5";

    // My client ID Google in website: https://console.developers.google.com/
    private final String googleClientId = "1046534921769-0ce6sb6v97gen0mbqpgc3ct9vil3h078.apps.googleusercontent.com";

    @GetMapping("home")
    public String hello() {
        return "Welcome to the project Spring security";
    }

    @GetMapping("user")
    public String user() {
        return ("Welcome USER");
    }

    @GetMapping("admin")
    public String admin() {
        return ("Welcome ADMIN");
    }

    @GetMapping("employee")
    public String management() {
        return ("Welcome EMPLOYEE");
    }

    // parameter : username+password
    // true -> OK -> return: token + User
    // false -> username+password ( fail or account locked ) -> return : ERROR
    private ResponseEntity<AuthenticationResponse> login(AuthenticationRequest authenticationRequest) {
        String jwt = null;
        UserLoginDTO userLoginDto = null;
        String status;
        HttpStatus httpStatus;
        try {
            // Check username & password exists in database?
            this.authenticationManager.authenticate(new UsernamePasswordAuthenticationToken(authenticationRequest.getUsername(), authenticationRequest.getPassword()));
            UserDetails userDetails = this.userDetailsService.loadUserByUsername(authenticationRequest.getUsername());
            jwt = jwtUtil.generateToken(userDetails);
            User user = this.userService.getByUsername(authenticationRequest.getUsername());
            userLoginDto = this.modelMapper.map(user, UserLoginDTO.class);
            status = "Success";
            httpStatus = HttpStatus.OK;
        } catch (DisabledException disabledException) {
            // Catch Var enable = false
            status = "Account locked";
            httpStatus = HttpStatus.BAD_REQUEST;
        } catch (BadCredentialsException badCredentialsException) {
            // Catch username & password exists in database
            status = "Wrong password or username";
            httpStatus = HttpStatus.BAD_REQUEST;
        } catch (Exception exception) {
            status = "Error server";
            httpStatus = HttpStatus.BAD_REQUEST;
        }
        return new ResponseEntity<>(new AuthenticationResponse(jwt, userLoginDto, status), httpStatus);
    }

    // Check username exitsts,
    // true -> get user
    // false -> save user
    // call login (user.username, passwordSocial)
    private ResponseEntity<AuthenticationResponse> loginSocial(String email, String fullName, String image, Provider provider) {
        User user = new User();
        Account account = new Account();

        account.setUsername(email);
        account.setPassword(this.passwordEncoder.encode(this.passwordSocial));
        account.setRole(ERole.ROLE_USER);
        account.setEnable(true);

        user.setAccount(account);
        user.setEmail(email);
        user.setFullName(fullName);
        user.setImage(image);
        user.setProvider(provider);

        if (this.accountService.isUsernameExists(email)) {
            user = this.userService.getByUsername(email);
        } else {
            user = this.userService.save(user);
        }

        return login(new AuthenticationRequest(user.getAccount().getUsername(), this.passwordSocial));
    }

    @PostMapping("login")
    public ResponseEntity<AuthenticationResponse> loginLocal(@RequestBody AuthenticationRequest authenticationRequest) {
        return login(authenticationRequest);
    }

    @PostMapping("/google")
    public ResponseEntity<AuthenticationResponse> LoginGoogle(@RequestBody TokenDTO tokenDto) {
        String status;
        try {
            final NetHttpTransport transport = new NetHttpTransport();
            final JacksonFactory jacksonFactory = JacksonFactory.getDefaultInstance();
            GoogleIdTokenVerifier.Builder verifier =
                    new GoogleIdTokenVerifier.Builder(transport, jacksonFactory)
                            .setAudience(Collections.singletonList(this.googleClientId));
            final GoogleIdToken googleIdToken = GoogleIdToken.parse(verifier.getJsonFactory(), tokenDto.getToken());
            final GoogleIdToken.Payload payload = googleIdToken.getPayload(); // data user

            String email = payload.getEmail();
            String fullName = payload.get("name").toString();
            String image = payload.get("picture").toString();
            Provider provider = Provider.GOOGLE;

            return loginSocial(email, fullName, image, provider);
        } catch (JsonParseException | BaseEncoding.DecodingException | IllegalArgumentException invalid) {
            status = "Token invalid";
        } catch (ExpiredAuthorizationException expiredAuthorizationException) {
            status = "Token expires";
        } catch (Exception e) {
            status = "Error server";
        }
        return new ResponseEntity<>(new AuthenticationResponse(null, null, status), HttpStatus.BAD_REQUEST);
    }

    @PostMapping("/facebook")
    public ResponseEntity<AuthenticationResponse> loginFacebook(@RequestBody TokenDTO tokenDto) {
        String status;
        try {
            Facebook facebook = new FacebookTemplate(tokenDto.getToken());
            final String[] data = {"email", "name", "picture"};
            // get data for parameters
            org.springframework.social.facebook.api.User userFacebook = facebook.fetchObject("me", org.springframework.social.facebook.api.User.class, data); // data user

            String email = userFacebook.getEmail();
            String fullName = userFacebook.getName();
            // get url image for Object LinkedHashmap (extra,picture,data)
            String image = ((LinkedHashMap) ((LinkedHashMap) userFacebook.getExtraData().get("picture")).get("data")).get("url").toString();

            Provider provider = Provider.FACEBOOK;

            return loginSocial(email, fullName, image, provider);
        } catch (InvalidAuthorizationException invalid) {
            status = "Token invalid";
        } catch (ExpiredAuthorizationException expiredAuthorizationException) {
            status = "Token expires";
        } catch (Exception e) {
            status = "Error server";
        }
        return new ResponseEntity<>(new AuthenticationResponse(null, null, status), HttpStatus.BAD_REQUEST);
    }

}