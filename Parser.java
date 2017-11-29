import java.io.BufferedReader;
import java.io.IOException;
import java.nio.charset.Charset;
import java.nio.file.Files;
import java.nio.file.Paths;
import java.util.HashSet;
import java.util.Set;
import java.util.regex.Pattern;


public class Parser {

    private HashSet<String> letters = new HashSet<>();
    private final Pattern spacePat = Pattern.compile(" ");
    private final Pattern csvPat = Pattern.compile(",");
    private HashSet<String> paramsSeen = new HashSet<>();
    private int uniqueSuffix = 0;

    public Parser()
    {
        fill(letters, 'A', 'Z');
        fill(letters, 'a', 'z');
    }

    public KB parseKb(String filename)
    {
        String line;
        HashSet<Sentence> sentences = new HashSet<>();
        HashSet<Sentence> refuted = new HashSet<>();

        try (BufferedReader reader = Files.newBufferedReader(Paths.get(filename), Charset.defaultCharset()))
        {
            while ((line = reader.readLine()) != null)
            {
                if (!line.equals(""))
                {
                    sentences.add(parseSentence(line));
                }
                else
                {
                    //if we hit a newline in the middle, read the next line for the refuted part
                    refuted.add(parseSentence(reader.readLine()));
                }
            }
        }
        catch (IOException ex)
        {
            System.err.println("Error parsing file");
        }
        return new KB(sentences, refuted);
    }

    public Sentence parseSentence(String line)
    {
        HashSet<Predicate> preds = new HashSet<>();

        line = line.replaceAll("\\s+(?=[^()]*\\))", "");
        String[] splitPreds = spacePat.split(line);
        for (String pred : splitPreds)
        {
            preds.add(parsePred(pred));
        }
        return new Sentence(preds);
    }

    public Predicate parsePred(String pred)
    {
        boolean neg = false;
        StringBuilder name = new StringBuilder();
        String[] args = null;
        pred = pred.replaceAll(" ", "");

        for (int i = 0; i < pred.length() && pred.charAt(i) != '.'; i++)
        {
            if (i == 0 && pred.charAt(i) == '!')
            {
                neg = true;
            }
            else if (letters.contains(pred.charAt(i) + ""))
            {
                name.append(pred.charAt(i));
            }
            else if (pred.charAt(i) == '(')
            {
                String paramList = pred.substring(i+1, pred.indexOf(')'));
                args = csvPat.split(paramList);
                break;
            }
            else
            {
                System.err.println("Some provided predicate does not conform to the rules of the grammar. Exiting.");
                System.exit(1);
            }
        }
        return new Predicate(neg, name.toString(), args);
    }

    private void fill(Set<String> s, char lo, char hi)
    {
        for (char c = lo; c <= hi; c++)
        {
            s.add(c + "");
        }
    }
}
