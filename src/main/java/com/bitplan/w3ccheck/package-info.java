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
 * This library contains of a single class see {@link com.bitplan.w3ccheck.W3CValidator} that allows
 * to connect to a W3C Markup Validation Service via a Java API.
 * @see <a href="http://validator.w3.org/">W3C Markup Validation Service</a>
 * 
 */
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