package lp.frontend;

import javafx.application.Application;
import javafx.application.Platform;
import javafx.geometry.Pos;
import javafx.scene.Scene;
import javafx.scene.control.Button;
import javafx.scene.control.CheckBox;
import javafx.scene.control.ScrollPane;
import javafx.scene.control.Tab;
import javafx.scene.control.TabPane;
import javafx.scene.control.TextArea;
import javafx.scene.control.ToggleButton;
import javafx.scene.layout.BorderPane;
import javafx.scene.layout.FlowPane;
import javafx.scene.layout.Pane;
import javafx.scene.layout.VBox;
import javafx.stage.Stage;
import lp.Manager;
import lp.business.dto.Episode;
import lp.service.DialogService;
import lp.serviceimpl.DialogServiceImpl;

import java.awt.Toolkit;

public class MainAPP extends Application {

    private final DialogService dialogService = DialogServiceImpl.getInstance();

    private final Manager manager = Manager.getInstance();

    private BorderPane mainPane;
    private FlowPane menuPane;
    private ScrollPane episodeScrollPane;
    private VBox episodePane;
    private TabPane areaPane;
    private FlowPane footerPane;
    private Button copyButton;
    private TextArea textArea;

    public void start(Stage stage) {
        mainPane = new BorderPane();
        StyleClasses.addStyle(mainPane, StyleClasses.PANE);
        Scene scene = new Scene(mainPane, Toolkit.getDefaultToolkit().getScreenSize().width / 2, Toolkit.getDefaultToolkit().getScreenSize().height / 2);
        stage.setScene(scene);
        scene.getStylesheets().add(getClass().getResource(TextEnum.RESOURCES.getText()).toExternalForm());
        stage.setTitle(TextEnum.APPLICATION_TITLE.getText());
        stage.show();

        menuPane = new FlowPane();
        StyleClasses.addStyle(menuPane, StyleClasses.MENU);
        mainPane.setTop(menuPane);
        setMenu();
        setEpisodePane();
        setTabPane();
        setFooter();
        setStageListeners(stage);
        resize();
    }

    private void setTabPane() {
        areaPane = new TabPane();
        mainPane.setRight(areaPane);
        setNotePane(areaPane);
    }

    private void setNotePane(TabPane areaPane) {
        Tab tab = new Tab(TextEnum.NOTES_TITLE.getText());
        textArea = new TextArea();
        textArea.setWrapText(true);
        tab.setContent(textArea);
        tab.setClosable(false);
        areaPane.getTabs().addAll(tab);
    }

    private void setMenu() {
        if (manager.getImportedEpisode() == null) {
            return;
        }
        manager.getImportedEpisode().getSubEpisodes().forEach((categoryTitle, categories) -> {
            Button menuButton = new Button(categoryTitle);
            StyleClasses.addStyle(menuButton, StyleClasses.MENU_BUTTON);
            menuPane.getChildren().add(menuButton);
            menuButton.setOnMouseClicked(evt -> {
                changeSelectedButton(menuButton);
                fillEpisodePane();
            });
        });
    }

    private void changeSelectedButton(Button clickedButton) {
        if (manager.getSelectedButton() != null) {
            manager.getSelectedButton().getStyleClass().remove(StyleClasses.SELECTED.getClassName());
            manager.setScrollPanePosition(episodeScrollPane.getVvalue());
        }
        if (clickedButton != null) {
            manager.setSelectedButton(clickedButton);
            StyleClasses.addStyle(manager.getSelectedButton(), StyleClasses.SELECTED);
        }
    }

    private void setEpisodePane() {
        episodePane = new VBox();
        episodePane.setStyle("-fx-background-color: #7e6969");
        episodeScrollPane = new ScrollPane(episodePane);
        episodeScrollPane.setHbarPolicy(ScrollPane.ScrollBarPolicy.NEVER);
        mainPane.setCenter(new Pane(episodeScrollPane));
    }

    private void setStageListeners(Stage stage) {
        stage.widthProperty().addListener((obs, oldVal, newVal) -> resize());
        stage.heightProperty().addListener((obs, oldVal, newVal) -> resize());
        stage.maximizedProperty().addListener((obs, oldVal, newVal) -> Platform.runLater(this::resize));
        String[] sceneKeyEvents = new String[]{new String(), TextEnum.SUPER_SECRET_PASSWORD.getText()};
        stage.getScene().setOnKeyPressed(evt -> {
            sceneKeyEvents[0] += evt.getCode();
            if (sceneKeyEvents[0].contains(sceneKeyEvents[1])) {
                copyButton.setDisable(!copyButton.isDisabled());
                sceneKeyEvents[0] = "";
            } else {
                copyButton.setText(TextEnum.COPY_FILES.getText());
                copyButton.setGraphic(null);
            }
            if (sceneKeyEvents[0].length() > 100) {
                sceneKeyEvents[0] = "";
            }
        });
    }

    private void resize() {
        if (episodePane != null && areaPane != null) {
            episodePane.setPrefSize(mainPane.getWidth() / 2, mainPane.getHeight() - menuPane.getHeight() - footerPane.getHeight());
            episodePane.getChildren().stream().forEach(item -> ((CheckBox) item).setPrefWidth(mainPane.getWidth() / 2 - 20));
            areaPane.setPrefSize(mainPane.getWidth() / 2, mainPane.getHeight() - menuPane.getHeight() - footerPane.getHeight());
        }
    }

    private void fillEpisodePane() {
        if (manager.getSelectedButton() == null) {
            return;
        }
        if (!manager.checkIfEpisodeCheckBoxContainsSelectedButton()) {
            EpisodeCheckBox episodeCheckBox = new EpisodeCheckBox();
            episodeCheckBox.createCheckBox(manager.getSelectedButton().getText());
            episodeCheckBox = mapEpisodeToEpisodeCheckBox(episodeCheckBox, manager.getImportedEpisode().getSubEpisodes().get(manager.getSelectedButton().getText()));
            manager.getPreparedEpisodeCheckBoxToExport().getEpisodeCheckBoxes().put(manager.getSelectedButton().getText(), episodeCheckBox);
        }
        episodePane = new VBox();
        episodePane.setStyle("-fx-background-color: #7e6969");
        episodePane.setMinWidth(mainPane.getWidth() / 2);
        episodeScrollPane.setContent(episodePane);

        manager.getPreparedEpisodeCheckBoxToExport().getEpisodeCheckBoxes().get(manager.getSelectedButton().getText()).getEpisodeCheckBoxes().values().forEach(episodeCheckBox -> {
            addToEpisodePane(episodePane, episodeCheckBox);
        });
        resize();
        episodeScrollPane.setVvalue(manager.getPreparedEpisodeCheckBoxToExport().getEpisodeCheckBoxes().get(manager.getSelectedButton().getText()).getScrollPanePosition());
    }

    private void addToEpisodePane(VBox episodePane, EpisodeCheckBox episodeCheckBox) {
        episodePane.getChildren().add(episodeCheckBox.getCheckBox());
        if (episodeCheckBox.isSelected()) {
            StyleClasses.addStyle(episodeCheckBox.getCheckBox(), StyleClasses.SELECTED);
        }
        if (!episodeCheckBox.getEpisodeCheckBoxes().isEmpty() && ((ToggleButton) episodeCheckBox.getCheckBox().getGraphic()).isSelected()) {
            episodeCheckBox.getEpisodeCheckBoxes().values().forEach(subEpisodeCheckBox -> addToEpisodePane(episodePane, subEpisodeCheckBox));
        }
    }

    private void setFooter() {
        footerPane = new FlowPane();
        footerPane.setAlignment(Pos.CENTER);
        StyleClasses.addStyle(footerPane, StyleClasses.MENU);
        mainPane.setBottom(footerPane);

        Button clearButton = new Button(TextEnum.CLEAR_SELECTED_BUTTON_TEXT.getText());
        clearButton.setOnAction(evt -> {
            String answer = dialogService.useConfirmDialog(TextEnum.RESET_SELECTED_TITLE.getText(), TextEnum.RESET_SELECTED_QUESTION.getText());
            if (answer.equals(TextEnum.YES_TEXT.getText()) && manager.getSelectedButton() != null) {
                manager.getSelectedButton().getStyleClass().remove(StyleClasses.SELECTED.getClassName());
                manager.setSelectedButton(null);
                manager.getPreparedEpisodeCheckBoxToExport().getEpisodeCheckBoxes().clear();
                episodePane.getChildren().clear();
            }
        });
        Button exportButton = new Button(TextEnum.EXPORT_ITEMS.getText());
        exportButton.setOnAction(evt -> {
            manager.setNoteText(textArea.getText());
            manager.exportCurrentItemMap();
        });
        copyButton = new Button(TextEnum.COPY_FILES.getText());
        copyButton.setDisable(true);
        copyButton.setOnAction(evt -> {
            String path = dialogService.useTextInputDialog(TextEnum.COPY_FILE_TEXT_INPUT_TITLE.getText(), TextEnum.COPY_FILE_TEXT_INPUT_MESSAGE.getText(), TextEnum.DIRECTORY_FOR_COPY_FILES.getText());
            manager.copyFilesTo(path);
        });
        footerPane.getChildren().addAll(clearButton, exportButton, copyButton);
        footerPane.getChildren().forEach(node -> StyleClasses.addStyle(node, StyleClasses.MENU_BUTTON));
    }

    private EpisodeCheckBox mapEpisodeToEpisodeCheckBox(EpisodeCheckBox episodeCheckBox, Episode episode) {
        episode.getSubEpisodes().forEach((subEpisodeTitle, subEpisode) -> {
            EpisodeCheckBox newEpisodeCheckBox = new EpisodeCheckBox();
            newEpisodeCheckBox.createCheckBox(subEpisode.getSubEpisodes().isEmpty() ? subEpisodeTitle.substring(0, subEpisodeTitle.length() - 4) : subEpisodeTitle);
            if (subEpisode.isSelected()) {
                newEpisodeCheckBox.setSelected(true);
                newEpisodeCheckBox.getCheckBox().setSelected(true);
            }
            newEpisodeCheckBox.getEpisodeParents().addAll(episodeCheckBox.getEpisodeParents());
            newEpisodeCheckBox.getEpisodeParents().add(episodeCheckBox);
            episodeCheckBox.getEpisodeCheckBoxes().put(subEpisodeTitle, newEpisodeCheckBox);
            if (!episode.getSubEpisodes().get(subEpisodeTitle).getSubEpisodes().isEmpty()) {
                ToggleButton toggleButton = new ToggleButton(TextEnum.CLOSED_SUB_LIST.getText());
                toggleButton.setOnAction(action -> {
                    if (toggleButton.isSelected()) {
                        toggleButton.setText(TextEnum.OPENED_SUB_LIST.getText());
                    } else {
                        toggleButton.setText(TextEnum.CLOSED_SUB_LIST.getText());
                    }
                    fillEpisodePane();
                });
                episodeCheckBox.getEpisodeCheckBoxes().get(subEpisodeTitle).addToggleButton(toggleButton);
                mapEpisodeToEpisodeCheckBox(episodeCheckBox.getEpisodeCheckBoxes().get(subEpisodeTitle), episode.getSubEpisodes().get(subEpisodeTitle));
            }
        });
        return episodeCheckBox;
    }
}