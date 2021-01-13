package mycomposer.view;

import javafx.scene.Scene;
import javafx.scene.control.Label;
import javafx.scene.control.TabPane;
import javafx.scene.control.Tooltip;
import javafx.scene.effect.ColorAdjust;
import javafx.scene.input.MouseButton;
import javafx.scene.layout.ColumnConstraints;
import javafx.scene.layout.GridPane;
import javafx.scene.layout.HBox;
import javafx.scene.layout.Priority;
import javafx.scene.layout.StackPane;
import javafx.scene.layout.VBox;
import javafx.stage.Modality;
import javafx.stage.Stage;
import javafx.util.Duration;
import mycomposer.MIDI;
import mycomposer.Player;
import mycomposer.model.Layer;
import mycomposer.model.unit.Instrument;
import mycomposer.model.unit.Percussion;
import mycomposer.model.unit.Unit;

/**
 * A visual representation of a layer which shows each unit in the layer.
 */
public class LayerGrid extends GridPane {

  private final Layer layer;
  private final Runnable refresh;
  private final Runnable pause;
  private final Runnable unpause;

  /**
   * Creates a layer grid for the given layer.
   *
   * @param layer        the layer to create a layer grid for
   * @param refresh      an operation to refresh the song
   * @param pausePopup   an operation to pause the song when an add/edit unit popup is shown
   * @param unpausePopup an operation to unpause the song when an add/edit unit popup is closed
   */
  public LayerGrid(Layer layer, Runnable refresh, Runnable pausePopup, Runnable unpausePopup) {
    this.setMinHeight(40);
    this.setMaxHeight(40);

    this.layer = layer;
    this.refresh = refresh;
    this.pause = pausePopup;
    this.unpause = unpausePopup;

    this.setStyle("-fx-background-color: -window-background-color;-fx-border-style: solid;"
        + "-fx-border-color: -grid-border-color;-fx-border-width: 0 0 1 0;");

    int finalBeat = (Player.FINAL_BEAT == 0) ? 0 : Player.FINAL_BEAT + 1;

    for (int i = 0; i < finalBeat + MIDI.EXTRA_BEATS; i++) {
      ColumnConstraints colConstraints = new ColumnConstraints();
      colConstraints.setMinWidth(40);
      colConstraints.setMaxWidth(40);
      this.getColumnConstraints().add(colConstraints);
    }

    for (int i = 0; i < finalBeat + MIDI.EXTRA_BEATS; i++) {
      VBox cell = new VBox();
      cell.setMinWidth(40);
      cell.setMaxWidth(40);
      GridPane.setVgrow(cell, Priority.ALWAYS);

      this.setCellStyle(cell, i);

      int beat = i;
      cell.setOnMouseClicked(e -> {
        if (e.getButton() == MouseButton.PRIMARY) {
          pausePopup.run();
          Stage addNoteStage = new Stage();
          TabPane addNotePopup = new UnitPopup(layer, beat, refresh, unpausePopup);

          Scene addNoteScene = new Scene(addNotePopup, 350, 350);
          addNoteScene.getStylesheets().add("mycomposer/view/Popup.css");
          addNoteStage.setScene(addNoteScene);

          addNoteStage.initOwner(this.getScene().getWindow());
          addNoteStage.initModality(Modality.WINDOW_MODAL);
          addNoteStage.setResizable(false);
          addNoteStage.setTitle("Add Note");

          addNoteStage.show();
          addNotePopup.requestFocus();

          addNoteStage.setOnHidden(event -> {
            Player.stopAllUnits();
            this.unpause.run();
          });
        }
      });

      this.add(cell, i, 0);
    }

    for (Unit unit : layer.getUnits()) {
      StackPane unitBox = this.unitBox(unit);

      this.add(unitBox, unit.getStartBeat(), 0);
    }
  }

  /**
   * Creates a box representing the given unit in the layer. The box is set to span the same number
   * of columns as its duration.
   *
   * @param unit the unit to represent
   * @return a box representing the given unit in the layer
   */
  private StackPane unitBox(Unit unit) {
    HBox unitBox = new HBox();
    GridPane.setVgrow(unitBox, Priority.ALWAYS);
    unitBox.setMouseTransparent(true);

    HBox colorLayer = new HBox();
    GridPane.setVgrow(colorLayer, Priority.ALWAYS);

    int endBeat = unit.getEndBeat();

    int rWidth = 1;
    if (!this.layer.beatOverlapsUnit(endBeat + 1)) { // there is another beat directly to the right
      if ((endBeat + 1) % 8 == 0) {
        rWidth = 6;
      } else if ((endBeat + 1) % 4 == 0) {
        rWidth = 4;
      }
    }

    String style = "-fx-background-color: " + this.layer.getColor().getHexCode()
        + ";-fx-border-style: solid;-fx-border-color: -grid-border-color;-fx-border-width: 0 "
        + rWidth + " 1 0;";

    colorLayer.setStyle(style);

    ColorAdjust colorAdjust = new ColorAdjust();
    colorLayer.setEffect(colorAdjust);
    Effects.setDarkenOnHover(colorLayer, colorAdjust);

    unitBox.setStyle("-fx-background-color: null;");

    if (!unit.isDrum()) {
      Label pitch = new Label(unit.getPitchFormat());
      pitch.setStyle("-fx-border-width: 0;-fx-font-size: 12;-fx-text-fill: " + this.layer.getColor()
          .bestContrast() + ";");

      unitBox.getChildren().add(pitch);
    }

    colorLayer.setOnMouseClicked(e -> {
      if (e.getButton() == MouseButton.PRIMARY) {
        this.pause.run();
        Stage addNoteStage = new Stage();
        TabPane addNotePopup = new UnitPopup(unit, this.layer, unit.getStartBeat(), this.refresh,
            this.unpause);

        Scene addNoteScene = new Scene(addNotePopup, 350, 350);
        addNoteScene.getStylesheets().add("mycomposer/view/Popup.css");
        addNoteStage.setScene(addNoteScene);

        addNoteStage.initOwner(this.getScene().getWindow());
        addNoteStage.initModality(Modality.WINDOW_MODAL);
        addNoteStage.setResizable(false);
        addNoteStage.setTitle("Edit Note");

        addNoteStage.show();
        addNotePopup.requestFocus();

        addNoteStage.setOnHidden(event -> {
          Player.stopAllUnits();
          this.unpause.run();
        });
      }
    });

    colorLayer.setOnMousePressed(e -> {
      if (e.getButton() == MouseButton.SECONDARY) {
        Player.playUnit(unit);
      }
    });

    colorLayer.setOnMouseReleased(e -> Player.stopAllUnits());

    StackPane pane = new StackPane();
    GridPane.setColumnSpan(pane, unit.getDuration());
    pane.getChildren().addAll(colorLayer, unitBox);
    this.addTooltip(unit, pane);

    return pane;
  }

  /**
   * Sets the style of each cell in the layer grid. Every 4 beats, the border width is set to be
   * wider than normal, and every 8 beats, the border width is set to be even wider than the width
   * at every 4 beats.
   *
   * @param cell the cell to style
   * @param beat the beat represented by the cell
   */
  private void setCellStyle(VBox cell, int beat) {
    String style;

    if ((beat + 1) % 8 == 0) {
      style = "-fx-border-style: solid;-fx-border-color: -grid-border-color;"
          + "-fx-border-width: 0 6 0 0;";
    } else if ((beat + 1) % 4 == 0) {
      style = "-fx-border-style: solid;-fx-border-color: -grid-border-color;"
          + "-fx-border-width: 0 4 0 0;";
    } else {
      style = "-fx-border-style: solid;-fx-border-color: -grid-border-color;"
          + "-fx-border-width: 0 1 0 0;";
    }

    cell.setStyle("-fx-background-color: null;" + style);
    cell.setOnMouseEntered(e -> cell.setStyle("-fx-background-color: -grid-hover-color;" + style));
    cell.setOnMouseExited(e -> cell.setStyle("-fx-background-color: null;" + style));
  }

  /**
   * Adds a tooltip with the given unit's information to the given unit box.
   *
   * @param unit    the unit represented by the unit box
   * @param unitBox the unit box to add a tooltip to
   */
  private void addTooltip(Unit unit, StackPane unitBox) {
    String styleString = "-fx-font-size: 10;-fx-font-family:Helvetica;"
        + "-fx-text-fill: -file-menu-text-color;";

    VBox tooltipBox = new VBox();
    tooltipBox.setStyle("-fx-background-color: transparent;");
    Label instrumentLabel = new Label();
    instrumentLabel.setStyle(styleString);

    if (unit.isDrum()) {
      instrumentLabel.setText(Percussion.getSound(unit.getInstrument()).getName());
    } else {
      instrumentLabel.setText(Instrument.getSound(unit.getInstrument()).getName());
    }

    Label startEnd = new Label((unit.getStartBeat() + 1) + "-" + (unit.getEndBeat() + 1));
    startEnd.setStyle(styleString);

    tooltipBox.getChildren().addAll(instrumentLabel, startEnd);

    if (!unit.isDrum()) {
      Label pitchOctave = new Label(
          "Pitch: " + unit.getPitch().getName() + "   Octave: " + unit.getOctave());
      pitchOctave.setStyle(styleString);
      tooltipBox.getChildren().add(pitchOctave);
    }

    Tooltip eventToolTip = new Tooltip();
    eventToolTip.setGraphic(tooltipBox);

    eventToolTip.setShowDelay(new Duration(150));
    Tooltip.install(unitBox, eventToolTip);
  }
}
