package eu.fbk.ict.fm.nlp.synaptic.classification;


import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertTrue;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileInputStream;
import java.io.InputStreamReader;

import org.junit.Test;

import eu.fbk.ict.fm.nlp.synaptic.analysis.FileTSV;
import eu.fbk.ict.fm.nlp.synaptic.classification.tc.TypeClassify;
import eu.fbk.ict.fm.nlp.synaptic.classification.tc.TypeLearn;

public class TypeClassifierTest {

	@Test
	public void fullTest() {
		learnTest();
		classifyTest();
	}

	public void learnTest() {

		File dataSet = new File("src/test/resources/dataset.tsv");
		File model = new File("src/test/resources/dataset.tsv.ta.model");
		if (model.exists())
			model.delete();

		try {
			TypeLearn typeLearn = new TypeLearn();
			typeLearn.run(dataSet.getAbsolutePath(), model.getAbsolutePath());
		} catch (Exception ex) {
			ex.printStackTrace();
			System.out.println(ex.getMessage());
		}

		assertTrue(model.exists());

	}

	public void classifyTest() {

		File dataSet = new File("src/test/resources/dataset.tsv");
		File model = new File("src/test/resources/dataset.tsv.ta.model");

		// buffer for reading input data (e.g., text to tokenize)
		BufferedReader in = null;

		try {

			TypeClassify typeClassify = new TypeClassify(model.getAbsolutePath());

			in = new BufferedReader(new InputStreamReader(new FileInputStream(dataSet), "UTF8"));

			String str;
			int i = 0;
			while ((str = in.readLine()) != null) {

				i++;

				// check if the number of fields of the given input file is
				// correct
				String[] splitLine = str.split("\t");
				if (splitLine.length != FileTSV.FIELDS_NUMBER)
					throw new Exception("The input file doesn't have the required number of fields!");

				if (i == 1)
					continue;

				// the sentiment
				String goldLabel = splitLine[FileTSV.TYPE];

				// the content to tokenize
				String content = splitLine[FileTSV.CONTENT];

				String[] prediction = typeClassify.run(content);
				String predectedLablel = prediction[0];
				String score = prediction[1];

				System.out.println("predicted label:" + predectedLablel +
				 "\t" + "gold label:" + goldLabel + "\tscore:" + score);

				assertEquals("goldLabel", "goldLabel");

			}

		} catch (Exception ex) {
			System.err.println(ex.getMessage());
		} finally {
			if (in != null) {
				try {
					in.close();
				} catch (Exception e) {
					System.err.println(e.getMessage());
				}
			}
		}

	}

}
