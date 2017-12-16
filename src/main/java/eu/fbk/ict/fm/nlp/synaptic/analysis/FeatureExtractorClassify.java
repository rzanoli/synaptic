package eu.fbk.ict.fm.nlp.synaptic.analysis;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileInputStream;
import java.io.InputStreamReader;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;

/**
 * FeatureExtractorClassify is used during the classifier annotation phase to
 * produce the features vectors of the examples in input and that have already
 * been pre-processed by the Preprocessor component @see
 * eu.fbk.ict.fm.nlp.analysisPreprocessor. The output is an array of features
 * corresponding to the features vector of the given example in input.
 * 
 * @author zanoli
 * 
 * @since December 2017
 * 
 */
public class FeatureExtractorClassify extends AbstractFeatureExtractor {

	// the logger
	// private static final Logger LOGGER =
	// Logger.getLogger(FeatureExtractorClassify.class.getName());

	/**
	 * Class constructor that initializes some data structures and loads the
	 * stop words, the features index and the labels index produced during the
	 * classifier training phase.
	 * 
	 * @param featuresIndexFileName
	 *            the file containing the features index
	 * @param labelsIndexFileName
	 *            the file containing the labels index
	 * @param enableStopWordsRemoval
	 *            true for enabling stop words removal; false otherwise
	 * 
	 */
	public FeatureExtractorClassify(String featuresIndexFileName, String labelsIndexFileName,
			boolean enableStopWordsRemoval) throws Exception {

		this.featuresIndex = new HashMap<String, Integer>();
		this.labelsIndex = new HashMap<String, Integer>();
		this.inverseLabelsIndex = new HashMap<Double, String>();
		this.enableStopWordsRemoval = enableStopWordsRemoval;
		this.stopWords = new HashSet<String>();

		// load the list of stop words that are in the resources directory
		if (enableStopWordsRemoval)
			loadStopWords();

		// load the feaures index and labels index
		loadFeaturesIndex(new File(featuresIndexFileName));
		loadLabelsIndex(new File(labelsIndexFileName));

	}

	/**
	 * Extracts the features vector from the given pre-processed text in input
	 * 
	 * @param text
	 *            the pre-processed text
	 * @return the features vector
	 * 
	 * @throws Exception
	 */
	public String[] extract(String[] text) throws Exception {

		List<String> tmpList = new ArrayList<String>();

		// text normalization
		String[] normalizedText = normalizeToLowerCase(text);
		// stop words removal
		if (enableStopWordsRemoval)
			normalizedText = removeStopWords(normalizedText);
		// generate the features vector; all the produced features have the same
		// weight equals to 1
		String[] features = generateNGrams(normalizedText);
		for (String feature : features) {
			int featureIndex = 0;
			if (featuresIndex.containsKey(feature)) {
				featureIndex = featuresIndex.get(feature);
				tmpList.add(featureIndex + ":1");
			}
		}

		String[] result = tmpList.toArray(new String[tmpList.size()]);

		return result;

	}

	/**
	 * Loads the labels index produced during the classifier training phase
	 * 
	 * @param file
	 *            the labels index file name
	 * 
	 * @throws Exception
	 */
	private void loadLabelsIndex(File file) throws Exception {

		BufferedReader buffer = null;

		try {

			buffer = new BufferedReader(new InputStreamReader(new FileInputStream(file), "UTF8"));

			String str;
			while ((str = buffer.readLine()) != null) {
				String[] splitLine = str.split("\t");
				String label = splitLine[0];
				double index = Double.parseDouble(splitLine[1]);
				this.inverseLabelsIndex.put(index, label);
			}

		} catch (Exception ex) {
			throw (ex);
		} finally {
			if (buffer != null)
				buffer.close();
		}
	}

	/**
	 * Loads the features index produced during the classifier training phase
	 * 
	 * @param file
	 *            the features index file name
	 * 
	 * @throws Exception
	 */
	private void loadFeaturesIndex(File file) throws Exception {

		BufferedReader buffer = null;

		try {

			buffer = new BufferedReader(new InputStreamReader(new FileInputStream(file), "UTF8"));

			String str;
			while ((str = buffer.readLine()) != null) {
				String[] splitLine = str.split("\t");
				int index = Integer.valueOf(splitLine[1]);
				String feature = splitLine[0];
				this.featuresIndex.put(feature, index);
			}

		} catch (Exception ex) {
			throw (ex);
		} finally {
			if (buffer != null)
				buffer.close();
		}
	}

}
