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
package br.com.auster.facelift.cycle.hibernate;

import java.sql.Date;
import java.util.Collection;
import java.util.Iterator;

import net.sf.hibernate.Criteria;
import net.sf.hibernate.HibernateException;
import net.sf.hibernate.Session;
import net.sf.hibernate.expression.Expression;
import net.sf.hibernate.expression.Order;

import br.com.auster.facelift.cycle.interfaces.BaseCycleManagerImpl;
import br.com.auster.facelift.cycle.interfaces.CycleManagerException;
import br.com.auster.facelift.cycle.model.Cycle;
import br.com.auster.facelift.cycle.model.CycleProcessingId;
import br.com.auster.facelift.persistence.FetchCriteria;
import br.com.auster.facelift.persistence.OrderClause;
import br.com.auster.facelift.persistence.PersistenceResourceAccessException;

/**
 * @author framos
 * @version $Id$
 */
public class CycleManagerImpl extends BaseCycleManagerImpl {

	
	
	/**
	 * @see br.com.auster.facelift.cycle.interfaces.CycleManager#loadCycles(br.com.auster.facelift.persistence.FetchCriteria)
	 */
	public Collection loadCycles(FetchCriteria _criteria) throws PersistenceResourceAccessException, CycleManagerException {
//	    Session session = null;
//	    try {
//			session = (Session) persistence.openResourceConnection();
//			Criteria criteria = session.createCriteria(Cycle.class);
//			setFetchParametersInCriteria(criteria, _criteria);
//			return criteria.list();
//		} catch (HibernateException he) {
//	        throw new CycleManagerException("caught persistence exception", he);
//	    } finally {
//			persistence.closeResourceConnection(session);
//	    }
		throw new AbstractMethodError("method not implemented yet");
	}

	/**
	 * @see br.com.auster.facelift.cycle.interfaces.CycleManager#loadCycle(java.lang.String, br.com.auster.facelift.persistence.FetchCriteria)
	 */
	public Collection loadCycleHistory(String _cycleCode, FetchCriteria _criteria) throws PersistenceResourceAccessException, CycleManagerException {
//	    Session session = null;
//	    try {
//			session = (Session) persistence.openResourceConnection();
//			Criteria criteria = session.createCriteria(Cycle.class);
//			criteria.add(Expression.eq("cycleCode", _cycleCode));
//			setFetchParametersInCriteria(criteria, _criteria);
//			return criteria.list();
//		} catch (HibernateException he) {
//	        throw new CycleManagerException("caught persistence exception", he);
//	    } finally {
//			persistence.closeResourceConnection(session);
//	    }
		throw new AbstractMethodError("method not implemented yet");
	}

	/**
	 * @see br.com.auster.facelift.cycle.interfaces.CycleManager#loadCycle(long)
	 */
	public Cycle loadCycle(long _cycleId) throws PersistenceResourceAccessException, CycleManagerException {
		throw new AbstractMethodError("method not implemented yet");
	}

	/**
	 * @see br.com.auster.facelift.cycle.interfaces.CycleManager#loadCycle(String, Date)
	 */
	public Cycle loadCycle(String _cycleCode, Date _cycleDate) throws PersistenceResourceAccessException, CycleManagerException {
		throw new AbstractMethodError("method not implemented yet");
	}
	
	/**
	 * @see br.com.auster.facelift.cycle.interfaces.CycleManager#registerCycle(br.com.auster.facelift.cycle.model.Cycle)
	 */
	public void registerCycle(Cycle _cycle) throws PersistenceResourceAccessException, CycleManagerException {
//	    Session session = null;
//	    try {
//			session = (Session) persistence.openResourceConnection();
//			session.saveOrUpdateCopy(_cycle);
//		} catch (HibernateException he) {
//	        throw new CycleManagerException("caught persistence exception", he);
//	    } finally {
//			persistence.closeResourceConnection(session);
//	    }
		throw new AbstractMethodError("method not implemented yet");
	}

	/**
	 * @see br.com.auster.facelift.cycle.interfaces.CycleManager#registerProcessing(br.com.auster.facelift.cycle.model.Cycle, java.util.Collection)
	 */
	public void registerProcessing(Cycle _cycle, Collection _ids) throws PersistenceResourceAccessException, CycleManagerException {
//	    Session session = null;
//	    Object transaction = null;
//	    try {
//			session = (Session) persistence.openResourceConnection();
//			transaction = persistence.beginTransaction(session);
//			for (Iterator it=_ids.iterator(); it.hasNext();) {
//				CycleProcessingId id = new CycleProcessingId(_cycle.getCycleId(), (String) it.next());
//				session.saveOrUpdate(id);
//			}
//		} catch (HibernateException he) {
//			persistence.rollbackTransaction(transaction);
//	        throw new CycleManagerException("caught persistence exception", he);
//	    } finally {	    	
//			persistence.closeResourceConnection(session, transaction);
//	    }
		throw new AbstractMethodError("method not implemented yet");
	}
	
	
    private void setFetchParametersInCriteria(Criteria _criteria, FetchCriteria _fetch) {
        if (_fetch == null) {
            return;
        }
        _criteria.setFirstResult(_fetch.getOffset());
        _criteria.setMaxResults(_fetch.getSize());
        Iterator orderIterator = _fetch.orderIterator();
        while (orderIterator.hasNext()) {
            OrderClause orderClause = (OrderClause) orderIterator.next();
            if (orderClause.isAscending()) {
                _criteria.addOrder( Order.asc(orderClause.getFieldName()) );
            } else {
                _criteria.addOrder( Order.desc(orderClause.getFieldName()) );                
            }
        }
    }


}
