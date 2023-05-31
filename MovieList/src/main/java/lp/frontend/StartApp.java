package lp.frontend;

import javafx.application.Application;
import javafx.application.Platform;
import javafx.geometry.Pos;
import javafx.scene.Node;
import javafx.scene.Scene;
import javafx.scene.control.Alert;
import javafx.scene.control.Button;
import javafx.scene.control.ButtonType;
import javafx.scene.control.CheckBox;
import javafx.scene.control.ChoiceDialog;
import javafx.scene.control.ScrollPane;
import javafx.scene.control.ToggleButton;
import javafx.scene.control.Tooltip;
import javafx.scene.layout.BorderPane;
import javafx.scene.layout.FlowPane;
import javafx.scene.layout.Pane;
import javafx.scene.layout.VBox;
import javafx.scene.text.Font;
import javafx.stage.Stage;
import lombok.Data;
import lp.business.dto.Episode;
import lp.service.DialogService;
import lp.serviceimpl.DialogServiceImpl;

import java.awt.Toolkit;
import java.util.LinkedHashMap;
import java.util.Map;
import java.util.Optional;

public class StartApp extends Application {

    private final DialogService dialogService = DialogServiceImpl.getInstance();
    private static Episode importedEpisode;

    public static Episode getImportedEpisode() {
        return importedEpisode;
    }

    public static void setImportedEpisode(Episode importedEpisode) {
        StartApp.importedEpisode = importedEpisode;
    }

    private final Map<String, Item> loadedItemList = new LinkedHashMap<>();

    private BorderPane mainPane;
    private FlowPane menuPane;
    private ScrollPane episodeScrollPane;
    private VBox episodePane;
    private FlowPane footerPane;

    private Button selectedButton;

    public void start(Stage stage) {
        mainPane = new BorderPane();
        addStyle(mainPane, StyleClasses.PANE);
        Scene scene = new Scene(mainPane, Toolkit.getDefaultToolkit().getScreenSize().width / 2, Toolkit.getDefaultToolkit().getScreenSize().height / 2);
        stage.setScene(scene);
        scene.getStylesheets().add(getClass().getResource(TextEnum.RESOURCES.getText()).toExternalForm());
        stage.setTitle(TextEnum.APPLICATION_TITLE.getText());
        stage.show();
        menuPane = new FlowPane();
        addStyle(menuPane, StyleClasses.MENU);
        mainPane.setTop(menuPane);
        setMenu();
        setEpisodePane();
        setStageListeners(stage);
        setFooter();
        resize();
    }

    private void addStyle(Node node, StyleClasses style) {
        node.getStyleClass().add(style.getClassName());
    }

    private void setMenu() {
        if (getImportedEpisode() == null || getImportedEpisode().getEpisodes().isEmpty()) {
            return;
        }
        getImportedEpisode().getEpisodes().forEach((categoryTitle, categories) -> {
            Button menuButton = new Button(categoryTitle);
            addStyle(menuButton, StyleClasses.MENU_BUTTON);
            menuPane.getChildren().add(menuButton);
            menuButton.setOnMouseClicked(evt -> {
                changeSelectedButton(menuButton);
                fillEpisodePane();
            });
        });
    }

    private void changeSelectedButton(Button clickedButton) {
        if (selectedButton != null) {
            selectedButton.getStyleClass().remove(StyleClasses.SELECTED.getClassName());
            loadedItemList.get(selectedButton.getText()).setScrollPanePosition(episodeScrollPane.getVvalue());
        }
        selectedButton = clickedButton;
        addStyle(selectedButton, StyleClasses.SELECTED);
    }

    private void setEpisodePane() {
        episodePane = new VBox();
        episodePane.setStyle("-fx-background-color: #7e6969");
        episodeScrollPane = new ScrollPane(episodePane);
        episodeScrollPane.setHbarPolicy(ScrollPane.ScrollBarPolicy.NEVER);
        mainPane.setCenter(new Pane(episodeScrollPane));
    }

    private void setStageListeners(Stage stage) {
        stage.widthProperty().addListener((obs, oldVal, newVal) -> {
            resize();
        });
        stage.heightProperty().addListener((obs, oldVal, newVal) -> {
            resize();
        });
        stage.maximizedProperty().addListener((obs, oldVal, newVal) -> {
            Platform.runLater(() -> {
                resize();
            });
        });
    }

    private void resize() {
        if (episodePane != null) {
            episodePane.setPrefSize(mainPane.getWidth() / 2, mainPane.getHeight() - menuPane.getHeight() - footerPane.getHeight());
            episodePane.getChildren().stream().forEach(item -> ((CheckBox) item).setPrefWidth(mainPane.getWidth() / 2 - 20));
        }
    }

    private void fillEpisodePane() {
        if (selectedButton == null) {
            return;
        }
        checkIfMapIsFilledWithSelectedButton();
        episodePane = new VBox();
        episodePane.setStyle("-fx-background-color: #7e6969");
        episodePane.setMinWidth(mainPane.getWidth() / 2);
        episodeScrollPane.setContent(episodePane);

        loadedItemList.get(selectedButton.getText()).items.forEach((k, v) -> {
            addToEpisodePane(episodePane, v);
        });
        resize();
        episodeScrollPane.setVvalue(loadedItemList.get(selectedButton.getText()).getScrollPanePosition());
    }

    private void checkIfMapIsFilledWithSelectedButton() {
        if (loadedItemList.containsKey(selectedButton.getText())) {
            return;
        }
        Item item = new Item(selectedButton.getText());
        item = fillItem(item, getImportedEpisode().getEpisodes().get(selectedButton.getText()));
        loadedItemList.put(selectedButton.getText(), item);
    }

    private Item fillItem(Item item, Episode episode) {
        episode.getEpisodes().forEach((key, value) -> {
            item.getItems().put(key.toString(), new Item(key.toString()));
            if (!episode.getEpisodes().get(key).getEpisodes().isEmpty()) {
                item.getItems().get(key.toString()).addToggleButton();
                fillItem(item.getItems().get(key.toString()), episode.getEpisodes().get(key));
            }
        });
        return item;
    }

    private void addToEpisodePane(VBox episodePane, Item item) {
        episodePane.getChildren().add(item.getCheckBox());
        if (!item.getItems().isEmpty() && ((ToggleButton) item.getCheckBox().getGraphic()).isSelected()) {
            item.getItems().forEach((k, v) -> addToEpisodePane(episodePane, v));
        }
    }

    private void setFooter() {
        footerPane = new FlowPane();
        footerPane.setAlignment(Pos.CENTER);
        addStyle(footerPane, StyleClasses.MENU);
        mainPane.setBottom(footerPane);

        Button clearButton = new Button(TextEnum.CLEAR_SELECTED_BUTTON_TEXT.getText());
        clearButton.setOnAction(evt -> {
            String answer = dialogService.useConfirmDialog(TextEnum.RESET_SELECTED_TITLE.getText(), TextEnum.RESET_SELECTED_QUESTION.getText()).get().getText();
            if (answer.equals(TextEnum.YES.getText())) {

            }
        });
        Button exportButton = new Button(TextEnum.EXPORT_ITEMS.getText());
        Button copyButton = new Button(TextEnum.COPY_FILES.getText());
        copyButton.setDisable(true);
        footerPane.getChildren().addAll(clearButton, exportButton, copyButton);
        footerPane.getChildren().forEach(node -> {
            addStyle(node, StyleClasses.MENU_BUTTON);
        });
    }

    @Data
    private class Item {
        private CheckBox checkBox;
        private double scrollPanePosition;
        private Map<String, Item> items = new LinkedHashMap<>();

        public Item(String title) {
            checkBox = new CheckBox(title);
            Tooltip checkBoxTooltip = new Tooltip(title);
            checkBoxTooltip.setFont(new Font("Arial", 16));
            checkBox.setTooltip(checkBoxTooltip);
            addStyle(checkBox, StyleClasses.CHECKBOX);
        }

        public void addToggleButton() {
            ToggleButton toggleButton = new ToggleButton(TextEnum.CLOSED_SUB_LIST.getText());
            toggleButton.setOnAction(action -> {
                if (toggleButton.isSelected()) {
                    toggleButton.setText(TextEnum.OPENED_SUB_LIST.getText());
                } else {
                    toggleButton.setText(TextEnum.CLOSED_SUB_LIST.getText());
                }
                fillEpisodePane();
            });
            checkBox.setGraphic(toggleButton);
        }
    }
}