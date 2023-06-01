package lp;

import javafx.scene.control.Button;
import lombok.Data;
import lp.business.dto.Episode;
import lp.frontend.EpisodeCheckBox;
import lp.frontend.StartApp;
import lp.frontend.TextEnum;
import lp.service.DialogService;
import lp.service.FileService;
import lp.serviceimpl.DialogServiceImpl;
import lp.serviceimpl.FileServiceImpl;

@Data
public class Manager {

    //=======================SINGLETON=========================
    private static Manager manager = new Manager();

    public static Manager getInstance() {
        return manager;
    }

    private Manager() {

    }
    //=======================SINGLETON=========================

    private final FileService fileService = FileServiceImpl.getInstance();
    private final DialogService dialogService = DialogServiceImpl.getInstance();
    private EpisodeCheckBox preparedEpisodeCheckBoxToExport = new EpisodeCheckBox();

    private Episode importedEpisode;
    private Button selectedButton;

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

    public void setScrollPanePosition(double value) {
        getPreparedEpisodeCheckBoxToExport().getEpisodeCheckBoxes().get(getSelectedButton().getText()).setScrollPanePosition(value);
    }

    public boolean checkIfMapIsFilledWithSelectedButton() {
        return getPreparedEpisodeCheckBoxToExport().getEpisodeCheckBoxes().containsKey(getSelectedButton().getText());
    }

    public void exportCurrentItemMap() {
        try {
            Episode rootEpisode = new Episode(importedEpisode.getTitle());
            fileService.writeDataToJSON(TextEnum.EXPORT_FILE.getText(), mappingCurrentItemMapIntoEpisodeDTO(rootEpisode, preparedEpisodeCheckBoxToExport));
            dialogService.useInformationDialog(TextEnum.SUCCESS_TITLE.getText(), TextEnum.SUCCESS_EXPORT.getText());
        } catch (Exception exp) {
            dialogService.useErrorDialog(exp);
        }
    }

    private Episode mappingCurrentItemMapIntoEpisodeDTO(Episode episode, EpisodeCheckBox episodeCheckBox) {
        episodeCheckBox.getEpisodeCheckBoxes().forEach((checkBoxTitle, subEpisodeCheckBox) -> {
            Episode subEpisode = new Episode(checkBoxTitle);
            subEpisode.setSelected(subEpisodeCheckBox.isSelected());
            episode.getSubEpisodes().put(checkBoxTitle, mappingCurrentItemMapIntoEpisodeDTO(subEpisode, subEpisodeCheckBox));
        });
        return episode;
    }
}
