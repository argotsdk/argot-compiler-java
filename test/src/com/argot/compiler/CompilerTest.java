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

import java.io.FileInputStream;

import com.argot.TypeLibrary;
import com.argot.TypeLibraryLoader;
import com.argot.common.CommonLoader;
import com.argot.dictionary.Dictionary;
import com.argot.dictionary.DictionaryLoader;
import com.argot.meta.MetaLoader;

import junit.framework.TestCase;

public class CompilerTest 
extends TestCase
{

	private TypeLibraryLoader libraryLoaders[] = {
		new MetaLoader(),
		new DictionaryLoader(),
		new CommonLoader()
	};
	
	protected void setUp() 
	throws Exception 
	{
		super.setUp();

		System.setProperty( "ARGOT_HOME", ".");
	}	
	
	public void testCompileCommonArgot()
	throws Exception
	{
		System.out.println("COMPILE ARGOT COMMON" );		
		String[] args = new String[1];
		args[0] = "argot/common.argot";
		
		ArgotCompiler compiler = new ArgotCompiler();
		compiler.argotCompile( args );
		
		TypeLibrary library = new TypeLibrary( libraryLoaders );
		Dictionary.readDictionary( library, new FileInputStream( "argot/common.dictionary" ));		
	}

	public void testCompileChannelArgot()
	throws Exception
	{
		System.out.println("COMPILE CHANNEL" );		
		String[] args = new String[1];
		args[0] = "argot/channel.argot";
		
		ArgotCompiler compiler = new ArgotCompiler();
		compiler.argotCompile( args );
		
		TypeLibrary library = new TypeLibrary( libraryLoaders );
		Dictionary.readDictionary( library, new FileInputStream( "argot/channel.dictionary" ));		
	}
	
	public void testCompileNetworkVMArgot()
	throws Exception
	{
		Thread.sleep( 1000 );
		System.out.println("COMPILE NETWORKVM" );
		
		String[] args = new String[1];
		args[0] = "argot/networkvm.argot";
		
		ArgotCompiler compiler = new ArgotCompiler();
		compiler.argotCompile( args );
		
		TypeLibrary library = new TypeLibrary( libraryLoaders );
		Dictionary.readDictionary( library, new FileInputStream( "argot/networkvm.dictionary" ));		
	}
	
	public void testCompileRemoteArgot()
	throws Exception
	{
		Thread.sleep( 1000 );
		System.out.println("COMPILE REMOTE" );

		String[] args = new String[1];
		args[0] = "argot/remote.argot";
		
		ArgotCompiler compiler = new ArgotCompiler();
		compiler.argotCompile( args );		

		TypeLibrary library = new TypeLibrary( libraryLoaders );
		Dictionary.readDictionary( library, new FileInputStream( "argot/remote.dictionary" ));		
	}

	public void testCompileRemoteRpcArgot()
	throws Exception
	{
		Thread.sleep( 1000 );
		System.out.println("COMPILE REMOTE RPC" );
		
		String[] args = new String[1];
		args[0] = "argot/remoterpc.argot";
		
		ArgotCompiler compiler = new ArgotCompiler();
		compiler.argotCompile( args );
		
		TypeLibrary library = new TypeLibrary( libraryLoaders );
		Dictionary.readDictionary( library, new FileInputStream( "argot/remoterpc.dictionary" ));		
	}
	
	public void testCompileNetArgot()
	throws Exception
	{
		Thread.sleep( 1000 );
		System.out.println("COMPILE NETARGOT" );
		
		String[] args = new String[1];
		args[0] = "argot/netargot.argot";
		
		ArgotCompiler compiler = new ArgotCompiler();
		compiler.argotCompile( args );
		
		TypeLibrary library = new TypeLibrary( libraryLoaders );
		Dictionary.readDictionary( library, new FileInputStream( "argot/netargot.dictionary" ));		
	}

	public void testCompileTestInterfaceArgot()
	throws Exception
	{
		Thread.sleep( 1000 );
		System.out.println("COMPILE NETTEST" );
		
		String[] args = new String[1];
		args[0] = "argot/nettest.argot";
		
		ArgotCompiler compiler = new ArgotCompiler();
		compiler.argotCompile( args );

		Thread.sleep( 1000 );
		
		TypeLibrary library = new TypeLibrary( libraryLoaders );
		Dictionary.readDictionary( library, new FileInputStream( "argot/nettest.dictionary" ));		
	}
	
	public void testCompileTestKnowledgeBaseArgot()
	throws Exception
	{
		System.out.println("COMPILE KNOWLEDGE" );
		
		String[] args = new String[1];
		args[0] = "argot/knowledgebase.argot";
		
		ArgotCompiler compiler = new ArgotCompiler();
		compiler.argotCompile( args );
		
		TypeLibrary library = new TypeLibrary( libraryLoaders );
		Dictionary.readDictionary( library, new FileInputStream( "argot/knowledgebase.dictionary" ));
	}	
	
}
