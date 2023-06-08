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

import java.util.ArrayList;
import java.util.List;

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
            Episode episode = mapEpisodeCheckBoxToEpisode(preparedEpisodeCheckBoxToExport, rootEpisode);
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

    public void loadData() {
        getImportedEpisode().getSubEpisodes().forEach((categories, episode) -> {
            EpisodeCheckBox episodeCheckBox = new EpisodeCheckBox();
            episodeCheckBox.createCheckBox(categories);
            getPreparedEpisodeCheckBoxToExport().getEpisodeCheckBoxes().put(categories,
                    mapEpisodeToEpisodeCheckBox(episode, episodeCheckBox));
        });
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
    private Episode mapEpisodeCheckBoxToEpisode(EpisodeCheckBox episodeCheckBox, Episode inputEpisode) {
        episodeCheckBox.getEpisodeCheckBoxes().forEach((checkBoxTitle, subEpisodeCheckBox) -> {
            Episode episode = new Episode(checkBoxTitle);
            episode.setSelected(subEpisodeCheckBox.getCheckBox().isSelected());
            episode.setSize(subEpisodeCheckBox.getSize());
            Episode subEpisode = mapEpisodeCheckBoxToEpisode(subEpisodeCheckBox, episode);
            inputEpisode.getSubEpisodes().put(checkBoxTitle, subEpisode);
        });
        return inputEpisode;
    }

    private EpisodeCheckBox mapEpisodeToEpisodeCheckBox(Episode episode, EpisodeCheckBox episodeCheckBox) {
        episode.getSubEpisodes().forEach((subEpisodeTitle, subEpisode) -> {
            EpisodeCheckBox newEpisodeCheckBox = new EpisodeCheckBox();
            newEpisodeCheckBox.createCheckBox(getCheckBoxName(subEpisode, subEpisodeTitle));
            newEpisodeCheckBox.getCheckBox().setSelected(subEpisode.isSelected());
            newEpisodeCheckBox.setSize(subEpisode.getSize());
            newEpisodeCheckBox.getEpisodeParents().addAll(getAllParents(episodeCheckBox));
            episodeCheckBox.getEpisodeCheckBoxes().put(subEpisodeTitle, newEpisodeCheckBox);
            if (!episode.getSubEpisodes().get(subEpisodeTitle).getSubEpisodes().isEmpty()) {
                episodeCheckBox.getEpisodeCheckBoxes().get(subEpisodeTitle).addToggleButton();
                mapEpisodeToEpisodeCheckBox(subEpisode, newEpisodeCheckBox);
            }
        });
        return episodeCheckBox;
    }

    private List<EpisodeCheckBox> getAllParents(EpisodeCheckBox episodeCheckBox) {
        List<EpisodeCheckBox> resultList = new ArrayList<>(episodeCheckBox.getEpisodeParents());
        resultList.add(episodeCheckBox);
        return resultList;
    }

    private String getCheckBoxName(Episode subEpisode, String subEpisodeTitle) {
        if (subEpisode.getSubEpisodes().isEmpty()) {
            subEpisodeTitle = subEpisodeTitle.substring(0, subEpisodeTitle.length() - 4);
        }
        return subEpisodeTitle;
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
