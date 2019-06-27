package eu.dzhw.fdz.metadatamanagement.tasks.datasetreporttask.config;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.core.io.FileSystemResource;
import org.springframework.core.io.Resource;

import eu.dzhw.fdz.metadatamanagement.tasks.datasetreporttask.ZipUtils;

/**
 * Prepare the template folder for uploading by zipping it.
 * 
 * @author René Reitmann
 */
@Configuration
public class ZippedTemplateConfiguration {

  /**
   * Create the zip and put it into the application context.
   * 
   * @param mainTex The main tex freemarker template.
   * @param variablelistTex The variable list template.
   * @param variableTex The template for one variable.
   * 
   * @return the zipped template.
   * @throws IOException If file io fails.
   */
  @Bean
  public FileSystemResource zippedTemplate(@Value("classpath:template/Main.tex") Resource mainTex,
      @Value("classpath:template/Variablelist.tex") Resource variablelistTex,
      @Value("classpath:template/variables/Variable.tex") Resource variableTex) throws IOException {
    Path tmpPath = Files.createTempDirectory("template-zips");
    String absoluteFilename = tmpPath.toAbsolutePath() + "/template.zip";
    ZipUtils.zipResources("template", absoluteFilename, mainTex, variablelistTex, variableTex);

    return new FileSystemResource(absoluteFilename);
  }
}
