package mycomposer.view;

import javafx.scene.control.Label;
import javafx.scene.layout.ColumnConstraints;
import javafx.scene.layout.GridPane;
import javafx.scene.layout.VBox;
import mycomposer.MIDI;
import mycomposer.Player;

/**
 * Represents the beat numbers above the layer grid to help the user keep track of the beats in the
 * song.
 */
public class BeatNumbers extends GridPane {

  /**
   * Constructs an instance of the beat numbers to be displayed above the layer grid.
   *
   * @param overlay    an overlay panel above the layer grid for the beat tracker bar
   * @param togglePlay an operation for toggling between pause and play
   */
  public BeatNumbers(BeatOverlay overlay, Runnable togglePlay) {
    this.setMinHeight(15);
    this.setMaxHeight(15);

    this.setStyle("-fx-background-color: -numbers-background-color;-fx-border-width: 0;");
    int finalBeat = (Player.FINAL_BEAT == 0) ? 0 : Player.FINAL_BEAT + 1;

    for (int i = 0; i < finalBeat + MIDI.EXTRA_BEATS; i++) {
      ColumnConstraints colConstraints = new ColumnConstraints();
      colConstraints.setMinWidth(40);
      colConstraints.setMaxWidth(40);
      this.getColumnConstraints().add(colConstraints);
    }

    for (int i = 0; i < finalBeat + MIDI.EXTRA_BEATS; i++) {
      VBox unit = new VBox();
      unit.setMinWidth(40);
      unit.setMaxWidth(40);

      Label measureNumber = new Label(String.valueOf(i + 1));
      measureNumber.setStyle("-fx-text-fill: -numbers-text-color;-fx-font-size: 10");

      if (i < Player.FINAL_BEAT) {
        int beat = i;
        unit.setOnMouseClicked(e -> {
          Player.setBeat(beat);
          overlay.clear();
          overlay.highlightCell(beat);

          togglePlay.run();
        });

        unit.setOnMouseEntered(e -> measureNumber
            .setStyle("-fx-text-fill: -numbers-hover-text-color;-fx-font-size: 10"));

        unit.setOnMouseExited(
            e -> measureNumber.setStyle("-fx-text-fill: -numbers-text-color;-fx-font-size: 10"));
      }

      unit.getChildren().add(measureNumber);
      this.add(unit, i, 0);
    }
  }
}
