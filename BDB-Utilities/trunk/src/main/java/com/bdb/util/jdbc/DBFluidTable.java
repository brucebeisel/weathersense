package com.bdb.util.jdbc;

import java.util.Date;

public abstract class DBFluidTable<T> extends DBTable<T> {

    public DBFluidTable(String tableName, DBConnection con) 
    {
	super(tableName, con);
    }

    public abstract int trimTable(Date start, Date end);
}
