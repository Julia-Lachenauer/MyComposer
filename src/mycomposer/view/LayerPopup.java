package mycomposer.view;

import java.util.function.Consumer;
import javafx.beans.binding.Bindings;
import javafx.geometry.Insets;
import javafx.geometry.Pos;
import javafx.scene.control.Button;
import javafx.scene.control.Label;
import javafx.scene.control.RadioButton;
import javafx.scene.control.Slider;
import javafx.scene.control.TextField;
import javafx.scene.control.ToggleGroup;
import javafx.scene.input.KeyCode;
import javafx.scene.layout.FlowPane;
import javafx.scene.layout.GridPane;
import javafx.scene.layout.HBox;
import javafx.scene.layout.Region;
import javafx.scene.layout.VBox;
import javafx.stage.Stage;
import mycomposer.FileManager;
import mycomposer.model.Layer;
import mycomposer.model.LayerColor;

/**
 * A popup window allowing the user to either add or edit a layer.
 */
public class LayerPopup extends VBox {

  private final TextField nameField;
  private Slider volumeSlider;
  private final Layer layer;
  private final ToggleGroup toggleGroup;

  private final Runnable refresh;
  private final Consumer<Layer> addLayer;
  private final Consumer<Layer> removeLayer;

  /**
   * Constructs a layer popup and sets the layer to edit to be {@code null}. This constructor should
   * be used when creating a popup to add a new layer.
   *
   * @param addLayer    an operation to add a new layer
   * @param removeLayer an operation to remove an existing layer
   * @param refresh     an operation to refresh the song
   */
  public LayerPopup(Consumer<Layer> addLayer, Consumer<Layer> removeLayer, Runnable refresh) {
    this(null, addLayer, removeLayer, refresh);
  }

  /**
   * Constructs a layer popup with the given layer. This constructor should be used when creating a
   * popup to edit an existing layer.
   *
   * @param layer       the layer to edit ({@code null} if adding a new layer)
   * @param addLayer    an operation to add a new layer
   * @param removeLayer an operation to remove an existing layer
   * @param refresh     an operation to refresh the song
   */
  public LayerPopup(Layer layer, Consumer<Layer> addLayer, Consumer<Layer> removeLayer,
      Runnable refresh) {
    this.refresh = refresh;
    this.layer = layer;
    this.addLayer = addLayer;
    this.removeLayer = removeLayer;

    this.toggleGroup = new ToggleGroup();

    this.setSpacing(15);
    this.setAlignment(Pos.TOP_CENTER);

    this.setOnKeyPressed(ke -> {
      if (ke.getCode() == KeyCode.ESCAPE) {
        Stage stage = (Stage) this.getScene().getWindow();
        stage.close();
      }
    });

    this.nameField = new TextField();
    this.nameField.setPromptText("Layer name");
    this.nameField.setMaxWidth(320);
    this.nameField.setMinWidth(320);

    if (this.layer != null) {
      this.nameField.setText(this.layer.getName());
    }

    this.setOnMouseClicked(e -> {
      if (!this.nameField.equals(e.getSource())) {
        this.requestFocus();
      }
    });

    GridPane layerNameGrid = new GridPane();
    layerNameGrid.setVgap(4);
    layerNameGrid.setPadding(new Insets(10, 0, 0, 15));

    layerNameGrid.add(this.nameField, 0, 0);
    layerNameGrid.add(new Label("Layer name"), 0, 1);

    this.getChildren().addAll(layerNameGrid, this.volumeBox(), this.colorBox(), this.buttonBox());
  }

  /**
   * Focuses on the text field containing the layer name. This should be called after the popup is
   * displayed.
   */
  public void focusOnTextField() {
    this.nameField.requestFocus();
  }

  /**
   * Creates a box with a confirm button for either confirming changes to an existing layer or
   * confirming the creation of a new layer. If this popup is for editing a layer (and not adding a
   * new layer), the box also contains a delete layer button.
   *
   * @return a box with a confirm button and (if this popup is for editing an existing layer) a
   * delete layer button.
   */
  private HBox buttonBox() {
    HBox buttonBox = new HBox();
    buttonBox.getChildren().add(this.confirm());
    buttonBox.setPadding(new Insets(20, 0, 0, 0));

    if (this.layer != null) {
      Button deleteButton = new Button("Remove layer");
      deleteButton.setOnAction(e -> {
        this.removeLayer.accept(this.layer);
        this.close();
      });

      buttonBox.getChildren().add(deleteButton);
    }

    return buttonBox;
  }

  /**
   * Creates a box containing a slider for setting the volume of the layer.
   *
   * @return a box containing a slider for setting the volume of the layer.
   */
  private GridPane volumeBox() {
    Label volLabel = new Label();
    volLabel.setStyle("-fx-text-fill: #ADADAD;");
    volLabel.setPadding(new Insets(0, 0, 0, 15));

    this.volumeSlider = new Slider() {
      @Override
      protected void layoutChildren() {
        super.layoutChildren();

        Region thumb = (Region) this.lookup(".thumb");
        if (thumb != null && thumb.layoutXProperty() != null) {
          volLabel.layoutXProperty().bindBidirectional(thumb.layoutXProperty());
        }
      }
    };

    this.volumeSlider.setMin(0);
    this.volumeSlider.setMax(100);
    this.volumeSlider.setMinWidth(320);
    this.volumeSlider.setMaxWidth(320);

    if (this.layer != null) {
      this.volumeSlider.setValue(this.layer.getVolume());
    } else {
      this.volumeSlider.setValue(50);
    }

    this.volumeSlider.styleProperty().bind(Bindings.createStringBinding(() -> {
      int val = (int) this.volumeSlider.getValue();
      return "-slider-track-color: linear-gradient(to right, -slider-fill-color " + val
          + "%, -slider-bg-color " + val + "%);";
    }, this.volumeSlider.valueProperty()));

    volLabel.textProperty().bind(this.volumeSlider.valueProperty().asString("%2.0f"));

    VBox volumeBox = new VBox();
    volumeBox.setAlignment(Pos.CENTER_LEFT);
    volumeBox.getChildren().addAll(volLabel, this.volumeSlider);
    volumeBox.setPadding(new Insets(0, 0, 0, 15));

    Label volumeLabel = new Label("Volume");
    volumeLabel.setPadding(new Insets(0, 0, 0, 15));

    GridPane volumeBoxWithLabel = new GridPane();
    volumeBoxWithLabel.setVgap(4);
    volumeBoxWithLabel.add(volumeBox, 0, 0);
    volumeBoxWithLabel.add(volumeLabel, 0, 1);

    return volumeBoxWithLabel;
  }

  /**
   * Creates a box with squares representing each of the layer colors to allow the user to choose
   * the color of the layer.
   *
   * @return a box showing each of the layer colors to allow the user to choose the color of the
   * layer
   */
  private FlowPane colorBox() {
    LayerColor[] layerColors = LayerColor.values();

    FlowPane flowPane = new FlowPane();
    flowPane.setAlignment(Pos.BASELINE_CENTER);
    flowPane.setHgap(20);
    flowPane.setVgap(20);
    flowPane.setMaxWidth(200);

    for (LayerColor layerColor : layerColors) {
      RadioButton colorSquare = new RadioButton();

      colorSquare.getStyleClass().remove("radio-button");
      colorSquare.getStyleClass().add("color-toggle-button");

      colorSquare.setMinSize(30, 30);
      colorSquare.setMaxSize(30, 30);
      colorSquare.setStyle("-fx-background-color: " + layerColor.getHexCode() + ";");

      colorSquare.setUserData(layerColor);
      colorSquare.setToggleGroup(this.toggleGroup);
      flowPane.getChildren().add(colorSquare);

      if (this.layer != null) {
        if (this.layer.getColor() == layerColor) {
          colorSquare.setSelected(true);
        }
      }
    }

    return flowPane;
  }

  /**
   * Creates a button to either confirm the changes to an existing layer or confirm the creation of
   * a new layer.
   *
   * @return a button to confirm either the changes to the layer or the creation of a new layer
   */
  private Button confirm() {
    Button confirm;

    if (this.layer != null) {
      confirm = new Button("Edit layer");
    } else {
      confirm = new Button("Add layer");
    }

    confirm.setOnAction(e -> {
      try {
        String name = this.nameField.getText();
        int volume = (int) this.volumeSlider.getValue();
        LayerColor layerColor = LayerColor.valueOf(
            this.toggleGroup.getSelectedToggle().getUserData().toString());

        if (this.layer != null) {
          try {
            this.layer.setColor(layerColor);
            this.layer.setVolume(volume);
            this.layer.setName(name);
            this.refresh.run();
            FileManager.UNSAVED_CHANGES_PROP.set(true);

            this.close();
          } catch (IllegalArgumentException iae) {
            ErrorPopup.showError(this.getScene().getWindow(), iae.getMessage());
          }
        } else {
          try {
            Layer newLayer = new Layer(name, volume, layerColor, false);
            this.addLayer.accept(newLayer);
            FileManager.UNSAVED_CHANGES_PROP.set(true);

            this.close();
          } catch (IllegalArgumentException iae) {
            ErrorPopup.showError(this.getScene().getWindow(), iae.getMessage());
          }
        }

      } catch (NullPointerException npe) {
        ErrorPopup.showError(this.getScene().getWindow(), "All items must be filled in.");
      }
    });

    return confirm;
  }

  /**
   * Closes the layer popup.
   */
  private void close() {
    Stage stage = (Stage) this.getScene().getWindow();
    stage.close();
  }
}
