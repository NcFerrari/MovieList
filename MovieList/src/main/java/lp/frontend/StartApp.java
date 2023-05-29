package lp.frontend;

import javafx.application.Application;
import javafx.application.Platform;
import javafx.scene.Node;
import javafx.scene.Scene;
import javafx.scene.control.Button;
import javafx.scene.control.CheckBox;
import javafx.scene.control.ScrollPane;
import javafx.scene.layout.BorderPane;
import javafx.scene.layout.FlowPane;
import javafx.scene.layout.Pane;
import javafx.scene.layout.VBox;
import javafx.stage.Stage;

import java.awt.Toolkit;
import java.util.Map;

public class StartApp extends Application {

    private static Map<String, Map> importedEpisode;

    public static Map<String, Map> getImportedEpisode() {
        return importedEpisode;
    }

    public static void setImportedEpisode(Map<String, Map> importedEpisode) {
        StartApp.importedEpisode = importedEpisode;
    }

    private BorderPane mainPane;
    private FlowPane menuPane;

    private Button selectedButton;
    private ScrollPane episodeScrollPane;
    private VBox episodePane;

//    private final Map<String, CategoryInfo> episodeMap = new HashMap<>();
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

        stage.setMaximized(true);
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
        getImportedEpisode().forEach((categoryTitle, categoryMap) -> {
            Button menuButton = new Button(categoryTitle);
            addStyle(menuButton, StyleClasses.MENU_BUTTON);
            menuPane.getChildren().add(menuButton);
            menuButton.setOnMouseClicked(evt -> {
                changeSelectedButton(menuButton);
                fillEpisodePane(categoryTitle);
//                if (currentCategorySelected != null) {
//                    currentCategorySelected.setScrollPanePosition(choiceScrollPane.getVvalue());
//                }
//                setSelectedButton(menuButton);
//                if (!episodeMap.containsKey(category.getTitle())) {
//                    List<Item> newItemList = new ArrayList<>();
//                    category.getEpisodes().stream().forEach(episode -> {
//                        Item item = new Item(new CheckBox(episode.getTitle()));
//                        addStyle(item.getCheckBox(), StyleClasses.CHECKBOX);
//                        Tooltip checkBoxTooltip = new Tooltip(episode.getTitle());
//                        checkBoxTooltip.setFont(new Font("Arial", 16));
//                        item.getCheckBox().setTooltip(checkBoxTooltip);
//                        if (!episode.getEpisodes().isEmpty()) {
//                            episode.getEpisodes().stream().forEach(subEpisode -> {
//                                CheckBox subCheckBox = new CheckBox(subEpisode.getTitle());
//                                subCheckBox.selectedProperty().addListener(observable -> {
//
//                                });
//                                item.getCheckBoxes().add(subCheckBox);
//                            });
//                            ToggleButton toggleButton = new ToggleButton(">");
//                            toggleButton.setOnAction(action -> {
//                                if (toggleButton.isSelected()) {
//                                    toggleButton.setText("v");
//                                } else {
//                                    toggleButton.setText(">");
//                                }
//                                fillChoicePane(category.getTitle());
//                            });
//                            item.getCheckBox().setGraphic(toggleButton);
//                        }
//                        newItemList.add(item);
//                    });
//                    episodeMap.put(category.getTitle(), new CategoryInfo(0.0, newItemList));
//                }
//                currentCategorySelected = episodeMap.get(menuButton.getText());
//                resize();
//                fillChoicePane(category.getTitle());
            });
        });
    }

    private void changeSelectedButton(Button clickedButton) {
        if (selectedButton != null) {
            selectedButton.getStyleClass().remove(StyleClasses.SELECTED.getClassName());
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
//            fillChoicePane(null);
        });
        stage.heightProperty().addListener((obs, oldVal, newVal) -> {
            resize();
//            fillChoicePane(null);
        });
        stage.maximizedProperty().addListener((obs, oldVal, newVal) -> {
            Platform.runLater(() -> {
                resize();
//                fillChoicePane(null);
            });
        });
    }

    private void resize() {
        episodePane.setPrefSize(mainPane.getWidth() / 2, mainPane.getHeight() - menuPane.getHeight());
    }

//    @Data
//    @AllArgsConstructor
//    private class CategoryInfo {
//        private double scrollPanePosition;
//        private List<Item> items;
//    }
//
//    @Data
//    private class Item {
//        private CheckBox checkBox;
//        private List<CheckBox> checkBoxes = new ArrayList<>();
//
//        public Item(CheckBox checkBox) {
//            this.checkBox = checkBox;
//        }
//    }

    private void fillEpisodePane(String title) {

        CheckBox checkBox = new CheckBox(title);
        addStyle(checkBox, StyleClasses.CHECKBOX);
        checkBox.setPrefWidth(mainPane.getWidth() / 2 - 20);
        episodePane.getChildren().add(checkBox);
//        episodePane = new VBox();
//        episodePane.setStyle("-fx-background-color: #7e6969");
//        episodePane.setMinWidth(mainPane.getWidth() / 2);
//        episodeScrollPane.setContent(episodePane);
//        episodeMap.get(title).getItems().stream().forEach(item -> {
//            item.getCheckBox().setPrefWidth(mainPane.getWidth() / 2 - 20);
//            episodePane.getChildren().add(item.getCheckBox());
//            if (item.getCheckBox().getGraphic() != null) {
//                if (((ToggleButton) item.getCheckBox().getGraphic()).isSelected()) {
//                    item.getCheckBoxes().stream().forEach(subItem -> {
//                        subItem.setPrefWidth(mainPane.getWidth() / 2 - 40);
//                        episodePane.getChildren().add(subItem);
//                    });
//                }
//            }
//        });
//        episodeScrollPane.setVvalue(episodeMap.get(title).getScrollPanePosition());
    }
}
