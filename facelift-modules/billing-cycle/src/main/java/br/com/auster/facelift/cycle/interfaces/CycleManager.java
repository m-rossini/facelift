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
package br.com.auster.facelift.cycle.interfaces;

import java.sql.Date;
import java.util.Collection;

import br.com.auster.facelift.cycle.model.Cycle;
import br.com.auster.facelift.persistence.FetchCriteria;
import br.com.auster.facelift.persistence.PersistenceResourceAccessException;
import br.com.auster.facelift.services.Service;

/**
 * @author framos
 * @version $Id$
 */
public interface CycleManager extends Service {

	/**
	 * Returns a collection of {@link br.com.auster.facelift.cycle.model.Cycle} instances, each representing one 
	 * 	cycle with all its dates and processing ids. If there are no cycles in the database, then an empty collection
	 *  is returned.
	 * 
	 * @return all the cycles registered
	 * 
	 * @throws PersistenceResourceAccessException
	 * @throws CycleManagerException
	 */
	public Collection loadCycles() throws PersistenceResourceAccessException, CycleManagerException;

	/**
	 * Same as {@link #loadCycles()} except that it handles a fetch criteria, when a limit of results or some sort
	 * 	order can be defined. 
	 * 
	 * @param _criteria the fetch criteria
	 * 
	 * @return all the cycles registered, respecting the fetch criteria
	 * 
	 * @throws PersistenceResourceAccessException
	 * @throws CycleManagerException
	 */
	public Collection loadCycles(FetchCriteria _criteria) throws PersistenceResourceAccessException, CycleManagerException;
	
	/**
	 * Returns all the cycle information found for the specified cycle code. Each element of the resulting collection
	 * 	corresponds to a different cycle date. If no information is found for such a cycle code, then the result will
	 *  be an empty collection.
	 *  
	 * @param _cycleCode the cycle code
	 * 
	 * @return all cycle information for the specified code
	 * 
	 * @throws PersistenceResourceAccessException
	 * @throws CycleManagerException
	 */
	public Collection loadCycleHistory(String _cycleCode) throws PersistenceResourceAccessException, CycleManagerException;
	
	/**
	 * Same as {@link #loadCycleHistory(String)} except that it handles a fetch criteria, when a limit of results or some sort
	 * 	order can be defined. 
	 * 
	 * @param _cycleCode the cycle code
	 * @param _criteria  the fetch criteria
	 * 
	 * @return all cycle information for the specified code, respecting the fetch criteria
	 * 
	 * @throws PersistenceResourceAccessException
	 * @throws CycleManagerException
	 */
	public Collection loadCycleHistory(String _cycleCode, FetchCriteria _criteria) throws PersistenceResourceAccessException, CycleManagerException;
	
	/**
	 * Returns the cycle information found for the specified cycle id. If no information is found for such a cycle id, 
	 *   then <code>null</code> is returned.
	 *  
	 * @param _cycleId the cycle unique id
	 * 
	 * @return cycle information for the specified id
	 * 
	 * @throws PersistenceResourceAccessException
	 * @throws CycleManagerException
	 */
	public Cycle loadCycle(long _cycleId) throws PersistenceResourceAccessException, CycleManagerException;	
	
	/**
	 * Returns the cycle information found for the specified cycle code and date. If no information is found for such a cycle id, 
	 *   then <code>null</code> is returned.
	 *  
	 * @param _cycleCode the cycle code
	 * @param _cycleDate the cycle end date
	 * 
	 * @return cycle information for the specified code and date
	 * 
	 * @throws PersistenceResourceAccessException
	 * @throws CycleManagerException
	 */
	public Cycle loadCycle(String _cycleCode, Date _cycleDate) throws PersistenceResourceAccessException, CycleManagerException;
	
	/**
	 * Register a new cycle information. To work correctly, the <code>cycleCode</code> and <code>cycleDate</code> attributes
	 * 	must be set; the description attribute is optional. If this cycle already exists, then no action will be taken. 
	 * <p>
	 * Initially the cycle list of processing ids is empty. To register ids use the {@link #registerProcessing(Cycle, String)} method. 
	 * 
	 * @param _cycle the new cycle information
	 * 
	 * @throws PersistenceResourceAccessException
	 * @throws CycleManagerException
	 */
	public void registerCycle(Cycle _cycle) throws PersistenceResourceAccessException, CycleManagerException;
	
	/**
	 * Assigns processing ids to the corresponding cycle. If the cycle already contains the id, then its ignored. 
	 * <p>
	 * To work correctly, the cycle instance must have the <code>id</code> attribute set, or the <code>cycleCode</code> 
	 *  <strong>and</strong> <code>cycleDate</code>. Without one of these two combinations of attributes, there is no way 
	 *   to identify uniquely the cycle the specified processing id is related to.
	 *  
	 * @param _cycle the cycle information
	 * @param _requestId the processing id
	 * 
	 * @throws PersistenceResourceAccessException
	 * @throws CycleManagerException
	 */
	public void registerProcessing(Cycle _cycle, String _requestId) throws PersistenceResourceAccessException, CycleManagerException;
	
	/**
	 * Just as {@link #registerProcessing(Cycle, String)} but instead of registering one single processing id, it registers
	 * 	multiple ids. The collection must be of <code>String</code> and the cycle information instance must comply with the
	 *  restrictions defined above.
	 *  
	 * @param _cycle the cycle information
	 * @param _requestId the list of processing ids
	 * 
	 * @throws PersistenceResourceAccessException
	 * @throws CycleManagerException
	 */
	public void registerProcessing(Cycle _cycle, Collection _requestId) throws PersistenceResourceAccessException, CycleManagerException;
	
}
