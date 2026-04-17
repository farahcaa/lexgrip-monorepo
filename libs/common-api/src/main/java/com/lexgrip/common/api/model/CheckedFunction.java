package com.lexgrip.common.api.model;

@FunctionalInterface
public interface CheckedFunction<R> {
  R apply() throws Exception;
}
