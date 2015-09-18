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
package br.com.auster.facelift.services.audit;

import br.com.auster.facelift.services.Service;

/**
 * @author framos
 * @version $Id$
 */
public interface AuditLog extends Service {
	
	
	
	/**
	 * Sends the <code>_message</code> directly to the audit log.
	 * 
	 * @param _message
	 */
	public void audit(String _message);
	public void audit(String _message, Throwable _exception);
	
	/**
	 * Uses the <code>_message</code> as a template, and appends the <code>_variable1</code> as 
	 * 	specified. The template should follow the specifications of resource bundles.
	 * 
	 * @param _message
	 * @param _variable1
	 */
	public void audit(String _message, String _variable1);
	public void audit(String _message, String _variable1, Throwable _exception);
	
	/**
	 * Same as {@link #audit(String, String)}, except it allows two variable attributes.
	 * 
	 * @param _message
	 * @param _variable1
	 * @param _variable2
	 */
	public void audit(String _message, String _variable1, String _variable2);
	public void audit(String _message, String _variable1, String _variable2, Throwable _exception);
	
	/**
	 * Same as {@link #audit(String, String)}, except it allows a undefied number of attributes.
	 * 
	 * @param _message
	 * @param _variables
	 */
	public void audit(String _message, String[] _variables);
	public void audit(String _message, String[] _variables, Throwable _exception);
}
