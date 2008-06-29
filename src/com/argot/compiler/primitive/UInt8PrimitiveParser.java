package com.argot.compiler.primitive;

import com.argot.TypeException;

public class UInt8PrimitiveParser 
implements ArgotPrimitiveParser
{

	public Object parse(String data) 
	throws TypeException 
	{
		return Short.decode(data);
	}

}
