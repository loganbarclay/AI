
import java.io.File;
import java.io.FileNotFoundException;
import java.util.ArrayList;
import java.util.HashSet;
import java.util.PriorityQueue;
import java.util.Scanner;

public class Parser {

	private boolean heuristics;

	public Parser() {
		heuristics = true;
	}

	public KnowledgeBase fillKnowledgeBase(File filename, boolean heuristics) throws FileNotFoundException {
		this.heuristics = heuristics;
		String line;
		HashSet<Sentence> kbSentences = new HashSet<>();
		HashSet<Sentence> kbRefute = new HashSet<>();
		KnowledgeBase retKB;

		Scanner inFile = new Scanner(filename);

		while (inFile.hasNextLine()) {
			line = inFile.nextLine();
			if (!line.equals("")) {
				kbSentences.add(parseSentence(line));
			} else {
				kbRefute.add(parseSentence(inFile.nextLine()));
			}
		}

		retKB = new KnowledgeBase(kbSentences, kbRefute);
		inFile.close();
		return retKB;
	}

	public Sentence parseSentence(String sentence) {
		PriorityQueue<Predicate> predicates = new PriorityQueue<>();
		Sentence retSentence;

		sentence = sentence.replaceAll("\\( ", "\\(");
		sentence = sentence.replaceAll(" \\)", "\\)");

		Scanner sent = new Scanner(sentence);

		while (sent.hasNext()) {
			Predicate retPred = parsePredicates(sent.next());
			if (retPred != null) {
				predicates.add(retPred);
			}
		}

		retSentence = new Sentence(predicates, heuristics);
		sent.close();
		return retSentence;
	}

	public Predicate parsePredicates(String predicate) {
		int j = 0;
		Predicate retPredicate;
		boolean negation = false;
		String nameStr = "";
		ArrayList<String> params = new ArrayList<String>();
		String tmpParams;
		// System.out.println(predicate);
		if (predicate.charAt(j) == '!') {
			negation = true;
			j++;
		}
		try {
			nameStr = predicate.substring(j, predicate.indexOf('('));
		} catch (Exception e) {
			return null;
		}
		j = (predicate.indexOf('(') + 1);
		try {
			tmpParams = predicate.substring(j, predicate.indexOf(')'));
		} catch (Exception e) {
			return null;
		}
		Scanner scan = new Scanner(tmpParams).useDelimiter(",");
		while (scan.hasNext()) {
			params.add(scan.next());
		}

		retPredicate = new Predicate(negation, nameStr, params);

		scan.close();
		return retPredicate;

	}

}
