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
 * Created on Mar 3, 2005
 */
package br.com.auster.facelift.services.properties;

import java.util.Set;

import br.com.auster.facelift.services.Service;

/**
 * Defines the basic oprations a property handler service must implement. This service allows the creation of 
 * 	realms, to group related property tokens. Also, it requires that one of these realms be declared as the 
 * 	default realm, in case no realm is specified when requesting for the value of a property. 
 * <P>
 * @author framos
 * @version $Id: PropertyHandler.java 46 2005-05-17 14:36:02Z framos $
 */
public interface PropertyHandler extends Service {

    /**
     * Returns the value of the property which matches the <code>_key</code> parameter in the default realm.
     *  If no value for that key could be found, <code>null</code> is returned.
     * <P>
     *  
     * @param _key the key of the property to lookup 
     * 
     * @return the value for the property
     */
    public String getProperty(String _key);
    
    
    /**
     * Returns the value of the property which matches the <code>_key</code> parameter for the specified realm.
     *  If no value for that key could be found, <code>null</code> is returned.
     * <P>
     * 
     * @param _realm the realm to search for
     * @param _key the key of the property to lookup
     *  
     * @return the value for the property
     */
    public String getProperty(String _key, String _realm);
    
    /**
     * Returns the set of realms configured for this Property Handler.
     * <P> 
     * 
     * @return the set of realms
     */
    public Set getRealms();
    
    /**
     * Returns the name of the default realm, as configured for this Property Handler.
     * <P>
     * 
     * @return the default realm name
     */
    public String getDefaultRealm();
}
