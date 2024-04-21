package com.hrm.Human.Resource.Management.jwt;

import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RestController;

@RestController
public class TokenController {

    private final JwtTokenProvider tokenProvider;

    public TokenController(JwtTokenProvider tokenProvider) {
        this.tokenProvider = tokenProvider;
    }

    @PostMapping("/verify-token")
    public boolean verifyToken(@RequestBody String token) {
        return tokenProvider.validateToken(token);
    }
}

