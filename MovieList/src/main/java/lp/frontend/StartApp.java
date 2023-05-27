package lp.frontend;

import javafx.application.Application;
import javafx.application.Platform;
import javafx.geometry.Insets;
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
import lombok.AllArgsConstructor;
import lombok.Data;
import lp.business.dto.Episode;

import java.awt.Dimension;
import java.awt.Toolkit;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class StartApp extends Application {

    private static Episode importedEpisode;

    public static Episode getImportedEpisode() {
        return importedEpisode;
    }

    public static void setImportedEpisode(Episode importedEpisode) {
        StartApp.importedEpisode = importedEpisode;
    }

    private final Toolkit tool = Toolkit.getDefaultToolkit();
    private final Dimension src = tool.getScreenSize();
    private final List<Button> buttonList = new ArrayList<>();
    private final Map<String, CategoryInfo> episodeMap = new HashMap<>();

    private BorderPane mainPane;
    private FlowPane menuPane;
    private ScrollPane choiceScrollPane;
    private VBox choicePane;
    private CategoryInfo currentCategorySelected;

    public void start(Stage stage) {
        mainPane = new BorderPane();
        addStyle(mainPane, StyleClasses.PANE);
        Scene scene = new Scene(mainPane, src.width / 2, src.height / 2);
        stage.setScene(scene);
        scene.getStylesheets().add(getClass().getResource("/css/fx-component.css").toExternalForm());
////        primaryStage.setMaximized(true);
        stage.setTitle("Seznam filmů");
        stage.show();

        setMenu();
        setCurrentSelectedItemPane();

        stage.widthProperty().addListener((obs, oldVal, newVal) -> {
            resize();
            fillChoicePane(null);
        });
        stage.heightProperty().addListener((obs, oldVal, newVal) -> {
            resize();
            fillChoicePane(null);
        });
        stage.maximizedProperty().addListener((obs, oldVal, newVal) -> {
            Platform.runLater(() -> {
                resize();
                fillChoicePane(null);
            });
        });
    }

    private void setMenu() {
        menuPane = new FlowPane();
        addStyle(menuPane, StyleClasses.MENU);
        mainPane.setTop(menuPane);
        if (getImportedEpisode().getEpisodes().isEmpty()) {
            return;
        }
        getImportedEpisode().getEpisodes().forEach(category -> {
            Button menuButton = new Button(category.getTitle());
            addStyle(menuButton, StyleClasses.MENU_BUTTON);
            buttonList.add(menuButton);
            menuPane.setMargin(menuButton, new Insets(10));
            menuPane.getChildren().add(menuButton);

            menuButton.setOnMouseClicked(evt -> {
                if (currentCategorySelected != null) {
                    currentCategorySelected.setScrollPanePosition(choiceScrollPane.getVvalue());
                }
                setSelectedButton(menuButton);
                if (!episodeMap.containsKey(category.getTitle())) {
                    List<Item> newItemList = new ArrayList<>();
                    category.getEpisodes().stream().forEach(episode -> {
                        Item item = new Item(new CheckBox(episode.getTitle()));
                        addStyle(item.getCheckBox(), StyleClasses.CHECKBOX);
                        Tooltip checkBoxTooltip = new Tooltip(episode.getTitle());
                        checkBoxTooltip.setFont(new Font("Arial", 16));
                        item.getCheckBox().setTooltip(checkBoxTooltip);
                        if (!episode.getEpisodes().isEmpty()) {
                            episode.getEpisodes().stream().forEach(subEpisode -> {
                                CheckBox subCheckBox = new CheckBox(subEpisode.getTitle());
                                subCheckBox.selectedProperty().addListener(observable -> {

                                });
                                item.getCheckBoxes().add(subCheckBox);
                            });
                            ToggleButton toggleButton = new ToggleButton(">");
                            toggleButton.setOnAction(action -> {
                                if (toggleButton.isSelected()) {
                                    toggleButton.setText("v");
                                } else {
                                    toggleButton.setText(">");
                                }
                                fillChoicePane(category.getTitle());
                            });
                            item.getCheckBox().setGraphic(toggleButton);
                        }
                        newItemList.add(item);
                    });
                    episodeMap.put(category.getTitle(), new CategoryInfo(0.0, newItemList));
                }
                currentCategorySelected = episodeMap.get(menuButton.getText());
                resize();
                fillChoicePane(category.getTitle());
            });
        });
    }

    private void fillChoicePane(String title) {
        if (title != null) {
            choicePane = new VBox();
            choicePane.setStyle("-fx-background-color: #7e6969");
            choicePane.setMinWidth(mainPane.getWidth() / 2);
            choiceScrollPane.setContent(choicePane);
            episodeMap.get(title).getItems().stream().forEach(item -> {
                item.getCheckBox().setPrefWidth(mainPane.getWidth() / 2 - 20);
                choicePane.getChildren().add(item.getCheckBox());
                if (item.getCheckBox().getGraphic() != null) {
                    if (((ToggleButton) item.getCheckBox().getGraphic()).isSelected()) {
                        item.getCheckBoxes().stream().forEach(subItem -> {
                            subItem.setPrefWidth(mainPane.getWidth() / 2 - 40);
                            choicePane.getChildren().add(subItem);
                        });
                    }
                }
            });
            choiceScrollPane.setVvalue(episodeMap.get(title).getScrollPanePosition());
        }
    }

    private void resize() {
        choiceScrollPane.setPrefSize(mainPane.getWidth() / 2, mainPane.getHeight() - menuPane.getHeight());
        choicePane.setPrefSize(mainPane.getWidth() / 2, mainPane.getHeight() - menuPane.getHeight());
    }

    private void setSelectedButton(Button selectedButton) {
        for (Button button : buttonList) {
            if (button.getStyleClass().contains(StyleClasses.SELECTED.getClassName())) {
                button.getStyleClass().remove(StyleClasses.SELECTED.getClassName());
                break;
            }
        }
        addStyle(selectedButton, StyleClasses.SELECTED);
    }

    private void setCurrentSelectedItemPane() {
        Pane paneForScrolling = new Pane();
        mainPane.setCenter(paneForScrolling);
        choiceScrollPane = new ScrollPane();
        paneForScrolling.getChildren().add(choiceScrollPane);
        choicePane = new VBox();
        choicePane.setStyle("-fx-background-color: #7e6969");
        choicePane.setMinWidth(mainPane.getWidth() / 2);
        choiceScrollPane.setContent(choicePane);
        choiceScrollPane.setHbarPolicy(ScrollPane.ScrollBarPolicy.NEVER);
        resize();
    }

    private void addStyle(Node node, StyleClasses style) {
        node.getStyleClass().add(style.getClassName());
    }

    @Data
    @AllArgsConstructor
    private class CategoryInfo {
        private double scrollPanePosition;
        private List<Item> items;
    }

    @Data
    private class Item {
        private CheckBox checkBox;
        private List<CheckBox> checkBoxes = new ArrayList<>();

        public Item(CheckBox checkBox) {
            this.checkBox = checkBox;
        }
    }
}
