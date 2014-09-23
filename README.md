w3cValidator
============

### Purpose
Java Library to call local or w3c.org w3cValidator to check html
see http://validator.w3.org/docs/

### Project
* Open Source hosted at https://github.com/WolfgangFahl/w3cValidator
* GPL 3.0 License 
* Maven based Java project including JUnit 4 tests.

### How to build
* git clone https://github.com/WolfgangFahl/w3cValidator
* cd w3cValidator
* mvn install

### How to use
This java Library calls the W3C Validator according to
* http://validator.w3.org/docs/api.html

Maven dependency:
```
<dependency>
  <groupId>com.bitplan</groupId>
  <artifactId>w3cValidator</artifactId>
  <version>0.0.1</version>
</dependency>
```

## Example:
  To check some html code with an unclosed div tag with the validator
  at w3.org:

```
  public static final String url="http://validator.w3.org/check";
  String html="<!DOCTYPE html>\n"+
    "<html>\n"+
    "  <head>\n"+
    "    <title>test W3CChecker</title>\n"+
    "  </head>\n"+
    "  <body>\n"+
    "    <div>\n"+
    "  </body>\n"+
    "</html>";
	W3CValidator checkResult = W3CValidator.check(url, html);
```		
  The returned W3CValidator object has the structure of the SOAP-Response as outlined in
  http://validator.w3.org/docs/api.html
  
  e.g. checkResult.body.response.errors.errorlist will contain a list of ValidationErrors
  with line col and message for each error.
  
  

## Version history
* 0.0.1 - 2014-09-22      : first published version
