package lp.frontend;

import javafx.application.Application;
import javafx.application.Platform;
import javafx.geometry.Pos;
import javafx.scene.Scene;
import javafx.scene.control.Button;
import javafx.scene.control.CheckBox;
import javafx.scene.control.ScrollPane;
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

public class StartApp extends Application {

    private final DialogService dialogService = DialogServiceImpl.getInstance();

    private final Manager manager = Manager.getInstance();

    private BorderPane mainPane;
    private FlowPane menuPane;
    private ScrollPane episodeScrollPane;
    private VBox episodePane;
    private FlowPane footerPane;

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
        setStageListeners(stage);
        setFooter();
        resize();
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
        manager.setSelectedButton(clickedButton);
        StyleClasses.addStyle(manager.getSelectedButton(), StyleClasses.SELECTED);
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
        stage.maximizedProperty().addListener((obs, oldVal, newVal) -> Platform.runLater(() -> resize()));
    }

    private void resize() {
        if (episodePane != null) {
            episodePane.setPrefSize(mainPane.getWidth() / 2, mainPane.getHeight() - menuPane.getHeight() - footerPane.getHeight());
            episodePane.getChildren().stream().forEach(item -> ((CheckBox) item).setPrefWidth(mainPane.getWidth() / 2 - 20));
        }
    }

    private void fillEpisodePane() {
        if (manager.getSelectedButton() == null) {
            return;
        }
        if (!manager.checkIfMapIsFilledWithSelectedButton()) {
            EpisodeCheckBox episodeCheckBox = new EpisodeCheckBox(manager.getSelectedButton().getText());
            episodeCheckBox = fillItem(episodeCheckBox, manager.getImportedEpisode().getSubEpisodes().get(manager.getSelectedButton().getText()));
            manager.getCurrentItemMap().put(manager.getSelectedButton().getText(), episodeCheckBox);
        }
        episodePane = new VBox();
        episodePane.setStyle("-fx-background-color: #7e6969");
        episodePane.setMinWidth(mainPane.getWidth() / 2);
        episodeScrollPane.setContent(episodePane);

        manager.getCurrentItemMap().get(manager.getSelectedButton().getText()).getEpisodeCheckBoxes().values().forEach(episodeCheckBox -> {
            addToEpisodePane(episodePane, episodeCheckBox);
        });
        resize();
        episodeScrollPane.setVvalue(manager.getCurrentItemMap().get(manager.getSelectedButton().getText()).getScrollPanePosition());
    }

    private void addToEpisodePane(VBox episodePane, EpisodeCheckBox episodeCheckBox) {
        episodePane.getChildren().add(episodeCheckBox.getCheckBox());
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
            String answer = dialogService.useConfirmDialog(TextEnum.RESET_SELECTED_TITLE.getText(), TextEnum.RESET_SELECTED_QUESTION.getText()).get().getText();
            if (answer.equals(TextEnum.YES.getText())) {
                manager.getCurrentItemMap().forEach((episodeTitle, episodeCheckBox) -> {

                });
            }
        });
        Button exportButton = new Button(TextEnum.EXPORT_ITEMS.getText());
        Button copyButton = new Button(TextEnum.COPY_FILES.getText());
        copyButton.setDisable(true);
        footerPane.getChildren().addAll(clearButton, exportButton, copyButton);
        footerPane.getChildren().forEach(node -> StyleClasses.addStyle(node, StyleClasses.MENU_BUTTON));
    }

    private EpisodeCheckBox fillItem(EpisodeCheckBox episodeCheckBox, Episode episode) {
        episode.getSubEpisodes().keySet().forEach((subEpisodeTitle) -> {
            episodeCheckBox.getEpisodeCheckBoxes().put(subEpisodeTitle, new EpisodeCheckBox(subEpisodeTitle));
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
                fillItem(episodeCheckBox.getEpisodeCheckBoxes().get(subEpisodeTitle), episode.getSubEpisodes().get(subEpisodeTitle));
            }
        });
        return episodeCheckBox;
    }
}