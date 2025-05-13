package com.example.praksa.Controllers;

import com.example.praksa.Converters.UserDTOConverter;
import com.example.praksa.Converters.UserNodeDTOConverter;
import com.example.praksa.DTOs.JWTAuthDTO;
import com.example.praksa.DTOs.UserDTO;
import com.example.praksa.Models.RefreshToken;
import com.example.praksa.Models.UserApp;
import com.example.praksa.Models.UserAppToken;
import com.example.praksa.Models.UserNode;
import com.example.praksa.Repositories.UserAppRepository;
import com.example.praksa.Repositories.UserNodeRepository;
import com.example.praksa.Security.TokenHandler;
import com.example.praksa.Services.RefreshTokenService;
import com.example.praksa.Services.RoleService;
import com.example.praksa.Services.UserAppService;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseCookie;
import org.springframework.http.ResponseEntity;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.web.bind.annotation.*;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.util.Objects;
import java.util.Optional;

@RestController
@RequestMapping(value = "/auth")
public class AuthenticationController {
    private final UserAppService userAppService;
    private final UserDTOConverter userDTOConverter;
    private final UserAppRepository userAppRepository;
    private final RoleService roleService;
    private final AuthenticationManager authenticationManager;
    private final TokenHandler tokenHandler;
    private final RefreshTokenService refreshTokenService;
    private final UserNodeRepository userNodeRepository;
    private final UserNodeDTOConverter userNodeDTOConverter;

    public AuthenticationController(UserAppService userAppService , UserDTOConverter userDTOConverter, UserAppRepository userAppRepository, RoleService roleService, AuthenticationManager authenticationManager, TokenHandler tokenHandler, RefreshTokenService refreshTokenService, UserNodeRepository userNodeRepository, UserNodeDTOConverter userNodeDTOConverter) {
        this.userAppService = userAppService;
        this.userDTOConverter = userDTOConverter;
        this.userAppRepository = userAppRepository;
        this.roleService = roleService;
        this.authenticationManager = authenticationManager;
        this.tokenHandler = tokenHandler;
        this.refreshTokenService = refreshTokenService;
        this.userNodeRepository = userNodeRepository;
        this.userNodeDTOConverter = userNodeDTOConverter;
    }

    @PostMapping("/register")
    public ResponseEntity<UserAppToken> registerUser (@RequestBody UserDTO request){
        if(userAppService.findByEmail(request.getEmail()) == null){

            UserApp user = userDTOConverter.DTOToUser(request);
            user.setRegistered(true);
            user.setRole(roleService.findByName("ROLE_USER"));
            userAppRepository.save(user);

            UserNode userNode = userNodeDTOConverter.DTOToUserNode(request);
            userNodeRepository.save(userNode);

            String jwt = this.tokenHandler.generateToken(user.getUsername(),user.getRole());
            ResponseCookie jwtCookie = tokenHandler.generateJwtCookie(user);
            RefreshToken refreshToken = refreshTokenService.createRefreshToken(user.getId());
            ResponseCookie jwtRefreshCookie = tokenHandler.generateRefreshJwtCookie(refreshToken.getToken());
            return ResponseEntity.ok()
                    .header(HttpHeaders.SET_COOKIE, jwtCookie.toString())
                    .header(HttpHeaders.SET_COOKIE, jwtRefreshCookie.toString())
                    .body(new UserAppToken(jwt,user.getEmail()));
        } else {
            return new ResponseEntity<>(null, HttpStatus.CONFLICT);
        }
    }
    @PostMapping("/login")
    public ResponseEntity<UserAppToken> login (@RequestBody JWTAuthDTO authDTO , HttpServletResponse response){
        Authentication authentication = this.authenticationManager.authenticate(new UsernamePasswordAuthenticationToken(authDTO.getEmail(), authDTO.getPassword()));
        SecurityContextHolder.getContext().setAuthentication(authentication);
        UserApp user = (UserApp) authentication.getPrincipal();
        String jwt = this.tokenHandler.generateToken(user.getUsername(),user.getRole());
        ResponseCookie jwtCookie = tokenHandler.generateJwtCookie(user);
        RefreshToken refreshToken = refreshTokenService.createRefreshToken(user.getId());
        ResponseCookie jwtRefreshCookie = tokenHandler.generateRefreshJwtCookie(refreshToken.getToken());
        return ResponseEntity.ok()
                .header(HttpHeaders.SET_COOKIE, jwtCookie.toString())
                .header(HttpHeaders.SET_COOKIE, jwtRefreshCookie.toString())
                .body(new UserAppToken(jwt,user.getEmail()));
    }


    @PostMapping("/logout")
    public ResponseEntity<?> logoutUser(){
        Object principal = SecurityContextHolder.getContext().getAuthentication().getPrincipal();
        if (!Objects.equals(principal.toString(), "anonymousUser")) {
            refreshTokenService.deleteByUserId(((UserApp) principal).getId());
        }

        ResponseCookie jwtCookie = tokenHandler.getCleanJwtCookie();
        ResponseCookie jwtRefreshCookie = tokenHandler.getCleanJwtRefreshCookie();

        return ResponseEntity.ok()
                .header(HttpHeaders.SET_COOKIE, jwtCookie.toString())
                .header(HttpHeaders.SET_COOKIE, jwtRefreshCookie.toString())
                .build();
    }

    @PostMapping("/refresh-token")
    public ResponseEntity<?> refreshToken(HttpServletRequest request) throws Exception {
        String refreshToken = tokenHandler.getJwtRefreshFromCookies(request);

        if ((refreshToken != null) && (refreshToken.length() > 0)) {
            Optional<RefreshToken> optionalRefreshToken = refreshTokenService.findByToken(refreshToken);
            if(optionalRefreshToken.isPresent()) {
                RefreshToken token = optionalRefreshToken.get();
                if(refreshTokenService.verifyExpiration(token) != null) {
                    UserApp tokenUser = token.getUser();
                    ResponseCookie jwtCookie = tokenHandler.generateJwtCookie(tokenUser);
                    return ResponseEntity.ok()
                            .header(HttpHeaders.SET_COOKIE, jwtCookie.toString())
                            .header(HttpHeaders.SET_COOKIE, refreshToken)
                            .build();
                }
            } else {
                return ResponseEntity.badRequest().build();
            }
        }

        return ResponseEntity.badRequest().build();
    }

    @GetMapping("/current")
    public ResponseEntity<UserDTO> getCurrentUser() {
        var user = (UserApp) SecurityContextHolder.getContext().getAuthentication().getPrincipal();
        return ResponseEntity.ok(userDTOConverter.UserToDTO(user));
    }
}
