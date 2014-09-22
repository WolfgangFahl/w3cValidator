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
 * test case for W3CValidator Java adapter
 * @author wf
 *
 */
public class TestW3CValidator {

	public static final String url="http://validator.w3.org/check";
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
				"<!DOCTYPE html><html><head><title>test W3CChecker</title></head><body><div></body></html>"
		};
		int[] expectedErrs={1,2};
		int[] expectedWarnings={1,2};
		int index=0;
		for (String html : htmls) {
			W3CValidator checkResult = W3CValidator.check(url, html);
			List<ValidationError> errlist = checkResult.body.response.errors.errorlist;
			List<ValidationWarning> warnlist = checkResult.body.response.warnings.warninglist;
			assertTrue(errlist.size()>=expectedErrs[index]);
			Object first = errlist.get(0);
			assertTrue("if first is a string, than moxy is not activated",first instanceof ValidationError);
			//System.out.println(first.getClass().getName());
			//System.out.println(first);
			System.out.println("Validation result for test "+index+":");
			for (ValidationError err:errlist) {
				System.out.println("\t"+err.toString());
			}
			assertTrue(warnlist.size()>=expectedWarnings[index]);
			for (ValidationWarning warn:warnlist) {
				System.out.println("\t"+warn.toString());
			}
			System.out.println();
			index++;
		}
	} // testW3CValidator

}
