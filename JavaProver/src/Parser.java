
import java.io.File;
import java.io.FileNotFoundException;
import java.util.ArrayList;
import java.util.HashSet;
import java.util.PriorityQueue;
import java.util.Scanner;

public class Parser {

	private HashSet<String> alphabet = new HashSet<>();

	public Parser() {
		for (char ch = 'a', CH = 'A'; ch <= 'z'; ch++, CH++) {
			alphabet.add(ch + "");
			alphabet.add(CH + "");
		}
	}

<<<<<<< HEAD
	public KnowledgeBase fillKnowledgeBase(File filename) throws FileNotFoundException {
=======
	public KnowledgeBase fillKnowledgeBase(String filename, boolean heuristics) {
>>>>>>> 9fc13cc451b6eb92ceecb2d67c3d5885146a05da
		String line;
		HashSet<Sentence> kbSentences = new HashSet<>();
		HashSet<Sentence> kbRefute = new HashSet<>();
		KnowledgeBase retKB;

<<<<<<< HEAD
		Scanner inFile = new Scanner(filename);
		
		while (inFile.hasNextLine()) {
			line = inFile.nextLine();
			if (!line.equals("")) {
				kbSentences.add(parseSentence(line));
			} else {
				kbRefute.add(parseSentence(inFile.nextLine()));
=======
		try (BufferedReader reader = Files.newBufferedReader(Paths.get(filename), Charset.defaultCharset())) {
			while ((line = reader.readLine()) != null) {
				if (!line.equals("")) {
					kbSentences.add(parseSentence(line, heuristics));
				} else {
					kbRefute.add(parseSentence(reader.readLine(), heuristics));
				}
>>>>>>> 9fc13cc451b6eb92ceecb2d67c3d5885146a05da
			}
		}
		
		retKB = new KnowledgeBase(kbSentences, kbRefute);
		inFile.close();
		return retKB;
	}

	public Sentence parseSentence(String sentence, boolean heuristics) {
		PriorityQueue<Predicate> predicates = new PriorityQueue<>();
		Sentence retSentence;
		
		Scanner sent = new Scanner(sentence);
		
		while (sent.hasNext()) {
			predicates.add(parsePredicates(sent.next()));
		}
<<<<<<< HEAD

		retSentence = new Sentence(predicates);
		sent.close();
=======
		retSentence = new Sentence(predicates, heuristics);
		
>>>>>>> 9fc13cc451b6eb92ceecb2d67c3d5885146a05da
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
