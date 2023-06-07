package lp;

import javafx.scene.control.Button;
import lombok.Data;
import lp.business.dto.Episode;
import lp.frontend.EpisodeCheckBox;
import lp.frontend.MainAPP;
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

    public static void main(String[] args) {
        getInstance().startApplication();
    }
    //=======================SINGLETON=========================


    //=======================ATTRIBUTES========================
    private final FileService fileService = FileServiceImpl.getInstance();
    private final DialogService dialogService = DialogServiceImpl.getInstance();
    private final EpisodeCheckBox preparedEpisodeCheckBoxToExport = new EpisodeCheckBox();

    private Episode importedEpisode;
    private Button selectedButton;
    private String noteText = "";
    private long countOfSelected;
    private long totalSize;
    private boolean loadedFromFile;
    //=======================ATTRIBUTES========================


    //=======================METHODS===========================
    public void startApplication() {
        if (!fileService.getFile(TextEnum.IMPORT_FILE.getText()).exists()) {
            dialogService.initTextInputDialog(
                    TextEnum.FILE_PATH_TITLE.getText(),
                    TextEnum.FILE_PATH_MESSAGE.getText());
        } else {
            setImportedEpisode(fileService.loadJSON(TextEnum.IMPORT_FILE.getText(), Episode.class));
            javafx.application.Application.launch(MainAPP.class);
        }
    }

    public void setScrollPanePosition(double value) {
        getPreparedEpisodeCheckBoxToExport()
                .getEpisodeCheckBoxes()
                .get(getSelectedButton().getText())
                .setScrollPanePosition(value);
    }

    public void exportCurrentItemMap() {
        try {
            String fileName = dialogService.useTextInputDialog(
                    TextEnum.EXPORT_FILE_TITLE.getText(),
                    TextEnum.EXPORT_FILE_MESSAGE.getText(),
                    TextEnum.EXPORT_FILE.getText());
            if (fileName == null) {
                return;
            }
            Episode rootEpisode = new Episode(importedEpisode.getTitle());
            Episode episode = mappingCurrentItemMapIntoEpisodeDTO(rootEpisode, preparedEpisodeCheckBoxToExport);
            fileService.writeDataToJSON(fileName + TextEnum.EXPORT_FILE_SUFFIX.getText(), episode);
            fileService.addToFile(fileName + TextEnum.EXPORT_FILE_SUFFIX.getText(), getNoteText());
            String message = TextEnum.SUCCESS_EXPORT_PREFIX.getText() +
                    fileName +
                    TextEnum.EXPORT_FILE_SUFFIX.getText() +
                    TextEnum.SUCCESS_EXPORT_SUFFIX.getText();
            dialogService.useInformationDialog(TextEnum.SUCCESS_TITLE.getText(), message);
        } catch (Exception exp) {
            dialogService.useErrorDialog(exp);
        }
    }

    public void copyFilesTo(String path) {
        if (path != null) {
            fileService.copyFilesTo(path, getImportedEpisode());
        }
    }
    //=======================METHODS===========================


    //=======================RETURN METHODS====================
    public boolean checkIfEpisodeCheckBoxContainsSelectedButton() {
        return getPreparedEpisodeCheckBoxToExport().getEpisodeCheckBoxes().containsKey(getSelectedButton().getText());
    }

    public long[] getSelectedCount() {
        countOfSelected = 0;
        totalSize = 0;
        count(getPreparedEpisodeCheckBoxToExport());
        return new long[]{countOfSelected, totalSize};
    }
    //=======================RETURN METHODS====================


    //=======================PRIVATE METHODS===================
    private Episode mappingCurrentItemMapIntoEpisodeDTO(Episode inputEpisode, EpisodeCheckBox episodeCheckBox) {
        episodeCheckBox.getEpisodeCheckBoxes().forEach((checkBoxTitle, subEpisodeCheckBox) -> {
            Episode episode = new Episode(checkBoxTitle);
            episode.setSelected(subEpisodeCheckBox.isSelected());
            episode.setSize(subEpisodeCheckBox.getSize());
            Episode subEpisode = mappingCurrentItemMapIntoEpisodeDTO(episode, subEpisodeCheckBox);
            inputEpisode.getSubEpisodes().put(checkBoxTitle, subEpisode);
        });
        return inputEpisode;
    }

    private void count(EpisodeCheckBox episodeCheckBox) {
        if (episodeCheckBox.getCheckBox() != null
                && episodeCheckBox.getCheckBox().isSelected()
                && episodeCheckBox.getEpisodeCheckBoxes().isEmpty()) {
            countOfSelected++;
            totalSize += episodeCheckBox.getSize();
        }
        for (EpisodeCheckBox subEpisodeCheckBox : episodeCheckBox.getEpisodeCheckBoxes().values()) {
            count(subEpisodeCheckBox);
        }
    }
    //=======================PRIVATE METHODS===================
}
