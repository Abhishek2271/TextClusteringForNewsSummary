package ClusterMain.Tokenizer;

import opennlp.tools.postag.POSModel;
import opennlp.tools.postag.POSTaggerME;
import opennlp.tools.sentdetect.SentenceDetector;
import opennlp.tools.sentdetect.SentenceDetectorME;
import opennlp.tools.sentdetect.SentenceModel;
import opennlp.tools.tokenize.WhitespaceTokenizer;

import java.io.FileInputStream;
import java.io.IOException;
import java.io.InputStream;
import java.util.ArrayList;
import java.util.List;

public class POSTagging
{

    POSTaggerME tagger = null;
    POSModel model = null;
    String rootLocation = System.getProperty("user.dir") + "\\src\\nlp\\";

    public void initialize(String lexiconFileName)
    {
        try
        {
            InputStream modelStream =  new FileInputStream(lexiconFileName);
            model = new POSModel(modelStream);
            tagger = new POSTaggerME(model);
        }
        catch (IOException e)
        {
            System.out.println(e.getMessage());
        }
    }

    public List<String> tag(String text, boolean FilterStopWords)
    {
        initialize(rootLocation + "en-pos-maxent.bin");
        List<String> TokenList = new ArrayList<>();
        try
        {
            if (model != null) {
                POSTaggerME tagger = new POSTaggerME(model);
                if (tagger != null) {
                    String[] sentences = detectSentences(text);
                    for (String sentence : sentences) {
                        String whitespaceTokenizerLine[] = WhitespaceTokenizer.INSTANCE
                                .tokenize(sentence);
                        String[] tags = tagger.tag(whitespaceTokenizerLine);
                        for (int i = 0; i < whitespaceTokenizerLine.length; i++) {
                            String word = whitespaceTokenizerLine[i].trim();
                            String tag = tags[i].trim();
                            //System.out.print(tag + ":" + word + "  ");
                            if(tag.toLowerCase() == "in" || tag.toLowerCase() == "dt" || tag.toLowerCase() == "uh"|| tag.toLowerCase() == "prp" || tag.toLowerCase() == "to"
                                    || tag.toLowerCase() == "rp" || tag.toLowerCase() == "cc" || tag.toLowerCase() == "pdt" || tag.toLowerCase() == "wdt"  || tag.toLowerCase() == "wp"  || tag.toLowerCase() == "wrb")
                                continue;
                            else
                                if(FilterStopWords)
                                {
                                    //Still use stop word filter to filter noun entites also which are not informative in a token set like: google, name of days etc
                                    StopWordChecker stopWordChecker = new StopWordChecker();
                                    if(!stopWordChecker.isStopWord(word.toLowerCase()))
                                        TokenList.add(word);
                                }
                                else
                                    TokenList.add(word);
                        }
                    }
                }
            }
        }
        catch (Exception e) {
            e.printStackTrace();
        }
        return TokenList;
    }

    public void tagfromtoken(String[] InputTokens){
        initialize(rootLocation + "en-pos-maxent.bin");
        try
        {
            if (model != null)
            {
                POSTaggerME tagger = new POSTaggerME(model);
                if (tagger != null) {


                        String[] tags = tagger.tag(InputTokens);
                        for (int i = 0; i < InputTokens.length; i++)
                        {
                            String word = InputTokens[i].trim();
                            String tag = tags[i].trim();
                            System.out.print(tag + ":" + word + "  ");
                        }

                }
            }
        }
        catch (Exception e)
        {
            e.printStackTrace();
        }
    }

    public String[] detectSentences(String paragraph) throws IOException
    {

        InputStream modelIn = new FileInputStream(rootLocation + "pt-sent.bin");
        final SentenceModel sentenceModel = new SentenceModel(modelIn);
        modelIn.close();

        SentenceDetector sentenceDetector = new SentenceDetectorME(sentenceModel);
        String sentences[] = sentenceDetector.sentDetect(paragraph);
        /*for (String sent : sentences) {
            System.out.println(sent);
        }*/
        return sentences;
    }
}
