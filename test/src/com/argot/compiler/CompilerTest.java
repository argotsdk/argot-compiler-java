/*
 * Copyright 2003-2005 (c) Live Media Pty Ltd. <argot@einet.com.au> 
 *
 * This software is licensed under the Argot Public License 
 * which may be found in the file LICENSE distributed 
 * with this software.
 *
 * More information about this license can be found at
 * http://www.einet.com.au/License
 * 
 * The Developer of this software is Live Media Pty Ltd,
 * PO Box 4591, Melbourne 3001, Australia.  The license is subject 
 * to the law of Victoria, Australia, and subject to exclusive 
 * jurisdiction of the Victorian courts.
 */

package com.argot.compiler;

import junit.framework.TestCase;

public class CompilerTest 
extends TestCase
{

	protected void setUp() 
	throws Exception 
	{
		super.setUp();

		System.setProperty( "ARGOT_HOME", ".");
	}	
	
	public void txestCompileCommonArgot()
	throws Exception
	{
		String[] args = new String[1];
		args[0] = "argot/common.argot";
		
		ArgotCompiler.argotCompile( args );		
	}

	public void txestCompileNetworkVMArgot()
	throws Exception
	{
		String[] args = new String[1];
		args[0] = "argot/networkvm.argot";
		
		ArgotCompiler.argotCompile( args );		
	}
	
	public void tstCompileRemoteArgot()
	throws Exception
	{
		String[] args = new String[1];
		args[0] = "argot/remote.argot";
		
		ArgotCompiler.argotCompile( args );
	}

	public void testCompileRemoteArgot()
	throws Exception
	{
		String[] args = new String[1];
		args[0] = "argot/remoterpc.argot";
		
		ArgotCompiler.argotCompile( args );
	}
	
	public void tsstCompileNetArgot()
	throws Exception
	{
		String[] args = new String[1];
		args[0] = "argot/netargot.argot";
		
		ArgotCompiler.argotCompile( args );
	}

	public void testCompileTestInterfaceArgot()
	throws Exception
	{
		String[] args = new String[1];
		args[0] = "argot/nettest.argot";
		
		ArgotCompiler.argotCompile( args );
	}
	
	
	
}
