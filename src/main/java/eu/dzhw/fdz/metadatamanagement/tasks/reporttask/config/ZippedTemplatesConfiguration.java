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
   * Create the german zipped template of the dataset report and put it into the application
   * context.
   * 
   * @param mainTex The main tex freemarker template.
   * @param variablelistTex The variable list template.
   * @param variableTex The template for one variable.
   * 
   * @return the zipped template.
   * @throws IOException If file io fails.
   */
  @Bean
  public FileSystemResource zippedGermanTemplateDataSetReport(
      @Value("classpath:templates/dataset-report/de/Main.tex") Resource mainTex,
      @Value("classpath:templates/dataset-report/de/Variablelist.tex") Resource variablelistTex,
      @Value("classpath:templates/dataset-report/de/variables/Variable.tex") Resource variableTex)
      throws IOException {
    Path tmpPath = Files.createTempDirectory("template-zips");
    String absoluteFilename = tmpPath.toAbsolutePath() + "/template_dataset_report_de.zip";
    ZipUtils.zipResources("de", absoluteFilename, mainTex, variablelistTex, variableTex);

    return new FileSystemResource(absoluteFilename);
  }

  /**
   * Create the english zipped template of the dataset report and put it into the application
   * context.
   * 
   * @param mainTex The main tex freemarker template.
   * @param variablelistTex The variable list template.
   * @param variableTex The template for one variable.
   * 
   * @return the zipped template.
   * @throws IOException If file io fails.
   */
  @Bean
  public FileSystemResource zippedEnglishTemplateDataSetReport(
      @Value("classpath:templates/dataset-report/en/Main.tex") Resource mainTex,
      @Value("classpath:templates/dataset-report/en/Variablelist.tex") Resource variablelistTex,
      @Value("classpath:templates/dataset-report/en/variables/Variable.tex") Resource variableTex)
      throws IOException {
    Path tmpPath = Files.createTempDirectory("template-zips");
    String absoluteFilename = tmpPath.toAbsolutePath() + "/template_dataset_report_en.zip";
    ZipUtils.zipResources("en", absoluteFilename, mainTex, variablelistTex, variableTex);

    return new FileSystemResource(absoluteFilename);
  }

  /**
   * Create the german zipped template of the data package overview and put it into the application
   * context.
   * 
   * @param mainTex The main tex freemarker template.
   * 
   * @return the zipped template.
   * @throws IOException If file io fails.
   */
  @Bean
  public FileSystemResource zippedGermanTemplateDataPackageOverview(
      @Value("classpath:templates/datapackage-overview/de/Main.tex") Resource mainTex)
      throws IOException {
    Path tmpPath = Files.createTempDirectory("template-zips");
    String absoluteFilename = tmpPath.toAbsolutePath() + "/template_datapackage_overview_de.zip";
    ZipUtils.zipResources("de", absoluteFilename, mainTex);

    return new FileSystemResource(absoluteFilename);
  }
  
  /**
   * Create the english zipped template of the data package overview and put it into the application
   * context.
   * 
   * @param mainTex The main tex freemarker template.
   * 
   * @return the zipped template.
   * @throws IOException If file io fails.
   */
  @Bean
  public FileSystemResource zippedEnglishTemplateDataPackageOverview(
      @Value("classpath:templates/datapackage-overview/en/Main.tex") Resource mainTex)
      throws IOException {
    Path tmpPath = Files.createTempDirectory("template-zips");
    String absoluteFilename = tmpPath.toAbsolutePath() + "/template_datapackage_overview_en.zip";
    ZipUtils.zipResources("en", absoluteFilename, mainTex);

    return new FileSystemResource(absoluteFilename);
  }
}
