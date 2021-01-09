import opennlp.tools.namefind.NameFinderME;
import opennlp.tools.namefind.TokenNameFinderModel;
import opennlp.tools.sentdetect.SentenceDetector;
import opennlp.tools.sentdetect.SentenceDetectorME;
import opennlp.tools.sentdetect.SentenceModel;
import opennlp.tools.tokenize.Tokenizer;
import opennlp.tools.tokenize.TokenizerME;
import opennlp.tools.tokenize.TokenizerModel;
import opennlp.tools.util.Span;
import org.jsoup.Jsoup;

import java.io.IOException;
import java.net.MalformedURLException;
import java.net.URL;
import java.util.Arrays;

public class Main {
    public static void main(String[] args) {
        if (args.length != 1) {
            System.out.println("Name finder only works with just one argument which is URL of the page that you want to be searched for names in its body." +
                    "\nPlease, re-type your arguments as example: " +
                    "\n for Windows NT -> 'java -jar target\\bim207hw2.jar https://opennlp.apache.org/books-tutorials-and-talks.html'" +
                    "\n for UNIX -> 'java -jar target/bim207hw2.jar https://opennlp.apache.org/books-tutorials-and-talks.html'");
            System.exit(0);
        } else {
            if (!isURL(args[0])) System.exit(0);
            try {
                String bodyText = Jsoup.connect(args[0]).get().body().toString().replace("\"", ""); // we're getting body html code of the whole page without "s to prevent misunderstandings like 'Brand "' due to tokenizer can only tokenize <NOTATION> or " notation at one time
                tokenizeAndFind(bodyText); // after that we have to tokenize and find the names in the given text to print them
            } catch (IOException e) {
                System.out.println("An unknown error has occurred during the process, please try again.");
            }
        }
    }

    public static void tokenizeAndFind(String bodyText) throws IOException { // to find names in a given text we have to tokenize the text
        URL tokenizerModel = Main.class.getClassLoader().getResource("en-token.bin"); // path of trained tokenizer model for English
        URL nameFinderModel = Main.class.getClassLoader().getResource("en-ner-person.bin"); // path of trained person name finder model for English
        URL sentenceDetectionModel = Main.class.getClassLoader().getResource("en-sent.bin"); // path of trained sentence detection model for English

        SentenceModel sentenceModel = new SentenceModel(sentenceDetectionModel); // we're just creating our trained model
        SentenceDetector sentenceDetector = new SentenceDetectorME(sentenceModel); // we re creating a maximum-entropy-based sentence detector class from our model
        String[] sentences = sentenceDetector.sentDetect(bodyText); // and we're detecting sentences based on the text given to use them later

        TokenizerModel modelTokenizer = new TokenizerModel(tokenizerModel); // we're just creating our trained model
        Tokenizer tokenizer = new TokenizerME(modelTokenizer); // we are creating a maximum-entropy-based tokenizer class from our model
        String[] tokens = tokenizer.tokenize(Arrays.toString(sentences)); // tokenizer tokenizes our text we'll use this tokens to find our name spans

        TokenNameFinderModel modelnameFinder = new TokenNameFinderModel(nameFinderModel); // we're just creating our trained model
        NameFinderME nameFinder = new NameFinderME(modelnameFinder); // we are creating a maximum-entropy-based name finder class from our model
        Span[] nameSpans = nameFinder.find(tokens); // we've finally ready to find our name spans but not our names

        for (Span nameSpan : nameSpans) // rest is just printing the names according to found name spans
            System.out.println(tokens[nameSpan.getStart()] + " " + tokens[nameSpan.getStart() + 1]);
    }

    public static boolean isURL(String URL) { // to prevent misspelling URLs and the probable errors that we could get due to wrong URL
        try {
            URL tempURL = new URL(URL);
            return true;
        } catch (MalformedURLException e) {
            System.out.println("Given URL is not valid. Please check and retype.");
            return false;
        }
    }
}