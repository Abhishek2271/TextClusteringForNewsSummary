package ClusterMain.Tokenizer;

import org.tartarus.snowball.*;
import org.tartarus.snowball.ext.*;

public class SnowballStemmer {

    public int STEM_REPEAT_TIMES = 1; // How many times to repeat stemming

    public englishStemmer _stemmer;

    public SnowballStemmer() {
        _stemmer = new englishStemmer();
    }

    public SnowballStemmer(int stem_repeat_times) {
        STEM_REPEAT_TIMES = stem_repeat_times;
    }

    public String stem(String s) {
        _stemmer.setCurrent(s);
        for (int i = STEM_REPEAT_TIMES; i != 0; i--) {
            _stemmer.stem();
        }
        return _stemmer.getCurrent();
    }

    public static void main(String args[]) {
        SnowballStemmer sbs = new SnowballStemmer();
        Test(sbs, "organise");
        Test(sbs, "organisation");
        Test(sbs, "exploded");
        Test(sbs, "explosion");
        Test(sbs, "search");
        Test(sbs, "research");
        Test(sbs, "buses");
    }

    public static void Test(SnowballStemmer sbs, String s) {
        System.out.println("'" + s + "' stem: '" + sbs.stem(s) + "'");
    }

}
