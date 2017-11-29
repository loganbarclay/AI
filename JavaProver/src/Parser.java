import java.io.BufferedReader;
import java.io.IOException;
import java.nio.charset.Charset;
import java.nio.file.Files;
import java.nio.file.Paths;
import java.util.HashSet;
import java.util.PriorityQueue;
import java.util.regex.Pattern;

public class Parser {

	private HashSet<String> alphabet = new HashSet<>();
	private Pattern whitespace = Pattern.compile(" ");
	private Pattern punctuation = Pattern.compile(",");


	public Parser() {
		for (char ch = 'a', CH = 'A'; ch <= 'z'; ch++, CH++) {
			alphabet.add(ch + "");
			alphabet.add(CH + "");
		}
	}

	public KnowledgeBase fillKnowledgeBase(String filename, boolean heuristics) {
		String line;
		HashSet<Sentence> kbSentences = new HashSet<>();
		HashSet<Sentence> kbRefute = new HashSet<>();
		KnowledgeBase retKB;

		try (BufferedReader reader = Files.newBufferedReader(Paths.get(filename), Charset.defaultCharset())) {
			while ((line = reader.readLine()) != null) {
				if (!line.equals("")) {
					kbSentences.add(parseSentence(line, heuristics));
				} else {
					kbRefute.add(parseSentence(reader.readLine(), heuristics));
				}
			}
		} catch (IOException ex) {
			System.err.println("Error parsing file");
		}
		retKB = new KnowledgeBase(kbSentences, kbRefute);
		
		return retKB;
	}

	public Sentence parseSentence(String sentence, boolean heuristics) {
		PriorityQueue<Predicate> predicates = new PriorityQueue<>();
		Sentence retSentence;

		sentence = sentence.replaceAll("\\s+(?=[^()]*\\))", "");
		String[] splitPreds = whitespace.split(sentence);
		for (String pred : splitPreds) {
			predicates.add(parsePredicates(pred));
		}
		retSentence = new Sentence(predicates, heuristics);
		
		return retSentence;
	}

	public Predicate parsePredicates(String predicate) {
		Predicate retPredicate;
		boolean negation = false;
		String nameStr = "";
		
		String[] splitParameters = null;
		predicate = predicate.replaceAll(" ", "");

		for (int i = 0; i < predicate.length() && predicate.charAt(i) != '.'; i++) {
			if (i == 0 && predicate.charAt(i) == '!') {
				negation = true;
			} else if (alphabet.contains(predicate.charAt(i) + "")) {
				nameStr += predicate.charAt(i);
			} else if (predicate.charAt(i) == '(') {
				String parameters = predicate.substring(i + 1, predicate.indexOf(')'));
				splitParameters = punctuation.split(parameters);
				break;
			} else {
				//something went wrong parsing a predicate
				System.err.println("Predicate error");
				System.exit(1);
			}
		}
		retPredicate = new Predicate(negation, nameStr, splitParameters);
		
		return retPredicate;
		
	}

}
