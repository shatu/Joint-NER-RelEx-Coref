##mvn package -DskipTests
mvn dependency:copy-dependencies
java -Xmx16g -cp ./target/classes/:./target/dependency/* edu.illinois.cs.cogcomp.cs546ccm2.corpus.ace2005.ACECorpus
java -Xmx16g -cp ./target/classes/:./target/dependency/* edu.illinois.cs.cogcomp.cs546ccm2.corpus.ace2005.PrepareTrainingData
