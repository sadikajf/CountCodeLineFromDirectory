import java.io.BufferedReader;
import java.io.FileReader;
import java.io.IOException;
import java.util.*;

public class DirectoryCodeLineCounter {

  private static final String EMPTY_STRING = "";
  private static final String DOUBLE_SLASH_STRING = "//";
  private static final String STAR_SLASH_STRING = "*/";
  private static final String SLASH_STAR_STRING = "/*";
  private static final String QUOTE_STRING = "\"";

  public static void main(String[] args) {
    Scanner scanner = new Scanner(System.in);
    System.out.print("Please enter root directory: ");
    String file = scanner.nextLine();
    System.out.print("The file contains " + countLineBufferedReader(file) + " lines of code!");
  }

  public static long countLineBufferedReader(String fileName) {

    int count = 0;
    boolean commentStarted = false;
    String line = null;
    try (BufferedReader reader = new BufferedReader(new FileReader(fileName))) {
      while ((line = reader.readLine()) != null) {
        line = line.trim();
        if (EMPTY_STRING.equals(line) || line.startsWith(DOUBLE_SLASH_STRING)) {
          continue;
        }
        if (commentStarted) {
          if (commentEnded(line)) {
            line = line.substring(line.indexOf(STAR_SLASH_STRING) + 2).trim();
            commentStarted = false;
            if (EMPTY_STRING.equals(line) || line.startsWith(DOUBLE_SLASH_STRING)) {
              continue;
            }
          } else {
            continue;
          }
        }
        if (isSourceCodeLine(line)) {
          count++;
        }
        if (commentStarted(line)) {
          commentStarted = true;
        }
      }
    } catch (IOException e) {
      e.printStackTrace();
    }
    return count;
  }

  private static boolean commentStarted(String line) {
    // it returns true if line = /* */ /*
    // it returns false if line = /* */
    int index = line.indexOf(SLASH_STAR_STRING);
    if (index < 0) {
      return false;
    }
    int quoteStartIndex = line.indexOf(QUOTE_STRING);
    if (quoteStartIndex != -1 && quoteStartIndex < index) {
      while (quoteStartIndex > -1) {
        line = line.substring(quoteStartIndex + 1);
        int quoteEndIndex = line.indexOf("\"");
        line = line.substring(quoteEndIndex + 1);
        quoteStartIndex = line.indexOf("\"");
      }
      return commentStarted(line);
    }
    return !commentEnded(line.substring(index + 2));
  }

  private static boolean commentEnded(String line) {
    // it returns true if line = */ /* */
    // it returns false if line = */ /*
    int index = line.indexOf(STAR_SLASH_STRING);
    if (index < 0) {
      return false;
    } else {
      String subString = line.substring(index + 2).trim();
      if (EMPTY_STRING.equals(subString) || subString.startsWith(DOUBLE_SLASH_STRING)) {
        return true;
      }
      if (commentStarted(subString)) {
        return false;
      } else {
        return true;
      }
    }
  }

  private static boolean isSourceCodeLine(String line) {
    line = line.trim();
    if (EMPTY_STRING.equals(line) || line.startsWith(DOUBLE_SLASH_STRING)) {
      return false;
    }
    if (line.length() == 1) {
      return true;
    }
    int index = line.indexOf(SLASH_STAR_STRING);
    if (index != 0) {
      return true;
    } else {
      while (line.length() > 0) {
        line = line.substring(index + 2);
        int endCommentPosition = line.indexOf(STAR_SLASH_STRING);
        if (endCommentPosition < 0) {
          return false;
        }
        if (endCommentPosition == line.length() - 2) {
          return false;
        } else {
          String subString = line.substring(endCommentPosition + 2)
              .trim();
          if (EMPTY_STRING.equals(subString) || subString.indexOf(DOUBLE_SLASH_STRING) == 0) {
            return false;
          } else {
            if (subString.startsWith(SLASH_STAR_STRING)) {
              line = subString;
              continue;
            }
            return true;
          }
        }
      }
    }
    return false;
  }
}
