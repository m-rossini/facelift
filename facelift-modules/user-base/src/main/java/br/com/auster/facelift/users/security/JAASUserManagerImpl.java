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
 * Created on 02/10/2006
 */
package br.com.auster.facelift.users.security;

import javax.security.auth.login.LoginContext;
import javax.security.auth.login.LoginException;

import org.apache.log4j.Logger;
import org.w3c.dom.Element;

import br.com.auster.common.xml.DOMUtils;
import br.com.auster.facelift.services.ConfigurationException;
import br.com.auster.facelift.users.interfaces.UserManager;
import br.com.auster.security.base.SecurityException;
import br.com.auster.security.jaas.SecurityCallbackHandler;

/**
 * @author framos
 * @version $Id$
 */
public class JAASUserManagerImpl extends UserManagerImpl implements UserManager {
	
	
	private String securityContext;
	
	
	private static final Logger log = Logger.getLogger(JAASUserManagerImpl.class);
	

	
	public JAASUserManagerImpl() {
		super();
	}
	
	
	/**
	 * @see br.com.auster.security.interfaces.SecurityFacade#authenticate(String, String)
	 */
	public boolean authenticate(String _userLogin, String _password) throws SecurityException {
		
        try {
            SecurityCallbackHandler handler = new SecurityCallbackHandler();
            LoginContext lc = new LoginContext(securityContext, handler);
            handler.setUserName(_userLogin);
            handler.setPassword(_password);
			log.debug("validating user login");
            lc.login();
        } catch (LoginException le) {
            throw new SecurityException(le);
        }
    	return true;
	}
	
	/**
	 * @see br.com.auster.facelift.services.Service#init(org.w3c.dom.Element)
	 */
	public void init(Element _configuration) throws ConfigurationException {
		super.init(_configuration);
        this.securityContext = DOMUtils.getAttribute(_configuration, "security-context", true);
		log.info("security context = " + securityContext);
	}	

}
