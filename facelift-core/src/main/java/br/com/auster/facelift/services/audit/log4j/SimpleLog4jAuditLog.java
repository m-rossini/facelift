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
 * Created on 16/09/2006
 */
package br.com.auster.facelift.services.audit.log4j;

import org.apache.log4j.Appender;
import org.apache.log4j.Logger;
import org.apache.log4j.xml.DOMConfigurator;
import org.w3c.dom.Element;

import br.com.auster.common.xml.DOMUtils;
import br.com.auster.facelift.services.ConfigurationException;
import br.com.auster.facelift.services.audit.AuditLogBase;

/**
 * @author framos
 * @version $Id$
 */
public class SimpleLog4jAuditLog extends AuditLogBase {

	
	public static final String AUDIT_APPENDER_ELEMENT = "appender";
	public static final String AUDIT_CONFIGURATION_FILE = "configuration-file";
	
	private Logger auditLogger; 
	
	/**
	 * @see br.com.auster.facelift.services.audit.AuditLog#audit(java.lang.String, java.lang.String[])
	 */
	public void audit(String _message, String[] _variables) {
		String auditMessage = this.applyVariables(_message, _variables);
		auditLogger.info(auditMessage);
	}

	/**
	 * @see br.com.auster.facelift.services.audit.AuditLog#audit(java.lang.String, java.lang.String[], java.lang.Throwable)
	 */
	public void audit(String _message, String[] _variables, Throwable _exception) {
		String[] variables = _variables;
		if (_exception != null) {
			variables = new String[_variables.length+1];
			System.arraycopy(_variables, 0, variables, 0, _variables.length);
			variables[_variables.length] = _exception.getLocalizedMessage();
		}
		String auditMessage = this.applyVariables(_message, variables);
		auditLogger.info(auditMessage);
	}
	
	/**
	 * @see br.com.auster.facelift.services.Service#init(org.w3c.dom.Element)
	 */
	public void init(Element _configuration) throws ConfigurationException {
		if (_configuration == null) {
			throw new ConfigurationException("Cannot init. Log4J audit without a valid configuration.");
		}
		// will help us building an appender out of a configuration
		AppenderConfigurationExtractor ace = new AppenderConfigurationExtractor();
		try {
			if (!AUDIT_APPENDER_ELEMENT.equals(_configuration.getLocalName())) {
				if (DOMUtils.getElement(_configuration,AUDIT_APPENDER_ELEMENT, false) != null) {
					_configuration = DOMUtils.getElement(_configuration,AUDIT_APPENDER_ELEMENT, true);
				} else {
					String configfile = DOMUtils.getAttribute(_configuration, AUDIT_CONFIGURATION_FILE, true);
					_configuration = DOMUtils.openDocument(configfile, false);
				}
			}
			Appender appender = ace.getAppender(_configuration);
			// this allows the audit configuration to be available only logger instances of this class
			this.auditLogger = Logger.getLogger(SimpleLog4jAuditLog.class);
			this.auditLogger.removeAllAppenders();
			this.auditLogger.addAppender(appender);
		} catch (Exception e) {
			throw new ConfigurationException("Could not init. Log4j audit appender:", e);
		}
	}

	
	private static final class AppenderConfigurationExtractor extends DOMConfigurator {
		public Appender getAppender(Element _configuration) {
			return this.parseAppender(_configuration);
		}
	}
	
}
