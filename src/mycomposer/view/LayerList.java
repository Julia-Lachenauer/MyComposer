package mycomposer.view;

import java.util.List;
import java.util.function.BiConsumer;
import java.util.function.Consumer;
import javafx.beans.property.BooleanProperty;
import javafx.beans.property.SimpleBooleanProperty;
import javafx.geometry.Insets;
import javafx.geometry.Pos;
import javafx.scene.Scene;
import javafx.scene.control.CheckBox;
import javafx.scene.control.ContextMenu;
import javafx.scene.control.Label;
import javafx.scene.control.MenuItem;
import javafx.scene.control.RadioButton;
import javafx.scene.control.TextField;
import javafx.scene.control.ToggleGroup;
import javafx.scene.control.Tooltip;
import javafx.scene.effect.ColorAdjust;
import javafx.scene.input.KeyCode;
import javafx.scene.layout.BorderPane;
import javafx.scene.layout.FlowPane;
import javafx.scene.layout.GridPane;
import javafx.scene.layout.HBox;
import javafx.scene.layout.RowConstraints;
import javafx.scene.layout.StackPane;
import javafx.stage.Modality;
import javafx.stage.Stage;
import javafx.util.Duration;
import mycomposer.FileManager;
import mycomposer.Player;
import mycomposer.model.Layer;
import mycomposer.model.LayerColor;

/**
 * A decorated list of all the layers in the song.
 */
public class LayerList extends GridPane {

  private final Consumer<Layer> addLayer;
  private final Consumer<Layer> removeLayer;
  private final Runnable pausePopup;
  private final Runnable unpausePopup;
  private final Runnable refresh;
  private final BiConsumer<Layer, Integer> updateLayerColor;

  private final BooleanProperty listTypingProp;

  /**
   * Constructs a visual list of the layers in the song.
   *
   * @param layers           the list of layers in the song
   * @param addLayer         an operation to add a layer to the song
   * @param removeLayer      an operation to remove a layer from the song
   * @param pausePopup       an operation to pause the song when the edit layer popup is shown
   * @param unpausePopup     an operation to unpause the song when the edit layer popup is closed
   * @param refresh          an operation to refresh the song
   * @param updateLayerColor an operation to set the color of the units in the given layer to the
   *                         given color in the composer view
   * @param mainTypingProp   a boolean property denoting whether or not a text field is currently
   *                         open
   */
  public LayerList(List<Layer> layers, Consumer<Layer> addLayer, Consumer<Layer> removeLayer,
      Runnable pausePopup, Runnable unpausePopup, Runnable refresh,
      BiConsumer<Layer, Integer> updateLayerColor, BooleanProperty mainTypingProp) {

    this.addLayer = addLayer;
    this.removeLayer = removeLayer;
    this.pausePopup = pausePopup;
    this.unpausePopup = unpausePopup;
    this.refresh = refresh;
    this.updateLayerColor = updateLayerColor;

    this.listTypingProp = new SimpleBooleanProperty();
    mainTypingProp.bind(this.listTypingProp);

    this.setStyle("-fx-background-color: -layer-list-background-color;");
    this.setMinWidth(120);

    for (int i = 0; i < layers.size(); i++) {
      RowConstraints rowConstraints = new RowConstraints();
      rowConstraints.setMinHeight(40);
      rowConstraints.setMaxHeight(40);
      this.getRowConstraints().add(rowConstraints);
    }

    int layerIndex = 0;
    for (Layer layer : layers) {
      StackPane layerItem = this.layerItem(layer, layerIndex);
      this.add(layerItem, 0, layerIndex);
      layerIndex++;
    }
  }

  /**
   * Creates an element in the layer list based on the given layer. This element can be
   * double-clicked to open a popup allowing the user to edit the layer.
   *
   * @param layer      the layer to represent in the layer list
   * @param layerIndex the index of the given layer in the layer list
   * @return an element in the layer list based on the given layer
   */
  private StackPane layerItem(Layer layer, int layerIndex) {
    BorderPane borderPane = new BorderPane();
    borderPane.setStyle("-fx-background-color: transparent;");
    borderPane.setMinHeight(40);
    borderPane.setMaxHeight(40);
    borderPane.setMinWidth(120);

    HBox colorBox = new HBox();
    colorBox.setStyle("-fx-border-width: 0 2 2 0;-fx-border-color: -layer-list-background-color;"
        + "-fx-background-color: " + layer.getColor().getHexCode() + ";");

    ColorAdjust colorAdjust = new ColorAdjust();
    colorBox.setEffect(colorAdjust);

    Effects.setDarkenOnHover(borderPane, colorAdjust);

    StackPane pane = new StackPane();
    pane.getChildren().add(colorBox);
    pane.getChildren().add(borderPane);

    Label label = this.editableLabel(layer);

    borderPane.setLeft(label);
    borderPane.setRight(this.muteButton(layer, layerIndex));

    ContextMenu contextMenu = this.colorMenu(layer, layerIndex, label, colorBox);

    borderPane.setOnMouseClicked(e -> {
      if (e.getClickCount() == 2) {
        this.pausePopup.run();
        Stage editLayerStage = new Stage();
        LayerPopup layerPopup = new LayerPopup(layer, this.addLayer, this.removeLayer,
            this.refresh);

        Scene addLayerScene = new Scene(layerPopup, 350, 350);
        addLayerScene.getStylesheets().add("mycomposer/view/Popup.css");
        editLayerStage.setScene(addLayerScene);

        editLayerStage.initOwner(this.getScene().getWindow());
        editLayerStage.initModality(Modality.WINDOW_MODAL);
        editLayerStage.setResizable(false);
        editLayerStage.setTitle("Edit Layer");

        editLayerStage.show();
        layerPopup.requestFocus();
        layerPopup.focusOnTextField();

        editLayerStage.setOnHidden(event -> this.unpausePopup.run());
      }
    });

    borderPane.setOnContextMenuRequested(
        e -> contextMenu.show(borderPane, e.getScreenX(), e.getScreenY()));

    Tooltip volumeTooltip = new Tooltip("Volume: " + layer.getVolume());
    volumeTooltip.setShowDelay(new Duration(200));
    Tooltip.install(borderPane, volumeTooltip);

    return pane;
  }

  /**
   * Creates a label that can be clicked on to edit the given layer's name.
   *
   * @param layer the layer whose name is displayed by the label
   * @return an editable label
   */
  private Label editableLabel(Layer layer) {
    Label label = new Label(layer.getName());

    String textColor = layer.getColor().bestContrast();
    String labelStyle = "-fx-font-size: 12;-fx-text-fill: " + textColor + ";";

    label.setStyle(labelStyle);

    label.setOnMouseEntered(
        e -> label.setStyle("-fx-background-color: -text-hover-color;" + labelStyle));

    label.setOnMouseExited(e -> label.setStyle(labelStyle));

    label.setOnMouseClicked(e -> {
      if (!this.listTypingProp.get()) {
        String oldName = layer.getName();

        this.listTypingProp.set(true);
        TextField field = new TextField();
        field.setText(oldName);
        field.setStyle("-fx-background-color: -text-hover-color;-fx-border-width: 0;"
            + "-fx-text-fill: black;-fx-background-radius: 0;");

        field.setPadding(new Insets(0, 0, 0, 0));

        field.setMinHeight(label.getHeight());
        field.setMaxHeight(label.getHeight());
        field.setMaxWidth(100);

        label.setText("");
        label.setGraphic(field);
        field.requestFocus();

        field.setOnKeyPressed(ke -> {
          String newName = field.getText();
          if (ke.getCode() == KeyCode.ENTER && newName.length() != 0) {
            label.setGraphic(null);
            label.setText(newName);
            layer.setName(newName);
            if (!newName.equals(oldName)) {
              FileManager.UNSAVED_CHANGES_PROP.set(true);
            }
            this.listTypingProp.set(false);
          } else if (ke.getCode() == KeyCode.ESCAPE) {
            label.setGraphic(null);
            label.setText(oldName);
            this.listTypingProp.set(false);
          }
        });

        field.focusedProperty().addListener((obs, oldVal, newVal) -> {
          String newName = field.getText();
          if (!newVal) {
            label.setGraphic(null);
            label.setText(newName);
            layer.setName(newName);
            if (!newName.equals(oldName)) {
              FileManager.UNSAVED_CHANGES_PROP.set(true);
            }
            this.listTypingProp.set(false);
          }
        });
      }
    });

    return label;
  }

  /**
   * Creates a mute button (which controls like a checkbox) to control the muting of the given
   * layer.
   *
   * @param layer      the layer to mute and unmute
   * @param layerIndex the index of the given layer in the layer list
   * @return a button to control the muting of the given layer
   */
  private CheckBox muteButton(Layer layer, int layerIndex) {
    CheckBox mute = new CheckBox();
    mute.setSelected(false);
    mute.getStyleClass().remove("check-box");
    mute.getStyleClass().add("mute-button");

    mute.setOnAction(e -> {
      layer.toggleMute();
      Player.toggleMuteLayer(layerIndex);
    });

    Tooltip tooltip = new Tooltip("Mute Layer");
    tooltip.setShowDelay(new Duration(100));
    Tooltip.install(mute, tooltip);

    mute.setMinSize(16, 38);
    mute.setMaxSize(16, 38);
    mute.setAlignment(Pos.CENTER);

    mute.setPadding(new Insets(0, 1, 0, 0));

    return mute;
  }

  /**
   * Creates a right-click menu containing a pane with squares representing each of the layer
   * colors. Choosing a layer color from this menu changes the color of the given layer.
   *
   * @param layer      the layer to change the color of
   * @param layerIndex the index of the layer in the layer list
   * @param label      the label in the list denoting the layer's name
   * @param box        the box displaying the color of the layer
   * @return a right-click menu allowing the user to quickly change the layer color
   */
  private ContextMenu colorMenu(Layer layer, int layerIndex, Label label, HBox box) {
    ContextMenu contextMenu = new ContextMenu();

    contextMenu.getStyleClass().remove("context-menu");
    contextMenu.getStyleClass().add("color-menu");

    MenuItem colorItem = new MenuItem();
    colorItem.getStyleClass().remove("menu-item");
    colorItem.getStyleClass().add("color-menu-item");

    FlowPane flowPane = new FlowPane();
    flowPane.setAlignment(Pos.CENTER);
    flowPane.setHgap(10);
    flowPane.setVgap(10);
    flowPane.setMaxWidth(140);
    flowPane.setMinHeight(70);
    flowPane.setMaxHeight(70);
    flowPane.setStyle("-fx-background-color: #27272B;-fx-border-width: 0;");

    ToggleGroup toggleGroup = new ToggleGroup();

    LayerColor[] layerColors = LayerColor.values();
    for (LayerColor layerColor : layerColors) {
      RadioButton colorSquare = new RadioButton();

      colorSquare.getStyleClass().remove("radio-button");
      colorSquare.getStyleClass().add("color-toggle-button");

      colorSquare.setMinSize(20, 20);
      colorSquare.setMaxSize(20, 20);
      colorSquare.setStyle("-fx-background-color: " + layerColor.getHexCode() + ";");

      colorSquare.setUserData(layerColor);
      colorSquare.setToggleGroup(toggleGroup);

      colorSquare.setOnAction(e -> {
        layer.setColor(layerColor);
        this.updateLayerColor.accept(layer, layerIndex);
        box.setStyle("-fx-border-width: 0 2 2 0;-fx-border-color: -layer-list-background-color;"
            + "-fx-background-color: " + layerColor.getHexCode() + ";");
        label.setStyle("-fx-border-width: 0;-fx-text-fill: " + layerColor.bestContrast() + ";");
        FileManager.UNSAVED_CHANGES_PROP.set(true);
      });

      flowPane.getChildren().add(colorSquare);

      if (layerColor == layer.getColor()) {
        colorSquare.setSelected(true);
      }
    }

    colorItem.setGraphic(flowPane);

    contextMenu.getItems().add(colorItem);

    return contextMenu;
  }
}
