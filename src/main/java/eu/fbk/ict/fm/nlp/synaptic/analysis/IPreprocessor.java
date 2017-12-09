package eu.fbk.ict.fm.nlp.synaptic.analysis;

/**
 * IProcessor is the interface of the class Processor for pre-processing input data (e.g., tokenization, lemmatization).
 * two methods have to be implemented: 
 * {@link process(String text)} accepts in input a text (i.e., the content) and returns an array of pre-processed tokens.
 * {@link process(String fileIn, String fileOut)} accepts in input a tsv file containing the text (i.e., the content) to pre-process, analyzes it and then saves the 
 * produced tokens into the given output file. The input file is a tsv file containing the fields: id, start/end time, semantic, type and content. 
 * The output is exactly the input file but with the field 'content' pre-processed.
 * 
 * @author zanoli
 */
public interface IPreprocessor {

	/**
	 * Pre-process the input text and returns an array of tokens
	 * 
	 * @param text
	 *            the text to pre-process
	 * @return a list of pre-processed tokens
	 * 
	 * @exception Exception
	 * 
	 */
	public String[] process(String text) throws Exception;
	
	/**
	 * Pre-process the text that is in the input file (in the 'content' field) and saves the result into the output file.
	 * The output file is exactly the input file file with the field text in the field 'content' pre-processed.
	 * 
	 * @param fileIn
	 *            the input file in tsv format and containing the field 'content' to pre-process
	 * @param fileOut
	 *            the output file in tsv format and containing the field 'content' pre-processed
	 *            
	 * @exception Exception
	 * 
	 */
	public void process(String fileIn, String fileOut) throws Exception;
	
}
