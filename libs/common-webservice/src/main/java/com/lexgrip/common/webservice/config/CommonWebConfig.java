package com.lexgrip.common.webservice.config;

import java.util.List;
import com.lexgrip.common.webservice.resolver.UserArgumentResolver;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Configuration;
import org.springframework.web.method.support.HandlerMethodArgumentResolver;
import org.springframework.web.servlet.config.annotation.WebMvcConfigurer;

@Configuration
public class CommonWebConfig implements WebMvcConfigurer {

  private final UserArgumentResolver userArgumentResolver;

  @Autowired
  public CommonWebConfig(UserArgumentResolver userArgumentResolver) {
    this.userArgumentResolver = userArgumentResolver;
  }

  @Override
  public void addArgumentResolvers(List<HandlerMethodArgumentResolver> resolvers) {
    resolvers.add(userArgumentResolver);
  }
}
