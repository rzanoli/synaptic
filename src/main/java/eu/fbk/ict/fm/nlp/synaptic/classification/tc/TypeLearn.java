package eu.fbk.ict.fm.nlp.synaptic.classification.tc;

import java.io.PrintWriter;
import java.io.StringWriter;
import java.util.logging.Level;
import java.util.logging.Logger;

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

/**
 * TypeLearn is the class that implements the classifier for training a new
 * model on a given dataset annotated with 'type' label The classifier can be
 * used from Command Line Interface or its API by calling the method 'run', e.g.,
 * 
 * CLI:
 * 
 * 		java TypeLearn -f datasetFileName -m modelFileName
 * 
 * API: 
 * 
 * 		TypeLearn typeLearn = new TypeLearn(); 
 * 		typeLearn.run(datasetFileName, modelFileName);
 *
 *
 * WHERE: 
 * 
 * 		datasetFileName is the name of the file containing the training dataset for training the classifier 
 * 		modelFileName is the model to generate
 * 
 * 
 * Produced files:
 * 
 * 		modelFileName							the generated model to use for annotating new examples
 * 		modelFileName.features.index			the features index to use for annotating new examples
 *      modelFileName.labels.index				the labels index to use for annotating new examples
 * 		datasetFileName.tc.token				the pre-processed dataset in input to use for debugging
 * 		datasetFileName.tc.token.vectors		the features vectors of the dataset in input to use for debugging
 * 
 * 
 * @author zanoli
 * 
 * @since December 2017
 *
 */
public class TypeLearn extends AbstractLearn {

	// the logger
	private static final Logger LOGGER = Logger.getLogger(TypeLearn.class.getName());

	// disable stop words removal
	private static boolean enableStopWordsRemoval = true;
	// the preprocessor for pre-processing data
	private Preprocessor preprocessor;
	// the feature extractor for extrating the features from the dataset
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
	 * Trains the classifier on the given dataset and produces the model to use
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
		
		LOGGER.info("Learning...");
		// learn the classifier
		learn(feacturesVectorFileName, modelFileName);
		LOGGER.info("done.");

	}

	/**
	 * The classifier entry point
	 * 
	 * Usage: java TypeLearn -f dataSet -m model
	 * 
	 * WHERE:
	 * 
	 * dataSet is the name of the file containing the training dataset for
	 * training the classifier model is the model file name to generate
	 * 
	 */
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

		// create the command line parser
		CommandLineParser parser = new BasicParser();
		// the formatter for parse exception
		HelpFormatter formatter = new HelpFormatter();
		StringWriter out = new StringWriter();
		PrintWriter pw = new PrintWriter(out);

		try {

			// parse the command line arguments
			CommandLine cmd = parser.parse(options, args);

			// the training dataset
			String dataSetFileName = cmd.getOptionValue("file");
			// the model to generate
			String modelFileName = cmd.getOptionValue("model");
			// create an instance of the classifier
			TypeLearn typeLearn = new TypeLearn();
			// run the classifier
			typeLearn.run(dataSetFileName, modelFileName);

		} catch (ParseException e) {

			formatter.printHelp(pw, 80, "", "TypeLearn", options, formatter.getLeftPadding(),
					formatter.getDescPadding(), "");
			pw.flush();
			LOGGER.log(Level.WARNING, out.toString());

		} catch (Exception ex) {

			LOGGER.log(Level.SEVERE, ex.getMessage());

		}

	}

}
