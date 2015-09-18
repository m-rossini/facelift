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
package br.com.auster.facelift.cycle.jdbc;

import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.SQLException;
import java.sql.Statement;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Collection;
import java.util.Date;
import java.util.HashMap;
import java.util.Map;

import junit.framework.TestCase;

import org.w3c.dom.Element;

import br.com.auster.facelift.cycle.interfaces.CycleManager;
import br.com.auster.facelift.cycle.model.Cycle;
import br.com.auster.facelift.persistence.PersistenceResourceAccessException;
import br.com.auster.facelift.persistence.PersistenceService;
import br.com.auster.facelift.services.ConfigurationException;

/**
 * @author framos
 * @version $Id$
 */
public class CycleManagerImplTest extends TestCase {

	
	private static class TestImpl extends CycleManagerImpl {
		
		public void init(Element _configuration) throws ConfigurationException {
			this.persistence = new PersistenceService() {

				public Object openResourceConnection() throws PersistenceResourceAccessException {
					Map testInfo = new HashMap();
					testInfo.put("driver", "org.postgresql.Driver");
					testInfo.put("url", "jdbc:postgresql://jean:5432/testdb");
					testInfo.put("user", "test");
					testInfo.put("password", "test");
					return openResourceConnection(testInfo);
				}

				public Object openResourceConnection(Map _connectionInfo) throws PersistenceResourceAccessException {
					try {
						Class.forName((String)_connectionInfo.get("driver"));
						return DriverManager.getConnection((String)_connectionInfo.get("url"), (String)_connectionInfo.get("user"), (String)_connectionInfo.get("password"));
					} catch (Exception e) {
						throw new PersistenceResourceAccessException(e);
					}
				}

				public Object beginTransaction(Object _session) throws PersistenceResourceAccessException {
					return null;
				}

				public void closeResourceConnection(Object _session) throws PersistenceResourceAccessException {
					closeResourceConnection(_session, null, null);
				}

				public void closeResourceConnection(Object _session, Object _transaction) throws PersistenceResourceAccessException {
					closeResourceConnection(_session, null, _transaction);
				}

				public void closeResourceConnection(Object _session, Map _connectionInfo) throws PersistenceResourceAccessException {
					closeResourceConnection(_session, _connectionInfo, null);
				}

				public void closeResourceConnection(Object _session, Map _conectionInfo, Object _transaction) throws PersistenceResourceAccessException {
					if (_session == null) {
						return;
					}
					try {
						((Connection)_session).close();
					} catch (SQLException sqle) {
						throw new PersistenceResourceAccessException(sqle);
					}
				}

				public void commitTransaction(Object _transaction) throws PersistenceResourceAccessException {
					return;
				}

				public void rollbackTransaction(Object _transaction) throws PersistenceResourceAccessException {
					return;
				}

				public void init(Element _configuration) throws ConfigurationException {
					try {
						Connection c = (Connection) openResourceConnection();
						Statement s = c.createStatement();
						s.execute("delete from cycle_processing_ids");
						s.execute("delete from cycle_info");
						s.execute("drop sequence cycleinfo_sequence");
						s.execute("create sequence cycleinfo_sequence increment by 1 start with 1");
						s.close();
						closeResourceConnection(c);
					} catch (Exception e) {
						throw new ConfigurationException(e);
					}
				}
				
			};
			this.persistence.init(null);
		}
		
	}
	
	
	public void testCycleOperations() {
		try {
			CycleManager manager = new TestImpl();
			manager.init(null);
			
			CycleOperations ops = new CycleOperations(manager);
			ops.createCycle();
			ops.loadCycle();
			ops.addProcIds();
			ops.loadAndAddMoreIds();
		} catch (Exception e) {
			e.printStackTrace();
			fail();
		}
	}

	
	class CycleOperations {
		
		CycleManager manager;
		
		CycleOperations(CycleManager _manager) {
			manager = _manager;
		}
		
		public void createCycle() throws Exception {
			SimpleDateFormat formatter = new SimpleDateFormat("dd-mm-yyyy");
			Cycle c= new Cycle();
			c.setCycleCode("55");
			c.setCycleDescription("Some cycle info");
			Date d = formatter.parse("20-01-2006");
			c.setCycleDate(new java.sql.Date(d.getTime()));
			manager.registerCycle(c);
		}
		
		public void loadCycle() throws Exception {
			Collection empty = manager.loadCycleHistory("56");
			assertEquals(0, empty.size());
			Collection cycles = manager.loadCycleHistory("55");
			assertEquals(1, cycles.size());
		}
		
		public void addProcIds() throws Exception {
			Collection cycles = manager.loadCycleHistory("55");
			assertEquals(1, cycles.size());
			Cycle c = (Cycle)cycles.iterator().next();
			
			Collection ids = new ArrayList();
			ids.add("10");
			ids.add("11");
			ids.add("12");
			
			manager.registerProcessing(c, ids);
		}
		
		public void loadAndAddMoreIds() throws Exception {
			SimpleDateFormat formatter = new SimpleDateFormat("dd-mm-yyyy");
			Date d = formatter.parse("20-01-2006");
			Cycle c = manager.loadCycle("55", new java.sql.Date(d.getTime()));
			assertEquals(3, c.getProcessingIds().size());
			assertTrue(c.getInsertDate().before(c.getLastInsert()));
		
			Collection ids = new ArrayList();
			ids.add("13");
			ids.add("14");
			manager.registerProcessing(c, ids);
			
			Cycle c2 = manager.loadCycle("55", new java.sql.Date(d.getTime()));
			assertEquals(5, c2.getProcessingIds().size());
			assertEquals(5, c.getProcessingIds().size());
			assertTrue(c2.getInsertDate().before(c2.getLastInsert()));			
		}
		
	}
	
	
}
