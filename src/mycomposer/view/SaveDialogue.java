package mycomposer.view;

import javafx.geometry.Insets;
import javafx.scene.Scene;
import javafx.scene.control.Button;
import javafx.scene.control.Label;
import javafx.scene.image.Image;
import javafx.scene.layout.HBox;
import javafx.scene.layout.VBox;
import javafx.stage.Modality;
import javafx.stage.Stage;
import javafx.stage.StageStyle;
import mycomposer.FileManager;

/**
 * Represents a save dialogue popup. This is to be used whenever there are unsaved changes and the
 * user wishes to leave the song (either by closing the program or opening another song).
 */
public class SaveDialogue {

  /**
   * Creates a save dialogue popup to be used when the user wishes to leave the song and there are
   * unsaved changes.
   *
   * @param mainStage       the stage which owns this popup
   * @param saveAs          an operation to open the "Save As" window
   * @param onDialogueClose an operation to run when this popup is closed
   */
  public static void showSaveDialogue(Stage mainStage, Runnable saveAs, Runnable onDialogueClose) {
    Stage saveStage = new Stage();

    VBox saveBox = new VBox();
    saveBox.setSpacing(10);
    saveBox.setStyle("-fx-background-color: -tab-header-background;"
        + "-fx-border-width: 0 2 2 2;-fx-border-color: -tab-selected-color;");

    Label errorLabel = new Label("You have unsaved changes to your song.\nWould you like to save?");
    errorLabel.setStyle("-fx-font-size: 14;");
    VBox.setMargin(errorLabel, new Insets(10, 10, 0, 10));

    Button saveButton = new Button("Save");
    saveButton.setOnAction(e -> {
      if (FileManager.OPEN_FILE_PATH == null) {
        saveAs.run();
      } else {
        FileManager.saveCurrentFile();
      }
      FileManager.saveRecentFiles();
      saveStage.close();
      onDialogueClose.run();
    });

    Button noSaveButton = new Button("Don't Save");
    noSaveButton.setOnAction(e -> {
      FileManager.saveRecentFiles();
      saveStage.close();
      onDialogueClose.run();
    });

    Button cancelButton = new Button("Cancel");
    cancelButton.setOnAction(e -> saveStage.close());

    HBox buttons = new HBox();
    buttons.getChildren().addAll(saveButton, noSaveButton, cancelButton);
    VBox.setMargin(buttons, new Insets(0, 10, 15, 10));

    saveBox.getChildren().addAll(errorLabel, buttons);

    Scene saveScene = new Scene(saveBox);
    saveScene.getStylesheets().add("mycomposer/view/Popup.css");
    saveStage.getIcons().add(new Image("/resources/icon.png"));

    saveStage.setTitle("Unsaved Changes");
    saveStage.setScene(saveScene);

    saveStage.initOwner(mainStage);
    saveStage.setResizable(false);
    saveStage.initStyle(StageStyle.DECORATED);
    saveStage.initModality(Modality.WINDOW_MODAL);

    saveStage.show();
  }
}
