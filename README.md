# synaptic

SYNAPTIC library contains of several components for pre-processing data, extracting relevant features and for 'sentiment' and 'type' classification for German.
Training and classification operations are accessible via the SYNAPTIC Application Program Interface (API). In addition, a Command Line Interface (CLI) is provided for convenience of experiments and training.

## Getting started

These instructions will get you a copy of the project up and running on your local machine for development and testing purposes.

### Prerequisites

- Java 1.7
- Apache Maven (http://maven.apache.org) (when using SYNAPTIC API)
- git (when using SYNAPTIC API)

### How to get the code

We provide 2 different distributions namely Jar Distribution and Java Distribution; the first one can be used for running SYNAPTIC from CLI, while the second distribution can be used when you want to use SYNAPTIC as a library from your java code.

#### Jar Distribution (CLI)

SYNAPTIC is distributed as a jar file containing all the Java code for training and testing. It can be download from its gihub repository https://github.com/hltfbk/Excitement-Open-Platform/archive/v{version}.tar.gz]]. 

#### Java Distribution (API)

SYNAPTIC is a Java Maven project that you can download from its gihub repository by running the following command:
git clone https://github.com/GoogleCloudPlatform/j.................................


## CLI Instructions

After getting the Jar Distribution as explained above, you are ready for training the classifer and annotating new datasets. These instructions are valid for both the classifier for 'sentiment' annotation and the classifier for 'type' annotation. In the rest of this section we will report examples only for the 'sentiment' classifier but they remain valid also for 'type' classifier; it is sufficient change the package name from 'sa' to 'tc' and the classifier name prefix from 'Sentiment' to 'Type', i.e.,

- eu.fbk.ict.fm.nlp.synaptic.classification.sa.SentimentLearn --> eu.fbk.ict.fm.nlp.synaptic.classification.tc.TypeLearn
- eu.fbk.ict.fm.nlp.synaptic.classification.sa.SentimentClassify --> eu.fbk.ict.fm.nlp.synaptic.classification.tc.TypeClassify

### Training

java -cp ....jar eu.fbk.ict.fm.nlp.synaptic.classification.sa.SentimentLearn -f datasetFileName -m modelFileName

Where:
	datasetFileName is the name of the file containing the training dataset for training the classifier 
 	modelFileName is the model to generate

Produced files:
 	
modelFileName.sa.model					the generated model to use for annotating new examples
modelFileName.sa.model.features.index	the features index to use for annotating new examples
modelFileName.sa.model.labels.index		the labels index to use for annotating new examples
datasetFileName.sa.token				the pre-processed dataset in input to use for debugging
datasetFileName.sa.token.vectors		the features vectors of the dataset in input that were used for training the classifier to use for debugging

### Classifying

java -cp ....jar eu.fbk.ict.fm.nlp.synaptic.classification.sa.SentimentClassifier -c content -m modelFileName

Where: 
	content is the text string to classify 
	modelFileName is the model generated during the classifier training phase


## API Instructions

SYNAPTIC is a Maven project and after getting the Java Distribution of the project as explained above, you need to install its maven artifactory into your local maven repository (i.e., m2) and put the following dependency into the project file (i.e., pom.xml) of your project.

<dependency>
	<groupId>eu.fbk.ict.fm.nlp</groupId>
	<artifactId>classifier</artifactId>
	<version>1.0-SNAPSHOT</version>
</dependency>

### Installing the code

SYNAPTIC is a Maven project and after getting the Java Distribution of the project as explained above, you need to install its maven artifactory into your local maven repository (i.e., m2); to do that, once you are in the directory that contains the project file (i.e., pom.xml) you have to run the command: mvn install.

### Training

SentimentLearn sentimentLearn = new SentimentLearn();
sentimentLearn.run(datasetFileName, modelFileName);

### Classifying

SentimentClassify sentimentClassify = new SentimentClassify(modelFileName);
String[] annotation = sentimentClassify.run(content); 
String label = annotation[0]; // the predicted label
String score = annotation[1]; // and its score
System.out.println("predicted label:" + label + " score:" + score);

### Example


import java.io.BufferedReader;
import java.io.File;
import java.io.FileInputStream;
import java.io.InputStreamReader;

import eu.fbk.ict.fm.nlp.synaptic.classification.sa.SentimentClassify;
import eu.fbk.ict.fm.nlp.synaptic.classification.sa.SentimentLearn;

public class LearnClassifyTest {

	static String dataSet = "/Users/zanoli/Projects/git-repositories/synaptic-git/supersede-german-trainingset_iesa_userfeedback_from_ticket_system_2016.tsv";
	static String model = "/tmp/supersede-german.sa.model";

	public static void Learn() throws Exception {

		SentimentLearn semanticLearn = new SentimentLearn();
		semanticLearn.run(dataSet, model);

	}

	public static void Classify() throws Exception {

		BufferedReader in = null;

		try {
			SentimentClassify semanticClassify = new SentimentClassify(model);
			File file = new File(dataSet);
			in = new BufferedReader(new InputStreamReader(new FileInputStream(file), "UTF8"));
			String str;
			double examples = 0;
			double correctPredictions = 0;
			while ((str = in.readLine()) != null) {
				String[] splitLine = str.split("\t");
				String content = splitLine[4];
				String goldLabel = splitLine[2]; //sentiment
				String[] annotation = semanticClassify.run(content);
				String label = annotation[0];
				String score = annotation[1];
				System.out.println("predicted label:" + label + " score:" + score);
				if (goldLabel.equals(label))
					correctPredictions++;
				examples++;
			}
			System.out.println(correctPredictions/examples);
			
		} catch (Exception ex) {
			throw(ex);
		} finally {
			if (in != null)
				try {
					in.close();
				} catch (Exception e) {
					throw(e);
				}
		}

	}

	public static void main(String[] args) {

		try {
			LearnClassifyTest.Learn();
			LearnClassifyTest.Classify();
		} catch (Exception ex) {
			System.err.println(ex.getMessage());
		}

	}

}



## Authors

## License
