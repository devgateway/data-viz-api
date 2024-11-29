package org.devgateway.viz.gateway.service;

import org.devgateway.viz.gateway.repository.JwtTokenRepository;
import org.devgateway.viz.gateway.repository.UserRepository;
import org.devgateway.viz.gateway.security.JwtTokenProvider;
import org.devgateway.viz.gateway.security.domain.JwtToken;
import org.devgateway.viz.gateway.security.domain.User;
import org.devgateway.viz.gateway.security.exception.AuthException;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.AuthenticationException;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;

import java.util.ArrayList;

@Service
public class LoginService {
    @Autowired
    private PasswordEncoder passwordEncoder;

    @Autowired
    private JwtTokenProvider jwtTokenProvider;

    @Autowired
    private AuthenticationManager authenticationManager;

    @Autowired
    private UserRepository userRepository;

    @Autowired
    private JwtTokenRepository jwtTokenRepository;

    public String login(String username, String password) {
        try {
            User user = userRepository.findByUsername(username);
            if (user == null) {
                throw new AuthException("Invalid username");
            }

            authenticationManager.authenticate(new UsernamePasswordAuthenticationToken(username, password));
            String token = jwtTokenProvider.createToken(username, new ArrayList<>());

            return token;

        } catch (AuthenticationException e) {
            throw new AuthException("Invalid username or password");
        }
    }

    public User saveUser(User user) {
        user.setPassword(passwordEncoder.encode(user.getPassword()));
        return userRepository.save(user);
    }

    public boolean logout(String token) {
        jwtTokenRepository.delete(new JwtToken(token));

        return true;
    }

    public Boolean isValidToken(String token) {
        return jwtTokenProvider.validateToken(token);
    }

}
