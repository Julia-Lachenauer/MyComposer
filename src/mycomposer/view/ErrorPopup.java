package mycomposer.view;

import javafx.geometry.Insets;
import javafx.scene.Scene;
import javafx.scene.control.Button;
import javafx.scene.control.Label;
import javafx.scene.image.Image;
import javafx.scene.input.KeyCode;
import javafx.scene.layout.VBox;
import javafx.stage.Modality;
import javafx.stage.Stage;
import javafx.stage.StageStyle;
import javafx.stage.Window;

/**
 * Represents an error message popup.
 */
public class ErrorPopup {

  /**
   * Constructs and shows an error message popup.
   *
   * @param stageOwner   the window which owns this error popup
   * @param errorMessage the error message to display
   */
  public static void showError(Window stageOwner, String errorMessage) {
    Stage errorStage = new Stage();

    VBox errorBox = new VBox();
    errorBox.setSpacing(10);
    errorBox.setStyle("-fx-background-color: -tab-header-background;"
        + "-fx-border-width: 0 2 2 2;-fx-border-color: -tab-selected-color;");

    errorBox.setOnKeyPressed(ke -> {
      if (ke.getCode() == KeyCode.ESCAPE) {
        errorStage.close();
      }
    });

    Label errorLabel = new Label(errorMessage);
    errorLabel.setStyle("-fx-font-size: 14;");
    VBox.setMargin(errorLabel, new Insets(10, 10, 0, 10));

    Button closeButton = new Button("OK");
    closeButton.setOnAction(e -> errorStage.close());
    VBox.setMargin(closeButton, new Insets(0, 10, 15, 10));

    errorBox.getChildren().addAll(errorLabel, closeButton);

    Scene errorScene = new Scene(errorBox);
    errorScene.getStylesheets().add("mycomposer/view/Popup.css");

    errorStage.setTitle("Error");
    errorStage.setScene(errorScene);
    errorStage.getIcons().add(new Image("/resources/icon.png"));

    errorStage.initOwner(stageOwner);
    errorStage.setResizable(true);
    errorStage.initStyle(StageStyle.DECORATED);
    errorStage.initModality(Modality.WINDOW_MODAL);

    errorStage.show();
    errorStage.setWidth(errorLabel.getWidth() + 60);
  }
}
