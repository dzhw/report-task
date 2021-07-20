package eu.dzhw.fdz.metadatamanagement.tasks.reporttask;

import java.io.File;

import org.apache.commons.io.FileUtils;
import org.springframework.boot.CommandLineRunner;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.boot.context.properties.EnableConfigurationProperties;
import org.springframework.boot.loader.tools.RunProcess;
import org.springframework.cloud.task.configuration.EnableTask;
import org.springframework.context.annotation.Bean;

import eu.dzhw.fdz.metadatamanagement.tasks.reporttask.config.MdmProperties;
import eu.dzhw.fdz.metadatamanagement.tasks.reporttask.config.TaskProperties;
import eu.dzhw.fdz.metadatamanagement.tasks.reporttask.mdm.MdmRestClient;
import eu.dzhw.fdz.metadatamanagement.tasks.reporttask.mdm.dto.Task.TaskType;
import lombok.extern.slf4j.Slf4j;

/**
 * This Spring Boot Task generates one report (of type {@link TaskType}) and uploads it to the MDM.
 *
 * @author René Reitmann
 */
@SpringBootApplication
@EnableConfigurationProperties({TaskProperties.class, MdmProperties.class})
@Slf4j
@EnableTask
public class ReportTaskApplication {

  public static void main(String[] args) {
    SpringApplication.run(ReportTaskApplication.class, args);
  }

  /**
   * Run the task for instance with 'java --jar report-task.jar --task.data-set-id=dat-cmp2014-ds1$
   * --task.language=de --task.on-behalf-of=rreitmann --task.version=1.0.0
   * --task.type=DATA_SET_REPORT'.
   *
   * @param mdmClient The fully configured {@link MdmRestClient}
   * @param taskProperties All properties for this task.
   * @return springs {@link CommandLineRunner}
   * @throws Exception if anything goes wrong.
   */
  @Bean
  public CommandLineRunner run(MdmRestClient mdmClient, TaskProperties taskProperties)
      throws Exception {
    log.info("Start report generation '{}' for id '{}' on behalf of '{}' in language '{}'.",
        taskProperties.getType(), taskProperties.getId(), taskProperties.getOnBehalfOf(),
        taskProperties.getLanguage());
    return args -> {
      byte[] filledTemplate = mdmClient.fillTemplate(taskProperties.getLanguage(),
          taskProperties.getId(), taskProperties.getVersion(), taskProperties.getType());
      File variablesDir = new File(taskProperties.getLatexInputDir().getFile(), "variables");
      if (variablesDir.exists()) {
        FileUtils.cleanDirectory(variablesDir);
      } else {
        if (variablesDir.mkdir()) {
          log.debug("Created variables directory.");
        }
      }
      ZipUtils.unzip(filledTemplate, taskProperties.getLatexInputDir().getFile());
      log.info("Successfully unzipped template to folder: "
          + taskProperties.getLatexInputDir().getFile().getAbsolutePath());
      RunProcess latexCompileProcess =
          new RunProcess(taskProperties.getLatexProcessWorkingDir().getFile(),
              taskProperties.getLatexProcessCommand());
      if (latexCompileProcess.run(true) == 0) {
        log.info(
            "Successfully created report/overview: " + taskProperties.getPdfReport().getPath());
        mdmClient.uploadReport(taskProperties.getLanguage(), taskProperties.getPdfReport(),
            taskProperties.getId(), taskProperties.getOnBehalfOf(), taskProperties.getType());
        boolean deleted = taskProperties.getPdfReport().getFile().delete();
        if (!deleted) {
          log.warn("Unable to delete file:" + taskProperties.getPdfReport().getPath());
        }
        log.info("Successfully uploaded '{}'-report/overview to the MDM.",
            taskProperties.getLanguage());
      } else {
        throw new RuntimeException(
            "Latex compilation failed: directory '" + taskProperties.getLatexProcessWorkingDir()
                + "', command '" + taskProperties.getLatexProcessCommand() + "'");
      }
    };
  }
}
