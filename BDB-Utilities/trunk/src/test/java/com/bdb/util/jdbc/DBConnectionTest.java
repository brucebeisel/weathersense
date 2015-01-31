/* 
 * Copyright (C) 2015 Bruce Beisel
 *
 * This program is free software: you can redistribute it and/or modify
 * it under the terms of the GNU General Public License as published by
 * the Free Software Foundation, either version 3 of the License, or
 * (at your option) any later version.
 *
 * This program is distributed in the hope that it will be useful,
 * but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
 * GNU General Public License for more details.
 *
 * You should have received a copy of the GNU General Public License
 * along with this program.  If not, see <http://www.gnu.org/licenses/>.
 */
package com.bdb.util.jdbc;


import java.sql.Connection;
import java.sql.SQLException;

import junit.framework.TestCase;
import org.junit.After;
import org.junit.Before;
import org.junit.Test;

public class DBConnectionTest extends TestCase
{

    public DBConnectionTest(String arg0)
    {
        super(arg0);
    }

    @Before
    protected void setUp() throws Exception
    {
        super.setUp();
    }

    @After
    protected void tearDown() throws Exception
    {
        super.tearDown();
    }

    @Test
    public void testDBConnectionStringStringStringString()
    {
        
    }

    @Test
    public void testDBConnectionStringStringStringStringBoolean()
    {
        try {
            DBConnection dbcon = new DBConnection("jdbc:derby:memory:weathersense21;create=true",
                    "", "", true);
            
            dbcon.connect();
            
            Connection con = dbcon.getConnection();
            
            if (!con.getAutoCommit())
                fail("Auto-commit has incorrect value");
        }
        catch (SQLException e) {
            e.printStackTrace();
            fail("Unexpected exception");
        }
        
    }

    @Test
    public void testClose()
    {
       
    }

    @Test
    public void testGetConnection()
    {
        
    }

    public void testGetTableNames()
    {
       
    }

    public void testTableExists()
    {
        
    }

    public void testExecute()
    {
        
    }

    public void testExecuteQuery()
    {
        
    }

    public void testExecuteUpdatePreparedStatement()
    {
        
    }

    public void testExecuteUpdateString()
    {
        
    }

    public void testCommit()
    {
       
    }

    public void testRollback()
    {
        
    }

    public void testLockTables()
    {
        
    }

    public void testRun()
    {
        
    }

    public void testTerminationRequest()
    {
        
    }

    public void testMain()
    {
        
    }

}
