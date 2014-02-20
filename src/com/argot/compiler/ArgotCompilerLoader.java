package com.argot.compiler;

import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
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
    private final InputStream _input;
	private final String _resource;
	private byte[] _dictionaryData;

	public ArgotCompilerLoader(final String resource )
	{
		_resource = resource;
		_input = null;
		_dictionaryData = null;
	}

	public ArgotCompilerLoader(final String resource, final InputStream input )
	{
	    _resource = resource;
	    _input = input;
	    _dictionaryData = null;
	}

	private InputStream getDictionaryStream( final String location )
	{
	    if (_input != null)
	    {
	        return _input;
	    }

		final File dictionaryFile = new File( location );
		if ( dictionaryFile.exists())
		{
			try
			{
				return new FileInputStream( dictionaryFile );
			}
			catch (final FileNotFoundException e)
			{
				// ignore and drop through.
			}
		}

		InputStream is = getClass().getResourceAsStream( location );
		if (is != null)
		{
			return is;
		}

		final ClassLoader cl = this.getClass().getClassLoader();
		is = cl.getResourceAsStream( location );
		if ( is == null )
		{
			return null;
		}
		return is;
	}

	@Override
    public void load(final TypeLibrary library)
	throws TypeException
	{
		final URL[] paths = null;

		final InputStream is = getDictionaryStream( _resource );
		if ( is == null )
		{
			throw new TypeException("Failed to load:" + _resource );
		}

		try
		{
			final ArgotCompiler compiler = new ArgotCompiler( is, paths );
			compiler.setLoadCommon(true);

			final ByteArrayOutputStream out = new ByteArrayOutputStream();

			compiler.compileDictionary( out );

			_dictionaryData = out.toByteArray();
		}
		catch (final IOException e)
		{
			e.printStackTrace();
			throw new TypeException("Failed to load argot source file: " + _resource, e);
		}
		catch (final ArgotCompilerException e)
		{
			e.printErrors(System.err);
			throw new TypeException("Failed to load argot source file: " + _resource, e);
		}

		try
		{
			Dictionary.readDictionary( library, new ByteArrayInputStream(_dictionaryData) );
		}
		catch (final TypeException e)
		{
			throw new TypeException("Error loading dictionary: " + _resource, e );
		}
		catch (final IOException e)
		{
			throw new TypeException("Error loading dictionary: " + _resource, e );
		}

		bind(library);
	}

	public byte[] getDictionaryData()
	throws TypeException
	{
		if (_dictionaryData == null)
		{
			throw new TypeException("ArgotCompilerLoader - Source not compiled");
		}
		return _dictionaryData;
	}

	public void bind( final TypeLibrary library )
	throws TypeException
	{

	}

	@Override
    public String getName()
	{
		return _resource;
	}

}
