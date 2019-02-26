import java.util.HashMap;
import java.util.ArrayList;
import java.util.Map;
import java.util.Random;
import java.util.Collections;
import java.io.FileReader;
import java.io.BufferedReader;
import java.io.IOException;

/**
 *   Extraction of words from a text.
 *   <p>
 *   This class loads words from text and counts occurrences.
 *   It provides methods for retrieving the most frequent words.
 */
public class TextAnalyzer {

	private HashMap<String,Integer> _hm = null;
	private ArrayList<WordCounter>  _al = null;

	/**
	 *     Utility class.
	 *     <p>
	 *     It packs together one word and the number of times
	 *     it appears.
	 */
	public class WordCounter implements Comparable<WordCounter> {
		private String _w;
		private int    _cnt;

		/**
		 *    Plain constructor.
		 *
		 *    @param w A word.
		 *    @param cnt The number of times it appears in the text.
		 */
		public WordCounter(String w, int cnt) {
			_w = w;
			_cnt = cnt;
		}

		/**
		 *    Getter.
		 *
		 *    @return A word.
		 */
		public String getWord() {
			return _w;
		}

		/**
		 *    Getter.
		 *
		 *    @return A number of occurrences.
		 */
		public int getCount() {
			return _cnt;
		}

		/**
		 *    Comparator of two WordCounter objects.
		 *    <p>
		 *    Note that as we are interested in most
		 *    frequent objects, the default sort is
		 *    from most frequent to least frequent.
		 *
		 *    @param  wc A WordCounter object to compare to the current one.
		 *    @return -1 if the current object appears more than the one that
		 *            is compared, 1 if it appears less, and if they appear
		 *            the same number of times we fall back to String
		 *            comparison and sort by (regular) alphabetical order.
		 */
		public int compareTo(WordCounter wc) {
			if (wc == null) {
				return 1;
			}
			if (this._cnt > wc._cnt) {
				return -1;
			} else if (this._cnt < wc._cnt) {
				return 1;
			} else {
				return this._w.compareTo(wc._w);
			}
		}
	}

	/**
	 *    Constructor
	 *
	 *    @param fname Name of a file that contains the text to analyze.
	 *    @throws IOException if anything goes wrong with the file.
	 */

	public TextAnalyzer(String fname) throws IOException {
		BufferedReader br = new BufferedReader(new FileReader(fname));
		StringBuffer   sb = new StringBuffer();
		int            i;
		int            j;
		char           c;
		Integer        occ;
		String         w;
		_hm = new HashMap<String,Integer>();
		_al = new ArrayList<WordCounter>();
		while ((i = br.read()) != -1) {
			c = (char)i;
			if (Character.isLetter(c) || (c == '\'')) {
				sb.append(c);
			} else if (Character.isDigit(c)) {
				// Forget
				sb.setLength(0);
			} else { // End of word
				if (sb.length() > 0) {
					// Suppress single quotes at the beginning or end
					while ((sb.length() > 0)
							&& (sb.charAt(0) == '\'')) {
						sb = sb.deleteCharAt(0);
					}
					j = sb.length() - 1;
					while ((j >= 0) && (sb.charAt(j) == '\'')) {
						sb = sb.deleteCharAt(j);
						j--;
					}
					if (sb.length() > 0) {
						w = sb.toString().toLowerCase();
						occ = _hm.get(w);
						if (occ == null) {
							_hm.put(w, 1);
						} else {
							_hm.put(w, occ + 1);
						} 
						sb.setLength(0);

					}
				}
			}
		}
		for (Map.Entry<String,Integer> me: _hm.entrySet()) {
			_al.add(new WordCounter(me.getKey(), me.getValue()));
		}
		Collections.sort(_al);
	}

	@SuppressWarnings("resource")
	public TextAnalyzer(String filename, String stopWords) throws IOException {
		int i;
		Integer occurence = 0;
		String word = "";
		String[] wordSplit;
		_hm = new HashMap<String,Integer>();
		_al = new ArrayList<WordCounter>();
		BufferedReader br = new BufferedReader(new FileReader(filename));
		BufferedReader brStop = new BufferedReader(new FileReader(stopWords));
		ArrayList<String> al = new ArrayList<String>();
		ArrayList<String> alStop = new ArrayList<String>();
		char[] charLigne;
		String ligne = br.readLine();
		String ligneStop = brStop.readLine();
		while (ligne != null) {
			
			ligne = ligne.toLowerCase();
			charLigne = ligne.toCharArray();

			for (i = 0; i<charLigne.length; i++) {
				ligne = ligne.replaceAll("[^\\w]", " ");
				ligne = ligne.replaceAll("_", " ");
				ligne = ligne.replaceAll("[0123456789]", "");
			}

			wordSplit = ligne.split(" ");
			
			for (String s : wordSplit) {
				al.add(s);
			}
			
			ligne = br.readLine();
		}

		wordSplit = null;

		while (ligneStop != null) {
			alStop.add(ligneStop);
			ligneStop = brStop.readLine();
		}
		
		//Remove stopWords
		for (int j = 1; j<al.size(); j++) {
			int k = 0;
			String words = al.get(j);

			for (k = 0; k<alStop.size(); k++) {
				if (words.equals(alStop.get(k))){
					al.remove(words);
					j-=1;
				}
			}

		}

		if (al.size() > 0) {
			for (i = 0; i<al.size(); i++) {
				word = (String) al.get(i);
				occurence = _hm.get(word);
				if (occurence == null) {
					_hm.put(word, 1);
				} else {
					_hm.put(word, occurence + 1);
				} 
			}
		}
		for (Map.Entry<String,Integer> me: _hm.entrySet()) {
			_al.add(new WordCounter(me.getKey(), me.getValue()));
		}
		Collections.sort(_al);
	}

	/**
	 *     Returns the most frequent words.
	 *     <p>
	 *     Words are returned in lower case. Note that if some words
	 *     appear the same number of times they are all returned. It is
	 *     thus possible to get back an array that contains more values
	 *     than required.
	 *
	 *     @param n The number of words to return.
	 *     @return An array of WordCounter objects, sorted from most frequent
	 *             to least frequent.
	 */
	public WordCounter[] topWords(int n) {
		if (n <= 0) {
			return null;
		}
		
		int i = 0;
		
		while ((i < n) || _al.get(i).getCount() == _al.get(i-1).getCount()) {
			i++;
		}

		WordCounter[] arr = new WordCounter[n];
		
		//During spliting in Textanalyzer, some blank are stocked at position 0 during sorting.
		_al.remove(_al.get(0));

		return _al.subList(0, n).toArray(arr);
	}

	/**
	 * 
	 * @param words Array of WordCounter which contains words with their occurrences sorted.
	 * @return String with the HTML code to design a tag cloud
	 */
	public static String HTMLCloud(WordCounter[] words) {
		int i = 0;
		StringBuilder script = new StringBuilder();
		script.append("<!DOCTYPE html><html><body><div>");
		String[] orientationWord = {"horizontal-tb", "vertical-rl"};
		String[] colorCode = {"000000","FF0000","00FF00","0000FF","FFFF00","00FFFF","FF00FF","C0C0C0"};

		shuffleArray(words);
		
		for (WordCounter count : words) {
			script.append("<style> span.div" + i + " { writing-mode: " + orientationWord[(int) Math.floor(Math.random() * orientationWord.length)] + "; } </style>");
			script.append("<span class=\"div" + i + "\" style=\"color:#" + colorCode[(int) Math.floor(Math.random() * colorCode.length)] + ";font-size: " + (count._cnt*10) + "px\">");
			script.append(count._w + "</span>  ");
			i+=1;
		}

		script.append("</div></body></html> ");

		return script.toString();
	}
	
	/**
	 * This method shuffle the array of WordCounter in parameter
	 * 
	 * @param wc Array of WordCounter 
	 */
	public static void shuffleArray(WordCounter[] wc) {
		Random rand = new Random();
		WordCounter var;
		int j;
		for (int i = 0; i<wc.length; i++) {
			j = rand.nextInt(wc.length);
			var = wc[i];
			wc[i] = wc[j];
			wc[j] = var;
		}
	}
}

