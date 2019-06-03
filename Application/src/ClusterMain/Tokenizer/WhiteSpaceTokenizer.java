package ClusterMain.Tokenizer;

import java.text.BreakIterator;
import java.util.ArrayList;
import java.util.Locale;

/*
    <Created Date> Nov 20, 2018 </CreatedDate>
    <About>
        A simple WhiteSpaceTokenizer Tokenizer. Returns set of tokens considering white spaces as stop chars.
        BreakIterator Code snippet is ref. from samples at: https://docs.oracle.com/javase/7/docs/api/java/text/BreakIterator.html
    </About>
*/
//TODO: EXTEND THIS CLASS TO HANDLE NOISE WORDS AS WELL.
public class WhiteSpaceTokenizer {


    BreakIterator _iter = null;

    public WhiteSpaceTokenizer() {
        Locale currentLocale = new Locale("en", "US");
        _iter = BreakIterator.getWordInstance(currentLocale);
    }

    public WhiteSpaceTokenizer(Locale l) {
        _iter = BreakIterator.getWordInstance(l);
    }

    public ArrayList<String> extractTokens(String text, boolean lowercase) {

        ArrayList<String> tokens = new ArrayList<String>();

        _iter.setText(text);
        int start = _iter.first();
        int end = _iter.next();

        while (end != BreakIterator.DONE) {
            String word = text.substring(start, end);
            //Only select if the character is letter or digit that is ignore the white space.
            if (Character.isLetterOrDigit(word.charAt(0))) {
                if (lowercase)
                    word = word.toLowerCase();
                tokens.add(word);
            }
            start = end;
            end = _iter.next();
        }

        return tokens;
    }


    public static void main(String[] args) {

        String test = "TEST If the tokenizer is WORKING.";
        System.out.println("Sentence: " + test);

        WhiteSpaceTokenizer st = new WhiteSpaceTokenizer();
        ArrayList<String> tokens = st.extractTokens(test, true);
        System.out.println("Tokens:");
        for (String token : tokens) {
            System.out.println("'" + token + "'");
        }
    }
}


