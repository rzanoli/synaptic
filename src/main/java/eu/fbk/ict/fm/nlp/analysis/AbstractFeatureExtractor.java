package eu.fbk.ict.fm.nlp.analysis;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileInputStream;
import java.io.InputStreamReader;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Set;


public class AbstractFeatureExtractor {
	
	// the features index containing the mapping between the produced features and their numeric IDs used by the classifiers
	// this index is produced during the classifier training phase and then used during the classifier test phase
	protected HashMap<String, Integer> featuresIndex;
	// it is used to create a mapping between the labels of the annotated data set and their numeric IDs used by the classifiers
	// this index is produced during the classifier training phase and then used during the classifier test phase
	protected HashMap<String, Integer> labelsIndex;
	// the labels index containing the mapping between the labels of the annotated dataset and their numeric IDs used 
	// to by the classifiers to change from their numeric predictions to the corresponding labels.
	protected HashMap<Double, String> inverseLabelsIndex;
	// the list of stop words that have to be removed from the pre-processed text in input
	protected Set<String> stopWords;
	// enable stop words removal
	protected boolean enableStopWordsRemoval;

	/**
	 * Generates the n-grams (i.e., bigrams) of the given input tokens 
	 * 
	 * @param tokens the tokens
	 * @return the generated n-grams
	 * @throws Exception
	 */
	public String[] generateNGrams(String[] tokens) throws Exception {

		// unigrams and bigrams
		String[] result = new String[tokens.length + tokens.length - 1];

		// copy unigrams
		for (int i = 0; i < tokens.length; i++)
			result[i] = tokens[i];

		// generate bigrams
		for (int i = 0; i < tokens.length - 1; i++)
			result[tokens.length + i] = tokens[i] + "___" + tokens[i + 1];

		return result;

	}
	
	/**
	 * Removes the stop words from the input tokens
	 * 
	 * @param tokens the input tokens
	 * @return the tokens in input with the stop words removed
	 * 
	 * @throws Exception
	 */
	public String[] removeStopWords(String[] tokens) throws Exception {

		List<String> tmpList = new ArrayList<String>();
		for (int i = 0; i < tokens.length; i++)
			if (!this.stopWords.contains(tokens[i]))
				tmpList.add(tokens[i]);

		String[] result = tmpList.toArray(new String[tmpList.size()]);
		
		return result;

	}
	
	/**
	 * Normalizes the input tokens to lower case 
	 * 
	 * @param tokens the input tokens
	 * @return the normalized tokens
	 * 
	 * @throws Exception
	 */
	public String[] normalizeToLowerCase(String[] tokens) throws Exception {

		String[] result = new String[tokens.length];
		
		for (int i = 0; i < tokens.length; i++)
			result[i] = tokens[i].toLowerCase();
		
		return result;

	}

	
	/**
	 * Loads the stop words
	 * 
	 * @throws Exception
	 */
	public void loadStopWords() throws Exception {

		// Get model from resources folder
		ClassLoader classLoader = getClass().getClassLoader();
		
		BufferedReader buffer = null;

		try {

			File file = new File(classLoader.getResource("stopwords-de.txt").getFile());
			buffer = new BufferedReader(new InputStreamReader(new FileInputStream(file), "UTF8"));

			String str;
			while ((str = buffer.readLine()) != null) {
				stopWords.add(str);
			}

		} catch (Exception ex) {
			throw (ex);
		} finally {
			if (buffer != null)
				buffer.close();
		}
		
	}
	
	/**
	 * Gets the label string given its numeric id
	 * 
	 * @param the label id
	 * 
	 * @return the label string
	 * 
	 */
	public String getLabel(double labelId) {
		
		String label = this.inverseLabelsIndex.get(labelId);
		
		return label;
		
	}
	
	

}
