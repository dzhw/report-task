package eu.dzhw.fdz.metadatamanagement.tasks.reporttask.config;

import javax.validation.constraints.NotEmpty;

import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.validation.annotation.Validated;

import lombok.Getter;
import lombok.Setter;

/**
 * Properties for interacting with the MDM API.
 * 
 * @author René Reitmann
 */
@ConfigurationProperties(prefix = "mdm", ignoreUnknownFields = false)
@Getter
@Setter
@Validated
public class MdmProperties {
  /**
   * The API endpoint of the mdm. E.g. "http://localhost:8080".
   */
  @NotEmpty
  private String endpoint;
}
