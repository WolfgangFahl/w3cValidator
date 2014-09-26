/**
 * see
 * https://github.com/WolfgangFahl/w3cValidator/blob/master/LICENSE
 */
package com.bitplan.w3ccheck;

import static org.junit.Assert.*;

import java.util.List;
import org.junit.Test;
import com.bitplan.w3ccheck.W3CValidator.Body.ValidationResponse.Errors.ValidationError;
import com.bitplan.w3ccheck.W3CValidator.Body.ValidationResponse.Warnings.ValidationWarning;

/**
 * test case for W3CValidator W3C Markup Validation service Java adapter
 * @author wf
 *
 */
public class TestW3CValidator {


	
	/**
	 * test the w3cValidator interface with some html code
	 * @throws Exception
	 */
	@Test
	public void testW3CValidator() throws Exception {
		String preamble="<!DOCTYPE HTML PUBLIC \"-//W3C//DTD HTML 4.01 Transitional//EN\"\n" + 
				"   \"http://www.w3.org/TR/html4/loose.dtd\">\n"+
				"<html>\n"+
				"  <head>\n"+
				"    <meta http-equiv=\"Content-Type\" content=\"text/html; charset=utf-8\">\n"+
				"    <title>test</title>\n"+
				"  </head>\n"+
				"  <body>\n";
		String footer="  </body>\n"+
				"</html>\n";
		String[] htmls = {
				preamble+
				"    <div>\n"+
				footer,
				"<!DOCTYPE html><html><head><title>test W3CChecker</title></head><body><div></body></html>",
				preamble+footer+"\u0000"
		};
		int[] expectedErrs={1,2,0};
		int[] expectedWarnings={1,2,1};
		int index=0;
		System.out.println("Testing "+htmls.length+" html messages via "+W3CValidator.url);
		for (String html : htmls) {
			W3CValidator checkResult = W3CValidator.check(html);
			List<ValidationError> errlist = checkResult.body.response.errors.errorlist;
			List<ValidationWarning> warnlist = checkResult.body.response.warnings.warninglist;
			if (errlist.size()>0) {
				Object first = errlist.get(0);
				assertTrue("if first is a string, than moxy is not activated",first instanceof ValidationError);
			}
			//System.out.println(first.getClass().getName());
			//System.out.println(first);
			System.out.println("Validation result for test "+(index+1)+":");
			for (ValidationError err:errlist) {
				System.out.println("\t"+err.toString());
			}
			for (ValidationWarning warn:warnlist) {
				System.out.println("\t"+warn.toString());
			}
			System.out.println();
			assertTrue(errlist.size()>=expectedErrs[index]);
			assertTrue(warnlist.size()>=expectedWarnings[index]);
			index++;
		}
	} // testW3CValidator

} // TestW3CValidator
