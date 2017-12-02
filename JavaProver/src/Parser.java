
import java.io.File;
import java.io.FileNotFoundException;
import java.util.ArrayList;
import java.util.HashSet;
import java.util.PriorityQueue;
import java.util.Scanner;

public class Parser {

	private HashSet<Predicate> subsumptionSet;
	private boolean heuristics;
	private final boolean USE_SUBSUMPTION = false;

	public Parser() {
		subsumptionSet = new HashSet<>();
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

	@SuppressWarnings("unused")
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
		Scanner scanner = new Scanner(tmpParams);
		Scanner scan = scanner.useDelimiter(",");
		while (scan.hasNext()) {
			params.add(scan.next());
		}

		retPredicate = new Predicate(negation, nameStr, params);

		scanner.close();

		///////////////////////////////
		// LOGIC FOR SUBSUMPTION IS HERE
		///////////////////////////////

		if (heuristics && USE_SUBSUMPTION) {
			if (!checkSubsumptionSet(retPredicate)) {
				retPredicate = null;
			} else {
				subsumptionSet.add(retPredicate);
			}
		}
		return retPredicate;

	}

	/**
	 * Checks for subsumption
	 * 
	 * @param checkPred
	 * @return
	 */
	private boolean checkSubsumptionSet(Predicate checkPred) {
		boolean shouldAdd = true;
		ArrayList<String> checkParams = checkPred.getParams();
		int checkSize = checkParams.size();
		boolean checkNeg = checkPred.isNeg();
		boolean allConstants = checkPred.isAllConstants();

		if (allConstants) {
			for (Predicate predicate : this.subsumptionSet) {
				if (!predicate.getName().equals(checkPred.getName())) {

					continue;
				} else if (checkNeg == predicate.isNeg()) {

					continue;
				} else if (predicate.getParams().size() != checkSize) {
					continue;
				} else {
					
					if(predicate.isAllVariables()){
						shouldAdd = false;
						break;
					}

				}
			}

		}

		return shouldAdd;

	}

}
