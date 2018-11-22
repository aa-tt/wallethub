1. Install gradle in your machine
2. Goto project directory and execute commands, based on type of OS -
	if Windows-  >./gradlew.bat bootJar
	if Unix- >./gradlew bootJar
3. Copy the created 'Parser.jar' in the folder ./build/libs/
4. Execute below command to get the output

if Unix-
>java -jar Parser.jar --accesslog=/path/to/file/access.log --startDate=2017-01-01.00:00:00 --duration=daily --threshold=500

if Windows-
>java -jar Parser.jar --accesslog=F:\\path\to\file\access.log --startDate=2017-01-01.00:00:00 --duration=daily --threshold=500
