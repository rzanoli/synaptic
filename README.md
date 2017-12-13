# synaptic

SYNAPTIC library consists of several components for pre-processing data, extracting relevant features and for 'sentiment' and 'type' classification for German. Training and classification operations are accessible via the SYNAPTIC Application Program Interface (API). In addition, a Command Line Interface (CLI) is provided for convenience of experiments and training.

## Getting started

These instructions will get you a copy of the project up and running on your local machine for development and testing purposes.

### Prerequisites

- Java 1.7
- Apache Maven (http://maven.apache.org) (SYNAPTIC API)
- git (SYNAPTIC API)

### How to get the code

We provide 2 different distributions namely Jar Distribution and Java Distribution; the first one can be used for running SYNAPTIC from CLI, while the second distribution can be used when you want to use SYNAPTIC as a library from your java code.

#### Jar Distribution (CLI)

SYNAPTIC is distributed as a jar file containing all the Java code for training and testing. It can be download from its gihub repository https://github.com/rzanoli/synaptic/........ 

#### Java Distribution (API)

SYNAPTIC is a Maven project that you can download from its gihub repository by running the following command:
git clone https://github.com/rzanoli/...............


## CLI Instructions

After getting the Jar Distribution as explained above, you are ready for training the classifer and annotating new datasets. These instructions are valid for both the 'sentiment' classifier and the 'type' classifier. In the rest of this section we will report instructions only for the 'sentiment' classifier but they remain also valid for the other classifier; it is sufficient to change the package name from 'sa' to 'tc' and the classifier name prefix from 'Sentiment' to 'Type' (i.e.,
sa.SentimentLearn --> tc.TypeLearn, sa.SentimentClassify --> tc.TypeClassify).

### Training

```java -cp ....jar eu.fbk.ict.fm.nlp.synaptic.classification.sa.SentimentLearn -f datasetFileName -m modelFileName```

Where:
	datasetFileName is the name of the file containing the training dataset for training the classifier 
 	modelFileName is the model to generate

Produced files:
 	
modelFileName.sa.model			the generated model
modelFileName.sa.model.features.index	the features index
modelFileName.sa.model.labels.index	the labels index
datasetFileName.sa.token		the pre-processed dataset in input
datasetFileName.sa.token.vectors	the features vectors of the dataset in input

the model files with prefix modelFileName will be used in the next phase of annotating new datasets while the files with prefix datasetFileName are produced for debugging purposes only.


### Classifying

```java -cp ....jar eu.fbk.ict.fm.nlp.synaptic.classification.sa.SentimentClassifier -c content -m modelFileName```

Where: 
	content is the text string to classify 
	modelFileName is the model generated during the classifier training phase


## API Instructions

SYNAPTIC is a Maven project and after getting the Java Distribution of the project as explained above, you need to install its maven artifact into your local maven repository (i.e., m2) and then put the following dependency into the project file (i.e., pom.xml) of your project.

```
<dependency>
	<groupId>eu.fbk.ict.fm.nlp</groupId>
	<artifactId>classifier</artifactId>
	<version>1.0-SNAPSHOT</version>
</dependency>
```

### Training

```java
SentimentLearn sentimentLearn = new SentimentLearn();
sentimentLearn.run(datasetFileName, modelFileName);
```

Where: 
	datasetFileName is the training dataset for training the classifier 
	modelFileName is the model generated during the classifier training phase

### Classifying

```java
SentimentClassify sentimentClassify = new SentimentClassify(modelFileName);
String[] annotation = sentimentClassify.run(content); 
String label = annotation[0]; // the predicted label
String score = annotation[1]; // and its score
System.out.println("predicted label:" + label + " score:" + score);
```

### Example of a java project using SYNAPTIC API

```java
import java.io.BufferedReader;
import java.io.File;
import java.io.FileInputStream;
import java.io.InputStreamReader;

import eu.fbk.ict.fm.nlp.synaptic.classification.sa.SentimentClassify;
import eu.fbk.ict.fm.nlp.synaptic.classification.sa.SentimentLearn;

/**
* This class shows an example on how the SYNAPTIC API can be used for training the semantic classifier on
* a given dataset and then using the produced classifier for classifying the same dataset.
*
*/
public class LearnClassifyTest {

	static String dataSet = "dataset.tsv"; // your dataset 
	static String model = "/tmp/dataset.sa.model";

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
			System.out.println("accuracy:" + correctPredictions/examples);
			
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
```

## Authors

## License

This project is licensed under the Apache License v2.0.
