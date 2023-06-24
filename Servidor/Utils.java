import java.io.BufferedReader;
import java.io.FileReader;
import java.io.IOException;
import java.text.Normalizer;
import java.util.HashMap;
import java.util.Map;
import java.util.HashMap;
import java.util.regex.Pattern;
import java.io.File;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

public class Utils {

    public static String cleanWord(String word) {
        String cleanedWord = word.replaceAll("[^\\p{L}\\p{N}\\s]", "");
        cleanedWord = Normalizer.normalize(cleanedWord, Normalizer.Form.NFD)
                .replaceAll("\\p{M}", "");
        cleanedWord = cleanedWord.toLowerCase();
        return cleanedWord;
    }

    public static int getCountFiles(String pathBook) {
        File directory = new File(pathBook);
        File[] files = directory.listFiles();
        int count = 0;
        if (files != null) {
            for (File file : files) {
                if (file.isFile()) {
                    count++;
                }
            }
        }
        
        return count;
    }

    public static Map<String, Integer> getWords(String filePath) {
        Map<String, Integer> words = new HashMap<>();
        try (BufferedReader reader = new BufferedReader(new FileReader(filePath))) {
            String line;
            while ((line = reader.readLine()) != null) {
                String[] wordsInLine = line.trim().split("\\s+");
                for (String word : wordsInLine) {
                    String cleanedWord = cleanWord(word);
                    int frequency = words.getOrDefault(cleanedWord, 0);
                    words.put(cleanedWord, frequency + 1);
                }
            }
        } catch (IOException e) {
            e.printStackTrace();
        }
        return words;
    }

    public static String[] getAllFiles(String path){
        File directory = new File(path);
        File[] files = directory.listFiles();

        if (files != null) {
            String[] documents = new String[files.length];
            for (int i = 0; i < files.length; i++) {
                if (files[i].isFile()) {
                    documents[i] = files[i].getName();
                }
            }
            return documents;
        } else {
            return new String[0];
        }
    }

    public static int wordFrequencyInAllFiles(String pathBooks,String word){

        String [] files = getAllFiles(pathBooks);
        int count = 0;
        for (String file:files){
            Map<String, Integer> words = getWords(pathBooks + file);
            int frequency = words.getOrDefault(word, 0);
            if(frequency > 0){
                count++;
            }
        }

        return count;
    }

    public static String[] getCleanArray(String text){
        String[] words = text.split(" ");
        String[] result = new String[words.length];
        for (int i = 0; i < words.length; i++) {
            result[i] = cleanWord(words[i]);
        }
        return result;
    }

    public static double MiningText(String pathFile,String text,String pathBook){
        String [] arrayCleanWords = getCleanArray(text);
        Map<String, Integer> words = getWords(pathFile);
        int countWords = words.keySet().size();
        int countFiles = getCountFiles(pathBook);
        double factor = 0;
        for (int i = 0; i<arrayCleanWords.length; i++){
            int frequency = words.getOrDefault(arrayCleanWords[i], 0);
            double result = (double) frequency / countWords;
            double itf = (double) countFiles / wordFrequencyInAllFiles(pathBook, arrayCleanWords[i]);
            double total = result * Math.log10(itf);
            //System.out.println(total);
            factor+= total;
        }
        return factor;
    }

    public static String getMaxValueFromAHashMap(Map <String,Double> hashMap){
        String maxKey = null;
        double maxValue = Double.MIN_VALUE;
        for (Map.Entry <String,Double> entry: hashMap.entrySet()){
            String key = entry.getKey();
            double value = entry.getValue();

            if (value > maxValue){
                maxValue = value;
                maxKey = key;
            }

        }

        return maxKey;

    }

    public static List<List<String>> getIntervals(String[] allFiles, int number) {
        List<List<String>> intervals = new ArrayList<>();
        int index = 0;

        while (index < allFiles.length) {
            List<String> interval = Arrays.asList(Arrays.copyOfRange(allFiles, index, Math.min(index + number, allFiles.length)));
            intervals.add(interval);
            index += number;
        }

        return intervals;
    }

    public static void main(String[] args) {
        /* 
        String pathBooks = "books/";
        System.out.println(wordFrequencyInAllFiles(pathBooks, "capitulo"));

        String chain = "capitulo CAjas bótin";
        String [] clean = getCleanArray(chain);
        for (String word: clean){
            System.out.println(word);
        }
        */
        Map <String,Double> results = new HashMap <>();
        String pathBook = "books/";
        String [] files = getAllFiles(pathBook);
        for (String file: files){
            String filePath = pathBook + file;
            double value = MiningText(filePath, "Hermanas AMOR hogar crecimiento", pathBook);
            results.put(file, value);
            //results.put(filePath,MiningText(file, "capitulo CAjas bótin", pathBook));

        }

        String key = getMaxValueFromAHashMap(results);
        System.out.println(key);


        //System.out.println(MiningText("books/Adler_Olsen,_Jussi__1997_._La_casa_del_alfabeto_[7745].txt", "capitulo CAjas bótin","books/"));
        
        
        

        // Imprimir el contenido del HashMap
        /* 
        for (Map.Entry<String, Integer> entry : words.entrySet()) {
            String word = entry.getKey();
            Integer frequency = entry.getValue();
            System.out.println("Palabra: " + word + ", Frecuencia: " + frequency);
        }
        */

        
    }
}
