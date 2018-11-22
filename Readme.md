1. git clone or download as zip
2. Goto project directory and execute commands, based on type of OS -

if Windows-  
>./gradlew.bat bootJar

if Unix- 
>./gradlew bootJar

3. Copy the created 'Parser.jar' in the folder ./build/libs/
4. Execute below command to get the output

if Unix-
>java -jar Parser.jar --accesslog=/path/to/file/access.log --startDate=2017-01-01.00:00:00 --duration=daily --threshold=500

if Windows-
>java -jar Parser.jar --accesslog=F:\\path\to\file\access.log --startDate=2017-01-01.00:00:00 --duration=daily --threshold=500


5. SQL schema and queries.

CREATE table log_table (log_id integer not null, detail varchar(255), ip_address varchar(255), protocol varchar(255), status integer, timestamp timestamp, primary key (log_id));

SELECT ip_address, count(*) FROM LOG_TABLE WHERE timestamp >= '2017-01-01 00:00:00.0' and timestamp < '2017-01-02 00:00:00.0' group by(ip_address) having count(*) > 500;
