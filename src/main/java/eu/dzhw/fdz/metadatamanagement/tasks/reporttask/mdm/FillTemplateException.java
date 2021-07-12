package eu.dzhw.fdz.metadatamanagement.tasks.reporttask.mdm;

import eu.dzhw.fdz.metadatamanagement.tasks.reporttask.mdm.dto.ErrorListDto;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.ToString;

/**
 * Exception which is thrown when the mdm task, which fills the latex template, returns an error.
 * 
 * @author René Reitmann
 */
@AllArgsConstructor
@Getter
@ToString
public class FillTemplateException extends Exception {

  private static final long serialVersionUID = 5248783072597192541L;

  private ErrorListDto errorListDto;
}
