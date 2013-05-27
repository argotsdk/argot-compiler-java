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
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.net.URL;
import java.net.URLClassLoader;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;
import java.util.Map;

import org.antlr.runtime.ANTLRInputStream;
import org.antlr.runtime.CommonTokenStream;
import org.antlr.runtime.RecognitionException;
import org.antlr.runtime.tree.CommonTree;
import org.antlr.runtime.tree.CommonTreeNodeStream;

import com.argot.TypeException;
import com.argot.TypeLibraryLoader;
import com.argot.TypeMap;
import com.argot.TypeLibrary;
import com.argot.TypeMapperCore;
import com.argot.TypeMapperDynamic;
import com.argot.TypeMapperError;
import com.argot.common.CommonLoader;
import com.argot.compiler.dictionary.LibraryLoader;
import com.argot.compiler.primitive.ArgotPrimitiveParser;
import com.argot.compiler.primitive.MetaNameParser;
import com.argot.compiler.primitive.MetaVersionParser;
import com.argot.compiler.primitive.StringPrimitiveParser;
import com.argot.compiler.primitive.UInt16PrimitiveParser;
import com.argot.compiler.primitive.UInt8PrimitiveParser;
import com.argot.dictionary.Dictionary;
import com.argot.dictionary.DictionaryLoader;
import com.argot.meta.MetaExtensionLoader;
import com.argot.meta.MetaLoader;
import com.argot.remote.RemoteLoader;


/**
 *  This compiles a file full of types into a typemap that can be
 *  written to a typemap file.  The programmer then reads in the
 *  typemap and binds objects to the definitions.
 */
public class ArgotCompiler 
{
	private TypeLibrary _library;
	private InputStream _inputFile;
	private File _outputFile;
	private URL[] _paths;
	private ClassLoader _classLoader;
	private boolean _loadExtensions;
	private boolean _loadCommon;
	private boolean _loadRemote;
	private boolean _compileDictionary;
	private Map _primitiveParsers;
	private TypeMap _map;
	private int _lastType;
	
	public ArgotCompiler( InputStream inputFile, File outputFile, URL[] paths ) 
	throws TypeException 
	{
		_inputFile = inputFile;
		_outputFile = outputFile;
		_paths = paths;
		_loadExtensions = true;
		_loadCommon = true;
		_loadRemote = true;
		_compileDictionary = true;
		_primitiveParsers = new HashMap();

		_library = new TypeLibrary(false);
		_library.loadLibrary( new MetaLoader() );
		_library.loadLibrary( new DictionaryLoader() );
		_library.loadLibrary( new LibraryLoader() );
		
		setPrimitiveParser( "meta.name", new StringPrimitiveParser() );
		setPrimitiveParser( "u8ascii", new StringPrimitiveParser() );
		setPrimitiveParser( "u8utf8", new StringPrimitiveParser() );
		setPrimitiveParser( "uint8", new UInt8PrimitiveParser() );
		setPrimitiveParser( "uint16", new UInt16PrimitiveParser() );
		setPrimitiveParser( "uvint28", new UInt16PrimitiveParser() );
		setPrimitiveParser( "meta.version", new MetaVersionParser() );
		setPrimitiveParser( "meta.name", new MetaNameParser(_library) );

		if (paths==null)
		{
			_classLoader = this.getClass().getClassLoader();
		} 
		else
		{
			_classLoader = new URLClassLoader(_paths, this.getClass().getClassLoader() );
		}
		printHeader();
		
	}
	
	public void setLoadExtensions(boolean load)
	{
		_loadExtensions = load;
	}
	
	public void setLoadCommon(boolean load)
	{
		_loadCommon = load;
	}
	
	public void setLoadRemote(boolean load)
	{
		_loadRemote = load;
	}
	
	public void setCompileDictionary(boolean dict)
	{
		_compileDictionary = dict;
	}
	
	public void setPrimitiveParser( String type, ArgotPrimitiveParser parser )
	{
		_primitiveParsers.put( type, parser );
	}
	
	public ArgotPrimitiveParser getPrimitiveParser( String type )
	{
		return (ArgotPrimitiveParser) _primitiveParsers.get( type );
	}

	private void printHeader()
	{
		System.out.println("\nArgot Compiler Version 1.3.b2");
		System.out.println("Copyright 2004-2013 (C) Live Media Pty Ltd.");
		System.out.println("www.argot-sdk.org\n");		
	}
	
	private Object parse( TypeMap readMap, TypeMap map, InputStream inputFile )
	throws TypeException, FileNotFoundException, IOException
	{
		//FileInputStream fin = new FileInputStream( inputFile );
		InputStream fin = inputFile;
		ANTLRInputStream input = new ANTLRInputStream(fin);
		ArgotLexer lexer = new ArgotLexer( input );
		
		CommonTokenStream tokens = new CommonTokenStream(lexer);
		
		ArgotParser parser = new ArgotParser(tokens);
		
		try
		{
			ArgotParser.file_return r = parser.file();
			CommonTree t = (CommonTree)r.getTree(); // get tree from parser
			CommonTreeNodeStream nodes = new CommonTreeNodeStream(t);

			ArgotTree tree = new ArgotTree(nodes); // create a tree parser
			tree.setLibrary( _library );
			tree.setTypeMap( map );
			tree.setReadTypeMap(readMap);
			//tree.setValidateReference( false );
			tree.setArgotCompiler( this );
			Object[] o = (Object[]) tree.file();
			
			List errors = tree.getErrors();
			if (errors.isEmpty()) return map;
			
			Iterator errorIterator = errors.iterator();
			while(errorIterator.hasNext())
			{
				String msg = (String) errorIterator.next();
				System.err.println(msg);
			}
			throw new TypeException("Compile failed");
		}
		catch (RecognitionException e)
		{
			throw new TypeException( "Specification not recognised" + e.toString() );
		}
	}
	

	public void loadDictionary( String fileName )
	throws TypeException, FileNotFoundException, IOException
	{
		System.out.println("Loading: " + fileName );
		InputStream inStream = null;

		inStream = _classLoader.getResourceAsStream( fileName );
		if (inStream == null)
			throw new FileNotFoundException("File not found as resource");

		try
		{
			Dictionary.readDictionary( _library, inStream );
		}
		catch( IOException ex )
		{
			throw new TypeException("Failed to load dictionary: " + fileName, ex );
		}
		catch( Throwable ex )
		{			
			throw new TypeException("Failed to load dictionary: " + fileName, ex );
		}
	}
	
	public void loadOptionalDictionary( TypeLibrary library, TypeLibraryLoader loader )
	{
		System.out.println("Loading file: " + loader.getName() );

		try
		{
			library.loadLibrary( loader );
		}
		catch (TypeException e)
		{
			System.out.println("WARNING: '" + loader.getName() + "' failed to load.");
		}		
	}

	
	public void doCompile() 
	throws TypeException, IOException
	{
		if (_loadExtensions)
		{
			loadOptionalDictionary( _library, new MetaExtensionLoader() );
		}
		
		if (_loadExtensions && _loadCommon)
		{
			loadOptionalDictionary( _library, new CommonLoader() );
		}
		
		if (_loadExtensions && _loadCommon && _loadRemote)
		{
			loadOptionalDictionary( _library, new RemoteLoader() );
		}
		
		String argotHomeString = System.getProperty("ARGOT_HOME");
		if ( argotHomeString == null )
		{
			argotHomeString = ".";
		}		
		

		//System.out.println("Compiling: " + _inputFile.getName() );
		
		TypeMap readMap = new TypeMap( _library, new TypeMapperDynamic(new TypeMapperCore(new TypeMapperError())));

		TypeMap map = new TypeMap( _library, new TypeMapperDynamic( new TypeMapperError() ) );
		Object o = parse( readMap, map, _inputFile );
		if (_compileDictionary)
		{
			FileOutputStream fout = new FileOutputStream( _outputFile );
			Dictionary.writeDictionary( fout, map );			
		}
		else
		{
			throw new TypeException("not implemented");
		}

	}
	
	public static void argotCompile( String[] args ) 
	throws FileNotFoundException, TypeException, IOException
	{	
		if ( args.length == 0 )
		{
			System.out.println("Usage: ac <input.argot>");
			System.exit(-1);
		}

		String inputFileName = args[0];
		String outputFileName = null;

		File inputFile = new File( inputFileName );
		if (!inputFile.exists())
		{
			System.out.println("Input File not found");
			System.out.println("Usage: ac <input.argot>");
			System.exit(-1);
		}

		int lastIndex = inputFileName.lastIndexOf(".");
		if ( lastIndex != -1 )
		{
			outputFileName = inputFileName.substring(0,lastIndex) + ".dictionary";
		}
		else
		{
			outputFileName = inputFileName + ".dictionary";
		}
		File outputFile = new File(outputFileName);
		
		FileInputStream fin = new FileInputStream( inputFile );
		ArgotCompiler compiler = new ArgotCompiler( fin, outputFile, null );
		compiler.doCompile();
	}

	public static void main(String[] args)
	{
		try 
		{
			argotCompile( args );
		} 
		catch (FileNotFoundException e) 
		{
			e.printStackTrace();
		} 
		catch (TypeException e) 
		{
			e.printStackTrace();
		}
		catch (IOException e )
		{
			e.printStackTrace();
		}
		catch (Exception e)
        {
            e.printStackTrace();
        }
	}
}
