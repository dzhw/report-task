package eu.dzhw.fdz.metadatamanagement.tasks.reporttask.mdm.dto;

import java.io.Serializable;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import lombok.ToString;

/**
 * The error dto.
 */
@ToString
@NoArgsConstructor
@AllArgsConstructor
@Data
public class ErrorDto implements Serializable {
  private static final long serialVersionUID = 1459627043777361503L;
  
  private String entity;
  private String message;
  private Object invalidValue;
  private String property;
}
