import java.util.Arrays;
import java.util.HashSet;


public class Sentence implements Comparable {

    private HashSet<Predicate> preds;
    private Sentence parent1;
    private Sentence parent2;
    private String substitution = null;

    public Sentence(HashSet<Predicate> preds)
    {
        this.preds = preds;
    }
    
    public Sentence(HashSet<Predicate> preds, Sentence p1, Sentence p2)
    {
        this.preds = preds;
        this.parent1 = p1;
        this.parent2 = p2;
    }
    
    public Sentence(HashSet<Predicate> preds, Sentence p1, Sentence p2, String substitution)
    {
        this.preds = preds;
        this.parent1 = p1;
        this.parent2 = p2;
        this.substitution = substitution;
    }

    @Override
    public String toString()
    {
        Predicate[] predArray = preds.toArray(new Predicate[0]);
        Arrays.sort(predArray);
        StringBuilder builder = new StringBuilder();
        for (Predicate predicate : predArray)
        {
            builder.append(predicate.toString());
        }
        builder.append("\n");
        return builder.toString();
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
        return Integer.compare(this.weight(), that.weight());
    }

    public HashSet<Predicate> getPreds()
    {
        return preds;
    }
    
    public boolean isGoal()
    {
        return preds.isEmpty() && parent1 != null && parent2 != null;
    }

    public int weight()
    {
        int weight = 0;
        weight += 2 * preds.size();
        //two for predicates
        for (Predicate predicate : preds)
        {
            weight += predicate.getParams().length;
            //one for params
        }
        return weight;
    }

    public Sentence resolve(Sentence that)
    {
        for (Predicate p1 : this.preds)
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
        HashSet<Predicate> newPreds = new HashSet<>();
        for (Predicate predicate : this.getPreds())
        {
            newPreds.add(predicate.substitute(sub));
        }
        return new Sentence(newPreds, this.parent1, this.parent2, sub);
    }

    private Sentence or(Sentence that, Sentence thisP, Sentence thatP)
    {
        HashSet<Predicate> combinedPreds = new HashSet<>();
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
        HashSet<Predicate> toRemove = new HashSet<>();
        HashSet<Predicate> clone = (HashSet<Predicate>)preds.clone();
        for (Predicate predicate : clone)
        {
            if (predicate.equals(p1) || predicate.equals(p1Toggled))
            {
                toRemove.add(predicate);
            }
        }
        clone.removeAll(toRemove);
        return new Sentence(clone, this.parent1, this.parent2);
    }
    
    public String derivedFrom(String path)
    {
        if (this.parent1 != null && this.parent2 != null)
        {   String me = this.toString().trim().equals("") ? "goal " : this.toString().replace("\n", "") ;
            String derivedFrom = path + "\nunify(" + this.parent1.toString().replace("\n", "") + ", " + this.parent2.toString().replace("\n", "") + ") => " + me;
            return this.parent1.derivedFrom("") + this.parent2.derivedFrom("") + derivedFrom + (this.substitution == null ? "Negations cancel" : "Substitution:" + this.substitution.replaceAll("/", "="));
        }
        return path;
    }
}
