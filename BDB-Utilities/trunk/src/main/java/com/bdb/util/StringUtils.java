package com.bdb.util;

import java.util.*;
import java.io.*;

//
// CLASS: StringUtils
//
// DESCRIPTION:
//	Group of static methods for manipulating strings.
//
public final class StringUtils {
    public static final String DEFAULT_DELIMITERS = " \t";
    public static final String DEFAULT_QUOTE_CHARS = "'\"";

    public static List<String> tokenize(String input, String delimiters, String quoteChars) {
	ArrayList<String> list = new ArrayList<>();

	//
	// Set up the string tokenizer to only recognize words as tokens (As
	// opposed to numbers and words)
	//
	StreamTokenizer st = new StreamTokenizer(new StringReader(input));
	st.resetSyntax();
	st.wordChars(' ', '~');

	//
	// Register with the tokenizer all the white space characters
	//
	for (int i = 0; i < delimiters.length(); i++)
	    st.whitespaceChars(delimiters.charAt(i), delimiters.charAt(i));

	//
	// Register with the tokenizer all the quote characters
	//
	for (int i = 0; i < quoteChars.length(); i++)
	    st.quoteChar(quoteChars.charAt(i));
	
	try {
	    int tt;
	    //
	    // Loop through the string, extract tokens and add them to the List to
	    // be returned.
	    while ((tt = st.nextToken()) != StreamTokenizer.TT_EOF) {
		if (tt == StreamTokenizer.TT_WORD || quoteChars.indexOf(tt) != -1)
		    list.add(st.sval);
		else
		    System.err.println("StringUtils.tokenizer(): Unexpected token type: " + tt);
	    }
	}
	catch (IOException e) {
	    System.err.println("StringUtils.tokenizer(): Expected exception: " + e);
	}

	return list;
    }

    public static List<String> tokenize(String input) {
	return tokenize(input, DEFAULT_DELIMITERS, DEFAULT_QUOTE_CHARS);
    }
}
