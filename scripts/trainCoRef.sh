##mvn package -DskipTests
##mvn dependency:copy-dependencies
java -Xmx16g -cp ./target/classes/:./target/dependency/* edu.illinois.cs.cogcomp.cs546ccm2.disjoint.CoRef.LocalClassifier.CoRefDriver doTrainTest data/ACE2005_Split true
