package eu.dzhw.fdz.metadatamanagement.tasks.datasetreporttask.mdm.dto;

import java.io.Serializable;

import lombok.ToString;

/**
 * The error dto.
 */
@ToString
public class ErrorDto implements Serializable {

  private static final long serialVersionUID = 1L;
    
  private final String entity;
  private final String message;
  private final Object invalidValue;
  private final String property;
  
  
  /**
   * Construct the error dto.
   * @param entity the name of the entity
   * @param message the internationalized message
   * @param invalidValue the rejected value of the property
   * @param property the name of the property (empty for global errors)
   */
  public ErrorDto(String entity, String message, Object invalidValue, String property) {
    this.entity = entity;
    this.message = message;
    this.invalidValue = invalidValue;
    this.property = property;
  }

  public String getEntity() {
    return entity;
  }

  public String getMessage() {
    return message;
  }

  public Object getInvalidValue() {
    return invalidValue;
  }

  public String getProperty() {
    return property;
  }
}
