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
 * Created on Apr 29, 2005
 */
package br.com.auster.facelift.queries.interfaces;

import java.util.Collection;

import br.com.auster.facelift.queries.model.Query;
import br.com.auster.facelift.queries.model.ViewObject;
import br.com.auster.facelift.services.Service;


/**
 * Facelift service that handles configurable single table queries. The possible tables are pre-configured and this
 *  information is loaded during initialization of the service. Then, with the table definition and an instance of
 *  <code>Query</code>, a SQL statment can be executed.
 * <P>
 * The service allows for aggregation function, filter options and ordering.
 * 
 * @author framos
 * @version $Id$
 */
public interface QueryManager extends Service {

    
	/**
	 * Returns a list of names, representing the configured tables
	 * 
	 * @return the list of configured tables
	 */
    public Collection listViews();
    
	/**
	 * Returns the table definition object, containing all information needed to start building the SQL statement. If the
	 *  table with the specified name does not exists, then <code>null</code> is returned.
	 *   
	 * @param _name the name of the table
	 * @return the table definition object
	 */
    public ViewObject loadViewDetails(String _name);
	
	/**
	 * Creates a <code>Query</code> object, which will enable the creation of a query for the specified table. 
	 *   
	 * @param _object the table definition object
	 * @return the newly created query object
	 */    
    public Query createQuery(ViewObject _object) throws QueryException;

	/**
	 * Executes the query, after being composed and returns a collection with the resulting rows.
	 * 
	 * @param _query the query object
	 * @return the collection of selected rows
	 * @throws QueryException if any error while executing the query was detected
	 */
    public Collection executeQuery(Query _query) throws QueryException;
}
