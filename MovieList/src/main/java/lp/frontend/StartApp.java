package lp.frontend;

import javafx.application.Application;
import javafx.application.Platform;
import javafx.scene.Node;
import javafx.scene.Scene;
import javafx.scene.control.Button;
import javafx.scene.control.CheckBox;
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

import java.awt.Toolkit;
import java.util.LinkedHashMap;
import java.util.Map;

public class StartApp extends Application {

    private static Map<String, Map<String, Map>> importedEpisode;

    public static Map<String, Map<String, Map>> getImportedEpisode() {
        return importedEpisode;
    }

    public static void setImportedEpisode(Map<String, Map<String, Map>> importedEpisode) {
        StartApp.importedEpisode = importedEpisode;
    }

    private final Map<String, Item> loadedItemList = new LinkedHashMap<>();

    private BorderPane mainPane;
    private FlowPane menuPane;
    private ScrollPane episodeScrollPane;
    private VBox episodePane;
    private Button selectedButton;
//    private CategoryInfo currentCategorySelected;

    public void start(Stage stage) {
        mainPane = new BorderPane();
        addStyle(mainPane, StyleClasses.PANE);
        Scene scene = new Scene(mainPane, Toolkit.getDefaultToolkit().getScreenSize().width / 2, Toolkit.getDefaultToolkit().getScreenSize().height / 2);
        stage.setScene(scene);
        scene.getStylesheets().add(getClass().getResource(TextEnum.RESOURCES.getText()).toExternalForm());
        stage.setTitle(TextEnum.APPLICATION_TITLE.getText());
        stage.show();
        setMenu();
        setEpisodePane();
        setStageListeners(stage);

//        stage.setMaximized(true);
        resize();
    }

    private void addStyle(Node node, StyleClasses style) {
        node.getStyleClass().add(style.getClassName());
    }

    private void setMenu() {
        if (getImportedEpisode().isEmpty()) {
            return;
        }
        menuPane = new FlowPane();
        addStyle(menuPane, StyleClasses.MENU);
        mainPane.setTop(menuPane);
        getImportedEpisode().forEach((rootFile, categories) -> categories.keySet().stream().forEach(categoryTitle -> {
            Button menuButton = new Button(categoryTitle);
            addStyle(menuButton, StyleClasses.MENU_BUTTON);
            menuPane.getChildren().add(menuButton);
            menuButton.setOnMouseClicked(evt -> {
                changeSelectedButton(menuButton);
                fillEpisodePane();
            });
        }));
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
        episodePane.setPrefSize(mainPane.getWidth() / 2, mainPane.getHeight() - menuPane.getHeight());
        episodePane.getChildren().stream().forEach(item -> ((CheckBox) item).setPrefWidth(mainPane.getWidth() / 2 - 20));
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

    private void addToEpisodePane(VBox episodePane, Item item) {
        episodePane.getChildren().add(item.getCheckBox());
        if (!item.getItems().isEmpty() && ((ToggleButton) item.getCheckBox().getGraphic()).isSelected()) {
            item.getItems().forEach((k, v) -> addToEpisodePane(episodePane, v));
        }
    }

    private void checkIfMapIsFilledWithSelectedButton() {
        if (loadedItemList.containsKey(selectedButton.getText())) {
            return;
        }
        getImportedEpisode().forEach((rootFile, categories) -> {
            Item item = new Item(selectedButton.getText());
            item = fillItem(item, categories.get(selectedButton.getText()));
            loadedItemList.put(selectedButton.getText(), item);
        });
    }

    private Item fillItem(Item item, Map<String, Map<String, Map>> map) {
        map.forEach((key, value) -> {
            item.getItems().put(key.toString(), new Item(key.toString()));
            if (!map.get(key).isEmpty()) {
                item.getItems().get(key.toString()).addToggleButton();
                fillItem(item.getItems().get(key.toString()), (Map) map.get(key));
            }
        });
        return item;
    }
}