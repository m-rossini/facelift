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
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Collection;

import junit.framework.TestCase;
import br.com.auster.common.xml.DOMUtils;
import br.com.auster.facelift.cycle.interfaces.CycleManager;
import br.com.auster.facelift.cycle.model.Cycle;

/**
 * @author framos
 * @version $Id$
 */
public class CycleManagerImplTest extends TestCase {

	
	
	public void testCycleOperations() {
//		try {
//			CycleManager manager = new CycleManagerImpl();
//			manager.init(DOMUtils.openDocument(CycleManagerImplTest.class.getResourceAsStream("testcase-configuration.xml")));
//			
//			CycleOperations ops = new CycleOperations(manager);
//			ops.createCycle();
//			ops.loadCycle();
//			ops.addProcIds();
//			ops.loadCycleAndIds();
//		} catch (Exception e) {
//			e.printStackTrace();
//			fail();
//		}
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
			java.util.Date d = formatter.parse("20-01-2006");
			c.setCycleDate(new Date(d.getTime()));
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
		
		public void loadCycleAndIds() throws Exception {
			Collection cycles = manager.loadCycleHistory("55");
			assertEquals(1, cycles.size());
			Cycle c = (Cycle)cycles.iterator().next();
			assertEquals(3, c.getProcessingIds().size());
			assertTrue(c.getInsertDate().before(c.getLastInsert()));
		}
	}
	
	
}
