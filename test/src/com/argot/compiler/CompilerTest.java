/*
 * Copyright 2003-2009 (c) Live Media Pty Ltd. <argot@einet.com.au> 
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

import java.io.File;
import java.io.FileInputStream;


import com.argot.TypeLibrary;
import com.argot.TypeLibraryLoader;
import com.argot.TypeMap;
import com.argot.common.CommonLoader;
import com.argot.dictionary.Dictionary;
import com.argot.dictionary.DictionaryLoader;
import com.argot.meta.MetaLoader;
import com.argot.remote.RemoteLoader;

import junit.framework.TestCase;

public class CompilerTest 
extends TestCase
{

	private TypeLibraryLoader libraryLoaders[] = {
		new MetaLoader(),
		new DictionaryLoader(),
		new CommonLoader(),
		new RemoteLoader()
	};

	private TypeLibraryLoader baseCommonLoaders[] = {
			new MetaLoader(),
			new DictionaryLoader(),
			new CommonLoader(),
		};
	
	private TypeLibraryLoader baselibraryLoaders[] = {
		new MetaLoader(),
		new DictionaryLoader()
	};
	
	protected void setUp() 
	throws Exception 
	{
		super.setUp();

		System.setProperty( "ARGOT_HOME", ".");
	}	

	public void testCompileMetaArgot()
	throws Exception
	{
		System.out.println("COMPILE ARGOT META" );		
		String[] args = new String[1];
		args[0] = "argot/meta.argot";

		ArgotCompiler ac = new ArgotCompiler( new File( args[0] ), new File( "argot/meta.dictionary" ), null);
		ac.setLoadCommon(false);
		ac.doCompile();
		
		TypeLibrary library = new TypeLibrary( baselibraryLoaders );
		Dictionary.readDictionary( library, new FileInputStream( "argot/meta.dictionary" ));		
	}

	public void testCompileMetaExtensionsArgot()
	throws Exception
	{
		System.out.println("COMPILE ARGOT META EXTENSIONS" );		
		String[] args = new String[1];
		args[0] = "argot/meta_extensions.argot";

		ArgotCompiler ac = new ArgotCompiler( new File( args[0] ), new File( "argot/meta_extensions.dictionary" ), null);
		ac.setLoadCommon(false);
		ac.doCompile();
		
		TypeLibrary library = new TypeLibrary( baselibraryLoaders );
		TypeMap map = Dictionary.readDictionary( library, new FileInputStream( "argot/meta_extensions.dictionary" ));
		assertEquals( 5, map.size() );
	}

	public void testCompileDictionaryArgot()
	throws Exception
	{
		System.out.println("COMPILE ARGOT DICTIONARY" );		
		String[] args = new String[1];
		args[0] = "argot/dictionary.argot";

		ArgotCompiler ac = new ArgotCompiler( new File( args[0] ), new File( "argot/dictionary.dictionary" ), null);
		ac.setLoadCommon(true);
		ac.doCompile();
		
		TypeLibrary library = new TypeLibrary( baseCommonLoaders );
		TypeMap map = Dictionary.readDictionary( library, new FileInputStream( "argot/dictionary.dictionary" ));
		assertEquals( 8, map.size() );
	}	
	
	public void testCompileCommonArgot()
	throws Exception
	{
		System.out.println("COMPILE ARGOT COMMON" );		
		String[] args = new String[1];
		args[0] = "argot/common.argot";

		ArgotCompiler ac = new ArgotCompiler( new File( args[0] ), new File( "argot/common.dictionary" ), null);
		ac.setLoadCommon(false);
		ac.doCompile();
		
		TypeLibrary library = new TypeLibrary( baselibraryLoaders );
		TypeMap map = Dictionary.readDictionary( library, new FileInputStream( "argot/common.dictionary" ));
		assertEquals( 23, map.size() );
	}

	public void testCompileChannelArgot()
	throws Exception
	{
		System.out.println("COMPILE CHANNEL" );		
		String[] args = new String[1];
		args[0] = "argot/channel.argot";
		
		ArgotCompiler.argotCompile( args );
		
		TypeLibrary library = new TypeLibrary( libraryLoaders );
		Dictionary.readDictionary( library, new FileInputStream( "argot/channel.dictionary" ));		
	}

	public void testCompileRemoteArgot()
	throws Exception
	{
		Thread.sleep( 1000 );
		System.out.println("COMPILE REMOTE" );

		String[] args = new String[1];
		args[0] = "argot/remote.argot";
		
		ArgotCompiler ac = new ArgotCompiler( new File( args[0] ), new File( "argot/remote.dictionary" ), null);
		ac.setLoadRemote(false);
		ac.doCompile();
		
		TypeLibrary library = new TypeLibrary( baseCommonLoaders );
		Dictionary.readDictionary( library, new FileInputStream( "argot/remote.dictionary" ));		
	}
	
	public void testCompileNetworkVMArgot()
	throws Exception
	{
		Thread.sleep( 1000 );
		System.out.println("COMPILE NETWORKVM" );
		
		String[] args = new String[1];
		args[0] = "argot/networkvm.argot";
		
		ArgotCompiler.argotCompile( args );
		
		TypeLibrary library = new TypeLibrary( libraryLoaders );
		Dictionary.readDictionary( library, new FileInputStream( "argot/networkvm.dictionary" ));		
	}
	


	public void testCompileRemoteRpcArgot()
	throws Exception
	{
		Thread.sleep( 1000 );
		System.out.println("COMPILE REMOTE RPC" );
		
		String[] args = new String[1];
		args[0] = "argot/remoterpc.argot";
		
		ArgotCompiler.argotCompile( args );
		
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
		
		ArgotCompiler.argotCompile( args );
		
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
		
		ArgotCompiler.argotCompile( args );

		Thread.sleep( 1000 );
		
		TypeLibrary library = new TypeLibrary( libraryLoaders );
		Dictionary.readDictionary( library, new FileInputStream( "argot/nettest.dictionary" ));		
	}
/*
	public void testCompileTestKBArgot()
	throws Exception
	{
		System.out.println("COMPILE KB" );
		
		String[] args = new String[1];
		args[0] = "argot/kb.argot";
		
		ArgotCompiler compiler = new ArgotCompiler();
		compiler.argotCompile( args );
		
		TypeLibrary library = new TypeLibrary( libraryLoaders );
		Dictionary.readDictionary( library, new FileInputStream( "argot/networkvm.dictionary" ));
		Dictionary.readDictionary( library, new FileInputStream( "argot/kb.dictionary" ));
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
		Dictionary.readDictionary( library, new FileInputStream( "argot/networkvm.dictionary" ));
		Dictionary.readDictionary( library, new FileInputStream( "argot/kb.dictionary" ));
		Dictionary.readDictionary( library, new FileInputStream( "argot/knowledgebase.dictionary" ));
	}	
*/
}
