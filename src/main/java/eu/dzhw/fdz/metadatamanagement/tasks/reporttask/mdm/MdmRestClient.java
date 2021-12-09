package eu.dzhw.fdz.metadatamanagement.tasks.reporttask.mdm;

import java.net.URI;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.boot.web.client.RestTemplateBuilder;
import org.springframework.core.io.FileSystemResource;
import org.springframework.http.HttpEntity;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpMethod;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.security.oauth2.client.AuthorizedClientServiceOAuth2AuthorizedClientManager;
import org.springframework.security.oauth2.client.ClientAuthorizationException;
import org.springframework.security.oauth2.client.OAuth2AuthorizeRequest;
import org.springframework.security.oauth2.core.OAuth2AuthorizationException;
import org.springframework.stereotype.Component;
import org.springframework.util.LinkedMultiValueMap;
import org.springframework.util.MultiValueMap;
import org.springframework.web.client.HttpClientErrorException;
import org.springframework.web.client.RestTemplate;

import eu.dzhw.fdz.metadatamanagement.tasks.reporttask.config.MdmProperties;
import eu.dzhw.fdz.metadatamanagement.tasks.reporttask.mdm.dto.Task;
import eu.dzhw.fdz.metadatamanagement.tasks.reporttask.mdm.dto.TaskErrorNotification;
import eu.dzhw.fdz.metadatamanagement.tasks.reporttask.mdm.dto.Task.TaskState;
import eu.dzhw.fdz.metadatamanagement.tasks.reporttask.mdm.dto.Task.TaskType;
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

  private final FileSystemResource zippedGermanTemplateDataSetReport;

  private final FileSystemResource zippedEnglishTemplateDataSetReport;

  private final FileSystemResource zippedGermanTemplateDataPackageOverview;

  private final FileSystemResource zippedEnglishTemplateDataPackageOverview;

  private AuthorizedClientServiceOAuth2AuthorizedClientManager authorizedClientManager;

  /**
   * Create the {@link RestTemplate} from the {@link MdmProperties}.
   * 
   * @param mdmProperties Properties holding username and password of the mdm.
   * @param templateBuilder Springs {@link RestTemplateBuilder}.
   * @param zippedGermanTemplateDataSetReport The zipped german data set report template folder.
   * @param zippedEnglishTemplateDataSetReport The zipped english data set report template folder.
   */
  public MdmRestClient(
      @Value("${spring.security.oauth2.client.registration.fdz.client-id}") final String clientId,
      AuthorizedClientServiceOAuth2AuthorizedClientManager authorizedClientManager,
      MdmProperties mdmProperties,
      RestTemplateBuilder templateBuilder,
      FileSystemResource zippedGermanTemplateDataSetReport,
      FileSystemResource zippedEnglishTemplateDataSetReport,
      FileSystemResource zippedGermanTemplateDataPackageOverview,
      FileSystemResource zippedEnglishTemplateDataPackageOverview
  ) {
    super();
    this.authorizedClientManager = authorizedClientManager;
    mdmTemplate = templateBuilder
        .defaultHeader("Authorization", generateAuthorizationHeaderValue(clientId))
        .rootUri(mdmProperties.getEndpoint()).build();
    this.zippedGermanTemplateDataSetReport = zippedGermanTemplateDataSetReport;
    this.zippedEnglishTemplateDataSetReport = zippedEnglishTemplateDataSetReport;
    this.zippedGermanTemplateDataPackageOverview = zippedGermanTemplateDataPackageOverview;
    this.zippedEnglishTemplateDataPackageOverview = zippedEnglishTemplateDataPackageOverview;
  }

  /**
   * Fill the given freemarker latex templates with the data from the mdm for the given dataset or
   * data package.
   * 
   * @param language The language of the template which needs to be uploaded.
   * @param id The id of the dataset or data package for which the report/overview will be
   *        generated.
   * @param version The version of the report.
   * @param type the type of report to be filled in
   * @return A byte array containing a zip file with the latex files.
   * @throws InterruptedException We need to sleep until the mdm has finished processing the
   *         template.
   * @throws FillTemplateException In case the mdm task returns an error.
   */
  public byte[] fillTemplate(String language, String id, String version, TaskType type)
      throws InterruptedException, FillTemplateException {
    HttpHeaders headers = new HttpHeaders();
    headers.setContentType(MediaType.MULTIPART_FORM_DATA);
    MultiValueMap<String, Object> body = new LinkedMultiValueMap<>();
    body.add("version", version);
    HttpEntity<MultiValueMap<String, Object>> requestEntity = new HttpEntity<>(body, headers);
    URI taskLocation = null;
    switch (type) {
      case DATA_SET_REPORT:
        if (language.equals("de")) {
          body.add("file", zippedGermanTemplateDataSetReport);
        } else if (language.equals("en")) {
          body.add("file", zippedEnglishTemplateDataSetReport);
        } else {
          throw new IllegalArgumentException("Unsupported language '" + language + "'!");
        }
        taskLocation = mdmTemplate.postForLocation("/api/data-sets/{id}/report/fill-template",
            requestEntity, id);
        break;
      case DATA_PACKAGE_OVERVIEW:
        if (language.equals("de")) {
          body.add("file", zippedGermanTemplateDataPackageOverview);
        } else if (language.equals("en")) {
          body.add("file", zippedEnglishTemplateDataPackageOverview);
        } else {
          throw new IllegalArgumentException("Unsupported language '" + language + "'!");
        }
        taskLocation = mdmTemplate.postForLocation("/api/data-packages/{id}/overview/fill-template",
            requestEntity, id);
        break;
      default:
        throw new IllegalArgumentException("Unsupported type '" + type + "'!");
    }

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
        if (task != null && task.getErrorList() != null
            && task.getErrorList().getErrors().size() > 0) {
          throw new FillTemplateException(task.getErrorList());
        } else {
          log.error("MDM task errored: " + (task != null ? task.toString() : " No Task found!"));
        }
      }
    }
    return new byte[0];
  }

  /**
   * Upload the compiled pdf report/overview to the MDM.
   * 
   * @param language in which the report/overview has been generated.
   * @param report The compiled report.
   * @param id The id of the dataSet for which the report has been generated.
   * @param onBehalfOf The name of the user who has started the report generation.
   */
  public void uploadReport(String language, FileSystemResource report, String id, String onBehalfOf,
      TaskType type) {
    HttpHeaders headers = new HttpHeaders();
    headers.setContentType(MediaType.MULTIPART_FORM_DATA);
    MultiValueMap<String, Object> body = new LinkedMultiValueMap<>();
    body.add("file", report);
    body.add("onBehalfOf", onBehalfOf);
    HttpEntity<MultiValueMap<String, Object>> requestEntity = new HttpEntity<>(body, headers);

    log.debug("MDM report upload starting for id: " + id);

    ResponseEntity<String> response = null;
    switch (type) {
      case DATA_SET_REPORT:
        response = mdmTemplate.postForEntity("/api/data-sets/{id}/report/{language}", requestEntity,
            String.class, id, language);
        break;
      case DATA_PACKAGE_OVERVIEW:
        response = mdmTemplate.postForEntity("/api/data-packages/{id}/overview/{language}",
            requestEntity, String.class, id, language);
        break;
      default:
        throw new IllegalArgumentException("Unsupported type '" + type + "'!");
    }
    if (response.getStatusCode() != HttpStatus.OK) {
      log.error("MDM report upload failed with status {}: {}", response.getStatusCode(),
          response.getBody());
    }
  }

  /**
   * Send a notification to the user if any error occurred during task execution.
   * 
   * @param id The id of the dataSet or data package for which the report should have been
   *        generated.
   * @param onBehalfOf The name of the user who has started the report generation.
   * @param errorMessage A message indicating the error.
   * @param taskType one of {@link TaskType}
   */
  public void postTaskError(String id, String onBehalfOf, String errorMessage, TaskType taskType) {
    log.error("Sending error during report generation for '{}' on behalf of '{}' to MDM: {}", id,
        onBehalfOf, errorMessage);
    TaskErrorNotification errorNotification = TaskErrorNotification.builder().domainObjectId(id)
        .onBehalfOf(onBehalfOf).errorMessage(errorMessage).taskType(taskType).build();
    ResponseEntity<String> response =
        mdmTemplate.postForEntity("/api/tasks/error-notification", errorNotification, String.class);
    if (response.getStatusCode() != HttpStatus.OK) {
      log.error("MDM error notification failed with status {}: {}", response.getStatusCode(),
          response.getBody());
    }
  }

  private String generateAuthorizationHeaderValue(final String principal) {
    var request = OAuth2AuthorizeRequest
            .withClientRegistrationId("fdz")
            .principal(principal)
            .build();

    var client = this.authorizedClientManager
            .authorize(request);
    if (client == null) {
      throw new IllegalStateException("Could not obtain OAuth2 client");
    }

    var token = client.getAccessToken();

    return String.format(
            "Bearer %s",
            token.getTokenValue()
    );
  }
}
