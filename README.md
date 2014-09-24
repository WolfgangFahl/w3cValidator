w3cValidator
============

### Purpose
At http://validator.w3.org/ the W3C offers an online validation service for checking html code to
be standard compliant. There is also an API available that is described at 
http://validator.w3.org/docs/api.html

This is a Java Library to call this API. The url of the service may be the official w3c 
validator at http://validator.w3.org/check or a copy of this service installed in your own environment.

### Project
* Open Source hosted at https://github.com/WolfgangFahl/w3cValidator
* GPL 3.0 License 
* Maven based Java project including JUnit 4 tests.
* Project page at http://wolfgangfahl.github.io/w3cValidator/

Available at http://search.maven.org/#artifactdetails|com.bitplan|w3cValidator|0.0.1|jar

Maven dependency:
```
<dependency>
  <groupId>com.bitplan</groupId>
  <artifactId>w3cValidator</artifactId>
  <version>0.0.1</version>
</dependency>
```

### How to build
* git clone https://github.com/WolfgangFahl/w3cValidator
* cd w3cValidator
* mvn install

### How to use
This java Library calls the W3C Validator according to
* http://validator.w3.org/docs/api.html

## Example:
  To check some html code with an unclosed div tag with the validator
  at w3.org:

```
  // url to use - modify if you have your own installation
  public static final String url="http://validator.w3.org/check";
  
  // html code to check
  String html="<!DOCTYPE html>\n"+
    "<html>\n"+
    "  <head>\n"+
    "    <title>test W3CChecker</title>\n"+
    "  </head>\n"+
    "  <body>\n"+
    "    <div>\n"+
    "  </body>\n"+
    "</html>";
    
  // API call 
	W3CValidator checkResult = W3CValidator.check(url, html);
```		
  The returned W3CValidator object has the structure of the SOAP-Response as outlined in
  http://validator.w3.org/docs/api.html
  
  e.g. checkResult.body.response.errors.errorlist will contain a list of ValidationErrors
  with line col and message for each error.
  
  

## Version history
* 0.0.1 - 2014-09-22      : first published version
