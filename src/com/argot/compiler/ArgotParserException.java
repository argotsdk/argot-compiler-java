package com.argot.compiler;

import org.antlr.runtime.IntStream;
import org.antlr.runtime.RecognitionException;

public class ArgotParserException
extends RecognitionException
{
	private String _reason;
	
	public ArgotParserException(String reason, IntStream input) {
		super(input);
		_reason = reason;
	}
	
	public String getReason()
	{
		return _reason;
	}
}
