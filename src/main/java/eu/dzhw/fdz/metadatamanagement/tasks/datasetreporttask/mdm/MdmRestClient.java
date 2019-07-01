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
import lombok.extern.slf4j.Slf4j;

/**
 * Component for interacting with the REST APi of the MDM.
 * 
 * @author René Reitmann
 */
@Component
@Slf4j
public class MdmRestClient {
  private RestTemplate mdmTemplate;

  /**
   * Create the {@link RestTemplate} from the {@link MdmProperties}.
   * 
   * @param mdmProperties Properties holding username and password of the mdm.
   * @param templateBuilder Springs {@link RestTemplateBuilder}.
   */
  public MdmRestClient(MdmProperties mdmProperties, RestTemplateBuilder templateBuilder) {
    super();
    mdmTemplate = templateBuilder
        .basicAuthentication(mdmProperties.getUsername(), mdmProperties.getPassword())
        .rootUri(mdmProperties.getEndpoint()).build();
  }

  /**
   * Fill the given freemarker latex templates with the data from the mdm for the given dataset.
   * 
   * @param zippedTemplate The zip file containing freemarker latex templates.
   * @param dataSetId The id of the dataset for which the report will be generated.
   * @param version The version of the report.
   * @return A byte array containing a zip file with the latex files.
   * @throws InterruptedException We need to sleep until the mdm has finished processing the
   *         template.
   */
  public byte[] fillTemplate(FileSystemResource zippedTemplate, String dataSetId, String version)
      throws InterruptedException {
    HttpHeaders headers = new HttpHeaders();
    headers.setContentType(MediaType.MULTIPART_FORM_DATA);
    MultiValueMap<String, Object> body = new LinkedMultiValueMap<>();
    body.add("file", zippedTemplate);
    body.add("version", version);
    HttpEntity<MultiValueMap<String, Object>> requestEntity = new HttpEntity<>(body, headers);

    URI taskLocation = mdmTemplate.postForLocation("/api/data-sets/{dataSetId}/fill-template",
        requestEntity, dataSetId);
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
   * @param report The compiled report.
   * @param dataSetId The id of the dataSet for which the report has been generated.
   * @param onBehalfOf The name of the user who has started the report generation.
   */
  public void uploadReport(FileSystemResource report, String dataSetId, String onBehalfOf) {
    HttpHeaders headers = new HttpHeaders();
    headers.setContentType(MediaType.MULTIPART_FORM_DATA);
    MultiValueMap<String, Object> body = new LinkedMultiValueMap<>();
    body.add("file", report);
    body.add("onBehalfOf", onBehalfOf);
    HttpEntity<MultiValueMap<String, Object>> requestEntity = new HttpEntity<>(body, headers);

    log.debug("MDM report upload starting for dataSetId: " + dataSetId);

    ResponseEntity<String> response = mdmTemplate.postForEntity("/api/data-sets/{dataSetId}/report",
        requestEntity, String.class, dataSetId);
    if (response.getStatusCode() != HttpStatus.OK) {
      log.error("MDM report upload failed with status {}: {}", response.getStatusCode(),
          response.getBody());
    }
  }

  public void postTaskError(String dataSetId, String onBehalfOf, String errorMessage) {
    log.debug("Sending error during report generation for '{}' on behalf of '{}' to MDM: {}",
        dataSetId, onBehalfOf, errorMessage);
  }

}
