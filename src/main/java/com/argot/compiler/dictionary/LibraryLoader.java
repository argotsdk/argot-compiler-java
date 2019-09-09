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
package com.argot.compiler.dictionary;

import com.argot.TypeException;
import com.argot.TypeLibrary;
import com.argot.TypeLibraryLoader;
import com.argot.auto.TypeReaderAuto;
import com.argot.common.UVInt28;
import com.argot.meta.DictionaryDefinition;
import com.argot.meta.DictionaryName;
import com.argot.meta.MetaArray;
import com.argot.meta.MetaCluster;
import com.argot.meta.MetaDefinition;
import com.argot.meta.MetaExpression;
import com.argot.meta.MetaIdentity;
import com.argot.meta.MetaMarshaller;
import com.argot.meta.MetaName;
import com.argot.meta.MetaReference;
import com.argot.meta.MetaSequence;
import com.argot.meta.MetaTag;
import com.argot.meta.MetaVersion;

public class LibraryLoader implements TypeLibraryLoader {
    public static final String DICTIONARY = "library_source.dictionary";

    @Override
    public String getName() {
        return DICTIONARY;
    }

    @Override
    public void load(TypeLibrary library) throws TypeException {

        library.register(new DictionaryName(library, "library"), new MetaCluster());

        /*
         * (library.entry
         * 	(library.definition u8utf8:"library.base" u8utf8:"1.3")
         *  (meta.sequence []))
         * 
         */
        int lbId = library.register(new DictionaryName(MetaName.parseName(library, "library.base")), new MetaIdentity());
        MetaDefinition libBaseDef = new MetaSequence(new MetaExpression[] {});
        library.register(new DictionaryDefinition(lbId, MetaName.parseName(library, "library.definition"), MetaVersion.parseVersion("1.3")), libBaseDef, new TypeReaderAuto(LibraryBase.class),
                        new MetaMarshaller(), LibraryBase.class);

        /*
         * (library.entry
         * 	  (library.definition u8ascii:"library.name" u8ascii:"1.3")
         *    (meta.sequence [
         *    		(meta.tag u8ascii:"name" (meta.reference #meta.name))
         *    ]))
         */

        int lnId = library.register(new DictionaryName(MetaName.parseName(library, "library.name")), new MetaIdentity());
        MetaDefinition libNameDef = new MetaSequence(new MetaExpression[] { new MetaTag("name", new MetaReference(library.getTypeId("meta.name"))), });
        library.register(new DictionaryDefinition(lnId, MetaName.parseName(library, "library.name"), MetaVersion.parseVersion("1.3")), libNameDef, new TypeReaderAuto(LibraryName.class),
                        new MetaMarshaller(), LibraryName.class);

        /*
         * (library.entry
         * 	  (library.definition u8ascii:"library.definition" u8ascii:"1.3")
         *    (meta.sequence [
         *    		(meta.tag u8ascii:"name" (meta.reference #meta.name))
         *    		(meta.tag u8ascii:"version" (meta.reference #meta.version))
         *    ]))
         */

        int ldId = library.register(new DictionaryName(MetaName.parseName(library, "library.definition")), new MetaIdentity());
        MetaDefinition dlibDef = new MetaSequence(
                        new MetaExpression[] { new MetaTag("name", new MetaReference(library.getTypeId("meta.name"))), new MetaTag("version", new MetaReference(library.getTypeId("meta.version"))) });
        library.register(new DictionaryDefinition(ldId, MetaName.parseName(library, "library.definition"), MetaVersion.parseVersion("1.3")), dlibDef, new TypeReaderAuto(LibraryDefinition.class),
                        new MetaMarshaller(), LibraryDefinition.class);

        int lrId = library.register(new DictionaryName(MetaName.parseName(library, "library.relation")), new MetaIdentity());
        MetaDefinition dlibRel = new MetaSequence(new MetaExpression[] { new MetaTag("id", new MetaReference(library.getTypeId("meta.id"))),
                        new MetaTag("version", new MetaReference(library.getTypeId("meta.version"))), new MetaTag("tag", new MetaReference(library.getTypeId("u8utf8"))) });
        library.register(new DictionaryDefinition(lrId, MetaName.parseName(library, "library.relation"), MetaVersion.parseVersion("1.3")), dlibRel, new TypeReaderAuto(LibraryRelation.class),
                        new MetaMarshaller(), LibraryRelation.class);

        /*
         * (library.entry
         * 	  (library.definition u8ascii:"library.entry" u8ascii:"1.3")
         *    (meta.sequence [
         *    		(meta.tag u8ascii:"location" (meta.reference #library.location))
         *    		(meta.tag u8ascii:"definition" (meta.reference #meta.definition))
         *    ]))
         */
        int msId = library.register(new DictionaryName(MetaName.parseName(library, "library.entry")), new MetaIdentity());
        MetaDefinition dSourceStructure = new MetaSequence(new MetaExpression[] { new MetaTag("location", new MetaReference(library.getTypeId("dictionary.location"))),
                        new MetaTag("definition", new MetaReference(library.getTypeId("meta.definition"))) });
        library.register(new DictionaryDefinition(msId, MetaName.parseName(library, "library.entry"), MetaVersion.parseVersion("1.3")), dSourceStructure, new TypeReaderAuto(LibraryEntry.class),
                        new MetaMarshaller(), LibraryEntry.class);

        /*
         * (library.entry
         * 		(library.definition u8ascii:"library.list")
         * 		(meta.sequence [
         * 			(meta.array
         * 				(meta.reference #uint16)
         * 				(meta.reference #library.list.item))))
         */
        int dsId = library.register(new DictionaryName(MetaName.parseName(library, "library.list")), new MetaIdentity());
        MetaDefinition dSource = new MetaSequence(
                        new MetaExpression[] { new MetaArray(new MetaReference(library.getTypeId(UVInt28.TYPENAME)), new MetaReference(library.getTypeId("library.entry"))) });
        library.register(new DictionaryDefinition(dsId, MetaName.parseName(library, "library.list"), MetaVersion.parseVersion("1.3")), dSource, new TypeReaderAuto(LibraryList.class),
                        new MetaMarshaller(), LibraryList.class);

    }

}
