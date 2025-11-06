# **Task description** 

Launch URL: https://www.saucedemo.com/ 

***UC-1 Test Login form with empty credentials:***
  Type any credentials into "Username" and "Pas sword" fields. 
  Clear the inputs. 
  Hit the "Login" button. 
  **Check the error messages: "Username is required".**
  
***UC-2 Test Login form with credentials by passing Username:*** 
  Type any credentials in username. 
  Enter password. 
  Clear the "Password" input. 
  Hit the "Login" button. 
  **Check the error messages: "Password is required".**
  
***UC-3 Test Login form with credentials by passing Username & Password:*** 
  Type credentials in username which are under Accepted username are sections.
  Enter password as secret sauce. 
  Click on Login and **validate the title “Swag Labs” in the dashboard.** 

Provide parallel execution, add logging for tests and use Data Provider to parametrize tests. 
Make sure that all tasks are supported by these 3 conditions: UC-1; UC-2; UC-3. 
Please, add task description as README.md into your solution! 

To perform the task use the various of additional options: 
  Test Automation tool: Selenium WebDriver; Project Builder: Maven; 
  Browsers: 1) Firefox; 2) Chrome; 
  Locators: CSS; Test 
  Runner: TestNG; 
  [Optional] Patterns: 1) Singleton; 2) Adapter; 3) 
  Strategy; [Optional] 
  Test automation approach: BDD; Assertions: AssertJ; 
  [Optional] Loggers: SLF4J.

## Specific instructions for running this project
  Run tests using Maven from the project root:

**Sequential execution:** mvn clean test -Dsurefire.suiteXmlFiles=src/test/resources/testng.xml


**Parallel execution (multiple browsers/threads):** mvn clean test -Dsurefire.suiteXmlFiles=src/test/resources/testng-parallel.xml


**Parallel execution in Firefox:**
mvn clean test -Dbrowser=firefox -Dsurefire.suiteXmlFiles=src/test/resources/testng-parallel.xml

**Cucumber**
mvn test -Dcucumber.features=src/test/resources/features/login.feature

