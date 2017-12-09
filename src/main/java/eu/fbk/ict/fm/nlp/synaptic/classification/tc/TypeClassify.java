package eu.fbk.ict.fm.nlp.synaptic.classification.tc;

import org.apache.commons.cli.BasicParser;
import org.apache.commons.cli.CommandLine;
import org.apache.commons.cli.CommandLineParser;
import org.apache.commons.cli.HelpFormatter;
import org.apache.commons.cli.Option;
import org.apache.commons.cli.Options;
import org.apache.commons.cli.ParseException;

import eu.fbk.ict.fm.nlp.synaptic.analysis.FeatureExtractorClassify;
import eu.fbk.ict.fm.nlp.synaptic.analysis.Preprocessor;
import eu.fbk.ict.fm.nlp.synaptic.classification.AbstractClassify;
import libsvm.svm;


public class TypeClassify extends AbstractClassify {

	// the logger
	// private static final Logger LOGGER =
	// Logger.getLogger(SemanticClassify.class.getName());

	// enable stop words removal
	private static boolean enableStopWordsRemoval = false;

	// the preprocessor for pre-processing data
	private Preprocessor preprocessor;
	// the feature extractor for producing the features vectors
	private FeatureExtractorClassify featureExtractor;

	/**
	 * Class constructor; it uses the model generated during the classifier training phase and the 
	 * two other files (modelFileName.features.index, modelFileName.labels.index) always produced by the classifier while training
	 * to initializes the classifier itself as well as the pipeline for pre-processing data to be annotated.
	 * 
	 * @param modelFileName the model to use for classifying data
	 */
	public TypeClassify(String modelFileName) throws Exception {

		// load the model generated during the classifier training phase
		model = svm.svm_load_model(modelFileName);
		// initialize the preprocessor for pre-processing data
		preprocessor = new Preprocessor();
		// the index of the features and labels generated during the training phase to produce the model
		String featuresIndexFileName = modelFileName + ".features.index";
		String labelsIndexFileName = modelFileName + ".labels.index";
		// initialize the feature extractor for generating the features from the dataset
		featureExtractor = new FeatureExtractorClassify(featuresIndexFileName, labelsIndexFileName,
				enableStopWordsRemoval);

	}

	/**
	 * Classifiers the given content; it returns the predicted label and its score 
	 * 
	 * @param content the content text to classify
	 * 
	 * @return the label assigned to the given content and a score value
	 * 
	 * @throws Exception
	 * 
	 */
	public String[] run(String content) throws Exception {

		String[] result = null;

		try {

			// pre-process the content
			String[] preprocessedContent = preprocessor.process(content);
			// extract the features vector
			String[] featuresVector = featureExtractor.extract(preprocessedContent);
			// classify
			double[] prediction = classify(featuresVector);
			// get the predicted label
			String label = featureExtractor.getLabel(prediction[0]);
			// and its score
			String score = String.valueOf(prediction[1]);
			result = new String[2];
			result[0] = label;
			result[1] = score;

		} catch (Exception ex) {
			ex.printStackTrace();
		}

		return result;

	}

	public static void main(String[] args) {

		// create Options object
		Options options = new Options();

		// add data set option
		Option content = new Option("c", "content", true, "content to classify");
		content.setRequired(true);
		options.addOption(content);

		// add model set option
		Option model = new Option("m", "model", true, "generated model");
		model.setRequired(true);
		options.addOption(model);

		// create the command line parser
		CommandLineParser parser = new BasicParser();
		HelpFormatter formatter = new HelpFormatter();

		try {

			// parse the command line arguments
			CommandLine cmd = parser.parse(options, args);

			String text = cmd.getOptionValue("content");
			String modelFileName = cmd.getOptionValue("model");

			TypeClassify semanticClassify = new TypeClassify(modelFileName);
			String[] result = semanticClassify.run(text);
			String label = result[0];
			String score = result[1];

			System.out.println("predicted label:" + label + " score:" + score);

		} catch (ParseException e) {

			formatter.printHelp("TypeClassify", options);
			
		} catch (Exception ex) {

			System.err.println(ex.getMessage());

		}

	}

}
