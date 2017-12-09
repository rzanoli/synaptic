package eu.fbk.ict.fm.nlp.synaptic.classification.tc;

//import java.util.logging.Logger;

import org.apache.commons.cli.BasicParser;
import org.apache.commons.cli.CommandLine;
import org.apache.commons.cli.CommandLineParser;
import org.apache.commons.cli.HelpFormatter;
import org.apache.commons.cli.Option;
import org.apache.commons.cli.Options;
import org.apache.commons.cli.ParseException;

import eu.fbk.ict.fm.nlp.synaptic.analysis.FeatureExtractorLearn;
import eu.fbk.ict.fm.nlp.synaptic.analysis.FileTSV;
import eu.fbk.ict.fm.nlp.synaptic.analysis.Preprocessor;
import eu.fbk.ict.fm.nlp.synaptic.classification.AbstractLearn;

public class TypeLearn extends AbstractLearn {

	// the logger
	// private static final Logger LOGGER =
	// Logger.getLogger(SemanticLearn.class.getName());

	// enable stop words removal
	private static boolean enableStopWordsRemoval = false;
	// the preprocessor for pre-processing data
	private Preprocessor preprocessor;
	// the feature extractor for extrating the features from the data set
	private FeatureExtractorLearn featureExtractor;

	/**
	 * Class constructor; it initializes the classifier loading all the required
	 * resources
	 * 
	 * @param modelFileName
	 *            the model to use for classifying data
	 */
	public TypeLearn() throws Exception {

		// initializes the preprocessor
		preprocessor = new Preprocessor();
		// initializes the feature extractor
		featureExtractor = new FeatureExtractorLearn(enableStopWordsRemoval);

	}

	/**
	 * Trains the classifier on the given data set and produces the model to use
	 * in classification
	 * 
	 * @param dataSetFileName
	 *            the dataset to use for training the classifier
	 * 
	 * @return modelFileName the file name of the model to produce
	 * 
	 * @throws Exception
	 * 
	 */
	public void run(String dataSetFileName, String modelFileName) throws Exception {

		// pre-process the given dataset and puts the output in the file with
		// extension '.token'
		String preprocessedDataSetFileName = dataSetFileName + ".tc.token";
		preprocessor.process(dataSetFileName, preprocessedDataSetFileName);
		// extract the features from the pre-processed dataset and produces 3
		// files:
		// --the features vectors
		// --the features index containing the mapping between the features and
		// their indexes used by the classifier
		// --the labels index containing the mapping between the labels and
		// their indexes used by the classifier
		String feacturesVectorFileName = preprocessedDataSetFileName + ".vectors";
		String featuresIndexFileName = modelFileName + ".features.index";
		String labelsIndexFileName = modelFileName + ".labels.index";
		int datasetLabelPosition = FileTSV.TYPE;
		featureExtractor.extract(preprocessedDataSetFileName, feacturesVectorFileName, featuresIndexFileName,
				labelsIndexFileName, datasetLabelPosition);
		// learn the classifier
		learn(feacturesVectorFileName, modelFileName);

	}

	public static void main(String[] args) {

		// create Options object
		Options options = new Options();

		// add data set option
		Option file = new Option("f", "file", true, "dataset for training the classifier");
		file.setRequired(true);
		options.addOption(file);

		// add data set option
		Option model = new Option("m", "model", true, "model to produce");
		model.setRequired(true);
		options.addOption(model);

		// add data set option
		// Option validation = new Option("v", "cross_validation", false, "perform cross validation");
		// validation.setRequired(false);
		// options.addOption(validation);

		// add data set option
		// Option c = new Option("c", "C", false, "svm parameter: trade-off between training error");
		// validation.setRequired(false);
		// options.addOption(c);

		// add data set option
		// Option eps = new Option("e", "eps", false, "svm parameter: allows that error for termination criterion");
		// validation.setRequired(false);
		// options.addOption(eps);

		// create the command line parser
		CommandLineParser parser = new BasicParser();
		HelpFormatter formatter = new HelpFormatter();

		try {

			// parse the command line arguments
			CommandLine cmd = parser.parse(options, args);

			String dataSetFileName = cmd.getOptionValue("file");
			String modelFileName = cmd.getOptionValue("model");

			TypeLearn semanticLearn = new TypeLearn();
			semanticLearn.run(dataSetFileName, modelFileName);

			// src/main/java/dataset_example.tsv

		} catch (ParseException e) {

			formatter.printHelp("TypeLearn", options);

		} catch (Exception ex) {

			System.err.println(ex.getMessage());

		}

	}

}
