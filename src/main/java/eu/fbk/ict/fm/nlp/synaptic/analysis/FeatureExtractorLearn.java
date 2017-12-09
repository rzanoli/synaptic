package eu.fbk.ict.fm.nlp.synaptic.analysis;

import java.io.BufferedReader;
import java.io.BufferedWriter;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.InputStreamReader;
import java.io.OutputStreamWriter;
import java.util.HashMap;
import java.util.HashSet;
import java.util.logging.Logger;

/**
 * FeatureExtractorLearn is used during the classifier training phase to extract some features (e.g., n-grams) from the pre-processed training dataset. Tha dataset
 * has been already pre-processed by the Preprocessor @see eu.fbk.ict.fm.nlp.analysisPreprocessor. The output is a file
 * containing the features index, a file with the labels index and the file containing the features vectors of the given dataset.
 * 
 * @author zanoli
 */
public class FeatureExtractorLearn extends AbstractFeatureExtractor {

	// the logger
	private static final Logger LOGGER = Logger.getLogger(FeatureExtractorLearn.class.getName());

	/**
	 * Class constructor that initialized some data structures, and loads the stop words
	 */
	public FeatureExtractorLearn(boolean enableStopWordsRemoval) throws Exception {
		
		this.featuresIndex = new HashMap<String, Integer>();
		this.labelsIndex = new HashMap<String, Integer>();
		this.inverseLabelsIndex = new HashMap<Double, String>();
		this.enableStopWordsRemoval = enableStopWordsRemoval;
		this.stopWords = new HashSet<String>();
		// load the list of stop words that are in the resources directory
		if (enableStopWordsRemoval)
			loadStopWords();

	}

	/**
	 * Extracts some features from the given dataset in input and produces the features vectors. Other files
	 * produced are the one of the features index and the one of the labels index. 
	 * 
	 * @param datasetFileName the pre-processed dataset in input
	 * @param featuresVectorFileName the file of the features vectors
	 * @param featuresIndexFileName the file of the features index
	 * @param labelsIndexFileName the file of the labels index
	 * @param datasetLabelIndex the label field position, in the input dataset containing the label (i.e, semantic|type)
	 * 
	 * @throws Exception
	 */
	public void extract(String datasetFileName, 
			String featuresVectorFileName, 
			String featuresIndexFileName, 
			String labelsIndexFileName, 
			int datasetLabelIndex) throws Exception {

		LOGGER.info("Extracting features....");

		// the pre-processed dataset in input
		BufferedReader in = null;
		// the file that will contain the features index
		BufferedWriter outFeaturesIndex = null;
		// the file that will contain the labels index
		BufferedWriter outLabelsIndex = null;
		// the file that will contain the generated features vectors
		BufferedWriter outFeaturesVector = null;

		try {

			in = new BufferedReader(new InputStreamReader(new FileInputStream(datasetFileName), "UTF8"));
			outLabelsIndex = new BufferedWriter(
					new OutputStreamWriter(new FileOutputStream(labelsIndexFileName), "UTF-8"));
			outFeaturesIndex = new BufferedWriter(
					new OutputStreamWriter(new FileOutputStream(featuresIndexFileName), "UTF-8"));
			outFeaturesVector = new BufferedWriter(
					new OutputStreamWriter(new FileOutputStream(featuresVectorFileName), "UTF-8"));

			String str;
			int lineCounter = 0;
			// for each example in the pre-processed dataset
			while ((str = in.readLine()) != null) {

				lineCounter++;

				if (lineCounter == 1) // this line contains the fields names
										// (e.g., start/end time, semantic)
					continue;

				// check if the number of fields of the given file is correct
				String[] splitLine = str.split("\t");
				if (splitLine.length != FileTSV.FIELDS_NUMBER)
					throw new Exception("Invalid Format Exception");

				// get the label of the current example
				String label = splitLine[datasetLabelIndex];
				int index = 0;
				if (labelsIndex.containsKey(label))
					index = labelsIndex.get(label);
				else {
					index = labelsIndex.size() + 1; // labels start from index 1
					labelsIndex.put(label, index);
					outLabelsIndex.write(label + " " + index + "\n"); // update the labels index
				}
				outFeaturesVector.write(String.valueOf(index)); // print the
																// label as the
																// first element
																// of the
																// feature
																// vector

				// get the pre-processed content
				String[] preprocessedContent = splitLine[FileTSV.CONTENT].split(" ");
				// token normalization
				preprocessedContent = normalizeToLowerCase(preprocessedContent);
				// stop words removal
				if (enableStopWordsRemoval)
					preprocessedContent = removeStopWords(preprocessedContent);

				// generate the features vector
				String[] features = generateNGrams(preprocessedContent);
				for (String feature : features) {
					int featureIndex = 0;
					if (featuresIndex.containsKey(feature))
						featureIndex = featuresIndex.get(feature);
					else {
						featureIndex = featuresIndex.size() + 1;
						featuresIndex.put(feature, featureIndex);
						outFeaturesIndex.write(feature + " " + featureIndex + "\n");
					}
					outFeaturesVector.write(" " + featureIndex + ":1"); // print
																		// the
																		// feature
																		// (all
																		// the
																		// features
																		// have
																		// the
																		// same
																		// weight
																		// equals
																		// to 1)
				}
				outFeaturesVector.write("\n");

			}

			LOGGER.info("done.");

		} catch (Exception ex) {
			ex.printStackTrace();
			LOGGER.info(ex.getMessage());
			throw (ex);
		} finally {
			if (in != null)
				in.close();
			if (outLabelsIndex != null)
				outLabelsIndex.close();
			if (outFeaturesIndex != null)
				outFeaturesIndex.close();
			if (outFeaturesIndex != null)
				outFeaturesIndex.close();
			if (outFeaturesVector != null)
				outFeaturesVector.close();
		}

	}

	
	public static void main(String args[]) {

		try {

			FeatureExtractorLearn featureExtractor = new FeatureExtractorLearn(true);
			String dataSet = "src/main/java/dataset.tsv.token";
			String featuresVectors = "src/main/java/dataset.tsv.token.vectors";
			String featuresIndex = "src/main/java/dataset.tsv.token.features.index";
			String labelsIndex = "src/main/java/dataset.tsv.token.labels.index";
			int labelPosition = 2; // semantic
			featureExtractor.extract(dataSet, featuresVectors, featuresIndex, labelsIndex, labelPosition);

		} catch (Exception ex) {
			System.err.println(ex.getMessage());
		}

	}

}
