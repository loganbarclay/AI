import java.util.Collections;
import java.util.HashSet;
import java.util.LinkedList;
import java.util.List;


public class Prover {
    private static int resolutionCount = 0;
    private static long timeStart = 0;
    
    /**
     * @param args the command line arguments
     */
    public static void main(String[] args)
    {
        Parser parser = new Parser();
        KB kb = parser.parseKb(args[0]);
        System.out.println(kb.toString());

        kb = kb.uniqueVariables();
        //the variable names should all be unique 
        //and because of my hashing scheme there should be no duplicate literals in a sentence and no duplicate sentences
        
        //i am timing the amount of time required to complete the proof, not the parsing
        timeStart = System.currentTimeMillis();
        HashSet<Sentence> support = resolve(kb.getSentences(), kb.getRefuted());
        if (support == null)
        {
            System.out.println("failure");
            System.exit(0);
        }
        
        //if support ever == null, there is no proof
        while (support != null)
        {
            support = resolve(kb.getSentences(), support);
        }
        System.out.println("failure");
        System.exit(0);
    }

    public static HashSet<Sentence> resolve(HashSet<Sentence> sentencesSet, HashSet<Sentence> supportSet)
    {
        List<Sentence> sentences = new LinkedList<>(sentencesSet);
        List<Sentence> support = new LinkedList<>(supportSet);
        Collections.sort(sentences);
        Collections.sort(support);

        Sentence result;
        for (Sentence supporting : support)
        {
            for (Sentence sentence : sentences)
            {
                result = supporting.resolve(sentence);
                if (result != null)
                {
                    if (result.isGoal())
                    {
                        System.out.println(result.derivedFrom("") + "\n");
                        System.out.println("********* Heuristic results *********");
                        System.out.println("Resolutions required: " + ++resolutionCount);
                        System.out.println("Time required (ms): " + (System.currentTimeMillis() - timeStart));
                        System.out.println("*************************************");
                        System.exit(0);
                    }
                    else if (supportSet.add(result))
                    {
                        //this is the number of successful resolutions that contributed to the set of support
                        ++resolutionCount;
                        return supportSet;
                    }
                }
            }
        }
        return null;
    }
}
