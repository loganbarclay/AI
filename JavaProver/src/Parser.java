
import java.io.File;
import java.io.FileNotFoundException;
import java.util.ArrayList;
import java.util.HashSet;
import java.util.PriorityQueue;
import java.util.Scanner;

public class Parser {

	private HashSet<String> alphabet = new HashSet<>();
	private boolean heuristics = true;

	public Parser() {
		for (char ch = 'a', CH = 'A'; ch <= 'z'; ch++, CH++) {
			alphabet.add(ch + "");
			alphabet.add(CH + "");
		}
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
		
		Scanner sent = new Scanner(sentence);
		
		while (sent.hasNext()) {
			predicates.add(parsePredicates(sent.next()));
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
		ArrayList params = new ArrayList();
		String tmpParams;
		
		if (predicate.charAt(j) == '!') {
			negation = true;
			j++;
		}
		
		nameStr = predicate.substring(j, predicate.indexOf('('));
		j = (predicate.indexOf('(')+1);
		
		tmpParams = predicate.substring(j, predicate.indexOf(')'));
		
		Scanner scan = new Scanner(tmpParams).useDelimiter(",");
		while (scan.hasNext()) {
			params.add(scan.next());
		}

		retPredicate = new Predicate(negation, nameStr, params);
		
		scan.close();
		return retPredicate;
		
	}

}
