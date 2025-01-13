
On both operative systems:
1-Ensure that you have both the JDK (Java Development Kit) and Prolog installed on your system.

2-
On windows:

1-Open the Windows command prompt. Press Win + R, type "cmd," and press Enter.
Navigate to the directory where your files are located using the cd command. 
For example, if your files are in the folder "C:\path\to\files," type the following command:
cd C:\path\to\files

2-Compile the Java files using the javac command:
javac App.java
This will compile the Java file and generate an "App.class" file in the same directory.

3-Compile the Prolog files using the specific Prolog compiler you are using. For example, if you 
are using SWI-Prolog, you can use the swipl command along with the Prolog file:
swipl -s test.pl -g true -t halt
swipl -s credit.pl -g true -t halt
swipl -s udpatesTransactions.pl -g true -t halt
swipl -s update_balance_plus_credit.pl -g true -t halt

4-After compiling the Java and Prolog files, you can execute the Java program using the java command.
 Make sure to include the path to the Prolog JAR file (if necessary) using the -cp option:
java -cp .;path/to/prolog.jar App


On Linux:

1-Open the terminal on your Linux system.
Navigate to the directory where your files are located using the cd command. 
For example, if your files are in the folder "/path/to/files," type the following command:
cd /path/to/files

2-Compile the Java file using the javac command. :
javac App.java

3-Compile the Prolog files using the specific Prolog compiler you are using. For example, if you 
are using SWI-Prolog, you can use the swipl command along with the Prolog file:
swipl -s test.pl -g true -t halt
swipl -s credit.pl -g true -t halt
swipl -s udpatesTransactions.pl -g true -t halt
swipl -s update_balance_plus_credit.pl -g true -t halt

4-4-After compiling the Java and Prolog files, you can execute the Java program using the java command.
 Make sure to include the path to the Prolog JAR file (if necessary) using the -cp option:
java -cp .;path/to/prolog.jar App

Thanks for reading. Have a great day!



