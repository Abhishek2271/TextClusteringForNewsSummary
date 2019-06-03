package ClusterMain.Tokenizer;

import java.io.InputStream;
import java.io.IOException;
import opennlp.tools.namefind.NameFinderME;
import opennlp.tools.namefind.TokenNameFinderModel;
import opennlp.tools.tokenize.TokenizerME;
import opennlp.tools.tokenize.TokenizerModel;
import opennlp.tools.util.InputStreamFactory;
import opennlp.tools.util.Span;
import java.io.FileInputStream;

public class NamedEntityTokenizer
{
    String rootLocation = System.getProperty("user.dir") + "\\src\\nlp\\";

    public void Name_Entities(String[] InputTokens)
    {
        try
        {
            InputStream inputStreamNameFinder = new FileInputStream(rootLocation + "\\nl-ner-person.bin");
            TokenNameFinderModel model = new TokenNameFinderModel(inputStreamNameFinder);

            NameFinderME locFinder = new NameFinderME(model);
            //String[] tokens = tokenize("This is Jack");


            Span nameSpans[] = locFinder.find(InputTokens);
            for (Span span : nameSpans)
                System.out.println("Position - " + span.toString() + "    Name - " + InputTokens[span.getStart()]);
        }
        catch(Exception exp)
        {
            String message = exp.getMessage();
            System.out.println(message);
        }
    }

    public void Org_Entities(String[] InputTokens)
    {
        try
        {
            InputStream inputStreamNameFinder = new FileInputStream(rootLocation + "\\nl-ner-organization.bin");
            TokenNameFinderModel model = new TokenNameFinderModel(inputStreamNameFinder);

            NameFinderME locFinder = new NameFinderME(model);
            Span nameSpans[] = locFinder.find(InputTokens);
            for (Span span : nameSpans)
                System.out.println("Position - " + span.toString() + "    organisation - " + InputTokens[span.getStart()]);
        }
        catch (Exception exp)
        {
            String message = exp.getMessage();
            System.out.println(message);
        }
    }

    public void Loc_Entities(String[] InputTokens)
    {
        try {
            InputStream inputStreamNameFinder = new FileInputStream(rootLocation + "\\nl-ner-location.bin");
            TokenNameFinderModel model = new TokenNameFinderModel(inputStreamNameFinder);

            NameFinderME locFinder = new NameFinderME(model);
            Span nameSpans[] = locFinder.find(InputTokens);
            for (Span span : nameSpans)
                System.out.println("Position - " + span.toString() + "    LocationName - " + InputTokens[span.getStart()]);
        } catch (Exception exp) {
            String message = exp.getMessage();
            System.out.println(message);
        }
    }


    public String[] tokenize(String sentence) throws IOException{
        InputStream inputStreamTokenizer = new FileInputStream(rootLocation +"\\nl-token.bin");
        TokenizerModel tokenModel = new TokenizerModel(inputStreamTokenizer);
        TokenizerME tokenizer = new TokenizerME(tokenModel);
        return tokenizer.tokenize(sentence);
    }

}
