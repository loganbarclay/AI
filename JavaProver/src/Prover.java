import java.io.File;
import java.io.FileNotFoundException;
import java.util.HashSet;
import java.util.PriorityQueue;

public class Prover {
	private static int heurNumResolutions = 0;
	private static long heurStart = 0;
	private static long heurFinish = 0;
	private static int randNumResolutions = 0;
	private static long randStart = 0;
	private static long randFinish = 0;
	private static String heurParents = "";
	private static String randParents = "";

	/**
	 * @param args
	 *            the command line arguments
	 * @throws FileNotFoundException
	 */
	public static void main(String[] args) throws FileNotFoundException {
		if (args.length < 1) {
			System.out.println("Usage: specify valid filename");
			System.exit(1);
		}

		File inputFile = new File(args[0]);
		boolean heuristics = true;
		boolean random = false;

		Parser heurParser = new Parser();
		KnowledgeBase heuristicsKB = heurParser.fillKnowledgeBase(inputFile, heuristics);
		Parser randParser = new Parser();
		KnowledgeBase randomKB = randParser.fillKnowledgeBase(inputFile, random);
		System.out.println(heuristicsKB.toString());

		heuristicsKB = heuristicsKB.standardizeVariables();

		heurStart = System.currentTimeMillis();
		HashSet<Sentence> support = resolve(heuristicsKB.getSentences(), heuristicsKB.getRefuted(), heuristics);
		if (support == null) {
			System.out.println("failure");
		}
		while (support != null) {
			support = resolve(heuristicsKB.getSentences(), support, heuristics);
		}
		heurFinish = System.currentTimeMillis();

		randStart = System.currentTimeMillis();
		support = resolve(randomKB.getSentences(), randomKB.getRefuted(), random);
		if (support == null) {
			System.out.println("failure");
		}

		while (support != null) {
			support = resolve(randomKB.getSentences(), support, random);
		}
		randFinish = System.currentTimeMillis();

		printResults();

		System.exit(0);
	}

	public static HashSet<Sentence> resolve(HashSet<Sentence> sentencesSet, HashSet<Sentence> supportSet,
			boolean heuristics) {

		// List<Sentence> sentences = new LinkedList<>(sentencesSet);
		// List<Sentence> support = new LinkedList<>(supportSet);
		// Collections.sort(sentences);
		// Collections.sort(support);
		PriorityQueue<Sentence> sentences = new PriorityQueue<>(sentencesSet);
		PriorityQueue<Sentence> support = new PriorityQueue<>(supportSet);

		Sentence result;
		for (Sentence supporting : support) {
			for (Sentence sentence : sentences) {
				result = supporting.resolve(sentence);
				if (result != null) {
					if (result.isGoal()) {

						if (heuristics) {
							heurFinish = System.currentTimeMillis();
							heurNumResolutions++;
							heurParents = result.getParents("") + "\n";
						} else {
							randFinish = System.currentTimeMillis();
							randNumResolutions++;
							randParents = result.getParents("") + "\n";

						}

						return null;
					} else if (supportSet.add(result)) {
						if (heuristics) {
							heurNumResolutions++;
						} else {
							randNumResolutions++;
						}

						return supportSet;
					}
				}
			}
		}
		return null;
	}

	private static void printResults() {
		System.out.println(heurParents);
		System.out.println("===============================");
		System.out.println("===== Heuristic results =======");
		System.out.println("Resolutions: " + heurNumResolutions);
		System.out.println("Time: " + (heurFinish - heurStart) + " milliseconds");
		System.out.println("===============================");

		System.out.println(randParents);
		System.out.println("===============================");
		System.out.println("===== Random results =======");
		System.out.println("Resolutions: " + randNumResolutions);
		System.out.println("Time: " + (randFinish - randStart) + " milliseconds");
		System.out.println("===============================");

	}
}
