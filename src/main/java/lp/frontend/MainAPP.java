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
import java.util.Objects;

public class MainAPP extends Application {
    //=======================ATTRIBUTES========================
    private final DialogService dialogService = DialogServiceImpl.getInstance();
    private final Manager manager = Manager.getInstance();

    private BorderPane mainPane;
    private FlowPane menuPane;
    private ScrollPane episodeScrollPane;
    private VBox episodePane;
    private TabPane areaPane;
    private FlowPane footerPane;
    private Button copyFilesButton;
    private TextArea textArea;
    //=======================ATTRIBUTES========================


    //=======================METHODS===========================
    public void start(Stage stage) {
        stage.setTitle(TextEnum.APPLICATION_TITLE.getText());
        mainPane = new BorderPane();
        Scene scene = new Scene(mainPane,
                Toolkit.getDefaultToolkit().getScreenSize().getWidth() / 2,
                Toolkit.getDefaultToolkit().getScreenSize().getHeight() / 2);
        setCssStyles(scene);
        stage.setScene(scene);
        StyleClasses.addStyle(mainPane, StyleClasses.PANE);
        setStageListeners(stage);
        stage.show();

        initMenu();
        initEpisodePane();
        initTabPane();
        initFooter();

        resize();
    }
    //=======================METHODS===========================


    //=======================RETURN METHODS====================
    //=======================RETURN METHODS====================


    //=======================PRIVATE METHODS===================
    private void setCssStyles(Scene scene) {
        String[] cssStyles = new String[]{
                Objects.requireNonNull(getClass().getResource(TextEnum.CSS_CHECK_BOXES.getText())).toExternalForm(),
                Objects.requireNonNull(getClass().getResource(TextEnum.CSS_PANE.getText())).toExternalForm(),
                Objects.requireNonNull(getClass().getResource(TextEnum.CSS_MENU.getText())).toExternalForm(),
                Objects.requireNonNull(getClass().getResource(TextEnum.CSS_TAB_PANE.getText())).toExternalForm(),
                Objects.requireNonNull(getClass().getResource(TextEnum.CSS_TEXT_AREA.getText())).toExternalForm()
        };
        scene.getStylesheets().addAll(cssStyles);
    }

    private void setStageListeners(Stage stage) {
        stage.widthProperty().addListener((obs, oldVal, newVal) -> resize());
        stage.heightProperty().addListener((obs, oldVal, newVal) -> resize());
        stage.maximizedProperty().addListener((obs, oldVal, newVal) -> Platform.runLater(this::resize));
        String[] sceneKeyEvents = new String[]{"", TextEnum.SUPER_SECRET_PASSWORD.getText()};
        stage.getScene().setOnKeyPressed(evt -> {
            sceneKeyEvents[0] += evt.getCode();
            if (sceneKeyEvents[0].contains(sceneKeyEvents[1])) {
                copyFilesButton.setDisable(!copyFilesButton.isDisabled());
                sceneKeyEvents[0] = "";
            } else if (sceneKeyEvents[0].length() > 100) {
                sceneKeyEvents[0] = "";
            }
        });
    }

    private void resize() {
        if (episodePane != null && areaPane != null) {
            episodePane.setPrefSize(
                    mainPane.getWidth() / 2,
                    mainPane.getHeight() - menuPane.getHeight() - footerPane.getHeight());
            episodePane.getChildren().forEach(item -> ((CheckBox) item).setPrefWidth(mainPane.getWidth() / 2 - 20));
            areaPane.setPrefSize(
                    mainPane.getWidth() / 2,
                    mainPane.getHeight() - menuPane.getHeight() - footerPane.getHeight());
        }
    }

    private void initMenu() {
        menuPane = new FlowPane();
        StyleClasses.addStyle(menuPane, StyleClasses.MENU);
        mainPane.setTop(menuPane);
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

    private void fillEpisodePane() {
        if (manager.getSelectedButton() == null) {
            return;
        }
        if (!manager.checkIfEpisodeCheckBoxContainsSelectedButton()) {
            EpisodeCheckBox episodeCheckBox = new EpisodeCheckBox();
            episodeCheckBox.createCheckBox(manager.getSelectedButton().getText(), manager.getInfoLabel());
            Episode episode = manager.getImportedEpisode().getSubEpisodes().get(manager.getSelectedButton().getText());
            episodeCheckBox = manager.mapEpisodeToEpisodeCheckBox(episode, episodeCheckBox);
            manager.getEpisodeCheckBoxToExport().getEpisodeCheckBoxes().put(
                    manager.getSelectedButton().getText(),
                    episodeCheckBox);
        }
        episodeScrollPane.setVvalue(manager.getSelectedEpisodeCheckBox().getScrollPanePosition());
        addNewEpisodePane();
        manager.getSelectedEpisodeCheckBox().getEpisodeCheckBoxes().values().forEach(this::addToEpisodePane);
        resize();
    }

    private void initEpisodePane() {
        episodeScrollPane = new ScrollPane();
        episodeScrollPane.setHbarPolicy(ScrollPane.ScrollBarPolicy.NEVER);
        mainPane.setLeft(new Pane(episodeScrollPane));
        addNewEpisodePane();
    }

    /**
     * It's needed to add new Pane everytime when category button is pressed because of scrollPane vertical
     * value. If there is still one Pane, it doesn't refresh it's size and count it incorrectly.
     */
    private void addNewEpisodePane() {
        episodePane = new VBox();
        episodeScrollPane.setContent(episodePane);
        StyleClasses.addStyle(episodePane, StyleClasses.EPISODE_PANE);
    }

    private void addToEpisodePane(EpisodeCheckBox episodeCheckBox) {
        episodePane.getChildren().add(episodeCheckBox.getCheckBox());
        if (!episodeCheckBox.getEpisodeCheckBoxes().isEmpty() && episodeCheckBox.getToggleButton().isSelected()) {
            episodeCheckBox.getEpisodeCheckBoxes().values().forEach(this::addToEpisodePane);
        }
        if (episodeCheckBox.getToggleButton() != null) {
            episodeCheckBox.getToggleButton().setOnAction(
                    action -> setToggleButtonListener(episodeCheckBox.getToggleButton()));
        }
    }

    private void setToggleButtonListener(ToggleButton toggleButton) {
        toggleButton.setText(TextEnum.CLOSED_SYMBOL_FOR_TOGGLE_BUTTON.getText());
        if (toggleButton.isSelected()) {
            toggleButton.setText(TextEnum.OPENED_SYMBOL_FOR_TOGGLE_BUTTON.getText());
        }
        fillEpisodePane();
    }

    private void initTabPane() {
        areaPane = new TabPane();
        mainPane.setCenter(areaPane);
        areaPane.getTabs().add(initNotePane());
    }

    private Tab initNotePane() {
        textArea = new TextArea();
        textArea.setWrapText(true);
        textArea.setText(manager.getNoteText());
        Tab tab = new Tab(TextEnum.NOTES_TITLE.getText());
        tab.setContent(textArea);
        tab.setClosable(false);
        return tab;
    }

    private void initFooter() {
        footerPane = new FlowPane();
        footerPane.setAlignment(Pos.CENTER);
        StyleClasses.addStyle(footerPane, StyleClasses.MENU);
        mainPane.setBottom(footerPane);

        Button resetButton = createResetButton();
        Button exportButton = createExportButton();
        Button copyButton = createCopyButton();
        footerPane.getChildren().addAll(resetButton, exportButton, copyButton);

        footerPane.getChildren().add(manager.getInfoLabel().getLabel());
        manager.getInfoLabel().updateCounter(manager.getSelectedCount());
    }

    private Button createResetButton() {
        Button resetButton = new Button(TextEnum.CLEAR_SELECTED_BUTTON_TEXT.getText());
        StyleClasses.addStyle(resetButton, StyleClasses.MENU_BUTTON);
        resetButton.setOnAction(evt -> {
            String answer = dialogService.useConfirmDialog(
                    TextEnum.RESET_SELECTED_TITLE.getText(),
                    TextEnum.RESET_SELECTED_QUESTION.getText());
            if (answer.equals(TextEnum.YES_TEXT.getText())) {
                manager.resetData();
                episodePane.getChildren().clear();
                fillEpisodePane();
            }
        });
        return resetButton;
    }

    private Button createExportButton() {
        Button exportButton = new Button(TextEnum.EXPORT_ITEMS.getText());
        StyleClasses.addStyle(exportButton, StyleClasses.MENU_BUTTON);
        exportButton.setOnAction(evt -> {
            manager.setNoteText(textArea.getText());
            manager.exportCurrentItemMap();
        });
        return exportButton;
    }

    /**
     * Copy button has global variable because it's used via disabling by key listener
     * in {@link #setStageListeners(Stage)}
     *
     * @return Button for copy selected files
     */
    private Button createCopyButton() {
        copyFilesButton = new Button(TextEnum.COPY_FILES.getText());
        StyleClasses.addStyle(copyFilesButton, StyleClasses.MENU_BUTTON);
        copyFilesButton.setDisable(true);
        copyFilesButton.setOnAction(evt -> {
            String path = dialogService.useTextInputDialog(
                    TextEnum.COPY_FILE_TEXT_INPUT_TITLE.getText(),
                    TextEnum.COPY_FILE_TEXT_INPUT_MESSAGE.getText(),
                    TextEnum.DIRECTORY_FOR_COPY_FILES.getText());
            manager.copyFilesTo(path);
        });
        return copyFilesButton;
    }
    //=======================PRIVATE METHODS===================
}