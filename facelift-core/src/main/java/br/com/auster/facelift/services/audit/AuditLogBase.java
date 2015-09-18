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

import java.text.MessageFormat;


/**
 * @author framos
 * @version $Id$
 */
public abstract class AuditLogBase implements AuditLog {



    // ----------------------
    // Public methods
    // ----------------------

	/**
	 * @see br.com.auster.facelift.services.audit.AuditLog#audit(java.lang.String)
	 */
	public final void audit(String _message) {
		this.audit(_message, (String[]) null);
	}

	/**
	 * @see br.com.auster.facelift.services.audit.AuditLog#audit(java.lang.String, java.lang.Throwable)
	 */
	public final void audit(String _message, Throwable _exception) {
		this.audit(_message, (String[]) null, _exception);
	}

	/**
	 * @see br.com.auster.facelift.services.audit.AuditLog#audit(java.lang.String, java.lang.String)
	 */
	public final void audit(String _message, String _variable1) {
		this.audit(_message, new String[] { _variable1 } );
	}

	/**
	 * @see br.com.auster.facelift.services.audit.AuditLog#audit(java.lang.String, java.lang.String, java.lang.Throwable)
	 */
	public final void audit(String _message, String _variable1, Throwable _exception) {
		this.audit(_message, new String[] { _variable1 }, _exception);
	}

	/**
	 * @see br.com.auster.facelift.services.audit.AuditLog#audit(java.lang.String, java.lang.String, java.lang.String)
	 */
	public final void audit(String _message, String _variable1, String _variable2) {
		this.audit(_message, new String[] { _variable1, _variable2 } );
	}

	/**
	 * @see br.com.auster.facelift.services.audit.AuditLog#audit(java.lang.String, java.lang.String, java.lang.String, java.lang.Throwable)
	 */
	public final void audit(String _message, String _variable1, String _variable2, Throwable _exception) {
		this.audit(_message, new String[] { _variable1, _variable2 }, _exception );
	}



    // ----------------------
    // Protected methods
    // ----------------------

	protected String applyVariables(String _message, String[] _variables) {
		return MessageFormat.format(_message, _variables);
	}
}
