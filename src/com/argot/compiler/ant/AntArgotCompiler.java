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

package com.argot.compiler.ant;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.net.URL;

import org.apache.tools.ant.BuildException;
import org.apache.tools.ant.Task;
import org.apache.tools.ant.types.Path;

import com.argot.TypeException;
import com.argot.compiler.ArgotCompiler;

public class AntArgotCompiler
extends Task
{
	private File _input;
	private File _output;
	private Path _path;
	private boolean _loadCommon = true;
	
	public void setInput( File file )
	{
		_input = file;
	}
	
	public void setOutput( File output )
	{
		_output = output;
	}
	
	public void setPath( Path path )
	{
		_path = path;
	}
	
	public void setLoadCommon( boolean load)
	{
		_loadCommon = load;
	}
	
	public void execute() 
	throws BuildException
	{
		try
		{
			if ( _input == null || !_input.exists() )
			{
				throw new BuildException("input file not supplied or doesn't exist");
			}
			
			if ( _output == null )
			{
				int lastIndex = _input.getName().lastIndexOf(".");
				if ( lastIndex != -1 )
				{
					_output = new File( _input.getParent(), _input.getName().substring(0,lastIndex) + ".dictionary" );
				}
				else
				{
					_output = new File( _input.getParent(), _input.getName() + ".dictionary" );
				}
			}

			if ( _output.lastModified() > _input.lastModified() )
			{
				// We don't need to recompile. exit task.
				return;
			}
			
			URL[] paths = null; 
			if ( _path != null )
			{
				String[] pathStrings = _path.list();
				paths = new URL[ pathStrings.length ];
				for (int x=0;x<pathStrings.length;x++)
				{
					String pathx = pathStrings[x];
					File file = new File(pathx);
					paths[x] = file.toURI().toURL();
					System.out.println("path:" + paths[x].toExternalForm() );
				}
			}
			
			FileInputStream fin = new FileInputStream( _input );
			ArgotCompiler compiler = new ArgotCompiler( fin, _output, paths );
			compiler.setLoadCommon(_loadCommon);
			compiler.doCompile();
		}
		catch (FileNotFoundException e)
		{
			throw new BuildException(e);
		}
		catch (TypeException e)
		{
			throw new BuildException(e);
		}
		catch (IOException e)
		{
			throw new BuildException(e);
		}
		catch( Throwable e )
		{
			throw new BuildException(e);
		}
	}
}
