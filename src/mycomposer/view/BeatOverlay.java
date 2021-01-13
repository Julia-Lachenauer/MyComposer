package mycomposer.view;

import javafx.scene.layout.HBox;
import javafx.scene.layout.VBox;
import mycomposer.MIDI;
import mycomposer.Player;

/**
 * Represents an overlay above the layer grid to show the beat tracker bar (a bar highlighting the
 * current beat of the song).
 */
public class BeatOverlay extends HBox {

  private int highlightedBeat;

  /**
   * Constructs the overlay above the layer grid to show the beat tracker bar to highlight the
   * current beat of the song.
   */
  public BeatOverlay() {
    this.highlightedBeat = -1;

    this.setStyle("-fx-background-color: transparent;-fx-border-width: 0;");
    this.setMouseTransparent(true);
    int finalBeat = (Player.FINAL_BEAT == 0) ? 0 : Player.FINAL_BEAT + 1;

    for (int i = 0; i < finalBeat + MIDI.EXTRA_BEATS; i++) {
      VBox cell = new VBox();
      cell.setMinWidth(40);
      cell.setStyle("-fx-background-color: transparent;-fx-border-width: 0;");

      this.getChildren().add(cell);
    }
  }

  /**
   * Highlights the beat at the given index. If the beat is after the first beat, removes any
   * highlight on the preceding beat.
   *
   * @param beat the beat to highlight
   */
  public void highlightCell(int beat) {
    this.highlightedBeat = beat;

    if (beat < Player.FINAL_BEAT + 1) {
      this.getChildren().get(beat)
          .setStyle("-fx-background-color: -overlay-bar-color;-fx-border-width: 0;");

      if (beat > 0) {
        this.getChildren().get(beat - 1)
            .setStyle("-fx-background-color: transparent;-fx-border-width: 0;");
      }
    }
  }

  /**
   * Removes highlights on all beats.
   */
  public void clear() {
    if (this.highlightedBeat != -1) {
      this.getChildren().get(this.highlightedBeat)
          .setStyle("-fx-background-color: transparent;-fx-border-width: 0;");
    }

    this.highlightedBeat = -1;
  }
}
