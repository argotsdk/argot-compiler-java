/*
 * Copyright (c) 2003-2010, Live Media Pty. Ltd.
 * All rights reserved.
 *
 * Redistribution and use in source and binary forms, with or without modification, are permitted
 * provided that the following conditions are met:
 *
 *  1. Redistributions of source code must retain the above copyright notice, this list of
 *     conditions and the following disclaimer.
 *  2. Redistributions in binary form must reproduce the above copyright notice, this list of
 *     conditions and the following disclaimer in the documentation and/or other materials
 *     provided with the distribution.
 *  3. Neither the name of Live Media nor the names of its contributors may be used to endorse
 *     or promote products derived from this software without specific prior written permission.
 *
 * THIS SOFTWARE IS PROVIDED BY THE COPYRIGHT HOLDERS AND CONTRIBUTORS "AS IS" AND ANY EXPRESS OR
 * IMPLIED WARRANTIES, INCLUDING, BUT NOT LIMITED TO, THE IMPLIED WARRANTIES OF MERCHANTABILITY
 * AND FITNESS FOR A PARTICULAR PURPOSE ARE DISCLAIMED. IN NO EVENT SHALL THE COPYRIGHT HOLDER
 * OR CONTRIBUTORS BE LIABLE FOR ANY DIRECT, INDIRECT, INCIDENTAL, SPECIAL, EXEMPLARY, OR
 * CONSEQUENTIAL DAMAGES (INCLUDING, BUT NOT LIMITED TO, PROCUREMENT OF SUBSTITUTE GOODS OR
 * SERVICES; LOSS OF USE, DATA, OR PROFITS; OR BUSINESS INTERRUPTION) HOWEVER CAUSED AND ON
 * ANY THEORY OF LIABILITY, WHETHER IN CONTRACT, STRICT LIABILITY, OR TORT (INCLUDING
 * NEGLIGENCE OR OTHERWISE) ARISING IN ANY WAY OUT OF THE USE OF THIS SOFTWARE, EVEN IF
 * ADVISED OF THE POSSIBILITY OF SUCH DAMAGE.
 */

package com.argot.compiler;

import java.io.File;
import java.io.FileInputStream;
import java.util.Iterator;
import java.util.List;


import com.argot.TypeLibrary;
import com.argot.TypeLibraryLoader;
import com.argot.TypeMap;
import com.argot.common.CommonLoader;
import com.argot.dictionary.Dictionary;
import com.argot.dictionary.DictionaryLoader;
import com.argot.meta.MetaExtensionLoader;
import com.argot.meta.MetaLoader;
import com.argot.remote.RemoteLoader;

import junit.framework.TestCase;

public class CompilerTest 
extends TestCase
{

	private TypeLibraryLoader libraryLoaders[] = {
		new MetaLoader(),
		new DictionaryLoader(),
		new MetaExtensionLoader(),			
		new CommonLoader(),
		new RemoteLoader()
	};

	private TypeLibraryLoader baseCommonLoaders[] = {
			new MetaLoader(),
			new DictionaryLoader(),
			new MetaExtensionLoader(),			
			new CommonLoader(),
		};
	
	private TypeLibraryLoader baselibraryLoaders[] = {
		new MetaLoader(),
		new DictionaryLoader(),
		new MetaExtensionLoader()
	};
	
	private TypeLibraryLoader coreLibraryLoaders[] = {
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
		ac.setLoadExtensions(false);
		ac.doCompile();
		
		TypeLibrary library = new TypeLibrary( coreLibraryLoaders );
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
		ac.setLoadExtensions(false);
		ac.doCompile();
		
		TypeLibrary library = new TypeLibrary( coreLibraryLoaders );
		TypeMap map = Dictionary.readDictionary( library, new FileInputStream( "argot/meta_extensions.dictionary" ));
		Iterator ids = map.getIdList().iterator();
		while(ids.hasNext())
		{
			Integer id = (Integer) ids.next();
			System.out.println(map.getName(id.intValue()).getFullName());
		}
		assertEquals( 13, map.size() );
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
		assertEquals( 36, map.size() );
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
		Iterator ids = map.getIdList().iterator();
		while(ids.hasNext())
		{
			Integer id = (Integer) ids.next();
			System.out.println(map.getName(id.intValue()).getFullName());
		}
		assertEquals( 33, map.size() );
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
	
	public void testSimpleArgot()
	throws Exception
	{
		Thread.sleep( 1000 );
		System.out.println("COMPILE NETTEST" );
		
		String[] args = new String[1];
		args[0] = "argot/simple.argot";
		
		ArgotCompiler.argotCompile( args );

		Thread.sleep( 1000 );
		
		TypeLibrary library = new TypeLibrary( libraryLoaders );
		Dictionary.readDictionary( library, new FileInputStream( "argot/simple.dictionary" ));		
	}	
	
	public void testZoneArgot()
	throws Exception
	{
		Thread.sleep( 1000 );
		System.out.println("COMPILE ZONE" );
		
		String[] args = new String[1];
		args[0] = "argot/zone.argot";
		
		ArgotCompiler.argotCompile( args );

		Thread.sleep( 1000 );
		
		TypeLibrary library = new TypeLibrary( libraryLoaders );
		Dictionary.readDictionary( library, new FileInputStream( "argot/zone.dictionary" ));		
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
