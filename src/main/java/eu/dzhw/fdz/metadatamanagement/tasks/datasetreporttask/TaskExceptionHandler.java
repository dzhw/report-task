package eu.dzhw.fdz.metadatamanagement.tasks.datasetreporttask;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.cloud.task.listener.annotation.FailedTask;
import org.springframework.cloud.task.repository.TaskExecution;
import org.springframework.stereotype.Component;

import eu.dzhw.fdz.metadatamanagement.tasks.datasetreporttask.config.TaskProperties;
import eu.dzhw.fdz.metadatamanagement.tasks.datasetreporttask.mdm.MdmRestClient;

/**
 * This component attempts to notify the MDM user who initiated this task in case any error occurred
 * during the task execution.
 * 
 * @author René Reitmann
 */
@Component
public class TaskExceptionHandler {
  @Autowired
  private MdmRestClient mdmClient;

  @Autowired
  private TaskProperties taskProperties;

  @FailedTask
  public void handleError(TaskExecution taskExecution, Throwable throwable) {
    mdmClient.postTaskError(taskProperties.getDataSetId(), taskProperties.getOnBehalfOf(),
        throwable.getMessage());
  }
}
