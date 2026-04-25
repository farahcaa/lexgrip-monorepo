package com.lexgrip.common.webservice.jwt;

import org.springframework.security.oauth2.jwt.Jwt;
import org.springframework.stereotype.Service;

/**
 * User claims extractor service
 *
 * <p>
 * A service responsible for extracting user claims from a JWT.
 * </p>
 */
@Service
public class UserClaimsExtractorService {

  private static final String PREFERRED_USERNAME_CLAIM = "preferred_username";
  private static final String EMAIL_CLAIM = "email";
  private static final String NAME_CLAIM = "fullName";

  /**
   * Extract user claims from a JWT
   *
   * @param jwt the JWT
   * @return the user claims
   */
  public UserClaims extractClaims(Jwt jwt) {
    String preferredUsername = jwt.getClaimAsString(PREFERRED_USERNAME_CLAIM);
    String email = jwt.getClaimAsString(EMAIL_CLAIM);
    String fullName = jwt.getClaimAsString(NAME_CLAIM);

    UserClaims userClaims = new UserClaims();

    userClaims.setPreferredUsername(preferredUsername);
    userClaims.setEmail(email);
    userClaims.setFullName(fullName);

    return userClaims;
  }
}
