import java.util.ArrayList;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

public class Predicate implements Comparable<Predicate> {

	private Pattern comma = Pattern.compile(",");
	private Pattern whitespace = Pattern.compile(" ");

	private boolean neg;
	private String name;
	private ArrayList<String> params;

	private final int PRIME = 73;

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

	public String getName() {
		return name;
	}

	public ArrayList<String> getParams() {
		return params;
	}

	public boolean isNeg() {
		return neg;
	}

	public boolean isAllConstants(){
		boolean allConstants = true;
		for (String param : this.params) {
			if (!ParameterChecker.isConstant(param)) {
				allConstants = false;
				break;
			}
		}
		
		return allConstants;
	}
	
	public boolean isUnifiable(Predicate that) {
		return this.name.equals(that.getName()) && this.params.size() == that.getParams().size()
				&& (this.neg == !that.isNeg() || !this.neg == that.isNeg());
	}

	public Predicate toggleNegation() {
		return new Predicate(neg ? !neg : neg, name, params);
	}

	/**
	 * function UNIFY-VAR(var, x , theta) returns a substitution 
	 * if {var/val} is an element of  theta
	 * then return UNIFY(val , x , theta) 
	 * else if {x/val} is an element of  theta then return UNIFY(var, val , theta) 
	 * else if OCCUR-CHECK?(var, x ) 
	 * then return failure 
	 * else return add {var/x } to theta
	 */
	public String unifyVariable(String variable, String x, String substitutions) {
		Pattern patternvariableiable = Pattern.compile(variable + "/[\\w^\\d]");
		Pattern pattern = Pattern.compile(x + "/[\\w^\\d]");
		Matcher matchvariableiable = patternvariableiable.matcher(substitutions);
		Matcher match = pattern.matcher(substitutions);

		String retUnify = "";

		if (matchvariableiable.matches()) {
			retUnify = unify(matchvariableiable.group(), x, substitutions);
		} else if (match.matches()) {
			retUnify = unify(variable, match.group(), substitutions);
		} else {
			retUnify = substitutions + " " + variable + "/" + x;
		}

		return retUnify;
	}

	public String unify(Predicate that) {
		String thisUnify = this.toString();
		String thatUnify = that.toString();
		String sub = unify(thisUnify, thatUnify, "");

		if (sub.equals("FAIL")) {
			sub = null;
		}

		return sub;
	}

	/**
	 * function UNIFY(x , y, theta) returns a substitution to make x and y identical
	 * inputs: x , a variable, constant, list, or compound expression y, a
	 * variable, constant, list, or compound expression theta, the substitution
	 * built up so far (optional, defaults to empty) if theta = failure then return
	 * failure else if x = y then return theta else if VARIABLE?(x ) then return
	 * UNIFY-VAR(x , y, theta) else if VARIABLE?(y) then return UNIFY-VAR(y, x , theta)
	 * else if COMPOUND?(x ) and COMPOUND?(y) then return UNIFY(x.ARGS,
	 * y.ARGS,UNIFY(x.OP, y.OP, theta)) else if LIST?(x ) and LIST?(y) then return
	 * UNIFY(x.REST, y.REST, UNIFY(x .FIRST, y.FIRST, theta)) else return failure
	 * 
	 */
	public String unify(String x, String y, String sub) {
		if (sub.equals("FAIL")) {
			return "FAIL";
		} else if (!x.contains("(") && x.contains(",") && !y.contains("(") && y.contains(",")) {
			String[] xList = comma.split(x);
			String xFirst = xList[0];
			String xRest = condenseParams(xList, 1, xList.length, true);
			String[] yList = comma.split(y);
			String yFirst = yList[0];
			String yRest = condenseParams(yList, 1, yList.length, true);
			return unify(xRest, yRest, unify(xFirst, yFirst, sub));
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
		} else if (!ParameterChecker.isConstant(x)) {
			return unifyVariable(x, y, sub);
		} else if (!ParameterChecker.isConstant(y)) {
			return unifyVariable(y, x, sub);
		} else {
			return "FAIL";
		}
	}

	public Predicate substitute(String substitution) {
		String[] subs = whitespace.split(substitution);
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

	private String condenseParams(String[] xList, int i, int length, boolean comma) {
		String result = "";
		for (; i < length; i++) {
			result += (comma ? "," : " ") + xList[i];
		}
		return result.indexOf(",") == 0 ? result.substring(1, result.length()) : result;
	}

	@Override
	public int compareTo(Predicate that) {

		int check = this.params.size() - that.params.size();
		if (check != 0) {

			return -1 * Integer.compare(this.params.size(), that.params.size());
		} else {
			int theseConstants = 0;
			int thoseConstants = 0;
			for (String param : this.params) {

				if (ParameterChecker.isConstant(param)) {
					theseConstants++;
				}
			}
			for (String param : that.params) {
				if (ParameterChecker.isConstant(param)) {
					thoseConstants++;
				}
			}

			return -1 * Integer.compare(theseConstants, thoseConstants);

		}

	}

	@Override
	public int hashCode() {
		int hash = 0;
		if (this.neg) {
			hash += PRIME;
		}
		hash += this.name.hashCode();
		for (String string : params) {
			hash += string.hashCode();
		}

		return hash;
	}

	public boolean equals(Predicate pred) {
		return hashCode() == pred.hashCode();
	}

	public boolean isAllVariables() {
		for(String param : this.params){
			if(ParameterChecker.isConstant(param)){
				return false;
			}
		}
		return true;
	}
}
