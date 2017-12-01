import java.util.ArrayList;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

public class Predicate implements Comparable<Predicate> {

	private final boolean neg;
	private final String name;
	private final ArrayList<String> params;
	private final Pattern csv = Pattern.compile(",");
	private final Pattern space = Pattern.compile(" ");

	public Predicate(boolean neg, String name, ArrayList<String> params) {
		this.neg = neg;
		this.name = name;
		this.params = params;
	}

	@Override
	public String toString() {
		String pred = "";
		
		if (neg == true) {
			pred += "!";
		}
		pred += name + "(";
		
		for (int i = 0; i < params.size(); i++) {
			pred += params.get(i);
			if (i != (params.size() - 1)) {
				pred += ",";
			}
		}
		pred += ") ";

		return pred;
	}

	@Override
	public int hashCode() {
		int hash = 0;
		hash += this.neg ? 71 : 0;
		hash += this.name.hashCode();
		for (String string : params) {
			/*
			 * leaving the number on for the hashcode because a predicate with the same
			 * params in one sentence is not equal to one in another sentence
			 */
			hash += string.hashCode();
		}
		return hash;
	}

	@Override
	public boolean equals(Object obj) {
		return hashCode() == ((Predicate) obj).hashCode();
	}

	public String getName() {
		return name;
	}

	public ArrayList<String> getParams() {
		return params;
	}

	public boolean isNeg() {
		return neg;
	}

	public boolean isUnifiable(Predicate that) {
		return this.name.equals(that.getName()) && this.params.size() == that.getParams().size()
				&& (this.neg == !that.isNeg() || !this.neg == that.isNeg());
	}

	public Predicate toggleNegation() {
		return new Predicate(neg ? !neg : neg, name, params);
	}

	public String unifyVar(String var, String x, String substitutions) {
		Pattern varPattern = Pattern.compile(var + "/[\\w^\\d]");
		Pattern xPattern = Pattern.compile(x + "/[\\w^\\d]");
		Matcher varMatcher = varPattern.matcher(substitutions);
		Matcher xMatcher = xPattern.matcher(substitutions);

		// a substitution exists already
		if (varMatcher.matches()) {
			return unify(varMatcher.group(), x, substitutions);
		} else if (xMatcher.matches()) {
			return unify(var, xMatcher.group(), substitutions);
		} else {
			return substitutions + " " + var + "/" + x;
		}
	}

	public String unify(Predicate that) {
		String thisUnify = this.toString();
		String thatUnify = that.toString();
		String sub = unify(thisUnify, thatUnify, "");
		return sub.equals("failure") ? null : sub;
	}

	public String unify(String x, String y, String sub) {
		if (sub.equals("failure")) {
			return "failure";
		} else if (!x.contains("(") && x.contains(",") && !y.contains("(") && y.contains(",")) {
			// for a list of arguments that have been passed
			String[] xList = csv.split(x);
			String xCar = xList[0];
			String xCdr = flatten(xList, 1, xList.length, true);
			String[] yList = csv.split(y);
			String yCar = yList[0];
			String yCdr = flatten(yList, 1, yList.length, true);
			return unify(xCdr, yCdr, unify(xCar, yCar, sub));
		} else if (x.contains("(") && y.contains("(")) {
			x = x.replace("!", "");
			y = y.replace("!", "");
			String xParams = x.substring(x.indexOf("(") + 1, x.indexOf(")"));
			String yParams = y.substring(y.indexOf("(") + 1, y.indexOf(")"));
			String xOp = x.substring(0, x.indexOf("("));
			String yOp = y.substring(0, y.indexOf("("));
			return unify(xParams, yParams, unify(xOp, yOp, sub));
		} else if (x.equals(y)) {
			return sub;
		} else if (!Param.isConst(x)) {
			return unifyVar(x, y, sub);
		} else if (!Param.isConst(y)) {
			return unifyVar(y, x, sub);
		} else {
			return "failure";
		}
	}

	public Predicate substitute(String substitution) {
		String[] subs = space.split(substitution);
		for (String aSub : subs) {
			String[] sub = aSub.trim().split("/");
			for (int i = 0; i < params.size(); i++) {
				if (params.get(i).equals(sub[0])) {
					params.set(i, sub[1]);
				}
			}
		}
		return new Predicate(neg, name, params);
	}

	private String flatten(String[] xList, int i, int length, boolean comma) {
		String result = "";
		for (; i < length; i++) {
			result += (comma ? "," : " ") + xList[i];
		}
		return result.indexOf(",") == 0 ? result.substring(1, result.length()) : result;
	}

	@Override
	public int compareTo(Predicate that) {

		return 0;
	}
}
