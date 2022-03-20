$ Compile the project
    The project must be compiled in Java 1.8 or later.
    The project is a Maven project, add the project as a Maven project in your IDE.

$ Run the project
    Run the main method(gui.MyFrame.main).

$ Run test
    The test is in MyTest.class, you may need to reset the file path in test cases.
    The test works with TestNG plugin, you may need to download the plugin if it is not installed in your IDE.
    If your IDE is Eclipse, useful link: https://www.lambdatest.com/blog/how-to-install-testng-in-eclipse-step-by-step-guide/
    If your IDE is IntelliJ IDEA, you do not need TestNG plugin as TestNG is bundled in IntelliJ IDEA.


>>>>>>>>>>>>>>>>>> Special Notes to The Developers of This Project >>>>>>>>>>>>>>>>>>

$ The editing of the GUI
    The GUI of the project is developed with IntelliJ IDEA GUI Designer.
    So IntelliJ IDEA(https://www.jetbrains.com/idea/download/) is highly recommended if you need to edit the GUI.
    MyFrame.form contains the component layout information(can only be used in IntelliJ IDEA).
    MyFrame.class contains the full source code that builds the GUI(can run in any IDE).
    After finishing the GUI, do remember to rebuilt the project, IntelliJ IDEA will automatically write the update in MyFrame.form into MyFrame.java, useful link: https://stackoverflow.com/questions/13744779/exporting-intellij-idea-ui-form-to-eclipse

$ Package the project
    All configurations for packaging have been finished in pom.xml.
        1) use the Maven plugin in your IDE to package
        2) use command line, go to the root path of the project, execute: mvn clean package, then a target folder will be generated which contains the Jar file

$ Package structure
    .
    ├── README.txt
    ├── pom.xml ----------------------------- Maven configuration XML
    └── src
        ├── META-INF ------------------------ The information about the files packaged in a JAR file
        │   └── MANIFEST.MF
        ├── main ---------------------------- The business code folder
        │   ├── java
        │   │   ├── gui
        │   │   │   ├── MyFrame.form -------- Produced by IntelliJ GUI Designer
        │   │   │   └── MyFrame.java -------- Java GUI source code
        │   │   ├── pojo
        │   │   │   └── Student.java
        │   │   ├── service
        │   │   │   ├── ExcelService.java
        │   │   │   ├── MyService.java
        │   │   │   ├── PdfEventHandler.java
        │   │   │   └── PdfService.java
        │   │   └── util
        │   │       ├── ChartUtil.java
        │   │       └── Constant.java
        │   └── resources
        │       ├── frame.properties -------- Defines the input label and text on GUI
        │       └── log4j.properties -------- Defines the Log4J configuration
        └── test ---------------------------- The test code folder
            └── java
                └── MyTest.java