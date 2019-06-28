package eu.dzhw.fdz.metadatamanagement.tasks.datasetreporttask.config;

import javax.validation.constraints.NotEmpty;
import javax.validation.constraints.NotNull;

import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.core.io.FileSystemResource;
import org.springframework.validation.annotation.Validated;

import lombok.Getter;
import lombok.Setter;

/**
 * Configuration Properties for this task. Properties are configured in the
 * application-{profile}.yml file.
 *
 * @author René Reitmann
 */
@ConfigurationProperties(prefix = "task", ignoreUnknownFields = false)
@Getter
@Setter
@Validated
public class TaskProperties {
  /**
   * Id of the dataset for which this task needs to generate the report.
   */
  @NotEmpty
  private String dataSetId;

  /**
   * Name of the user who triggered the report.
   */
  @NotEmpty
  private String onBehalfOf;

  /**
   * Version of the data set report.
   */
  @NotEmpty
  private String version;

  /**
   * The input folder containing all files for the latex to pdf compilation.
   */
  @NotNull
  private FileSystemResource latexInputDir;
  
  /**
   * Folder in which we need to start the latex compilation process.
   */
  @NotNull
  private FileSystemResource latexProcessWorkingDir;
  
  /**
   * Command which needs to be executed in order to compile the latex report.
   */
  @NotEmpty
  private String latexProcessCommand;
  
  /**
   * Absolute name of the pdf report file which will be generated.
   */
  @NotNull
  private FileSystemResource pdfReport;
}
