
import java.io.BufferedReader;
import java.io.File;
import java.io.FileReader;
import java.io.IOException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

/* Brainstorming Section

-l = counts number of lines
-w counts the number of words
-c counts the number of bytes
-m counts the number of characters
-L displays the length

    1. Last element of args will be file name.
    2. Last element of args must have (.).
    3. If there's no file but there's command, prompt that Provide file.
    4. If everything is correct then read the commands and call the respective functions.
    5. If there's no command provided then execute all functions.

     
 */

public class WCTool {

    public static void main(String[] args) {
        if (args.length == 0) {
            displayHelp();
            return;
        }
        boolean countLines = false, countWords = false, countChars = false, countBytes = false, displayLength = false;

        List<String> fileNames = new ArrayList<>();
        for (String arg : args) {
            switch (arg) {
                case "-l":
                    countLines = true;
                    break;
                case "-w":
                    countWords = true;
                    break;
                case "-c":
                    countChars = true;
                    break;
                case "-b":
                    countBytes = true;
                    break;
                case "-L":
                    displayLength = true;
                    break;
                default:
                    fileNames.add(arg);
                    break;
            }
        }
        if (!countLines && !countWords && !countChars && !countBytes && !displayLength) {
            countLines = countWords = countChars = countBytes = displayLength = true;
        }

        FileProcessor processor = new FileProcessor();
        for (String fileName : fileNames) {
            try {
                File file = new File(fileName);
                if (!file.exists()) {
                    System.err.println("File not found: " + fileName);
                    continue;
                }
                long startTime = System.nanoTime();
                Map<String, Integer> result = processor.processFile(file, countLines, countWords, countChars,
                        countBytes, displayLength);
                long endTime = System.nanoTime();
                long elapsedTime = endTime - startTime;
                System.out.println(formatOutput(fileName, result));
                System.out.println(elapsedTime);
            } catch (IOException e) {
                System.err.println("Error processing file: " + fileName);
            }
        }

    }

    private static String formatOutput(String fileName, Map<String, Integer> result) {
        StringBuilder output = new StringBuilder();
        output.append(fileName).append(": ");
        if (result.containsKey("lines")) {
            output.append("Lines: ").append(result.get("lines")).append(" ");
        }
        if (result.containsKey("words")) {
            output.append("Words: ").append(result.get("words")).append(" ");
        }
        if (result.containsKey("chars")) {
            output.append("Characters: ").append(result.get("chars")).append(" ");
        }
        if (result.containsKey("bytes")) {
            output.append("Bytes: ").append(result.get("bytes")).append(" ");
        }
        if (result.containsKey("length")) {
            output.append("Longest Line Length: ").append(result.get("length"));
        }
        return output.toString();
    }

    private static void displayHelp() {
        System.out.println("Usage: java WCTool [options] [file...]");
        System.out.println("Options:");
        System.out.println(" -l : Count Lines");
        System.out.println(" -w : Count Words");
        System.out.println(" -c : Count Characters");
        System.out.println(" -b : Count Bytes");
        System.out.println(" -L : Display longest line length");

    }
}

class FileProcessor {
    public Map<String, Integer> processFile(File file, boolean countLines, boolean countWords, boolean countChars,
            boolean countBytes, boolean displayLength)
            throws IOException {
        Map<String, Integer> counts = new HashMap<>();
        try (BufferedReader reader = new BufferedReader(new FileReader(file))) {
            String line;
            int lineCount = 0, wordCount = 0, charCount = 0, byteCount = 0, maxLength = 0;
            while ((line = reader.readLine()) != null) {
                if (countLines)
                    lineCount++;
                if (countWords)
                    wordCount += WCUtils.countWordsInLine(line);
                if (countChars)
                    charCount += line.length();
                if (countBytes)
                    byteCount += line.getBytes().length;
                if (displayLength)
                    maxLength = Math.max(maxLength, line.length());
            }
            reader.close();
            if (countLines)
                counts.put("lines", lineCount);
            if (countWords)
                counts.put("words", wordCount);
            if (countChars)
                counts.put("chars", charCount);
            if (countBytes)
                counts.put("bytes", byteCount);
            if (displayLength)
                counts.put("length", maxLength);
        }
        return counts;
    }
}

class WCUtils {
    public static int countWordsInLine(String line) {
        int wordCount = 0;
        boolean inWord = false;
        for (char c : line.toCharArray()) {
            if (Character.isWhitespace(c)) {
                if (inWord){
                    wordCount++;
                }
                inWord = false;
            } else {
                inWord = true;
            }
        }
        if (inWord)
            wordCount++; 
        return wordCount;

        // String[] words = line.trim().split("\\s+");
        // return words.length;
    }
    
}
