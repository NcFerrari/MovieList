package lp;

import lombok.Data;
import lp.business.dto.Episode;
import lp.frontend.StartApp;
import lp.frontend.TextEnum;
import lp.service.DialogService;
import lp.service.FileService;
import lp.serviceimpl.DialogServiceImpl;
import lp.serviceimpl.FileServiceImpl;

@Data
public class Manager {

    private static Manager manager = new Manager();

    public static Manager getInstance() {
        return manager;
    }

    private Manager() {

    }

    private final FileService fileService = FileServiceImpl.getInstance();
    private final DialogService dialogService = DialogServiceImpl.getInstance();

    private Episode importedEpisode;

    public void startApplication() {
        if (!fileService.getFile(TextEnum.IMPORT_FILE.getText()).exists()) {
            dialogService.initTextInputDialog(TextEnum.FILE_NOT_FOUND_TITLE.getText(), TextEnum.FILE_NOT_FOUND_MESSAGE.getText());
        } else {
            setImportedEpisode(fileService.loadJSON(TextEnum.IMPORT_FILE.getText(), Episode.class));
            javafx.application.Application.launch(StartApp.class);
        }
    }

    public static void main(String[] args) {
        getInstance().startApplication();
    }
}
