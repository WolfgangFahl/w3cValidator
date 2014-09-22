@XmlSchema(
    elementFormDefault=XmlNsForm.QUALIFIED,
    namespace="http://www.w3.org/2003/05/soap-envelope",
    xmlns={
      @XmlNs(prefix="m",  namespaceURI="http://www.w3.org/2005/10/markup-validator"),
      @XmlNs(prefix="env",namespaceURI="http://www.w3.org/2003/05/soap-envelope")
    }
)
package com.bitplan.w3ccheck;
 
import javax.xml.bind.annotation.XmlNs;
import javax.xml.bind.annotation.XmlNsForm;
import javax.xml.bind.annotation.XmlSchema;