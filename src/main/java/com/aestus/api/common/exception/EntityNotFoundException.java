package com.aestus.api.common.exception;

public class EntityNotFoundException extends Exception {

  public EntityNotFoundException(Class entityClass, Object id) {
    super(String.format(entityClass.getSimpleName() + " with id=%s NOT found.", id.toString()));
  }

  public EntityNotFoundException(String entityType, Object id) {
    super(String.format(entityType + " with id=%d NOT found.", id.toString()));
  }
}
