package eu.fbk.ict.fm.nlp.synaptic.analysis;

import java.io.IOException;

import opennlp.tools.util.InvalidFormatException;

/**
 * ITokenizerWrapper is the interface implemented by TokenizerWrapper for tokenizing data.
 * two methods have to be implemented: 
 * {@link tokenize(String content)} accepts in input a string text and returns an array of tokenized tokens.
 * {@link tokenize(String fileIn, String fileOut)} accepts in input a tsv file containing the text to tokenize, analyzes it and then saves the 
 * produced tokenized into the provided output file. The input file is in the format @see eu.fbk.ict.fm.nlp.analysis.FileTSV and the 
 * text to tokenize is the one in the filed namely 'content'. The output is exactly the same input but with the field 'content' tokenized.
 * 
 * @author zanoli
 */
public interface ITokenizerWrapper {
	
	/**
	 * Initializes the tokenizer by loading the needed resources (e.g., the
	 * model for tokenization)
	 */
	public void init();
	
	/**
	 * Tokenizes the input raw text and returns it as an array of tokenized tokens
	 * 
	 * @param text
	 *            the text to tokenize
	 * @return the tokenized text
	 * 
	 * @exception InvalidFormatException
	 * @exception IOException
	 * @exception Exception
	 * 
	 */
	public String[] tokenize(String content) throws Exception;
	
	/**
	 * Tokenizes the text that is in the input file (field 'content') in the tsv format and saves the result into the output file.
	 * The output contains exactly the same data of the input file but with the field 'content' tokenized.
	 * 
	 * @param fileIn
	 *            the input file in tsv format and containing the content field to tokenize
	 * @param fileOut
	 *            the output file in tsv format and containing the tokenized content field
	 *            
	 * @exception Exception
	 * 
	 */
	public void tokenize(String fileIn, String fileOut) throws Exception;

}
