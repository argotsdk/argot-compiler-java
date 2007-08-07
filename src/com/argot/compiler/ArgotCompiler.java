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

import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.net.URL;

import antlr.RecognitionException;
import antlr.TokenStreamException;

import com.argot.TypeBindCommon;
import com.argot.TypeException;
import com.argot.TypeMap;
import com.argot.TypeMapCore;

import com.argot.TypeLibrary;
import com.argot.dictionary.Dictionary;
import com.argot.dictionary.DictionaryMap;
import com.argot.remote.RemoteTypes;


/**
 *  This compiles a file full of types into a typemap that can be
 *  written to a typemap file.  The programmer then reads in the
 *  typemap and binds objects to the definitions.
 */
public class ArgotCompiler 
{

	public ArgotCompiler() 
	{
	}

	private void parse( TypeLibrary library, TypeMap map, InputStream in )
	throws TypeException
	{
		ArgotLexer lexer = new ArgotLexer( in );
		ArgotParser parser = new ArgotParser(lexer);
		
		parser.setLibrary( library );
		parser.setTypeMap( map );
		parser.setValidateReference( false );
		
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
	
	public InputStream getDictionaryStream( String argotHomeString, String location )
	{
		File dictionaryFile = new File( argotHomeString, location );
		if ( dictionaryFile.exists())
		{
			System.out.println("Loading file: " + dictionaryFile.getPath() );
			try
			{
				return new FileInputStream( dictionaryFile );
			}
			catch (FileNotFoundException e)
			{
				// ignore and drop through.
			}
		}

		ClassLoader cl = this.getClass().getClassLoader();
		URL fileUrl = cl.getResource( location );
		
		InputStream is = cl.getResourceAsStream( location );
		if ( is == null )
		{
			System.out.println("WARNING: Argot '" + location + "' file not found");
			return null;
		}
		System.out.println("Loading resource: " + fileUrl.toExternalForm() );		
		return is;
	}

	private void printHeader()
	{
		System.out.println("\nArgot Compiler Version 1.2.0");
		System.out.println("Copyright 2004-2007 (C) Live Media Pty Ltd.");
		System.out.println("www.einet.com.au\n");		
	}
	
	public void doCompile( File inputFile, File outputFile ) 
	throws TypeException, IOException
	{
		printHeader();

		String argotHomeString = System.getProperty("ARGOT_HOME");
		if ( argotHomeString == null )
		{
			argotHomeString = ".";
		}		
		
		ArgotCompiler compiler = new ArgotCompiler();
		TypeLibrary library = new TypeLibrary();
		TypeMapCore.loadLibrary( library );
		DictionaryMap.loadDictionaryMap( library );

		InputStream dictStream = getDictionaryStream( argotHomeString, "argot/common.dictionary");
		if (dictStream != null)
		{
			Dictionary.readDictionary( library, dictStream );
			TypeBindCommon.bindCommon(library);
		}
		
		dictStream = getDictionaryStream( argotHomeString, "argot/remote.dictionary");
		if (dictStream != null)
		{
			Dictionary.readDictionary( library, dictStream );
			RemoteTypes.bindTypes(library);
		}
		
		FileInputStream fin = new FileInputStream( inputFile );
		FileOutputStream fout = new FileOutputStream( outputFile );

		System.out.println("Compling: " + inputFile.getName() );
		
		TypeMap map = new TypeMap( library );			
		compiler.parse( library, map, fin );					
		Dictionary.writeDictionary( fout, map );
	}
	
	public void argotCompile( String[] args ) 
	throws FileNotFoundException, TypeException, IOException
	{
		printHeader();
		
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
		
		doCompile( inputFile, outputFile );
	}

	public static void main(String[] args)
	{
		try 
		{
			ArgotCompiler compiler = new ArgotCompiler();
			compiler.argotCompile( args );
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
