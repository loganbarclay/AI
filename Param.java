public class Param {
    public static boolean isConst(String p)
    {
        return Character.isUpperCase(p.charAt(0)) && !p.contains("(") && !p.contains(",");
    }
}