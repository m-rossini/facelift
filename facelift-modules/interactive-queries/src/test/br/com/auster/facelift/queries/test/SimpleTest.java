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
 * Created on May 2, 2005
 */
package br.com.auster.facelift.queries.test;

import java.util.Collection;
import java.util.Iterator;

import junit.framework.TestCase;

import org.w3c.dom.Element;

import br.com.auster.common.xml.DOMUtils;
import br.com.auster.facelift.queries.dao.SelectedRow;
import br.com.auster.facelift.queries.impl.QueryManagerImpl;
import br.com.auster.facelift.queries.interfaces.QueryFunctions;
import br.com.auster.facelift.queries.model.Query;
import br.com.auster.facelift.queries.model.ViewObject;

public class SimpleTest extends TestCase {

	/**
	 * @param args
	 */
	public void testView1() {

		try {
			Element configuration = DOMUtils.openDocument("src/test/sample-configuration.xml", false);
			QueryManagerImpl manager = new QueryManagerImpl();
			manager.init(configuration);

			ViewObject table = manager.loadViewDetails("view1");
			Query query = manager.createQuery(table);

			query.addColumn(QueryFunctions.COLUMN_RAW_VALUE, table.getColumn("web_request.request_id"));
			query.addColumn(QueryFunctions.COLUMN_RAW_VALUE, table.getColumn("proc_request.latest_status"));
			query.addColumn(QueryFunctions.DATE_NOW, null);
			query.addColumn(QueryFunctions.MATH_COUNT, table.getColumn("proc_request.request_label"));

			query.or(QueryFunctions.COLUMN_RAW_VALUE, table.getColumn("web_request.request_id"), QueryFunctions.NUMERIC_GREATER_EQUAL, "10");

			System.out.println("sql is : " + query.getSQLStatement());

			Collection result = manager.executeQuery(query);
			System.out.println("results are : " + result);

			System.out.println("finished !!!");
			assertTrue(true);
		} catch (Exception e) {
			e.printStackTrace();
			fail();
		}
	}

	public void testView2() {

		try {
			Element configuration = DOMUtils.openDocument("src/test/sample-configuration.xml", false);
			QueryManagerImpl manager = new QueryManagerImpl();
			manager.init(configuration);

			ViewObject table = manager.loadViewDetails("view2");
			Query query = manager.createQuery(table);

			query.addColumn(QueryFunctions.COLUMN_RAW_VALUE, table.getColumn("web_request.request_id"));
			query.addColumn(QueryFunctions.MATH_COUNT, table.getColumn("web_request_requests.proc_request_id"));

			query.or(QueryFunctions.COLUMN_RAW_VALUE, table.getColumn("web_request.request_id"), QueryFunctions.NUMERIC_GREATER_EQUAL, "10");

			System.out.println("sql is : " + query.getSQLStatement());

			Collection result = manager.executeQuery(query);
			System.out.println("results are : " + result);

			System.out.println("finished !!!");
			assertTrue(true);
		} catch (Exception e) {
			e.printStackTrace();
			fail();
		}
	}
	
	public void testGroupWithNoCondition() {

		try {
			Element configuration = DOMUtils.openDocument("src/test/sample-configuration.xml", false);
			QueryManagerImpl manager = new QueryManagerImpl();
			manager.init(configuration);

			ViewObject table = manager.loadViewDetails("view3");
			Query query = manager.createQuery(table);

			query.addColumn(QueryFunctions.COLUMN_RAW_VALUE, table.getColumn("web_request.request_id"));
			query.addColumn(QueryFunctions.MATH_COUNT, table.getColumn("web_request.request_id"));

			System.out.println("sql is : " + query.getSQLStatement());

			Collection result = manager.executeQuery(query);
			System.out.println("results are : " + result);

			System.out.println("finished !!!");
			assertTrue(true);
		} catch (Exception e) {
			e.printStackTrace();
			fail();
		}
	}
	
	public void testAscendingOrderBy() {

		try {
			Element configuration = DOMUtils.openDocument("src/test/sample-configuration.xml", false);
			QueryManagerImpl manager = new QueryManagerImpl();
			manager.init(configuration);

			ViewObject table = manager.loadViewDetails("view2");
			Query query = manager.createQuery(table);

			query.addColumn(QueryFunctions.COLUMN_RAW_VALUE, table.getColumn("web_request.request_id"));
			query.addColumn(QueryFunctions.COLUMN_RAW_VALUE, table.getColumn("web_request_requests.proc_request_id"));

			query.orderBy(table.getColumn("web_request.request_id"), true);
			
			String sql = query.getSQLStatement();
			System.out.println("sql is " + sql);
			assertTrue(sql.indexOf(" ORDER BY web_request.request_id ASC") > 0);
			
			Collection result = manager.executeQuery(query);
			long currentId = -1L;
			for (Iterator it = result.iterator(); it.hasNext();) {
				SelectedRow row = (SelectedRow) it.next();
				long id = ((Number)row.getCell(0)).longValue();
				assertTrue(currentId <= id);
				currentId = id;
			}
			
			assertTrue(true);
		} catch (Exception e) {
			e.printStackTrace();
			fail();
		}
	}	

	public void testDescendingOrderBy() {

		try {
			Element configuration = DOMUtils.openDocument("src/test/sample-configuration.xml", false);
			QueryManagerImpl manager = new QueryManagerImpl();
			manager.init(configuration);

			ViewObject table = manager.loadViewDetails("view2");
			Query query = manager.createQuery(table);

			query.addColumn(QueryFunctions.COLUMN_RAW_VALUE, table.getColumn("web_request.request_id"));
			query.addColumn(QueryFunctions.COLUMN_RAW_VALUE, table.getColumn("web_request_requests.proc_request_id"));

			query.orderBy(table.getColumn("web_request_requests.proc_request_id"), false);
			
			String sql = query.getSQLStatement();
			System.out.println("sql is " + sql);
			assertTrue(sql.indexOf(" ORDER BY web_request_requests.proc_request_id DESC") > 0);
			
			Collection result = manager.executeQuery(query);
			long currentId = Long.MAX_VALUE;
			for (Iterator it = result.iterator(); it.hasNext();) {
				SelectedRow row = (SelectedRow) it.next();
				long id = ((Number)row.getCell(1)).longValue();
				assertTrue(currentId > id);
				currentId = id;
			}
			
			assertTrue(true);
		} catch (Exception e) {
			e.printStackTrace();
			fail();
		}
	}	
	
	public void testOrderByNotBuilt() {

		try {
			Element configuration = DOMUtils.openDocument("src/test/sample-configuration.xml", false);
			QueryManagerImpl manager = new QueryManagerImpl();
			manager.init(configuration);

			ViewObject table = manager.loadViewDetails("view2");
			Query query = manager.createQuery(table);

			query.addColumn(QueryFunctions.COLUMN_RAW_VALUE, table.getColumn("web_request.request_id"));

			query.orderBy(table.getColumn("web_request_requests.proc_request_id"), true);
			
			String sql = query.getSQLStatement();
			System.out.println("sql is " + sql);
			assertTrue(sql.indexOf("ORDER BY web_request_requests.proc_request_id ASC") <= 0);
			
			assertTrue(true);
		} catch (Exception e) {
			e.printStackTrace();
			fail();
		}
	}	

	public void testMultipleOrderBy() {

		try {
			Element configuration = DOMUtils.openDocument("src/test/sample-configuration.xml", false);
			QueryManagerImpl manager = new QueryManagerImpl();
			manager.init(configuration);

			ViewObject table = manager.loadViewDetails("view2");
			Query query = manager.createQuery(table);

			query.addColumn(QueryFunctions.COLUMN_RAW_VALUE, table.getColumn("web_request.request_id"));
			query.addColumn(QueryFunctions.COLUMN_RAW_VALUE, table.getColumn("web_request_requests.proc_request_id"));

			query.orderBy(table.getColumn("web_request_requests.proc_request_id"), false);
			query.orderBy(table.getColumn("web_request.request_id"), true);
			
			String sql = query.getSQLStatement();
			System.out.println("sql is " + sql);
			assertTrue(sql.indexOf(" ORDER BY web_request_requests.proc_request_id DESC,web_request.request_id ASC") > 0);
			
			Collection result = manager.executeQuery(query);
			long currentId = Long.MAX_VALUE;
			for (Iterator it = result.iterator(); it.hasNext();) {
				SelectedRow row = (SelectedRow) it.next();
				long id = ((Number)row.getCell(1)).longValue();
				assertTrue(currentId > id);
				currentId = id;
			}
			
			assertTrue(true);
		} catch (Exception e) {
			e.printStackTrace();
			fail();
		}
	}		
}
