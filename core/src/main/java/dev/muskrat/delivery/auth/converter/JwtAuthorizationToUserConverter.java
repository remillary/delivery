package dev.muskrat.delivery.auth.converter;

import dev.muskrat.delivery.auth.dao.AuthorizedUser;
import dev.muskrat.delivery.auth.repository.AuthorizedUserRepository;
import dev.muskrat.delivery.auth.security.jwt.JwtTokenProvider;
import dev.muskrat.delivery.components.converter.ObjectConverter;
import dev.muskrat.delivery.components.exception.JwtAuthenticationException;
import dev.muskrat.delivery.components.exception.JwtTokenExpiredException;
import lombok.RequiredArgsConstructor;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.stereotype.Component;

import java.util.Optional;

@Component
@RequiredArgsConstructor
public class JwtAuthorizationToUserConverter implements ObjectConverter<String, AuthorizedUser> {

    private final AuthorizedUserRepository userRepository;
    private final JwtTokenProvider jwtTokenProvider;

    @Override
    public AuthorizedUser convert(String authorization) {
        String resolveToken = jwtTokenProvider.resolveToken(authorization);
        if (resolveToken == null)
            throw new JwtAuthenticationException("Jwt auth exception");

        if (!jwtTokenProvider.validateToken(resolveToken))
            throw new JwtTokenExpiredException("Token is expired");

        String username = jwtTokenProvider.getUsername(resolveToken);
        if (username == null)
            throw new JwtAuthenticationException("Jwt auth exception");

        Optional<AuthorizedUser> byUsername = userRepository.findByUsername(username);
        if (byUsername.isEmpty())
            throw new UsernameNotFoundException("AuthorizedUser with " + username + " not found");

        return byUsername.get();
    }
}