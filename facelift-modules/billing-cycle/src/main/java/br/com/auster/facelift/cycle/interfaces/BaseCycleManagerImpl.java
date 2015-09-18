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
 * Created on 09/03/2006
 */
package br.com.auster.facelift.cycle.interfaces;

import java.util.ArrayList;
import java.util.Collection;

import org.apache.log4j.Logger;
import org.w3c.dom.Element;

import br.com.auster.common.log.LogFactory;
import br.com.auster.common.xml.DOMUtils;
import br.com.auster.facelift.cycle.model.Cycle;
import br.com.auster.facelift.persistence.PersistenceResourceAccessException;
import br.com.auster.facelift.persistence.PersistenceService;
import br.com.auster.facelift.services.ConfigurationException;

/**
 * @author framos
 * @version $Id$
 */
public abstract class BaseCycleManagerImpl implements CycleManager {

	
	
	protected PersistenceService persistence;
    private Logger log = LogFactory.getLogger(BaseCycleManagerImpl.class);
	
	
	/**
	 * @see br.com.auster.facelift.cycle.interfaces.CycleManager#loadCycles()
	 */
	public Collection loadCycles() throws PersistenceResourceAccessException, CycleManagerException {
		return loadCycles(null);
	}

	/**
	 * @see br.com.auster.facelift.cycle.interfaces.CycleManager#loadCycleHistory(java.lang.String)
	 */
	public Collection loadCycleHistory(String _cycleCode) throws PersistenceResourceAccessException, CycleManagerException {
		return loadCycleHistory(_cycleCode, null);
	}

	/**
	 * @see br.com.auster.facelift.cycle.interfaces.CycleManager#registerProcessing(br.com.auster.facelift.cycle.model.Cycle, java.lang.String)
	 */
	public void registerProcessing(Cycle _cycle, String _requestId) throws PersistenceResourceAccessException, CycleManagerException {
		ArrayList list = new ArrayList();
		list.add(_requestId);
		registerProcessing(_cycle, list);
	}

	/**
	 * @see br.com.auster.facelift.services.Service#init(org.w3c.dom.Element)
	 */
	public void init(Element _configuration) throws ConfigurationException {
		try {
			log.info("Configuring Billing Cycle service");
			String klass = DOMUtils.getAttribute(_configuration, "persistence", true);
			log.info("persistence class =" + klass);
			persistence = (PersistenceService) Class.forName(klass).newInstance();
			persistence.init(DOMUtils.getElement(_configuration, "persistence-configuration", true));
		} catch (InstantiationException ie) {
			throw new ConfigurationException("could not initialize persistence service", ie);
		} catch (IllegalAccessException iae) {
			throw new ConfigurationException("could not create persistence service", iae);
		} catch (ClassNotFoundException cnfe) {
			throw new ConfigurationException("could not find persistence service class", cnfe);
		}
	}

	
}
