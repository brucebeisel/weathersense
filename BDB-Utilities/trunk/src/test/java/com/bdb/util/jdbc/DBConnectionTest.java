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
