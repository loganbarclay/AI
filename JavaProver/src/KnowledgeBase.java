import java.util.ArrayList;
import java.util.HashMap;
import java.util.HashSet;

public class KnowledgeBase {

	private HashSet<Sentence> sentences;
	private HashSet<Sentence> refuted;
	private static int next = 0;
	private HashSet<String> paramsSeen = new HashSet<>();

	public KnowledgeBase(HashSet<Sentence> sentences, HashSet<Sentence> refuted) {
		this.sentences = sentences;
		this.refuted = refuted;
	}

	@Override
	public String toString() {
		String kb = "";
		for (Sentence sentence : sentences) {
			kb += sentence.toString();
		}
		kb += "\n";

		for (Sentence refutedPredicate : refuted) {
			kb += refutedPredicate.toString();
		}
		return kb;
	}

	public KnowledgeBase standardizeVariables() {
		paramsSeen.clear();
		return new KnowledgeBase(standardize(sentences), standardize(refuted));
	}

	private HashSet<Sentence> standardize(HashSet<Sentence> sentences) {
		for (Sentence sentence : sentences) {
			HashSet<String> predsParams = new HashSet<>();
			HashMap<String, String> changedNames = new HashMap<>();
			for (Predicate p : sentence.getPreds()) {
				for (String string : p.getParams()) {
					predsParams.add(string);
				}
				for (String string : predsParams) {
					if (paramsSeen.contains(string) && !changedNames.containsKey(string) && !Param.isConst(string)) {
						changedNames.put(string, string + next++);
					}
				}
			}
			for (Predicate p : sentence.getPreds()) {
				ArrayList<String> params = p.getParams();
				for (int i = 0; i < params.size(); i++) {
					String param = params.get(i);
					if (changedNames.get(param) != null && !Param.isConst(param)) {
						params.set(i, changedNames.get(param));
					}
				}
			}
			paramsSeen.addAll(predsParams);
		}
		return sentences;
	}

	public HashSet<Sentence> getSentences() {
		return sentences;
	}

	public HashSet<Sentence> getRefuted() {
		return refuted;
	}
}
