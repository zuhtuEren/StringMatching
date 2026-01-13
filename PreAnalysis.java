// Zühtü Eren İncekara
// 22050111023
// Zekeriya Damcı
// 22050111074

/**
 * PreAnalysis interface for students to implement their algorithm selection logic
 * 
 * Students should analyze the characteristics of the text and pattern to determine
 * which algorithm would be most efficient for the given input.
 * 
 * The system will automatically use this analysis if the chooseAlgorithm method
 * returns a non-null value.
 */
public abstract class PreAnalysis {
    
    /**
     * Analyze the text and pattern to choose the best algorithm
     * 
     * @param text The text to search in
     * @param pattern The pattern to search for
     * @return The name of the algorithm to use (e.g., "Naive", "KMP", "RabinKarp", "BoyerMoore", "GoCrazy")
     *         Return null if you want to skip pre-analysis and run all algorithms
     * 
     * Tips for students:
     * - Consider the length of the text and pattern
     * - Consider the characteristics of the pattern (repeating characters, etc.)
     * - Consider the alphabet size
     * - Think about which algorithm performs best in different scenarios
     */
    public abstract String chooseAlgorithm(String text, String pattern);
    
    /**
     * Get a description of your analysis strategy
     * This will be displayed in the output
     */
    public abstract String getStrategyDescription();
}

/**
 * StudentPreAnalysis: Refined Strategy
 * This version adds special cases and tighter thresholds:
 * - Empty pattern/text handled separately
 * - Lower threshold for repetition ratio (KMP)
 * - GoCrazy chosen earlier for long text
 * - BoyerMoore only for very long patterns + diverse alphabet
 * - RabinKarp for medium patterns in medium-long text
 */
class StudentPreAnalysis extends PreAnalysis {

    // Helper: repetition ratio in pattern
    private double repetitionRatio(String pattern) {
        int m = pattern.length();
        if (m == 0) return 0.0;
        int repeatCount = 0;
        for (int i = 1; i < m; i++) {
            if (pattern.charAt(i) == pattern.charAt(i - 1)) repeatCount++;
        }
        return (double) repeatCount / m;
    }

    // Helper: alphabet size in text
    private int alphabetSize(String text) {
        java.util.HashSet<Character> set = new java.util.HashSet<>();
        for (char c : text.toCharArray()) set.add(c);
        return set.size();
    }

    @Override
    public String chooseAlgorithm(String text, String pattern) {
        int m = pattern.length();
        int n = text.length();

        // Special cases
        if (m == 0 && n == 0) return "Naive"; // both empty
        if (m == 0) return "BoyerMoore";      // empty pattern
        if (n == 0) return "Naive";           // empty text
        if (m > n) return "RabinKarp";        // pattern longer than text

        // Very short patterns
        if (m <= 3) return "Naive";

        // Long text → GoCrazy earlier
        if (n > 800) return "GoCrazy";

        // Very long pattern + diverse alphabet
        if (m > 15 && alphabetSize(text) > 6) return "BoyerMoore";

        // Repetitive pattern → KMP (lower threshold)
        if (repetitionRatio(pattern) > 0.15) return "KMP";

        // Medium pattern + medium-long text → RabinKarp
        if (m >= 5 && m <= 12 && n > 300) return "RabinKarp";

        // Default
        return "Naive";
    }

    @Override
    public String getStrategyDescription() {
        return "Refined Strategy: Special cases for empty inputs, lower repetition threshold for KMP, " +
               "GoCrazy for text >800, BoyerMoore only for very long patterns with diverse alphabet, " +
               "RabinKarp for medium patterns in medium-long text, otherwise Naive.";
    }
}

/**
 * Example implementation showing how pre-analysis could work
 * This is for demonstration purposes
 */
class ExamplePreAnalysis extends PreAnalysis {

    @Override
    public String chooseAlgorithm(String text, String pattern) {
        int textLen = text.length();
        int patternLen = pattern.length();

        // Simple heuristic example
        if (patternLen <= 3) {
            return "Naive"; // For very short patterns, naive is often fastest
        } else if (hasRepeatingPrefix(pattern)) {
            return "KMP"; // KMP is good for patterns with repeating prefixes
        } else if (patternLen > 10 && textLen > 1000) {
            return "RabinKarp"; // RabinKarp can be good for long patterns in long texts
        } else {
            return "Naive"; // Default to naive for other cases
        }
    }

    private boolean hasRepeatingPrefix(String pattern) {
        if (pattern.length() < 2) return false;

        // Check if first character repeats
        char first = pattern.charAt(0);
        int count = 0;
        for (int i = 0; i < Math.min(pattern.length(), 5); i++) {
            if (pattern.charAt(i) == first) count++;
        }
        return count >= 3;
    }

    @Override
    public String getStrategyDescription() {
        return "Example strategy: Choose based on pattern length and characteristics";
    }
}

/**
 * Instructor's pre-analysis implementation (for testing purposes only)
 * Students should NOT modify this class
 */
class InstructorPreAnalysis extends PreAnalysis {

    @Override
    public String chooseAlgorithm(String text, String pattern) {
        // This is a placeholder for instructor testing
        // Students should focus on implementing StudentPreAnalysis
        return null;
    }

    @Override
    public String getStrategyDescription() {
        return "Instructor's testing implementation";
    }
}
