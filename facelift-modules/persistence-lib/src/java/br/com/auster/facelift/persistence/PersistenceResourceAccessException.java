/*
 * Copyright (c) 2004 Auster Solutions. All Rights Reserved.
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
 * Created on Mai 10, 2005
 */
package br.com.auster.facelift.persistence;


/**
 * Defines exceptions raised when trying the acquire or release a reference to a persistence service. 
 *  <P> 
 * @author framos
 * @version $Id$
 */
public class PersistenceResourceAccessException extends PersistenceException {

	
	
    // ----------------------
    // Constructors
    // ----------------------
	
	public PersistenceResourceAccessException() {
		super();
	}
	
	public PersistenceResourceAccessException(String _message) {
		super(_message);
	}
	
	public PersistenceResourceAccessException(String _message, Throwable _cause) {
		super(_message, _cause);
	}
	
	public PersistenceResourceAccessException(Throwable _cause) {
		super(_cause);
	}	
}
