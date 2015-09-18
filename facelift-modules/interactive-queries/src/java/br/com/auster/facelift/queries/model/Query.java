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
 * Created on Sep 05, 2005
 */
package br.com.auster.facelift.queries.model;

import java.util.Collection;


/**
 * 
 * @author framos
 * @version $Id$
 */
public interface Query {

	
	public String getSQLStatement();

	public void addColumn(String _functionCode, ColumnObject _column);
	public Collection getQueriedColumns();
	
	public void and(String _leftFunctionCode, ColumnObject _leftColumn, String _operator);
	public void and(String _leftFunctionCode, ColumnObject _leftColumn, String _operator, Object _value);
	public void and(String _leftFunctionCode, ColumnObject _leftColumn, String _operator, String _rightFunctionCode, ColumnObject _rightColumn);

	public void or(String _leftFunctionCode, ColumnObject _leftColumn, String _operator);
	public void or(String _leftFunctionCode, ColumnObject _leftColumn, String _operator, Object _value);
	public void or(String _leftFunctionCode, ColumnObject _leftColumn, String _operator, String _rightFunctionCode, ColumnObject _rightColumn);

	public void clearOrderList();
	public void orderBy(ColumnObject _column, boolean _isAscending);
	
	public void setSourceView(ViewObject _view);
	public ViewObject getSourceView();
	
	public String formatFunction(String _functioncode, Object _value);
	
}
