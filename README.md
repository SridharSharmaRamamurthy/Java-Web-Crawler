# Java-Web-Crawler
Web Crawler for Crawling any of the site using Form UI.

This project will give you the sitemap which will be outputted after crawling the site which you want as show below.

![alt text] https://github.com/SridharSharmaRamamurthy/Java-Web-Crawler/blob/master/src/main/resources/static/images/Crawl-Form.png

# Building the Project
This is a Maven Project so to build the project we require maven installed on the system/IDE and then using command maven clean install will build the application.
if you want skip Junit Test then we should use maven clean install -skiptests=true. once you build the application you will get war file. you can configure either to get jar in pom.xml by using a tag called maven packaging tag.  

# Java Doc for the Project
Even i have generated the java docs for this project and it is in doc folder.

# Running the Project
Running the Project is very easy since we have got the war file you can just deploy this in tomcat and if you browse http://ip:port/index.html weather page will be rendered.
if we convert this to jar file then using java -jar projectname.jar will start the micro services and no need of tomcat also.

# Demo 
We can use just the below link to see how the project UI looks.

<a href="http://htmlpreview.github.io/?https://github.com/SridharSharmaRamamurthy/weather-forecast/blob/master/WebContent/index.html">Demo Link</a>

# Testing
Testing is done using TDD Approach and i have created <a href="https://github.com/SridharSharmaRamamurthy/weather-forecast/blob/master/src/src/test/WeatherTest.java">Junit_Test_Class</a>.
This class validate in 3 Sceniarios 
            1) To check whether the system is having proper internet connection.
            2) To check the Status code of the application with the valid Url and invalid Url.

# What could be done with more time
1) Since i was running short age of time i have just used css style sheet of the openweathermap site and embedded it.
2) i have used directly the ajax call to fetch the data in real time it wont be like that we will be using one WebServer layer (Controller) to map the things and to fetch data this can be improved of had more time.
3) if had more time i would have written more test cases scenario to validate the JSON Response type and other cases.
4) if i had created the Rest webservice then i can test webservice is running or not using Fitnesse Tool that would have been achieved.
5) Even deployment can be made automated if i would have used jenkins and shown some demo.

# Thanks for viewing my Page.
Sridhar
