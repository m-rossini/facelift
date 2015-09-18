/*
 * Copyright (c) 2004-2006 Auster Solutions. All Rights Reserved.
 *
 * THIS SOFTWARE IS PROVIDED BY THE COPYRIGHT HOLDERS AND CONTRIBUTORS "AS IS"
 * AND ANY EXPRESS OR IMPLIED WARRANTIES, INCLUDING, BUT NOT LIMITED TO,
 * THE IMPLIED WARRANTIES OF MERCHANTABILITY AND FITNESS FOR A PARTICULAR
 * PURPOSE ARE DISCLAIMED. IN NO EVENT SHALL THE COPYRIGHT OWNER OR
 * CONTRIBUTORS BE LIABLE FOR ANY DIRECT, INDIRECT, INCIDENTAL, SPECIAL,
 * EXEMPLARY, OR CONSEQUENTIAL DAMAGES (INCLUDING, BUT NOT LIMITED TO,
 * PROCUREMENT OF SUBSTITUTE GOODS OR SERVICES; LOSS OF USE, DATA, OR PROFITS;
 * OR BUSINESS INTERRUPTION) HOWEVER CAUSED AND ON ANY THEORY OF LIABILITY,
 * WHETHER IN CONTRACT, STRICT LIABILITY, OR TORT (INCLUDING NEGLIGENCE
 * OR OTHERWISE) ARISING IN ANY WAY OUT OF THE USE OF THIS SOFTWARE,
 * EVEN IF ADVISED OF THE POSSIBILITY OF SUCH DAMAGE.
 *
 * Created on 18/09/2006
 */
package br.com.auster.facelift.services.audit.log4j;

import java.util.MissingResourceException;
import java.util.ResourceBundle;

import org.apache.log4j.Logger;
import org.w3c.dom.Element;

import br.com.auster.common.xml.DOMUtils;
import br.com.auster.facelift.services.ConfigurationException;

/**
 * @author framos
 * @version $Id$
 */
public class ResourceBundledLog4jAuditLog extends SimpleLog4jAuditLog {


	public static final String BUNDLE_FILE_ATTR = "bundle-file";
	// should get root logger so it wont conflit with audit logger
	private static final Logger log = Logger.getRootLogger();


	protected ResourceBundle messages;


	/**
	 * Remember that when configuring the bundle filename, its extension <strong>must not</strong>
	 *   be specified.
	 *
	 * @see br.com.auster.facelift.services.audit.log4j.SimpleLog4jAuditLog#init(Element)
	 */
	public void init(Element _configuration) throws ConfigurationException {
		String bundleFile = null;
		try {
			bundleFile = DOMUtils.getAttribute(_configuration, BUNDLE_FILE_ATTR, true);
			this.messages = ResourceBundle.getBundle(bundleFile);
		} catch (MissingResourceException mre) {
			log.warn("Missing bundle file: " + bundleFile, mre);
		}
		super.init(_configuration);
	}


	/**
	 * Prior to printing the message, it will lookup the defined resource bundle using such message
	 *   as key.
	 *
	 * @see br.com.auster.facelift.services.audit.AuditLogBase#applyVariables(String, String[]);
	 */
	protected String applyVariables(String _message, String[] _variables) {
		String bundledMessage = _message;
		if (this.messages != null) {
			try {
				bundledMessage = this.messages.getString(_message);

				if (_variables != null) {
					for (int i=0; i < _variables.length; i++) {
						try {
							String variableValue = this.messages.getString(_variables[i]);
							_variables[i] = variableValue;
						} catch (MissingResourceException mre) {
							// nothing to do; keep variable info as the one to be printed
						}
					}
				}
			} catch (Exception e) {
				log.warn("Error getting message for: " + _message, e);
			}
		}
		return super.applyVariables(bundledMessage, _variables);
	}

}
