package lp;

import lp.frontend.StartApp;
import lp.frontend.TextEnum;
import lp.service.DialogService;
import lp.service.FileService;
import lp.serviceimpl.DialogServiceImpl;
import lp.serviceimpl.FileServiceImpl;

import java.util.Map;

public class Manager {

    private final FileService fileService = FileServiceImpl.getInstance();
    private final DialogService dialogService = DialogServiceImpl.getInstance();

    public Manager() {
        if (!fileService.getFile(TextEnum.IMPORT_FILE.getText()).exists()) {
            dialogService.useTextInputDialog(TextEnum.FILE_NOT_FOUND_TITLE.getText(), TextEnum.FILE_NOT_FOUND_MESSAGE.getText());
        } else {
            Map<String, Map> episode = fileService.loadJSON(TextEnum.IMPORT_FILE.getText(), Map.class);
            StartApp.setImportedEpisode(episode);
            javafx.application.Application.launch(StartApp.class);
        }
    }

    public static void main(String[] args) {
        new Manager();
    }
}
