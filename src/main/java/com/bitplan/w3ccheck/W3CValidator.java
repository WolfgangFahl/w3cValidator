/**
 * see
 * https://github.com/WolfgangFahl/w3cValidator/blob/master/LICENSE
 */
package com.bitplan.w3ccheck;

import java.io.StringReader;
import java.util.ArrayList;
import java.util.List;
import java.util.logging.Level;

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

@XmlRootElement(name = "Envelope", namespace = "http://www.w3.org/2003/05/soap-envelope")
@XmlAccessorType(XmlAccessType.FIELD)
/**
 * Wrapper for W3C Validator Soap responses see http://validator.w3.org/docs/api.html#requestformat
 * @author wf
 *
 */
public class W3CValidator {
	protected static java.util.logging.Logger LOGGER = java.util.logging.Logger
			.getLogger("com.bitplan.w3ccheck");

	public static boolean debug=false;
	
	/**
	 * create a W3CValidator result for the given url with the given html
	 * 
	 * @param url
	 * @param html
	 * @return
	 * @throws JAXBException
	 */
	public static W3CValidator check(String url, String html)
			throws JAXBException {
		W3CValidator result = null;
		WebResource resource = Client.create().resource(url);
		FormDataMultiPart form = new FormDataMultiPart();
		// File tmpHtml = File.createTempFile("upload", ".html");
		// FileUtils.writeStringToFile(tmpHtml, html);
		form.field("output", "soap12");
		FormDataBodyPart fdp = new FormDataBodyPart("uploaded_file",
				IOUtils.toInputStream(html),
				// new FileInputStream(tmpHtml),
				MediaType.APPLICATION_OCTET_STREAM_TYPE);
		form.bodyPart(fdp);
		ClientResponse response = resource.type(MediaType.MULTIPART_FORM_DATA)
				.post(ClientResponse.class, form);
		if (debug)
			LOGGER.log(Level.INFO, "status " + response.getStatus());
		if (response.getStatus() == 200) {
			String responseXml = response.getEntity(String.class);
			if (debug)
				LOGGER.log(Level.INFO,responseXml);
			JAXBContext context = JAXBContext.newInstance(W3CValidator.class);
			Unmarshaller u = context.createUnmarshaller();
			StringReader xmlReader = new StringReader(responseXml);
			result = (W3CValidator) u.unmarshal(xmlReader);
		}
		return result;

	}

	@XmlElement(name = "Body")
	public Body body = new Body();

	@XmlRootElement(name = "Body", namespace = "http://www.w3.org/2003/05/soap-envelope")
	@XmlAccessorType(XmlAccessType.FIELD)
	public static class Body {

		@XmlElement(name = "markupvalidationresponse", namespace = "http://www.w3.org/2005/10/markup-validator")
		public ValidationResponse response = new ValidationResponse();

		@XmlAccessorType(XmlAccessType.FIELD)
		public static class ValidationResponse {
			@XmlElement(namespace = "http://www.w3.org/2005/10/markup-validator")
			public String uri;

			@XmlElement(namespace = "http://www.w3.org/2005/10/markup-validator")
			public String checkedby;

			@XmlElement(namespace = "http://www.w3.org/2005/10/markup-validator")
			public String doctype;

			@XmlElement(namespace = "http://www.w3.org/2005/10/markup-validator")
			public String charset;

			@XmlElement(namespace = "http://www.w3.org/2005/10/markup-validator")
			public boolean validity;

			@XmlElement(namespace = "http://www.w3.org/2005/10/markup-validator")
			public Errors errors = new Errors();

			/**
			 * wrapped list of validation errors
			 * @author wf
			 *
			 */
			public static class Errors {
				@XmlElement(namespace = "http://www.w3.org/2005/10/markup-validator")
				public int errorcount;
				@XmlElementWrapper(name = "errorlist", namespace = "http://www.w3.org/2005/10/markup-validator")
				@XmlElement(name = "error", namespace = "http://www.w3.org/2005/10/markup-validator")
				public List<ValidationError> errorlist = new ArrayList<ValidationError>();

				@XmlRootElement(name = "error", namespace = "http://www.w3.org/2005/10/markup-validator")
				public static class ValidationError extends ValidationAtom {
				} // Error
			} // Errors

			@XmlElement(namespace = "http://www.w3.org/2005/10/markup-validator")
			public Warnings warnings = new Warnings();

			public static class Warnings {
				@XmlElement(namespace = "http://www.w3.org/2005/10/markup-validator")
				public int warningcount;
				@XmlElementWrapper(name = "warninglist", namespace = "http://www.w3.org/2005/10/markup-validator")
				@XmlElement(name = "warning", namespace = "http://www.w3.org/2005/10/markup-validator")
				public List<ValidationWarning> warninglist = new ArrayList<ValidationWarning>();

				@XmlRootElement(name = "error", namespace = "http://www.w3.org/2005/10/markup-validator")
				public static class ValidationWarning extends ValidationAtom {
				} // ValidationWarning
			} // Warnings

			/**
			 * base class for ValidationError and ValidationWarning
			 * @author wf
			 *
			 */
			public static class ValidationAtom {
				@XmlElement(namespace = "http://www.w3.org/2005/10/markup-validator")
				public int line;
				@XmlElement(namespace = "http://www.w3.org/2005/10/markup-validator")
				public int col;
				@XmlCDATA
				@XmlElement(namespace = "http://www.w3.org/2005/10/markup-validator")
				public String source;
				@XmlCDATA
				@XmlElement(namespace = "http://www.w3.org/2005/10/markup-validator")
				public String explanation;
				@XmlElement(namespace = "http://www.w3.org/2005/10/markup-validator")
				public int messageid;
				@XmlElement(namespace = "http://www.w3.org/2005/10/markup-validator")
				public String message;
				
				/**
				 * human readable version
				 */
				public String toString() {
					String kind=this.getClass().getSimpleName();
					String result=kind + " line " + line + " col " + col + ":'" + message	+ "'";
					return result;
				}
			} // Error
		}
	}
}
