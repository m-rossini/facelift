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
 * Created on 07/03/2006
 */
package br.com.auster.facelift.cycle.jdbc;

import java.sql.Connection;
import java.sql.Date;
import java.sql.SQLException;
import java.util.Collection;
import java.util.Iterator;

import br.com.auster.facelift.cycle.interfaces.BaseCycleManagerImpl;
import br.com.auster.facelift.cycle.interfaces.CycleManagerException;
import br.com.auster.facelift.cycle.model.Cycle;
import br.com.auster.facelift.persistence.FetchCriteria;
import br.com.auster.facelift.persistence.PersistenceResourceAccessException;

/**
 * @author framos
 * @version $Id$
 */
public class CycleManagerImpl extends BaseCycleManagerImpl {

	
	
	/**
	 * @see br.com.auster.facelift.cycle.interfaces.CycleManager#loadCycles(FetchCriteria)
	 */
	public Collection loadCycles(FetchCriteria _criteria) throws PersistenceResourceAccessException, CycleManagerException {
        Connection session = null;
        try {
            session = (Connection) persistence.openResourceConnection();
            CycleDAO dao = new CycleDAO();
    		return dao.selectCycles(session, null, _criteria);
        } catch (SQLException sqle) {
            throw new CycleManagerException("caught JDBC SQL exception", sqle);
        } finally {
			persistence.closeResourceConnection(session);
        }
	}	
	
	/**
	 * @see br.com.auster.facelift.cycle.interfaces.CycleManager#loadCycleHistory(String, FetchCriteria)
	 */
	public Collection loadCycleHistory(String _cycleCode, FetchCriteria _criteria) throws PersistenceResourceAccessException, CycleManagerException {
        Connection session = null;
        try {
            session = (Connection) persistence.openResourceConnection();
            CycleDAO dao = new CycleDAO();
    		return dao.selectCycles(session, _cycleCode, _criteria);
        } catch (SQLException sqle) {
            throw new CycleManagerException("caught JDBC SQL exception", sqle);
        } finally {
			persistence.closeResourceConnection(session);
        }
	}
	
	/**
	 * @see br.com.auster.facelift.cycle.interfaces.CycleManager#loadCycle(long)
	 */
	public Cycle loadCycle(long _cycleId) throws PersistenceResourceAccessException, CycleManagerException {
        Connection session = null;
        try {
            session = (Connection) persistence.openResourceConnection();
            CycleDAO dao = new CycleDAO();
    		Cycle c = dao.selectUniqueCycle(session, _cycleId, null, null);
    		if (c != null) {
    			c.setProcessingIds( dao.selectProcessingIds(session, _cycleId) );
    		}
    		return c;
        } catch (SQLException sqle) {
            throw new CycleManagerException("caught JDBC SQL exception", sqle);
        } finally {
			persistence.closeResourceConnection(session);
        }
	}

	/**
	 * @see br.com.auster.facelift.cycle.interfaces.CycleManager#loadCycle(String, Date)
	 */
	public Cycle loadCycle(String _cycleCode, Date _cycleDate) throws PersistenceResourceAccessException, CycleManagerException {
        Connection session = null;
        try {
            session = (Connection) persistence.openResourceConnection();
            CycleDAO dao = new CycleDAO();
    		Cycle c = dao.selectUniqueCycle(session, 0L, _cycleCode, _cycleDate);
    		if (c != null) {
    			c.setProcessingIds( dao.selectProcessingIds(session, c.getCycleId()) );
    		}
    		return c;
        } catch (SQLException sqle) {
            throw new CycleManagerException("caught JDBC SQL exception", sqle);
        } finally {
			persistence.closeResourceConnection(session);
        }
	}	
	
	/**
	 * @see br.com.auster.facelift.cycle.interfaces.CycleManager#registerCycle(br.com.auster.facelift.cycle.model.Cycle)
	 */
	public void registerCycle(Cycle _cycle) throws PersistenceResourceAccessException, CycleManagerException {
        Connection session = null;
        try {
            session = (Connection) persistence.openResourceConnection();
            CycleDAO dao = new CycleDAO();
    		dao.insertCycle(session, _cycle);
        } catch (SQLException sqle) {
            throw new CycleManagerException("caught JDBC SQL exception", sqle);
        } finally {
			persistence.closeResourceConnection(session);
        }
	}

	/**
	 * @see br.com.auster.facelift.cycle.interfaces.CycleManager#registerProcessing(br.com.auster.facelift.cycle.model.Cycle, java.util.Collection)
	 */
	public void registerProcessing(Cycle _cycle, Collection _ids) throws PersistenceResourceAccessException, CycleManagerException {
        Connection session = null;
        Object transaction = null;
        try {
            session = (Connection) persistence.openResourceConnection();
            transaction = persistence.beginTransaction(session);
            // saving ids in current cycle
            for (Iterator it=_ids.iterator(); it.hasNext();) {
            	_cycle.addProcessingId((String) it.next());
            }
            // saving to database
            CycleDAO dao = new CycleDAO();
        	dao.insertProcessingIds(session, _cycle, _cycle.getProcessingIds());
    		dao.updateCycleLastInsert(session, _cycle);
        } catch (SQLException sqle) {
            persistence.rollbackTransaction(transaction);
            throw new CycleManagerException("caught JDBC SQL exception", sqle);
        } finally {
        	persistence.commitTransaction(transaction);
			persistence.closeResourceConnection(session);
        }
	}

}
