package com.argot.compiler.primitive;

import com.argot.TypeException;

public class UInt16PrimitiveParser 
implements ArgotPrimitiveParser
{

	public Object parse(String data) 
	throws TypeException 
	{
		return Integer.decode(data);
	}

}
