
import java.util.PriorityQueue;
import java.util.Random;

public class Sentence implements Comparable<Sentence> {

	private PriorityQueue<Predicate> predicateQueue;
	private Sentence firstParent;
	private Sentence secondParent;
	private String substitution = null;
	private int score;
	private boolean heuristics;
	private final int PREDICATE_WEIGHT = 5;
	private final int PARAMETER_WEIGHT = 1;

	public Sentence(PriorityQueue<Predicate> preds, boolean heuristics) {
		this.heuristics = heuristics;
		this.predicateQueue = preds;
		score(heuristics);

	}

	public Sentence(PriorityQueue<Predicate> preds, Sentence firstParent, Sentence secondParent, boolean heuristics) {
		this.heuristics = heuristics;
		this.predicateQueue = preds;
		this.firstParent = firstParent;
		this.secondParent = secondParent;
		score(heuristics);

	}

	public Sentence(PriorityQueue<Predicate> preds, Sentence firstParent, Sentence secondParent, String substitution,
			boolean heuristics) {
		this.heuristics = heuristics;
		this.predicateQueue = preds;
		this.firstParent = firstParent;
		this.secondParent = secondParent;
		this.substitution = substitution;
		score(heuristics);
	}

	private void score(boolean heuristics) {
		if (heuristics) {
			heuristicScore();

		} else {
			randomScore();

		}
	}

	public void heuristicScore() {
		this.score = 0;
		for (Predicate predicate : predicateQueue) {
			this.score -= PARAMETER_WEIGHT * predicate.getParams().size();
		}
		this.score -= PREDICATE_WEIGHT * predicateQueue.size();

	}

	public void randomScore() {
		Random rand = new Random();
		this.score = rand.nextInt(20);
	}

	public int getScore() {
		return this.score;
	}

	public PriorityQueue<Predicate> getPreds() {
		return predicateQueue;
	}

	public boolean isGoal() {
		if (firstParent != null && secondParent != null && predicateQueue.isEmpty()) {
			return true;
		}
		return false;
	}

	public Sentence resolve(Sentence that) {
		Sentence retSentence = null;
		for (Predicate pred1 : this.predicateQueue) {
			for (Predicate pred2 : that.getPreds()) {
				if (pred1.isUnifiable(pred2)) {
					String sub = pred1.unify(pred2);
					if (sub != null && sub.equals("")) {
						Sentence removeThis = this.remove(pred1).remove(pred2);
						Sentence removedFromThat = that.remove(pred1).remove(pred2);

						retSentence = removeThis.folOR(removedFromThat, this, that);
					} else if (sub != null) {
						Sentence thisRemoved = this.remove(pred1).remove(pred2);
						Sentence thatRemoved = that.remove(pred1).remove(pred2);
						Sentence thisSubbed = thisRemoved.substitution(sub);
						Sentence thatSubbed = thatRemoved.substitution(sub);

						retSentence = thisSubbed.folOR(thatSubbed, this, that);
					} else {
						continue;
					}
				}
			}
		}
		return retSentence;
	}

	private Sentence substitution(String substStr) {
		PriorityQueue<Predicate> newPreds = new PriorityQueue<>();
		for (Predicate predicate : this.getPreds()) {
			newPreds.add(predicate.substitute(substStr));
		}
		return new Sentence(newPreds, this.firstParent, this.secondParent, substStr, this.heuristics);
	}

	private Sentence folOR(Sentence that, Sentence thisP, Sentence thatP) {
		PriorityQueue<Predicate> combinedPreds = new PriorityQueue<>();
		for (Predicate predicate : this.getPreds()) {
			combinedPreds.add(predicate);
		}
		for (Predicate predicate : that.getPreds()) {
			combinedPreds.add(predicate);
		}
		return new Sentence(combinedPreds, thisP, thatP, this.substitution, this.heuristics);
	}

	private Sentence remove(Predicate pred) {
		Sentence retSentence;
		Predicate negatedPred = pred.toggleNegation();

		PriorityQueue<Predicate> predQueueCopy = new PriorityQueue<>(predicateQueue);
		for (Predicate predicate : predicateQueue) {
			if (predicate.equals(pred) || predicate.equals(negatedPred)) {
				predQueueCopy.remove(predicate);
			}
		}

		retSentence = new Sentence(predQueueCopy, this.firstParent, this.secondParent, this.heuristics);
		return retSentence;
	}

	public String getParents(String path) {
		if (this.firstParent != null && this.secondParent != null) {
			String me = this.toString().trim().equals("") ? "goal " : this.toString().replace("\n", "");
			String derivedFrom = path + "\nunify(" + this.firstParent.toString().replace("\n", "") + ", "
					+ this.secondParent.toString().replace("\n", "") + ") => " + me;
			return this.firstParent.getParents("") + this.secondParent.getParents("") + derivedFrom
					+ (this.substitution == null ? "Negations cancel"
							: "Substitution:" + this.substitution.replaceAll("/", "="));
		}
		return path;
	}

	@Override
	public String toString() {
		String retVal = "";

		for (Predicate predicate : predicateQueue) {
			retVal += predicate.toString();
		}
		retVal += "\n";

		return retVal;
	}

	@Override
	public int hashCode() {
		return toString().hashCode();
	}

	@Override
	public boolean equals(Object o) {
		Sentence s = (Sentence) o;
		return this.hashCode() == s.hashCode();
	}

	@Override
	public int compareTo(Sentence that) {
		// return lower score for priority queue
		return Integer.compare(that.score, this.score);
	}

}
