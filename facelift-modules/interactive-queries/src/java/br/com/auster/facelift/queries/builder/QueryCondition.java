/*
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
package br.com.auster.facelift.queries.builder;

import java.io.Serializable;

import br.com.auster.facelift.queries.model.Query;

/**
 * Defines the basic behaviour a query filtering condition must implement. As of now, the only operation all must expose
 * 	is to create a string representation of itself.
 * 
 * @author framos
 * @version $Id: QueryCondition.java 158 2005-11-04 13:03:09Z framos $
 */
public abstract class QueryCondition implements Serializable {

	
	
    // ----------------------
    // Public methods
    // ----------------------
	
    /**
     * Returns the SQL statement representing this condition
     */
    public abstract String getSQLStatement(Query _query);
    
}
