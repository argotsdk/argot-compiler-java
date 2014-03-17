package com.argot.compiler;

import java.io.PrintStream;
import java.util.Iterator;
import java.util.List;

public class ArgotCompilerException 
extends Exception
{
	private static final long serialVersionUID = 1L;

	private List<String> _errors;
	
	public ArgotCompilerException( List<String> errors )
	{
		_errors = errors;
	}
	
	public List<String> getErrors()
	{
		return _errors;
	}

	public void printErrors(PrintStream err) 
	{
		Iterator<String> errorIterator = _errors.iterator();
		
		while(errorIterator.hasNext())
		{
			err.println(errorIterator.next());
		}
		
	}
	
}
