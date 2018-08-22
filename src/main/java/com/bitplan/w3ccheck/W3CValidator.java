/**
 * Copyright (c) 2018 BITPlan GmbH
 *
 * http://www.bitplan.com
 *
 * This file is part of the Opensource project at:
 * https://github.com/WolfgangFahl/w3cValidator
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *     http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */
/**
 * see
 * https://github.com/WolfgangFahl/w3cValidator/blob/master/LICENSE
 */
package com.bitplan.w3ccheck;

import java.io.StringReader;
import java.util.ArrayList;
import java.util.List;
import java.util.logging.Level;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import javax.ws.rs.core.MediaType;
import javax.xml.bind.JAXBContext;
import javax.xml.bind.JAXBException;
import javax.xml.bind.Unmarshaller;
import javax.xml.bind.annotation.XmlAccessType;
import javax.xml.bind.annotation.XmlAccessorType;
import javax.xml.bind.annotation.XmlElement;
import javax.xml.bind.annotation.XmlElementWrapper;
import javax.xml.bind.annotation.XmlRootElement;

import org.apache.commons.io.IOUtils;
import org.eclipse.persistence.oxm.annotations.XmlCDATA;

import com.sun.jersey.api.client.Client;
import com.sun.jersey.api.client.ClientResponse;
import com.sun.jersey.api.client.WebResource;
import com.sun.jersey.multipart.FormDataBodyPart;
import com.sun.jersey.multipart.FormDataMultiPart;

/**
 * Wrapper for W3C Validator Soap responses 
 * see <a href="http://validator.w3.org/docs/api.html#requestformat">W3C Markup Validation Service Request Format</a>
 * @author wf
 *
 * the JaxB annotations of this Wrapper are designed to be able to unmarshal a W3C Markup Validation Service
 * Soap 1.2 response to this Java Object
 * 
 * on the class level the root node of the SOAP message is covered:
 * <pre>
 * {@code
 * <env:Envelope xmlns:env="http://www.w3.org/2003/05/soap-envelope">
 * ...
 * </env:Envelope>
 * }
 * </pre>
 *  According to <a href="http://validator.w3.org/docs/api.html#requestformat">W3C Markup Validation Service Request Format documentation</a>:
 *  A SOAP response for the validation of a document (invalid) will look like this:
 * <pre>
 * {@code
 *  <?xml version="1.0" encoding="UTF-8"?>
 *    <env:Envelope xmlns:env="http://www.w3.org/2003/05/soap-envelope">
 *      <env:Body>
 *        <m:markupvalidationresponse
 *          env:encodingStyle="http://www.w3.org/2003/05/soap-encoding" 
 *          xmlns:m="http://www.w3.org/2005/10/markup-validator">
 *            <m:uri>http://qa-dev.w3.org/wmvs/HEAD/dev/tests/xhtml1-bogus-element.html</m:uri>
 *            <m:checkedby>http://validator.w3.org/</m:checkedby>
 *            <m:doctype>-//W3C//DTD XHTML 1.0 Transitional//EN</m:doctype>
 *            <m:charset>utf-8</m:charset>
 *            <m:validity>false</m:validity>
 *            <m:errors>
 *              <m:errorcount>1</m:errorcount>
 *              <m:errorlist>
 *              		          
 *                <m:error>
 *                  <m:line>13</m:line>
 *                  <m:col>6</m:col>                                           
 *                  <m:source><![CDATA[
 *                    &#60;foo<strong title="Position where error was detected.">&#62;</strong>This phrase is enclosed in a bogus FOO element.&#60;/foo&#62;
 *                  ]]>
 *                  </m:source>                                           
 *                  <m:explanation>
 *                    <![CDATA[
 *                    <p> ... </p<p>
 *                    ]]>
 *                  </m:explanation>                                           
 *                  <m:messageid>76</m:messageid>                                           
 *                  <m:message>element "foo" undefined</m:message>
 *               </m:error>
 *          </m:errorlist>
 *        </m:errors>
 *        <m:warnings>
 *          <m:warningcount>0</m:warningcount>
 *          <m:warninglist>	        
 *            ...		        
 *          </m:warninglist>
 *        </m:warnings>
 *      </m:markupvalidationresponse>
 *     </env:Body>
 *  </env:Envelope>
 *		}
 * </pre>
 *		the structure of this W3CValidator class is aligned to this format
 */
@XmlRootElement(name = "Envelope", namespace = "http://www.w3.org/2003/05/soap-envelope")
// no getters/setters are used - all fields are initialized to make this safe
// setting should only be done via the check function 
@XmlAccessorType(XmlAccessType.FIELD)
public class W3CValidator {
	/** 
	 * set to true if Logging should be enabled
	 */
	public static boolean debug=false;

	/**
	 *  Logging may be enabled by setting debug to true
	 */
	protected static java.util.logging.Logger LOGGER = java.util.logging.Logger
			.getLogger("com.bitplan.w3ccheck");
	
	/**
	 * the URL of the official W3C Markup Validation service
	 * if you'd like to run the tests against your own installation you might want to modify this
	 */
	public static String url="http://validator.w3.org/check";
	
	/**
	 * create a W3CValidator result for the default url http://validator.w3.org/check  with the given html
	 * 
	 * @param html - the html code to be checked
	 * @return - a W3CValidator response according to the SOAP response format or null if the
	 * http response status of the Validation service is other than 200
	 * explained at response http://validator.w3.org/docs/api.html#requestformat 
	 * @throws JAXBException if there is something wrong with the response message so that it
	 * can not be unmarshalled
	 */
	public static W3CValidator check(String html) throws JAXBException {
		W3CValidator result=check(url,html);
		return result;
	}
	
	/**
	 * create a W3CValidator result for the given url with the given html
	 * 
	 * @param url - the url of the validator e.g. "http://validator.w3.org/check"
	 * @param html - the html code to be checked
	 * @return - a W3CValidator response according to the SOAP response format or null if the
	 * http response status of the Validation service is other than 200
	 * explained at response http://validator.w3.org/docs/api.html#requestformat 
	 * @throws JAXBException if there is something wrong with the response message so that it
	 * can not be unmarshalled
	 */
	public static W3CValidator check(String url, String html)
			throws JAXBException {
		// initialize the return value
		W3CValidator result = null;
		
		// create a WebResource to access the given url
		WebResource resource = Client.create().resource(url);
		
		// prepare form data for posting
		FormDataMultiPart form = new FormDataMultiPart();
		
		// set the output format to soap12
		// triggers the various outputs formats of the validator. If unset, the usual Web format will be sent. 
		// If set to soap12, 
		// the SOAP1.2 interface will be triggered. See the SOAP 1.2 response format description at
		//  http://validator.w3.org/docs/api.html#requestformat
		form.field("output", "soap12");
	
		// make sure Unicode 0x0 chars are removed from html (if any)
		// see https://github.com/WolfgangFahl/w3cValidator/issues/1
		Pattern pattern = Pattern.compile("[\\000]*");
		Matcher matcher = pattern.matcher(html);
		if (matcher.find()) {
		   html = matcher.replaceAll("");
		}
		
		// The document to validate, POSTed as multipart/form-data
		FormDataBodyPart fdp = new FormDataBodyPart("uploaded_file",
				IOUtils.toInputStream(html),
				// new FileInputStream(tmpHtml),
				MediaType.APPLICATION_OCTET_STREAM_TYPE);
		
		// attach the inputstream as upload info to the form
		form.bodyPart(fdp);
		
		// now post the form via the Internet/Intranet
		ClientResponse response = resource.type(MediaType.MULTIPART_FORM_DATA)
				.post(ClientResponse.class, form);
		// in debug mode show the response status
		if (debug)
			LOGGER.log(Level.INFO, "response status for '"+url+"'=" + response.getStatus());
		// if the http Status is ok
		if (response.getStatus() == 200) {
			// get the XML encoded SOAP 1.2 response format
			String responseXml = response.getEntity(String.class);
			// in debug mode show the full xml 
			if (debug)
				LOGGER.log(Level.INFO,responseXml);
			// unmarshal the xml message to the format to a W3CValidator Java object
			JAXBContext context = JAXBContext.newInstance(W3CValidator.class);
			Unmarshaller u = context.createUnmarshaller();
			StringReader xmlReader = new StringReader(responseXml);
			// this step will convert from xml text to Java Object
			result = (W3CValidator) u.unmarshal(xmlReader);
		}
		// return the result which might be null if the response status was other than 200
		return result;

	} // check

	
	/**
	 * field that holds the  structure for the Body node of the message
	 * <pre>
   * {@code
	 * <env:Body>
	 * ...
	 * </env:Body>
	 * }
	 * </pre>
	 */
	@XmlElement(name = "Body")
	// initialize Body
	public Body body = new Body();

	/**
	 * structure for the Body node of the message
	 * <pre>
   * {@code
	 * <env:Body>
	 * ...
	 * </env:Body>
	 * }
	 * </pre>
	 */
	@XmlRootElement(name = "Body", namespace = "http://www.w3.org/2003/05/soap-envelope")
	@XmlAccessorType(XmlAccessType.FIELD)
	public static class Body {

		
		@XmlElement(name = "markupvalidationresponse", namespace = "http://www.w3.org/2005/10/markup-validator")
		public ValidationResponse response = new ValidationResponse();

		/**
		 * The main element of the validation response. Encloses all other information about the validation results.
		 * @author wf
		 * <pre>
		 * {@code
		 * <m:markupvalidationresponse env:encodingStyle="http://www.w3.org/2003/05/soap-encoding" xmlns:m="http://www.w3.org/2005/10/markup-validator">
		 * ...
		 * </m:markupvalidationresponse>
		 * }
		 * </pre>
		 */
		@XmlAccessorType(XmlAccessType.FIELD)
		public static class ValidationResponse {
			/**
			 * the address of the document validated. Will (likely?) be upload://Form Submission if an uploaded document or fragment was validated. In EARL terms, this is the TestSubject. 
			 */
			@XmlElement(namespace = "http://www.w3.org/2005/10/markup-validator")
			public String uri;

			/**
			 * Location of the service which provided the validation result. In EARL terms, this is the Assertor
			 */
			@XmlElement(namespace = "http://www.w3.org/2005/10/markup-validator")
			public String checkedby;
			
			/**
			 * Detected (or forced) Document Type for the validated document
			 */
			@XmlElement(namespace = "http://www.w3.org/2005/10/markup-validator")
			public String doctype;

			/**
			 * Detected (or forced) Character Encoding for the validated document
			 */
			@XmlElement(namespace = "http://www.w3.org/2005/10/markup-validator")
			public String charset;

			/**
			 * Whether or not the document validated passed or not formal validation (true|false boolean)
			 */
			@XmlElement(namespace = "http://www.w3.org/2005/10/markup-validator")
			public boolean validity;

			/**
			 * Encapsulates all data about errors encountered through the validation process
			 * <pre>
       * {@code
			 * <m:errors>
			 * ...
			 * </m:errors>
			 * }
			 * </pre>
			 */
			@XmlElement(namespace = "http://www.w3.org/2005/10/markup-validator")
			public Errors errors = new Errors();

			/**
			 * wrapped list of validation errors structurally equivalent to Warnings
			 */
			public static class Errors {
				/**
				 * a child of errors, counts the number of errors listed
				 */
				@XmlElement(namespace = "http://www.w3.org/2005/10/markup-validator")
				public int errorcount;
				
				/**
				 * a child of errors, contains the list of errors (surprise!)
				 */
				@XmlElementWrapper(name = "errorlist", namespace = "http://www.w3.org/2005/10/markup-validator")
				@XmlElement(name = "error", namespace = "http://www.w3.org/2005/10/markup-validator")
				public List<ValidationError> errorlist = new ArrayList<ValidationError>();

			 	/**
			 	 * a child of errorlist, contains the information on a single validation error. 
			 	 */
				@XmlRootElement(name = "error", namespace = "http://www.w3.org/2005/10/markup-validator")
				public static class ValidationError extends ValidationAtom {
				} // Error
			} // Errors

			/**
			 * Encapsulates all data about warnings encountered through the validation process
			 * <pre>
       * {@code
			 * <m:warnings>
			 * ...
			 * </m:warnings>
			 * }
			 * </pre>
			 */
			@XmlElement(namespace = "http://www.w3.org/2005/10/markup-validator")
			public Warnings warnings = new Warnings();

			/**
			 * wrapped list of validation warnings structurally equivalent to Errors
			 */
			public static class Warnings {
				/**
				 * a child of warnings, counts the number of warnings listed
				 */
				@XmlElement(namespace = "http://www.w3.org/2005/10/markup-validator")
				public int warningcount;
				
				/**
				 * a child of warnings, contains the list of warnings (surprise!)
				 */
				@XmlElementWrapper(name = "warninglist", namespace = "http://www.w3.org/2005/10/markup-validator")
				@XmlElement(name = "warning", namespace = "http://www.w3.org/2005/10/markup-validator")
				public List<ValidationWarning> warninglist = new ArrayList<ValidationWarning>();

				/**
			 	 * a child of warninglist, contains the information on a single validation warning. 
			 	 */
				@XmlRootElement(name = "warning", namespace = "http://www.w3.org/2005/10/markup-validator")
				public static class ValidationWarning extends ValidationAtom {
				} // ValidationWarning
			} // Warnings

			/**
			 * base class for ValidationError and ValidationWarning
			 * containing e.g. line, col and message 
			 *
			 */
			public static class ValidationAtom {
				/**
				 * Within the source code of the validated document, refers to the line where 
				 * the error was detected.
				 */
				@XmlElement(namespace = "http://www.w3.org/2005/10/markup-validator")
				public int line;
				
				/**
				 * Within the source code of the validated document, refers to the column of the 
				 * line where the error was detected.
				 */
				@XmlElement(namespace = "http://www.w3.org/2005/10/markup-validator")
				public int col;
				
				/**
				 * Snippet of the source where the error was found. Given as HTML fragment within CDATA block.
				 */
				@XmlCDATA
				@XmlElement(namespace = "http://www.w3.org/2005/10/markup-validator")
				public String source;
				
				/**
				 * Explanation for the error. Given as HTML fragment within CDATA block.
				 */
				@XmlCDATA
				@XmlElement(namespace = "http://www.w3.org/2005/10/markup-validator")
				public String explanation;
				
				/**
				 * The number/identifier of the error, as addressed internally by the validator
				 */
				@XmlElement(namespace = "http://www.w3.org/2005/10/markup-validator")
				public int messageid;
				
				/**
				 * The actual error message
				 */
				@XmlElement(namespace = "http://www.w3.org/2005/10/markup-validator")
				public String message;
				
				/**
				 * convert this W3CValidator to a human readable string
				 */
				public String toString() {
					String kind=this.getClass().getSimpleName();
					String result=kind + " line " + line + " col " + col + ":'" + message	+ "'";
					return result;
				}
			} // ValidationAtom
		} // ValidationResponse
	} // Body
} // W3CValidator
