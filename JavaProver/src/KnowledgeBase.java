import java.util.HashMap;
import java.util.HashSet;

public class KnowledgeBase {
    
    private HashSet<Sentence> sentences;
    private HashSet<Sentence> refuted;
    private static int uniqueifier = 0;
    private HashSet<String> paramsSeen = new HashSet<>();

    public KnowledgeBase(HashSet<Sentence> sentences, HashSet<Sentence> refuted)
    {
        this.sentences = sentences;
        this.refuted = refuted;
    }
    
    @Override
    public String toString()
    {
    	String retVal = "";
        StringBuilder builder = new StringBuilder();
        for (Sentence sentence : sentences)
        {
            
        	retVal += sentence.toString();
        	builder.append(sentence.toString());
        }
        retVal += "\n";
        builder.append("\n");
        for (Sentence refutedPredicate : refuted)
        {
        	retVal += refutedPredicate.toString();
            builder.append(refutedPredicate.toString());
        }
        return retVal;
    }

    public KnowledgeBase uniqueVariables()
    {
        paramsSeen.clear();
        return new KnowledgeBase(uniqueify(sentences), uniqueify(refuted)); 
    }
    
    private HashSet<Sentence> uniqueify(HashSet<Sentence> sentences)
    {
        for (Sentence sentence : sentences)
        {
            HashSet<String> predsParams = new HashSet<>();
            HashMap<String, String> changedNames = new HashMap<>();
            for (Predicate p : sentence.getPreds())
            {
                for (String string : p.getParams())
                {
                    predsParams.add(string);
                }
                for (String string : predsParams)
                {
                    if (paramsSeen.contains(string) && !changedNames.containsKey(string) && !Param.isConst(string))
                    {
                        changedNames.put(string, string+uniqueifier++);
                    }
                }
            }
            for (Predicate p : sentence.getPreds())
            {
                String[] params = p.getParams();
                for (int i = 0; i < params.length; i++)
                {
                    String param = params[i];
                    if (changedNames.get(param) != null && !Param.isConst(param))
                    {
                        params[i] = changedNames.get(param);
                    }
                }
            }
            paramsSeen.addAll(predsParams);
        }
        return sentences;
    }

    public HashSet<Sentence> getSentences()
    {
        return sentences;
    }

    public HashSet<Sentence> getRefuted()
    {
        return refuted;
    }
}
