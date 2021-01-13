package mycomposer.model;

import java.util.regex.Matcher;
import java.util.regex.Pattern;

/**
 * Represents the colors which can be used in the composer to color the layers in a song.
 */
public enum LayerColor {
  Pink("#FA9DCD"), Red("#F7394C"), Orange("#F77A31"), Yellow("#FFEE51"), Green("#C6F679"),
  Mint("#77E6A9"), Cyan("#40D0FF"), Violet("#8D56E8");

  private final String hexCode;

  /**
   * Constructs a layer color with the given color given as a 6 digit hex code.
   *
   * @param hexCode the color to set this layer to (given as a 6 digit hex code)
   * @throws IllegalArgumentException if the given color is not a valid 6 digit hex color code
   */
  LayerColor(String hexCode) throws IllegalArgumentException {
    validateHexCode(hexCode);
    this.hexCode = hexCode;
  }

  /**
   * Gets the 6 digit hex code associated with this layer color.
   *
   * @return the 6 digit hex code associated with this layer color
   */
  public String getHexCode() {
    return this.hexCode;
  }

  /**
   * Returns the hex code for either black or white based on which color is more readable on the
   * this layer color. Uses luminance equation from https://sites.cs.ucsb.edu/~yfwang/courses/cs181b/ps/Dis6.pdf.
   *
   * @return either {@code "#FFFFFF"} (white) or {@code "#000000"} (black) based on which of the two
   * colors is more readable on this layer color
   */
  public String bestContrast() {
    String hexCode = this.getHexCode();

    int red = Integer.valueOf(hexCode.substring(1, 3), 16);
    int green = Integer.valueOf(hexCode.substring(3, 5), 16);
    int blue = Integer.valueOf(hexCode.substring(5, 7), 16);

    double lum = 0.299 * red + 0.587 * green + 0.114 * blue;

    if (lum < 139) {
      return "#FFFFFF"; // white
    } else {
      return "#000000"; // black
    }
  }


  /**
   * Checks if the given string is a valid 6 digit hex color code. Does nothing if the string is a
   * valid 6 digit hex code, otherwise throws an exception.
   *
   * @param hexCode the hex code to validate
   * @throws IllegalArgumentException if the given color is not a valid 6 digit hex color code
   */
  public static void validateHexCode(String hexCode) throws IllegalArgumentException {
    String regex = "^#[A-Fa-f0-9]{6}";

    Pattern pattern = Pattern.compile(regex);
    Matcher matcher = pattern.matcher(hexCode);

    if (!matcher.matches()) {
      throw new IllegalArgumentException("Given color must be a valid 6 digit hex color code.");
    }
  }
}
