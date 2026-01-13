// Zühtü Eren İncekara
// 22050111023
// Zekeriya Damcı
// 22050111074

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

class Naive extends Solution {
	static {
		SUBCLASSES.add(Naive.class);
		System.out.println("Naive registered");
	}

	public Naive() {
	}

	@Override
	public String Solve(String text, String pattern) {
		List<Integer> indices = new ArrayList<>();
		int n = text.length();
		int m = pattern.length();

		for (int i = 0; i <= n - m; i++) {
			int j;
			for (j = 0; j < m; j++) {
				if (text.charAt(i + j) != pattern.charAt(j)) {
					break;
				}
			}
			if (j == m) {
				indices.add(i);
			}
		}

		return indicesToString(indices);
	}
}

class KMP extends Solution {
	static {
		SUBCLASSES.add(KMP.class);
		System.out.println("KMP registered");
	}

	public KMP() {
	}

	@Override
	public String Solve(String text, String pattern) {
		List<Integer> indices = new ArrayList<>();
		int n = text.length();
		int m = pattern.length();

		// Handle empty pattern - matches at every position
		if (m == 0) {
			for (int i = 0; i <= n; i++) {
				indices.add(i);
			}
			return indicesToString(indices);
		}

		// Compute LPS (Longest Proper Prefix which is also Suffix) array
		int[] lps = computeLPS(pattern);

		int i = 0; // index for text
		int j = 0; // index for pattern

		while (i < n) {
			if (text.charAt(i) == pattern.charAt(j)) {
				i++;
				j++;
			}

			if (j == m) {
				indices.add(i - j);
				j = lps[j - 1];
			} else if (i < n && text.charAt(i) != pattern.charAt(j)) {
				if (j != 0) {
					j = lps[j - 1];
				} else {
					i++;
				}
			}
		}

		return indicesToString(indices);
	}

	private int[] computeLPS(String pattern) {
		int m = pattern.length();
		int[] lps = new int[m];
		int len = 0;
		int i = 1;

		lps[0] = 0;

		while (i < m) {
			if (pattern.charAt(i) == pattern.charAt(len)) {
				len++;
				lps[i] = len;
				i++;
			} else {
				if (len != 0) {
					len = lps[len - 1];
				} else {
					lps[i] = 0;
					i++;
				}
			}
		}

		return lps;
	}
}

class RabinKarp extends Solution {
	static {
		SUBCLASSES.add(RabinKarp.class);
		System.out.println("RabinKarp registered.");
	}

	public RabinKarp() {
	}

	private static final int PRIME = 101; // A prime number for hashing

	@Override
	public String Solve(String text, String pattern) {
		List<Integer> indices = new ArrayList<>();
		int n = text.length();
		int m = pattern.length();

		// Handle empty pattern - matches at every position
		if (m == 0) {
			for (int i = 0; i <= n; i++) {
				indices.add(i);
			}
			return indicesToString(indices);
		}

		if (m > n) {
			return "";
		}

		int d = 256; // Number of characters in the input alphabet
		long patternHash = 0;
		long textHash = 0;
		long h = 1;

		// Calculate h = d^(m-1) % PRIME
		for (int i = 0; i < m - 1; i++) {
			h = (h * d) % PRIME;
		}

		// Calculate hash value for pattern and first window of text
		for (int i = 0; i < m; i++) {
			patternHash = (d * patternHash + pattern.charAt(i)) % PRIME;
			textHash = (d * textHash + text.charAt(i)) % PRIME;
		}

		// Slide the pattern over text one by one
		for (int i = 0; i <= n - m; i++) {
			// Check if hash values match
			if (patternHash == textHash) {
				// Check characters one by one
				boolean match = true;
				for (int j = 0; j < m; j++) {
					if (text.charAt(i + j) != pattern.charAt(j)) {
						match = false;
						break;
					}
				}
				if (match) {
					indices.add(i);
				}
			}

			// Calculate hash value for next window
			if (i < n - m) {
				textHash = (d * (textHash - text.charAt(i) * h) + text.charAt(i + m)) % PRIME;

				// Convert negative hash to positive
				if (textHash < 0) {
					textHash = textHash + PRIME;
				}
			}
		}

		return indicesToString(indices);
	}
}

//Boyer-Moore algorithm: uses "bad character rule" to skip ahead in the text
class BoyerMoore extends Solution {
	static {
		SUBCLASSES.add(BoyerMoore.class);
		System.out.println("BoyerMoore registered");
	}

	public BoyerMoore() {
	}

	// Preprocess: build a table with the last position of each character in the
	// pattern
	private Map<Character, Integer> preprocessBadChar(String pattern) {
		Map<Character, Integer> badCharTable = new HashMap<>();
		for (int i = 0; i < pattern.length(); i++) {
			badCharTable.put(pattern.charAt(i), i);
		}
		return badCharTable;
	}

	@Override
	public String Solve(String text, String pattern) {
		List<Integer> matches = new ArrayList<>();
		int n = text.length();
		int m = pattern.length();

		// If pattern is empty
		if (m == 0) {
			if (n == 0)
				matches.add(0);
			return indicesToString(matches);
		}
		if (n < m)
			return indicesToString(matches);

		// Build bad character table
		Map<Character, Integer> badCharTable = preprocessBadChar(pattern);
		int s = 0; // current shift in text

		// Main loop
		while (s <= n - m) {
			int j = m - 1; // start from the end of the pattern

			// Compare characters from right to left
			while (j >= 0 && pattern.charAt(j) == text.charAt(s + j)) {
				j--;
			}

			if (j < 0) {
				// Match found
				matches.add(s);
				s += 1; // move one step forward
			} else {
				// Mismatch: use bad character rule
				char badChar = text.charAt(s + j);
				int lastIndex = badCharTable.getOrDefault(badChar, -1);
				// Shift pattern so that badChar in text aligns with last occurrence in pattern
				s += Math.max(1, j - lastIndex);
			}
		}
		return indicesToString(matches);
	}
}

//GoCrazy algorithm: hybrid of naive search and rolling hash
class GoCrazy extends Solution {
	static {
		SUBCLASSES.add(GoCrazy.class);
		System.out.println("GoCrazy registered");
	}

	private static final int PRIME = 31;
	private static final long MODULUS = 1000000007L;

	public GoCrazy() {
	}

	// Simple naive search for short patterns (low overhead)
	private List<Integer> naiveSearch(String text, String pattern) {
		List<Integer> matches = new ArrayList<>();
		int n = text.length();
		int m = pattern.length();
		for (int i = 0; i <= n - m; i++) {
			boolean match = true;
			for (int j = 0; j < m; j++) {
				if (text.charAt(i + j) != pattern.charAt(j)) {
					match = false;
					break;
				}
			}
			if (match)
				matches.add(i);
		}
		return matches;
	}

	// Compute hash for first window
	private long computeInitialHash(String str, int m) {
		long hash = 0;
		for (int i = 0; i < m; i++) {
			hash = (hash * PRIME + str.charAt(i)) % MODULUS;
		}
		return hash;
	}

	// Check characters directly after hash match
	private boolean checkMatch(String text, String pattern, int start) {
		for (int j = 0; j < pattern.length(); j++) {
			if (text.charAt(start + j) != pattern.charAt(j))
				return false;
		}
		return true;
	}

	@Override
	public String Solve(String text, String pattern) {
		int m = pattern.length();
		int n = text.length();

		// Handle empty pattern
		if (m == 0) {
			List<Integer> matches = new ArrayList<>();
			if (n == 0)
				matches.add(0);
			return indicesToString(matches);
		}
		if (n < m)
			return indicesToString(new ArrayList<>());

		// Use naive search for very short patterns
		if (m <= 5)
			return indicesToString(naiveSearch(text, pattern));

		// Rolling hash for longer patterns
		List<Integer> matches = new ArrayList<>();
		long patternHash = computeInitialHash(pattern, m);
		long textHash = computeInitialHash(text, m);

		long powerM = 1; // PRIME^(m-1)
		for (int i = 0; i < m - 1; i++)
			powerM = (powerM * PRIME) % MODULUS;

		for (int i = 0; i <= n - m; i++) {
			if (textHash == patternHash) {
				if (checkMatch(text, pattern, i))
					matches.add(i);
			}
			if (i < n - m) {
				// Update rolling hash
				textHash = (textHash - (text.charAt(i) * powerM) % MODULUS + MODULUS) % MODULUS;
				textHash = (textHash * PRIME) % MODULUS;
				textHash = (textHash + text.charAt(i + m)) % MODULUS;
			}
		}
		return indicesToString(matches);
	}
}
