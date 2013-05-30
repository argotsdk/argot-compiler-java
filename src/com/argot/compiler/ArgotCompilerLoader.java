package com.argot.compiler;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.io.InputStream;
import java.net.URL;

import com.argot.TypeException;
import com.argot.TypeLibrary;
import com.argot.TypeLibraryLoader;
import com.argot.dictionary.Dictionary;

public class ArgotCompilerLoader 
implements TypeLibraryLoader
{
	private String _resource;
	private String _output;
	
	public ArgotCompilerLoader(String resource )
	{
		_resource = resource;
		int index = _resource.lastIndexOf(".");
		if (index>0)
		{
			_output = _resource.substring(0, index) + ".dictionary";
		}
		else
		{
			_output = _resource + ".dictionary";
		}
	}

	private InputStream getDictionaryStream( String location )
	{
		File dictionaryFile = new File( location );
		if ( dictionaryFile.exists())
		{
			try
			{
				return new FileInputStream( dictionaryFile );
			}
			catch (FileNotFoundException e)
			{
				// ignore and drop through.
			}
		}

		InputStream is = getClass().getResourceAsStream( location );
		if (is != null)
		{
			return is;
		}
		
		ClassLoader cl = this.getClass().getClassLoader();
		is = cl.getResourceAsStream( location );
		if ( is == null )
		{
			return null;
		}				
		return is;
	}
	
	public void load(TypeLibrary library) 
	throws TypeException 
	{
		URL[] paths = null;
		
		InputStream is = getDictionaryStream( _resource );
		if ( is == null )
		{
			throw new TypeException("Failed to load:" + _resource );
		}
		
		try 
		{
			ArgotCompiler compiler = new ArgotCompiler( is, new File( _output ), paths );
			compiler.setLoadCommon(true);
			compiler.setLoadRemote(true);
			
			compiler.doCompile();
		} 
		catch (IOException e) 
		{
			e.printStackTrace();
			throw new TypeException("Failed to load argot source file: " + _resource, e);
		}
		
		is = getDictionaryStream( _output );
		if ( is == null )
		{
			throw new TypeException("Failed to load:" + _resource );
		}
		
		try
		{
			Dictionary.readDictionary( library, is );
		}
		catch (TypeException e)
		{
			throw new TypeException("Error loading dictionary: " + _resource, e );
		}
		catch (IOException e)
		{
			throw new TypeException("Error loading dictionary: " + _resource, e );
		}
		
		bind(library);
	}
	
	public void bind( TypeLibrary library ) 
	throws TypeException
	{
				
	}

	public String getName() 
	{
		return _resource;
	}

}
