public class ParameterChecker {
	public static boolean isConstant(String p) {
		return Character.isUpperCase(p.charAt(0)) && !p.contains("(") && !p.contains(",");
	}
	
}