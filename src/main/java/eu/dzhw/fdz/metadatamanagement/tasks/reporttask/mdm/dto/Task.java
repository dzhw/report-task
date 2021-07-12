package eu.dzhw.fdz.metadatamanagement.tasks.reporttask.mdm.dto;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;
import lombok.ToString;

/**
 * DTO for getting task states from the MDM.
 * 
 * @author René Reitmann
 */
@ToString(callSuper = true)
@NoArgsConstructor
@AllArgsConstructor
@Data
@Builder
public class Task {
  /**
   * The id or task number of the task.
   */
  private String id;
  
  /**
   * The current state of the task.
   */
  private TaskState state;
  
  /**
   * The location URI of the result of the task.
   */
  private String location;
  
  /**
   * The type of the task.
   */
  private TaskType type;
  
  /**
   * The list of errors which occurred during execution of the task.
   */
  private ErrorListDto errorList;

  /**
   * State of tasks.
   */
  public enum TaskState {
    RUNNING, DONE, FAILURE;
  }

  /**
   * type of tasks.
   */
  public enum TaskType {
    DATA_SET_REPORT
  }
}
