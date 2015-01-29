package com.bdb.util;

import java.util.List;

/**
 *
 * @author Bruce
 */
public class StringUtilsTest {
    //
    // Unit Test Driver
    //
    public static void main(String[] args)
    {
	List<String> l = StringUtils.tokenize("Hello world");
	System.out.println(l);

	l = StringUtils.tokenize("Hello,cruel world", ", ", StringUtils.DEFAULT_QUOTE_CHARS);
	System.out.println(l);

	l = StringUtils.tokenize("'Hello;cruel';world", "; ", StringUtils.DEFAULT_QUOTE_CHARS);
	System.out.println(l);

	l = StringUtils.tokenize("'Hello cruel';world .hello again. there", "; ", ".");
	System.out.println(l);

	
    }
    
}
