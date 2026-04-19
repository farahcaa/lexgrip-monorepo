package com.lexgrip.app.platform.service.resolver;

import com.lexgrip.app.platform.service.model.user.UserEntity;
import com.lexgrip.app.platform.service.model.user.UserRepository;
import com.lexgrip.common.webservice.jwt.User;
import com.lexgrip.common.webservice.jwt.UserContextService;
import org.jetbrains.annotations.NotNull;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.core.MethodParameter;
import org.springframework.security.authentication.BadCredentialsException;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.oauth2.jwt.Jwt;
import org.springframework.stereotype.Component;
import org.springframework.web.bind.support.WebDataBinderFactory;
import org.springframework.web.context.request.NativeWebRequest;
import org.springframework.web.context.request.RequestAttributes;
import org.springframework.web.method.support.HandlerMethodArgumentResolver;
import org.springframework.web.method.support.ModelAndViewContainer;

import java.util.Optional;
import java.util.UUID;

@Component
public class CurrentUserEntityArgumentResolver implements HandlerMethodArgumentResolver {

    private static final Logger LOGGER = LoggerFactory.getLogger(CurrentUserEntityArgumentResolver.class);
    private final UserContextService userContextService;
    private final UserRepository userRepository;

    public CurrentUserEntityArgumentResolver(UserContextService userContextService, UserRepository userRepository) {
        this.userContextService = userContextService;
        this.userRepository = userRepository;
    }

    @Override
    public boolean supportsParameter(MethodParameter parameter) {
        return parameter.getParameterType().equals(UserEntity.class)
                && parameter.hasParameterAnnotation(CurrentUserEntity.class);
    }

    @Override
    public Object resolveArgument(@NotNull MethodParameter parameter,
                                  ModelAndViewContainer mavContainer,
                                  @NotNull NativeWebRequest webRequest,
                                  WebDataBinderFactory binderFactory) {
        LOGGER.info("Resolving @CurrentUserEntity for parameter '{}'", parameter.getParameterName());
        UserEntity cachedUserEntity =
                (UserEntity) webRequest.getAttribute("currentUserEntity", RequestAttributes.SCOPE_REQUEST);

        if (cachedUserEntity != null) {
            LOGGER.info("Using cached currentUserEntity id='{}'", cachedUserEntity.getId());
            return cachedUserEntity;
        }

        User currentUser = resolveCurrentJwtUser();
        if (currentUser == null) {
            LOGGER.error("Failed to resolve @CurrentUserEntity because JWT user could not be resolved");
            throw new BadCredentialsException("No authenticated user found");
        }

        UserEntity userEntity = resolveOrCreateUserEntity(currentUser);
        webRequest.setAttribute("currentUserEntity", userEntity, RequestAttributes.SCOPE_REQUEST);
        LOGGER.info("Resolved @CurrentUserEntity id='{}'", userEntity.getId());
        return userEntity;
    }

    private User resolveCurrentJwtUser() {
        Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
        Jwt jwt = extractJwt(authentication);
        if (jwt == null) {
            return null;
        }
        return userContextService.resolveUser(jwt);
    }

    private Jwt extractJwt(Authentication authentication) {
        if (authentication == null) {
            LOGGER.error("SecurityContext authentication is null");
            return null;
        }
        LOGGER.info("Authentication type='{}', principalType='{}', credentialsType='{}'",
                authentication.getClass().getName(),
                authentication.getPrincipal() == null ? "null" : authentication.getPrincipal().getClass().getName(),
                authentication.getCredentials() == null ? "null" : authentication.getCredentials().getClass().getName());
        Object principal = authentication.getPrincipal();
        if (principal instanceof Jwt jwt) {
            LOGGER.info("JWT extracted from authentication principal");
            return jwt;
        }
        Object credentials = authentication.getCredentials();
        if (credentials instanceof Jwt jwt) {
            LOGGER.info("JWT extracted from authentication credentials");
            return jwt;
        }
        LOGGER.error("JWT not found in authentication principal or credentials");
        return null;
    }

    private UserEntity resolveOrCreateUserEntity(User currentUser) {
        UUID userId = currentUser.getId();
        String email = currentUser.getClaims().getEmail().orElse(null);
        String preferredUsername = currentUser.getClaims().getPreferredUsername().orElse(null);
        LOGGER.info("Resolving app user for userId='{}', email='{}', preferredUsername='{}'",
                userId, email, preferredUsername);

        Optional<UserEntity> existingUser = findExistingUser(userId, email, preferredUsername);
        if (existingUser.isPresent()) {
            LOGGER.info("Found existing app user id='{}'", existingUser.get().getId());
            return existingUser.get();
        }

        UserEntity userEntity = new UserEntity();
        userEntity.setId(userId);
        userEntity.setUsername(buildUsername(preferredUsername, userId));
        userEntity.setEmail(buildEmail(email, userId));
        userEntity.setName(preferredUsername);

        try {
            LOGGER.info("Creating new app user with id='{}', username='{}', email='{}'",
                    userEntity.getId(), userEntity.getUsername(), userEntity.getEmail());
            return userRepository.save(userEntity);
        } catch (RuntimeException e) {
            LOGGER.error("Failed creating app user; retrying lookup. userId='{}'", userId, e);
            return findExistingUser(userId, email, preferredUsername).orElseThrow(
                    () -> new BadCredentialsException("Authenticated user could not be resolved", e)
            );
        }
    }

    private Optional<UserEntity> findExistingUser(UUID userId, String email, String preferredUsername) {
        Optional<UserEntity> byId = userRepository.findById(userId);
        if (byId.isPresent()) {
            LOGGER.info("Matched app user by id='{}'", userId);
            return byId;
        }

        if (email != null && !email.isBlank()) {
            Optional<UserEntity> byEmail = userRepository.findByEmail(email);
            if (byEmail.isPresent()) {
                LOGGER.info("Matched app user by email='{}' -> id='{}'", email, byEmail.get().getId());
                return byEmail;
            }
            LOGGER.info("No app user match by email='{}'", email);
        }

        if (preferredUsername != null && !preferredUsername.isBlank()) {
            Optional<UserEntity> byUsername = userRepository.findByUsername(preferredUsername);
            if (byUsername.isPresent()) {
                LOGGER.info("Matched app user by username='{}' -> id='{}'", preferredUsername, byUsername.get().getId());
                return byUsername;
            }
            LOGGER.info("No app user match by username='{}'", preferredUsername);
        }

        LOGGER.info("No existing app user found for userId='{}'", userId);
        return Optional.empty();
    }

    private String buildUsername(String preferredUsername, UUID userId) {
        if (preferredUsername != null && !preferredUsername.isBlank()) {
            return preferredUsername;
        }
        return "user-" + userId.toString().substring(0, 8);
    }

    private String buildEmail(String email, UUID userId) {
        if (email != null && !email.isBlank()) {
            return email;
        }
        return userId + "@local.invalid";
    }
}
