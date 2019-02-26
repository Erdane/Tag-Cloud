public class Main {

	public static void main(String[] args) {
		String speech ="C:\\Users\\mahob\\eclipse-workspace\\TD3 Génie Logiciel\\speechObama.txt";//"C:\\Users\\mahob\\Desktop\\livre.txt";
		String stopwords = "C:\\Users\\mahob\\eclipse-workspace\\TD3 Génie Logiciel\\stopwords.txt";
		String script; 

		try {
			TextAnalyzer ta = null;
			long start = System.currentTimeMillis();
			ta = new TextAnalyzer(speech, stopwords);
			//ta = new TextAnalyzer(speech);

			TextAnalyzer.WordCounter[] arr = ta.topWords(50);

			long end = System.currentTimeMillis();
			System.err.println("Milliseconds:" + Long.toString(end - start));

			for (TextAnalyzer.WordCounter wc: arr) {
				System.out.println(wc.getWord() + ":\t" + wc.getCount());
			}

			script = TextAnalyzer.HTMLCloud(arr);
			System.out.println(script);

		} catch (Exception e) {
			e.printStackTrace();
		}



	}



}
