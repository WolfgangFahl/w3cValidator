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

## Example:

```
  public static final String url="http://validator.w3.org/check";
  String html="<!DOCTYPE html><html><head><title>test W3CChecker</title></head><body><div></body></html>";
	W3CValidator checkResult = W3CValidator.check(url, html);
```		

## Version history
* 0.0.1 - 2014-09-22      : first published version
