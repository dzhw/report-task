package eu.dzhw.fdz.metadatamanagement.tasks.reporttask.config;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.core.io.FileSystemResource;
import org.springframework.core.io.Resource;

import eu.dzhw.fdz.metadatamanagement.tasks.reporttask.ZipUtils;

/**
 * Prepare the template folder for uploading by zipping it.
 * 
 * @author René Reitmann
 */
@Configuration
public class ZippedTemplatesConfiguration {

  /**
   * Create the german zip and put it into the application context.
   * 
   * @param mainTex The main tex freemarker template.
   * @param variablelistTex The variable list template.
   * @param variableTex The template for one variable.
   * 
   * @return the zipped template.
   * @throws IOException If file io fails.
   */
  @Bean
  public FileSystemResource zippedGermanTemplate(
      @Value("classpath:template/de/Main.tex") Resource mainTex,
      @Value("classpath:template/de/Variablelist.tex") Resource variablelistTex,
      @Value("classpath:template/de/variables/Variable.tex") Resource variableTex)
      throws IOException {
    Path tmpPath = Files.createTempDirectory("template-zips");
    String absoluteFilename = tmpPath.toAbsolutePath() + "/template_de.zip";
    ZipUtils.zipResources("de", absoluteFilename, mainTex, variablelistTex, variableTex);

    return new FileSystemResource(absoluteFilename);
  }

  /**
   * Create the english zip and put it into the application context.
   * 
   * @param mainTex The main tex freemarker template.
   * @param variablelistTex The variable list template.
   * @param variableTex The template for one variable.
   * 
   * @return the zipped template.
   * @throws IOException If file io fails.
   */
  @Bean
  public FileSystemResource zippedEnglishTemplate(
      @Value("classpath:template/en/Main.tex") Resource mainTex,
      @Value("classpath:template/en/Variablelist.tex") Resource variablelistTex,
      @Value("classpath:template/en/variables/Variable.tex") Resource variableTex)
      throws IOException {
    Path tmpPath = Files.createTempDirectory("template-zips");
    String absoluteFilename = tmpPath.toAbsolutePath() + "/template_en.zip";
    ZipUtils.zipResources("en", absoluteFilename, mainTex, variablelistTex, variableTex);

    return new FileSystemResource(absoluteFilename);
  }
}
