### w3cValidator
[Java library to call W3C Validator check](http://www.bitplan.com/W3cValidator) 

[![Travis (.org)](https://img.shields.io/travis/WolfgangFahl/w3cValidator.svg)](https://travis-ci.org/WolfgangFahl/w3cValidator)
[![Maven Central](https://img.shields.io/maven-central/v/com.bitplan/w3cValidator.svg)](https://search.maven.org/artifact/com.bitplan/w3cValidator/0.0.5/jar)
[![GitHub issues](https://img.shields.io/github/issues/WolfgangFahl/w3cValidator.svg)](https://github.com/WolfgangFahl/w3cValidator/issues)
[![GitHub issues](https://img.shields.io/github/issues-closed/WolfgangFahl/w3cValidator.svg)](https://github.com/WolfgangFahl/w3cValidator/issues/?q=is%3Aissue+is%3Aclosed)
[![GitHub](https://img.shields.io/github/license/WolfgangFahl/w3cValidator.svg)](https://www.apache.org/licenses/LICENSE-2.0)
[![BITPlan](http://wiki.bitplan.com/images/wiki/thumb/3/38/BITPlanLogoFontLessTransparent.png/198px-BITPlanLogoFontLessTransparent.png)](http://www.bitplan.com)

### Documentation
* [Wiki](http://www.bitplan.com/W3cValidator)
* [w3cValidator Project pages](https://WolfgangFahl.github.io/w3cValidator)
* [Javadoc](https://WolfgangFahl.github.io/w3cValidator/apidocs/index.html)
* [Test-Report](https://WolfgangFahl.github.io/w3cValidator/surefire-report.html)
### Maven dependency

Maven dependency
```xml
<dependency>
  <groupId>com.bitplan</groupId>
  <artifactId>w3cValidator</artifactId>
  <version>0.0.5</version>
</dependency>
```

[Current release at repo1.maven.org](http://repo1.maven.org/maven2/com/bitplan/w3cValidator/0.0.5/)

### How to build
```
git clone https://github.com/WolfgangFahl/w3cValidator
cd w3cValidator
mvn install
```


### Purpose
At http://validator.w3.org/ the W3C offers an online validation service for checking html code to
be standard compliant. There is also an API available that is described at 
http://validator.w3.org/docs/api.html

This is a Java Library to call this API. The url of the service may be the official w3c 
validator at http://validator.w3.org/check or a copy of this service installed in your own environment.


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
  
### Example Output (from JUnit tests):
```	
Validation result for test 1:
	ValidationError line 10 col 9:'end tag for "DIV" omitted, but its declaration does not permit this'
	ValidationWarning line 0 col 0:'Using Direct Input mode: UTF-8 character encoding assumed'

Validation result for test 2:
	ValidationError line 1 col 82:'End tag for  body seen, but there were unclosed elements.'
	ValidationError line 1 col 75:'Unclosed element div.'
	ValidationWarning line 0 col 0:'No Character encoding declared at document level'
	ValidationWarning line 0 col 0:'Using Direct Input mode: UTF-8 character encoding assumed'
```
## Version history
| Version | date | changes
| ------ | ----- | ---------
| 0.0.1 | 2014-09-22 | first published version
| 0.0.2 | 2014-09-26 | fixes issue with 0x0 unicode in html 
| 0.0.3 | 2016-06-15 | upgrades jersey to 1.19.1 and eclipselink to 2.6.3
| 0.0.4 | |
| 0.0.5 | 2018-08-22 | upgrades to Java8, changes License to Apache, improves README, upgrades Libraries
