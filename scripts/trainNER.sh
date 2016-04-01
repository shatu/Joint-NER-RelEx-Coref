##mvn package -DskipTests
##mvn dependency:copy-dependencies
java -Xmx16g -cp ./target/classes/:./target/dependency/* edu.illinois.cs.cogcomp.cs546ccm2.disjoint.NER.LocalClassifier.NerDriver doTrainTest /Users/shashank/workspace/CS546-CCM2/data/ACE2005_NER true
