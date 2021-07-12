package eu.dzhw.fdz.metadatamanagement.tasks.reporttask;

import java.io.ByteArrayInputStream;
import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.util.zip.ZipEntry;
import java.util.zip.ZipInputStream;
import java.util.zip.ZipOutputStream;

import org.springframework.core.io.Resource;
import org.springframework.util.StringUtils;

import edu.umd.cs.findbugs.annotations.SuppressFBWarnings;
import lombok.extern.slf4j.Slf4j;

/**
 * Utilities for zipping and unzipping folders.
 * 
 * @author René Reitmann
 */
@Slf4j
public class ZipUtils {
  /**
   * Unzip the given zipFile to the given destination.
   * 
   * @param zipFile The zipFile as byte-Array.
   * @param destinationDir The directory to which the zip will be extracted. It will be cleaned
   *        before unzipping.
   * @throws IOException When file io fails.
   */
  public static void unzip(byte[] zipFile, File destinationDir) throws IOException {
    try (InputStream inputStream = new ByteArrayInputStream(zipFile);
        ZipInputStream zip = new ZipInputStream(inputStream);) {
      ZipEntry zipEntry = zip.getNextEntry();
      byte[] buffer = new byte[1024];
      while (zipEntry != null) {
        if (zipEntry.getName().endsWith("/")) {
          if (new File(destinationDir, zipEntry.getName()).mkdir()) {
            zipEntry = zip.getNextEntry();
            continue;
          } else {
            throw new IOException("Directory of zip could not be created: " + zipEntry.getName());
          }
        }
        File newFile = createFile(destinationDir, zipEntry);
        try (FileOutputStream fos = new FileOutputStream(newFile)) {
          int len;
          while ((len = zip.read(buffer)) > 0) {
            fos.write(buffer, 0, len);
          }
        }
        zipEntry = zip.getNextEntry();
      }
    }
  }

  private static File createFile(File destinationDir, ZipEntry zipEntry) throws IOException {
    File destFile = new File(destinationDir, zipEntry.getName());

    String destDirPath = destinationDir.getCanonicalPath();
    String destFilePath = destFile.getCanonicalPath();

    if (!destFilePath.startsWith(destDirPath + File.separator)) {
      throw new IOException("Entry is outside of the target dir: " + zipEntry.getName());
    }

    return destFile;
  }

  @SuppressFBWarnings("RCN_REDUNDANT_NULLCHECK_OF_NONNULL_VALUE")
  private static void zipResource(Resource resourceToZip, String basePath, ZipOutputStream zipOut)
      throws IOException {
    log.debug("Add classpath resource to zip: " + resourceToZip.toString());
    String filename = resourceToZip.getURI().toString().replaceAll("^.*" + basePath + "\\/", "");
    try (InputStream is = resourceToZip.getInputStream()) {
      byte[] bytes = new byte[1024];
      int length = is.read(bytes);
      ZipEntry zipEntry = new ZipEntry(filename);
      zipOut.putNextEntry(zipEntry);
      while (length >= 0) {
        zipOut.write(bytes, 0, length);
        length = is.read(bytes);
      }
    } catch (IOException exception) {
      // we might have tried to read a directory
      if (!StringUtils.isEmpty(filename)) {
        if (filename.endsWith("/")) {
          zipOut.putNextEntry(new ZipEntry(filename));
          zipOut.closeEntry();
        } else {
          zipOut.putNextEntry(new ZipEntry(filename + "/"));
          zipOut.closeEntry();
        }
      }
    }
  }

  /**
   * Zip the given folder and write it to the destination.
   * 
   * @param resources The resources in the classpath which need to be zipped.
   * @param directoryName Name of the directory under which the resources reside.
   * @param destination The destination in the file system to which the zip will be written.
   * @throws IOException if file io fails.
   */
  public static void zipResources(String directoryName, String destination, Resource... resources)
      throws IOException {
    try (FileOutputStream fos = new FileOutputStream(destination);
        ZipOutputStream zipOut = new ZipOutputStream(fos)) {
      for (Resource resource : resources) {
        zipResource(resource, directoryName, zipOut);
      }
    }
  }
}
