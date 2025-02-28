package com.midas.crm.service.serviceImpl;

import com.midas.crm.entity.User;
import com.midas.crm.repository.UserRepository;
import com.midas.crm.security.UserPrincipal;
import com.midas.crm.security.jwt.JwtProvider;
import com.midas.crm.service.AuthenticationService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.stereotype.Service;

@Service
public class AuthenticationServiceImpl implements AuthenticationService {

    @Autowired
    private AuthenticationManager authenticationManager;

    @Autowired
    private JwtProvider jwtProvider;

    @Autowired
    private UserRepository userRepository;

    @Override
    public com.midas.crm.entity.User signInAndReturnJWT(com.midas.crm.entity.User signInRequest) {
        com.midas.crm.entity.User user = userRepository.findByEmail(signInRequest.getEmail())
                .orElseThrow(() -> new UsernameNotFoundException("El usuario no fue encontrado:" + signInRequest.getEmail()));

        Authentication authentication = authenticationManager.authenticate(
                new UsernamePasswordAuthenticationToken(user.getUsername(), signInRequest.getPassword())
        );

        UserPrincipal userPrincipal = (UserPrincipal) authentication.getPrincipal();
        String jwt = jwtProvider.generateToken(userPrincipal);

        com.midas.crm.entity.User sigInUser = userPrincipal.getUser();
        sigInUser.setToken(jwt);

        return sigInUser;
    }


}