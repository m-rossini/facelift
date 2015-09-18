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
package br.com.auster.facelift.services.audit;

import org.w3c.dom.Element;

import br.com.auster.facelift.services.ConfigurationException;

/**
 * This implementation is used whenever no audit is specified. It will just ignore all
 *   messages and do nothing. 
 * <p>
 * This class exists to avoid <code>NullPointerException</code>s and to make transparent to other
 * 	classes those situations where the audit log was not defined.
 * 
 * @author framos
 * @version $Id$
 *
 */
public class NoAuditConfiguredLog extends AuditLogBase {

	/**
	 * @see br.com.auster.facelift.services.audit.AuditLog#audit(java.lang.String, java.lang.String[])
	 */
	public final void audit(String _message, String[] _variables) {}

	/**
	 * @see br.com.auster.facelift.services.audit.AuditLog#audit(java.lang.String, java.lang.String[], java.lang.Throwable)
	 */
	public final void audit(String _message, String[] _variables, Throwable _exception) {}
	
	/**
	 * @see br.com.auster.facelift.services.Service#init(org.w3c.dom.Element)
	 */
	public final void init(Element _configuration) throws ConfigurationException {}

}
