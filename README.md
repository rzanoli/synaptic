# synaptic

The SYNAPTIC library consists of several components for pre-processing data (e.g., tokenization), extracting relevant features and for 'sentiment' and 'type' classification for German. Training and classification operations are accessible via the SYNAPTIC Application Program Interface (API). In addition, a Command Line Interface (CLI) is provided for convenience of experiments and training.

## Getting started

These instructions will get you a copy of the project up and running on your local machine.

### Prerequisites

- Java 1.7 or later
- Apache Maven (http://maven.apache.org) (required by SYNAPTIC API)
- git (required by SYNAPTIC API)

### How to get the code

Two different distributions of SYNAPTIC are provided: Jar Distribution and Java Source Distribution; the first distribution can be used for running SYNAPTIC from CLI, while the second distribution can be used when you want to use SYNAPTIC as a library from your java code.

#### Jar Distribution (CLI)

jar file containing all the Java code for training and testing. It can be download at this address: 

https://github.com/rzanoli/synaptic/........ 

#### Java Source Distribution (API)

Maven project that you can download from gihub by running the following command:

```> git clone https://github.com/rzanoli/...............```


## CLI Instructions

After getting the Jar Distribution as explained above, you are ready for training the classifer on a dataset and annotating new examples. These instructions are valid for both the 'sentiment' classifier and 'type' classifier. In the rest of this section we will report instructions only for the 'sentiment' classifier but they remain also valid for the other classifier: it is sufficient to change the package name from 'sa' to 'tc' and the classifier name prefix from 'Sentiment' to 'Type' (i.e.,
sa.SentimentLearn --> tc.TypeLearn, sa.SentimentClassify --> tc.TypeClassify).

### Installation

Save the jar file downloaded into your working directtory.

### Training

From your working directory, run the following command:

```> java -cp ....jar eu.fbk.ict.fm.nlp.synaptic.classification.sa.SentimentLearn -f datasetFileName -m modelFileName```

Where:
- datasetFileName is the name of the file containing the training dataset for training the classifier 
- modelFileName is the file name of the model to generate

Produced files:
 	
- modelFileName				the model
- modelFileName.features.index		the features index
- modelFileName.labels.index		the labels index
- datasetFileName.sa.token		the pre-processed dataset
- datasetFileName.sa.token.vectors	the features vectors

the generated files with prefix 'modelFileName' will be used in the next phase for annotating new examples while the files with prefix 'datasetFileName' are produced for training the classifier and the saved for debugging purposes only.


### Classifying

From your working directory, run the following command:

```> java -cp ....jar eu.fbk.ict.fm.nlp.synaptic.classification.sa.SentimentClassifier -c content -m modelFileName```

Where: 
- content is the text string to classify 
- modelFileName is the model generated during the classifier training phase (it consists of all the 3 files generated during the training phase: modelFileName, modelFileName.features.index, modelFileName.labels.index that have to stay in the same directory).


## API Instructions

SYNAPTIC has been developed as a Maven project and after getting its java Source Distribution as explained above, you first need to install its maven artifact into your local maven repository (i.e., m2), and then put the following dependency into the project file (i.e., pom.xml) of your java project.

```
<dependency>
	<groupId>eu.fbk.ict.fm.nlp</groupId>
	<artifactId>classifier</artifactId>
	<version>1.0-SNAPSHOT</version>
</dependency>
```

### Installation

Copy the project that you have cloned from Github into your working directory, and from that directory, run the following maven command to install the SYNAPTIC artifact into your maven local repository:

```
> mvn install
```

The SYNAPTIC API is now avalable in your favourite java IDE (e.g., Eclipse)

### Training

The following piece of code can be used to train the classifier:

```java
try {
    SentimentLearn sentimentLearn = new SentimentLearn();
    sentimentLearn.run(datasetFileName, modelFileName);
} catch (Exception ex) {
    System.err.println(ex.getMessage());
}
```

Where: 
- datasetFileName is the training dataset for training the classifier 
- modelFileName is the model generated during the classifier training phase

### Classifying

The following piece of code can be used to annotate new examples by using the classifier trained in the previous step:

```java
try {
    SentimentClassify sentimentClassify = new SentimentClassify(modelFileName);
    String[] annotation = sentimentClassify.run(content); 
    String label = annotation[0]; // the predicted label
    String score = annotation[1]; // and its score
    System.out.println("predicted label:" + label + " score:" + score);
} catch (Exception ex) {
      System.err.println(ex.getMessage());
}
```

### Example of calling SYNAPTIC API from java code

```java
import java.io.BufferedReader;
import java.io.File;
import java.io.FileInputStream;
import java.io.InputStreamReader;

import eu.fbk.ict.fm.nlp.synaptic.classification.sa.SentimentClassify;
import eu.fbk.ict.fm.nlp.synaptic.classification.sa.SentimentLearn;

/**
*
* This class shows an example on how the SYNAPTIC API can be used for training the semantic classifier on
* a given dataset and then use the produced classifier for classifying the same dataset.
*
*/
public class LearnAndClassifyTest {

	private static String dataSet = "dataset.tsv"; // your dataset 
	private static String model = "/tmp/dataset.sa.model";

        /**
	* Trains the classifier
	*/
	public static void Learn() throws Exception {

		SentimentLearn semanticLearn = new SentimentLearn();
		semanticLearn.run(dataSet, model);

	}

        /**
	* Classifies by using the generated model
	*/
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
				String goldLabel = splitLine[2]; //sentiment label
				String[] annotation = semanticClassify.run(content);
				String label = annotation[0]; //predicted label
				String score = annotation[1]; //and its score
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
