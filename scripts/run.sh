mvn package -DskipTests
mvn dependency:copy-dependencies
java -cp ./target/classes/:./target/dependency/* edu.illinois.cs.cogcomp.cs546ccm2.corpus.ace2004.FileProcessor
java -cp ./target/classes/:./target/dependency/* edu.illinois.cs.cogcomp.cs546ccm2.corpus.ace2004.PrepareEntities
java -cp ./target/classes/:./target/dependency/* edu.illinois.cs.cogcomp.cs546ccm2.corpus.ace2005.FileProcessor
java -cp ./target/classes/:./target/dependency/* edu.illinois.cs.cogcomp.cs546ccm2.corpus.ace2005.PrepareEntities
