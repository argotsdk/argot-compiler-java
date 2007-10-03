/*
 * Copyright 2003-2007 (c) Live Media Pty Ltd. <argot@einet.com.au> 
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
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.net.URL;
import java.net.URLClassLoader;

import antlr.RecognitionException;
import antlr.TokenStreamException;

import com.argot.TypeException;
import com.argot.TypeLibraryLoader;
import com.argot.TypeMap;
import com.argot.TypeLibrary;
import com.argot.common.CommonLoader;
import com.argot.dictionary.Dictionary;
import com.argot.dictionary.DictionaryLoader;
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
	private File _inputFile;
	private File _outputFile;
	private URL[] _paths;
	private ClassLoader _classLoader;
	private boolean _loadCommon;
	private boolean _loadRemote;
	
	public ArgotCompiler( File inputFile, File outputFile, URL[] paths ) 
	throws TypeException 
	{
		_inputFile = inputFile;
		_outputFile = outputFile;
		_paths = paths;
		_loadCommon = true;
		_loadRemote = true;

		if (paths==null)
		{
			_classLoader = this.getClass().getClassLoader();
		} 
		else
		{
			_classLoader = new URLClassLoader(_paths, this.getClass().getClassLoader() );
		}
		printHeader();
		
		_library = new TypeLibrary();
		_library.loadLibrary( new MetaLoader() );
		_library.loadLibrary( new DictionaryLoader() );		
	}
	
	public void setLoadCommon(boolean load)
	{
		_loadCommon = load;
	}
	
	public void setLoadRemote(boolean load)
	{
		_loadRemote = load;
	}

	private void printHeader()
	{
		System.out.println("\nArgot Compiler Version 1.2.1");
		System.out.println("Copyright 2004-2007 (C) Live Media Pty Ltd.");
		System.out.println("www.einet.com.au\n");		
	}
	
	private void parse( TypeMap map, File inputFile )
	throws TypeException, FileNotFoundException
	{
		FileInputStream fin = new FileInputStream( inputFile );
		ArgotLexer lexer = new ArgotLexer( fin );
		ArgotParser parser = new ArgotParser(lexer);
		
		parser.setLibrary( _library );
		parser.setTypeMap( map );
		parser.setValidateReference( false );
		parser.setArgotCompiler( this );
		try
		{
			parser.file();
		}
		catch (RecognitionException e)
		{
			e.printStackTrace();
			throw new TypeException( "Specification not recognised" + e.toString() );
		}
		catch (TokenStreamException e)
		{
			throw new TypeException( "Invalid specification" + e.toString() );
		}
	}
	
	public void loadDictionary( String fileName )
	throws TypeException, FileNotFoundException, IOException
	{
		System.out.println("Loading: " + fileName );
		File loadFile = new File( _inputFile.getParent(), fileName );
		InputStream inStream = null;
		if(!loadFile.exists())
		{
			inStream = _classLoader.getResourceAsStream( fileName );
			if (inStream == null)
				throw new FileNotFoundException("File not found as resource");
		}
		else
		{
			inStream = new FileInputStream(loadFile);
		}
        Dictionary.readDictionary( _library, inStream );	
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
		if (_loadCommon)
		{
			loadOptionalDictionary( _library, new CommonLoader() );
		}
		
		if (_loadCommon && _loadRemote)
		{
			loadOptionalDictionary( _library, new RemoteLoader() );
		}
		
		String argotHomeString = System.getProperty("ARGOT_HOME");
		if ( argotHomeString == null )
		{
			argotHomeString = ".";
		}		
		

		System.out.println("Compling: " + _inputFile.getName() );
		
		TypeMap map = new TypeMap( _library );
		parse( map, _inputFile );					

		FileOutputStream fout = new FileOutputStream( _outputFile );
		Dictionary.writeDictionary( fout, map );
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
		
		ArgotCompiler compiler = new ArgotCompiler( inputFile, outputFile, null );
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
