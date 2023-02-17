## JAVA messaging examples

### Running the client examples

Use maven to build the module, and additionally copy the dependencies
alongside their output:

```shell
mvn clean package dependency:copy-dependencies -DincludeScope=runtime -DskipTests
```

Run the classes using commands of the format:

`java -cp "target/classes/:target/dependency/*" local.test.jms.examples.MQTTProducer`

NOTE: inspired by the project https://github.com/apache/qpid-jms.git
