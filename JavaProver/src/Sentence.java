import java.util.Arrays;
import java.util.HashSet;
import java.util.PriorityQueue;


public class Sentence implements Comparable {

    private PriorityQueue<Predicate> predicateQueue;
    private Sentence firstParent;
    private Sentence secondParent;
    private String substitution = null;

    public Sentence(PriorityQueue<Predicate> preds)
    {
        this.predicateQueue = preds;
    }
    
    public Sentence(PriorityQueue<Predicate> preds, Sentence p1, Sentence p2)
    {
        this.predicateQueue = preds;
        this.firstParent = p1;
        this.secondParent = p2;
    }
    
    public Sentence(PriorityQueue<Predicate> preds, Sentence p1, Sentence p2, String substitution)
    {
        this.predicateQueue = preds;
        this.firstParent = p1;
        this.secondParent = p2;
        this.substitution = substitution;
    }

    @Override
    public String toString()
    {
    	String retVal = "";
       // Predicate[] predArray = preds.toArray(new Predicate[0]);
       // Arrays.sort(predArray);
        
        for (Predicate predicate : predicateQueue)
        {
        	retVal += predicate.toString();
                  }
        retVal += "\n";
     
        return retVal;
    }

    @Override
    public int hashCode()
    {
        return toString().hashCode();
    }

    @Override
    public boolean equals(Object o)
    {
        Sentence s = (Sentence) o;
        return this.hashCode() == s.hashCode();
    }

    @Override
    public int compareTo(Object o)
    {
        Sentence that = (Sentence) o;
        return Integer.compare(this.heuristicScore(), that.heuristicScore());
    }

    public PriorityQueue<Predicate> getPreds()
    {
        return predicateQueue;
    }
    
    public boolean isGoal()
    {
        return predicateQueue.isEmpty() && firstParent != null && secondParent != null;
    }

    public int heuristicScore()
    {
        int score = 0;
        score += predicateQueue.size();
        
        for (Predicate predicate : predicateQueue)
        {
            score += predicate.getParams().size();
            
        }
        return score;
    }

    public Sentence resolve(Sentence that)
    {
        for (Predicate p1 : this.predicateQueue)
        {
            for (Predicate p2 : that.getPreds())
            {
                if (p1.isUnifiable(p2))
                {
                    String sub = p1.unify(p2);
                    if (sub != null && sub.equals(""))
                    {
                        Sentence thisRemoved = this.remove(p1).remove(p2);
                        Sentence thatRemoved = that.remove(p1).remove(p2);
                        return thisRemoved.or(thatRemoved, this, that);
                    }
                    else if (sub != null)
                    {
                        Sentence thisRemoved = this.remove(p1).remove(p2);
                        Sentence thatRemoved = that.remove(p1).remove(p2);
                        Sentence thisSubbed = thisRemoved.substitute(sub);
                        Sentence thatSubbed = thatRemoved.substitute(sub);
                        return thisSubbed.or(thatSubbed, this, that);
                    }
                    else
                    {
                        continue;
                    }
                }
            }
        }
        return null;
    }

    private Sentence substitute(String sub)
    {
        PriorityQueue<Predicate> newPreds = new PriorityQueue<>();
        for (Predicate predicate : this.getPreds())
        {
            newPreds.add(predicate.substitute(sub));
        }
        return new Sentence(newPreds, this.firstParent, this.secondParent, sub);
    }

    private Sentence or(Sentence that, Sentence thisP, Sentence thatP)
    {
        PriorityQueue<Predicate> combinedPreds = new PriorityQueue<>();
        for (Predicate predicate : this.getPreds())
        {
            combinedPreds.add(predicate);
        }
        for (Predicate predicate : that.getPreds())
        {
            combinedPreds.add(predicate);
        }
        return new Sentence(combinedPreds, thisP, thatP, this.substitution);
    }
    
    private Sentence remove(Predicate p1)
    {
        Predicate p1Toggled = p1.toggleNegation();
        PriorityQueue<Predicate> toRemove = new PriorityQueue<>();
      
		PriorityQueue<Predicate> clone = new PriorityQueue<>(predicateQueue);
        for (Predicate predicate : clone)
        {
            if (predicate.equals(p1) || predicate.equals(p1Toggled))
            {
                toRemove.add(predicate);
            }
        }
        clone.removeAll(toRemove);
        return new Sentence(clone, this.firstParent, this.secondParent);
    }
    
    public String getParents(String path)
    {
        if (this.firstParent != null && this.secondParent != null)
        {   String me = this.toString().trim().equals("") ? "goal " : this.toString().replace("\n", "") ;
            String derivedFrom = path + "\nunify(" + this.firstParent.toString().replace("\n", "") + ", " + this.secondParent.toString().replace("\n", "") + ") => " + me;
            return this.firstParent.getParents("") + this.secondParent.getParents("") + derivedFrom + (this.substitution == null ? "Negations cancel" : "Substitution:" + this.substitution.replaceAll("/", "="));
        }
        return path;
    }
}
