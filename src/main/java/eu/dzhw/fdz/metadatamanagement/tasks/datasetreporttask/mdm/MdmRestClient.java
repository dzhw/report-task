package eu.dzhw.fdz.metadatamanagement.tasks.datasetreporttask.mdm;

import java.net.URI;

import org.springframework.boot.web.client.RestTemplateBuilder;
import org.springframework.core.io.FileSystemResource;
import org.springframework.http.HttpEntity;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpMethod;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Component;
import org.springframework.util.LinkedMultiValueMap;
import org.springframework.util.MultiValueMap;
import org.springframework.web.client.RestTemplate;

import eu.dzhw.fdz.metadatamanagement.tasks.datasetreporttask.config.MdmProperties;
import eu.dzhw.fdz.metadatamanagement.tasks.datasetreporttask.mdm.dto.Task;
import eu.dzhw.fdz.metadatamanagement.tasks.datasetreporttask.mdm.dto.Task.TaskState;
import eu.dzhw.fdz.metadatamanagement.tasks.datasetreporttask.mdm.dto.Task.TaskType;
import eu.dzhw.fdz.metadatamanagement.tasks.datasetreporttask.mdm.dto.TaskErrorNotification;
import lombok.extern.slf4j.Slf4j;

/**
 * Component for interacting with the REST APi of the MDM.
 * 
 * @author René Reitmann
 */
@Component
@Slf4j
public class MdmRestClient {
  private final RestTemplate mdmTemplate;

  private final FileSystemResource zippedGermanTemplate;

  private final FileSystemResource zippedEnglishTemplate;

  /**
   * Create the {@link RestTemplate} from the {@link MdmProperties}.
   * 
   * @param mdmProperties Properties holding username and password of the mdm.
   * @param templateBuilder Springs {@link RestTemplateBuilder}.
   * @param zippedGermanTemplate The zipped german template folder.
   * @param zippedEnglishTemplate The zipped english template folder. 
   */
  public MdmRestClient(MdmProperties mdmProperties, RestTemplateBuilder templateBuilder,
      FileSystemResource zippedGermanTemplate, FileSystemResource zippedEnglishTemplate) {
    super();
    mdmTemplate = templateBuilder
        .basicAuthentication(mdmProperties.getUsername(), mdmProperties.getPassword())
        .rootUri(mdmProperties.getEndpoint()).build();
    this.zippedGermanTemplate = zippedGermanTemplate;
    this.zippedEnglishTemplate = zippedEnglishTemplate;
  }

  /**
   * Fill the given freemarker latex templates with the data from the mdm for the given dataset.
   * 
   * @param language The language of the template which needs to be uploaded.
   * @param dataSetId The id of the dataset for which the report will be generated.
   * @param version The version of the report.
   * @return A byte array containing a zip file with the latex files.
   * @throws InterruptedException We need to sleep until the mdm has finished processing the
   *         template.
   */
  public byte[] fillTemplate(String language, String dataSetId, String version)
      throws InterruptedException {
    HttpHeaders headers = new HttpHeaders();
    headers.setContentType(MediaType.MULTIPART_FORM_DATA);
    MultiValueMap<String, Object> body = new LinkedMultiValueMap<>();
    if (language.equals("de")) {
      body.add("file", zippedGermanTemplate);      
    } else if (language.equals("en")) {
      body.add("file", zippedEnglishTemplate);
    } else {
      throw new IllegalArgumentException("Unsupported language '" + language + "'!");
    }
    body.add("version", version);
    HttpEntity<MultiValueMap<String, Object>> requestEntity = new HttpEntity<>(body, headers);

    URI taskLocation = mdmTemplate.postForLocation(
        "/api/data-sets/{dataSetId}/report/fill-template", requestEntity, dataSetId);
    if (taskLocation != null) {
      log.debug("MDM started filling template under location: " + taskLocation);

      Task task = null;
      do {
        Thread.sleep(5000);
        task = mdmTemplate.getForObject(taskLocation.toString(), Task.class);
      } while (task != null && task.getState() == TaskState.RUNNING);

      if (task != null && task.getState() == TaskState.DONE) {
        log.debug("MDM finished filling template, result is available under location: "
            + task.getLocation());
        ResponseEntity<byte[]> filledTemplate =
            mdmTemplate.exchange(task.getLocation(), HttpMethod.GET, null, byte[].class);
        if (filledTemplate.getStatusCode() == HttpStatus.OK) {
          byte[] template = filledTemplate.getBody();
          if (template != null) {
            log.debug("MDM client has successfully downloaded filledTemplate (size {} KB).",
                template.length / 1024);
            return template;
          }
        } else {
          log.error("Error occurred during download: " + filledTemplate.getStatusCodeValue());
        }
      } else {
        log.error("MDM task errored: " + task != null ? task.toString() : " No Task found!");
      }
    }
    return new byte[0];
  }

  /**
   * Upload the compiled pdf report to the MDM.
   * 
   * @param language in which the report has been generated.
   * @param report The compiled report.
   * @param dataSetId The id of the dataSet for which the report has been generated.
   * @param onBehalfOf The name of the user who has started the report generation.
   */
  public void uploadReport(String language, FileSystemResource report, String dataSetId,
      String onBehalfOf) {
    HttpHeaders headers = new HttpHeaders();
    headers.setContentType(MediaType.MULTIPART_FORM_DATA);
    MultiValueMap<String, Object> body = new LinkedMultiValueMap<>();
    body.add("file", report);
    body.add("onBehalfOf", onBehalfOf);
    HttpEntity<MultiValueMap<String, Object>> requestEntity = new HttpEntity<>(body, headers);

    log.debug("MDM report upload starting for dataSetId: " + dataSetId);

    ResponseEntity<String> response =
        mdmTemplate.postForEntity("/api/data-sets/{dataSetId}/report/{language}", requestEntity,
            String.class, dataSetId, language);
    if (response.getStatusCode() != HttpStatus.OK) {
      log.error("MDM report upload failed with status {}: {}", response.getStatusCode(),
          response.getBody());
    }
  }

  /**
   * Send a notification to the user if any error occurred during task execution.
   * 
   * @param dataSetId The id of the dataSet for which the report should have been generated.
   * @param onBehalfOf The name of the user who has started the report generation.
   * @param errorMessage A message indicating the error.
   */
  public void postTaskError(String dataSetId, String onBehalfOf, String errorMessage) {
    log.error("Sending error during report generation for '{}' on behalf of '{}' to MDM: {}",
        dataSetId, onBehalfOf, errorMessage);
    TaskErrorNotification errorNotification =
        TaskErrorNotification.builder().domainObjectId(dataSetId).onBehalfOf(onBehalfOf)
            .errorMessage(errorMessage).taskType(TaskType.DATA_SET_REPORT).build();
    ResponseEntity<String> response =
        mdmTemplate.postForEntity("/api/tasks/error-notification", errorNotification, String.class);
    if (response.getStatusCode() != HttpStatus.OK) {
      log.error("MDM error notification failed with status {}: {}", response.getStatusCode(),
          response.getBody());
    }
  }

}
