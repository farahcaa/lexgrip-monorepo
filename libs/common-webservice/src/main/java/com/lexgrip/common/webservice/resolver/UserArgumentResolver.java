package com.lexgrip.common.webservice.resolver;

import org.jetbrains.annotations.NotNull;
import com.lexgrip.common.webservice.jwt.User;
import com.lexgrip.common.webservice.jwt.UserContextService;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
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

@Component
public class UserArgumentResolver implements HandlerMethodArgumentResolver {

  private static final Logger LOGGER = LoggerFactory.getLogger(UserArgumentResolver.class);
  private final UserContextService userContextService;

  @Autowired
  public UserArgumentResolver(UserContextService userContextService) {
    this.userContextService = userContextService;
  }

  @Override
  public boolean supportsParameter(MethodParameter parameter) {
    return parameter.getParameterType().equals(User.class)
        && parameter.hasParameterAnnotation(CurrentUser.class);
  }

  @Override
  public Object resolveArgument(@NotNull MethodParameter parameter,
                                ModelAndViewContainer mavContainer, NativeWebRequest webRequest,
                                WebDataBinderFactory binderFactory) {
    LOGGER.info("Resolving @CurrentUser for parameter '{}'", parameter.getParameterName());
    // resolve user once from model or security context
    User cachedUser =
        (User) webRequest.getAttribute("currentUser", RequestAttributes.SCOPE_REQUEST);

    if (cachedUser != null) {
      return cachedUser;
    }

    User currentUser = resolveCurrentUser();

    if (currentUser == null) {
      LOGGER.error("Failed to resolve @CurrentUser. Authentication was present but JWT/User mapping returned null");
      throw new BadCredentialsException("No current user found");
    }

    webRequest.setAttribute("currentUser", currentUser, RequestAttributes.SCOPE_REQUEST);
    LOGGER.info("Resolved @CurrentUser with userId='{}'", currentUser.getId());

    return currentUser;
  }

  /**
   * Resolves the current user from the security context via the {@link UserContextService}
   *
   * @return the current user
   */
  private User resolveCurrentUser() {
    Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
    Jwt jwtUser = extractJwt(authentication);
    if (jwtUser == null) {
      return null;
    }

    return userContextService.resolveUser(jwtUser);
  }

  private Jwt extractJwt(Authentication authentication) {
    if (authentication == null) {
      LOGGER.error("SecurityContext authentication is null");
      return null;
    }
    LOGGER.info("Authentication type='{}', principalType='{}', credentialsType='{}'",
        authentication.getClass().getName(),
        authentication.getPrincipal() == null ? "null" : authentication.getPrincipal().getClass().getName(),
        authentication.getCredentials() == null ? "null"
            : authentication.getCredentials().getClass().getName());
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
}
