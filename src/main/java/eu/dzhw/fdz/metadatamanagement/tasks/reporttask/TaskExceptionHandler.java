package eu.dzhw.fdz.metadatamanagement.tasks.reporttask;

import org.springframework.cloud.task.listener.TaskExecutionListener;
import org.springframework.cloud.task.repository.TaskExecution;
import org.springframework.stereotype.Component;

import eu.dzhw.fdz.metadatamanagement.tasks.reporttask.config.TaskProperties;
import eu.dzhw.fdz.metadatamanagement.tasks.reporttask.mdm.MdmRestClient;
import lombok.RequiredArgsConstructor;

/**
 * This component attempts to notify the MDM user who initiated this task in case any error occurred
 * during the task execution.
 * 
 * @author René Reitmann
 */
@RequiredArgsConstructor
@Component
public class TaskExceptionHandler implements TaskExecutionListener {
  private final MdmRestClient mdmClient;

  private final TaskProperties taskProperties;

  @Override
  public void onTaskStartup(TaskExecution taskExecution) {
    // do nothing
  }

  @Override
  public void onTaskEnd(TaskExecution taskExecution) {
    // do nothing
  }

  @Override
  public void onTaskFailed(TaskExecution taskExecution, Throwable throwable) {
    Throwable cause = throwable.getCause();
    mdmClient.postTaskError(taskProperties.getId(), taskProperties.getOnBehalfOf(),
        cause != null ? cause.toString() : throwable.toString(), taskProperties.getType());
  }
}
