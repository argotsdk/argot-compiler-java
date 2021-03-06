/*
 * Copyright (c) 2003-2019, Live Media Pty. Ltd.
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

import static org.junit.jupiter.api.Assertions.assertEquals;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.lang.reflect.InvocationTargetException;
import java.util.Iterator;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import com.argot.TypeLibrary;
import com.argot.TypeLibraryLoader;
import com.argot.TypeMap;
import com.argot.common.CommonLoader;
import com.argot.dictionary.Dictionary;
import com.argot.meta.MetaLoader;

public class CompilerTest {

    private TypeLibraryLoader libraryLoaders[] = { new MetaLoader(), new CommonLoader() };

    private TypeLibraryLoader baseCommonLoaders[] = { new MetaLoader(), new CommonLoader(), };

    private TypeLibraryLoader coreLibraryLoaders[] = { new MetaLoader(), };

    private TypeLibraryLoader remoteLoader = null;

    @BeforeEach
    protected void setUp() throws Exception {
        System.setProperty("ARGOT_HOME", ".");
    }

    private boolean includeRemote() {

        try {
            Class<?> clss = Class.forName("com.argot.remote.RemoteLoader");

            remoteLoader = (TypeLibraryLoader) clss.getConstructor().newInstance();
            return true;
        } catch (ClassNotFoundException | InstantiationException | IllegalAccessException | IllegalArgumentException | InvocationTargetException | NoSuchMethodException | SecurityException e) {

        }

        System.out.println("Compiling library requires Argot Remote library");
        return false;
    }

    @Test
    public void testCompileMetaArgot() throws Exception {
        System.out.println("COMPILE ARGOT META");
        String[] args = new String[1];
        args[0] = "argot/meta.argot";

        FileInputStream fin = new FileInputStream(args[0]);
        FileOutputStream fout = new FileOutputStream(new File("argot/meta.dictionary"));
        ArgotCompiler ac = new ArgotCompiler(fin, null);
        ac.compileDictionary(fout);

        TypeLibrary library = new TypeLibrary(coreLibraryLoaders);
        Dictionary.readDictionary(library, new FileInputStream("argot/meta.dictionary"));
    }

    @Test
    public void testCompileCommonArgot() throws Exception {
        System.out.println("COMPILE ARGOT COMMON");
        String[] args = new String[1];
        args[0] = "argot/common.argot";

        FileInputStream fin = new FileInputStream(args[0]);
        FileOutputStream fout = new FileOutputStream(new File("argot/common.dictionary"));
        ArgotCompiler ac = new ArgotCompiler(fin, null);
        ac.setLoadCommon(false);
        ac.compileDictionary(fout);

        TypeLibrary library = new TypeLibrary(coreLibraryLoaders);
        TypeMap map = Dictionary.readDictionary(library, new FileInputStream("argot/common.dictionary"));
        Iterator<Integer> ids = map.getIdList().iterator();
        while (ids.hasNext()) {
            Integer id = ids.next();
            System.out.println(map.getName(id.intValue()).getFullName());
        }
        assertEquals(22, map.size());
    }

    @Test
    public void testCompilePlatformArgot() throws Exception {
        System.out.println("COMPILE ARGOT COMMON");
        String[] args = new String[1];
        args[0] = "argot/definition.argot";

        FileInputStream fin = new FileInputStream(args[0]);
        FileOutputStream fout = new FileOutputStream(new File("argot/definition.dictionary"));
        ArgotCompiler ac = new ArgotCompiler(fin, null);
        ac.setLoadCommon(true);
        ac.compileDictionary(fout);

        TypeLibrary library = new TypeLibrary(coreLibraryLoaders);
        TypeMap map = Dictionary.readDictionary(library, new FileInputStream("argot/definition.dictionary"));
        Iterator<Integer> ids = map.getIdList().iterator();
        while (ids.hasNext()) {
            Integer id = ids.next();
            System.out.println(map.getName(id.intValue()).getFullName());
        }
        // assertEquals(22, map.size());
    }

    @Test
    public void testCompileChannelArgot() throws Exception {
        System.out.println("COMPILE CHANNEL");
        String[] args = new String[1];
        args[0] = "argot/channel.argot";

        ArgotCompiler.argotCompile(args);

        TypeLibrary library = new TypeLibrary(libraryLoaders);
        Dictionary.readDictionary(library, new FileInputStream("argot/channel.dictionary"));
    }

    @Test
    public void testCompileRemoteArgot() throws Exception {
        Thread.sleep(1000);
        System.out.println("COMPILE REMOTE");

        String[] args = new String[1];
        args[0] = "argot/remote.argot";

        FileInputStream fin = new FileInputStream(args[0]);
        FileOutputStream fout = new FileOutputStream(new File("argot/remote.dictionary"));
        ArgotCompiler ac = new ArgotCompiler(fin, null);
        ac.compileDictionary(fout);

        TypeLibrary library = new TypeLibrary(baseCommonLoaders);
        Dictionary.readDictionary(library, new FileInputStream("argot/remote.dictionary"));
    }

    @Test
    public void testCompileNetworkVMArgot() throws Exception {
        if (!includeRemote())
            return;

        Thread.sleep(1000);
        System.out.println("COMPILE NETWORKVM");

        String[] args = new String[1];
        args[0] = "argot/networkvm.argot";

        ArgotCompiler.argotCompile(args);

        TypeLibrary library = new TypeLibrary(libraryLoaders);
        library.loadLibrary(remoteLoader);
        Dictionary.readDictionary(library, new FileInputStream("argot/networkvm.dictionary"));
    }

    @Test
    public void testCompileRemoteRpcArgot() throws Exception {
        if (!includeRemote())
            return;

        Thread.sleep(1000);
        System.out.println("COMPILE REMOTE RPC");

        String[] args = new String[1];
        args[0] = "argot/remoterpc.argot";

        ArgotCompiler.argotCompile(args);

        TypeLibrary library = new TypeLibrary(libraryLoaders);
        library.loadLibrary(remoteLoader);
        Dictionary.readDictionary(library, new FileInputStream("argot/remoterpc.dictionary"));
    }

    @Test
    public void testCompileNetArgot() throws Exception {
        if (!includeRemote())
            return;
        Thread.sleep(1000);
        System.out.println("COMPILE NETARGOT");

        String[] args = new String[1];
        args[0] = "argot/netargot.argot";

        ArgotCompiler.argotCompile(args);

        TypeLibrary library = new TypeLibrary(libraryLoaders);
        Dictionary.readDictionary(library, new FileInputStream("argot/netargot.dictionary"));
    }

    @Test
    public void testCompileTestInterfaceArgot() throws Exception {
        if (!includeRemote())
            return;
        Thread.sleep(1000);
        System.out.println("COMPILE NETTEST");

        String[] args = new String[1];
        args[0] = "argot/nettest.argot";

        ArgotCompiler.argotCompile(args);

        Thread.sleep(1000);

        TypeLibrary library = new TypeLibrary(libraryLoaders);
        library.loadLibrary(remoteLoader);
        Dictionary.readDictionary(library, new FileInputStream("argot/nettest.dictionary"));
    }

    @Test
    public void testSimpleArgot() throws Exception {
        if (!includeRemote())
            return;
        Thread.sleep(1000);
        System.out.println("COMPILE NETTEST");

        String[] args = new String[1];
        args[0] = "argot/simple.argot";

        ArgotCompiler.argotCompile(args);

        Thread.sleep(1000);

        TypeLibrary library = new TypeLibrary(libraryLoaders);
        library.loadLibrary(remoteLoader);
        Dictionary.readDictionary(library, new FileInputStream("argot/simple.dictionary"));
    }

    @Test
    public void testZoneArgot() throws Exception {
        if (!includeRemote())
            return;
        Thread.sleep(1000);
        System.out.println("COMPILE ZONE");

        String[] args = new String[1];
        args[0] = "argot/zone.argot";

        ArgotCompiler.argotCompile(args);

        Thread.sleep(1000);

        TypeLibrary library = new TypeLibrary(libraryLoaders);
        library.loadLibrary(remoteLoader);
        Dictionary.readDictionary(library, new FileInputStream("argot/zone.dictionary"));
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
