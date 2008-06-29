package com.argot.compiler.primitive;

import com.argot.TypeException;

public class StringPrimitiveParser 
implements ArgotPrimitiveParser
{

	public Object parse(String data) 
	throws TypeException 
	{
		// nothing to do but parse back data.
		return data;
	}

}
