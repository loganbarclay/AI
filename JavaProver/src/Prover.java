import java.io.File;
import java.io.FileNotFoundException;
import java.util.Collections;
import java.util.HashSet;
import java.util.LinkedList;
import java.util.List;


public class Prover {
    private static int numResolutions = 0;
    private static long start = 0;
    private static long finish = 0;
    
    /**
     * @param args the command line arguments
     * @throws FileNotFoundException 
     */
    public static void main(String[] args) throws FileNotFoundException
    {	
    	File inputFile = new File(args[0]);
        Parser parser = new Parser();
        KnowledgeBase kb = parser.fillKnowledgeBase(inputFile);
        System.out.println(kb.toString());

        kb = kb.uniqueVariables();
        //the variable names should all be unique 
        //and because of my hashing scheme there should be no duplicate literals in a sentence and no duplicate sentences
        
        //i am timing the amount of time required to complete the proof, not the parsing
        start = System.currentTimeMillis();
        HashSet<Sentence> support = resolve(kb.getSentences(), kb.getRefuted());
        if (support == null)
        {
            System.out.println("failure");
            System.exit(0);
        }

        while (support != null)
        {
            support = resolve(kb.getSentences(), support);
        }
        System.out.println("failure");
        System.exit(0);
    }

    @SuppressWarnings("unchecked")
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
                    	finish = System.currentTimeMillis();
                    	numResolutions++;
                        System.out.println(result.getParents("") + "\n");
                        System.out.println("===============================");
                        System.out.println("===== Heuristic results =======");
                        System.out.println("Resolutions: " + numResolutions);
                        System.out.println("Time: " + (finish - start) + " milliseconds");
                        System.out.println("===============================");
                        System.exit(0);
                    }
                    else if (supportSet.add(result))
                    {
                        
                        numResolutions++;
                        return supportSet;
                    }
                }
            }
        }
        return null;
    }
}
