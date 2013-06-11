
Copyright (c) 2003-2013, Live Media Pty. Ltd.
All rights reserved.

Redistribution and use in source and binary forms, with or without modification, are permitted
provided that the following conditions are met:

 1. Redistributions of source code must retain the above copyright notice, this list of
    conditions and the following disclaimer.
 2. Redistributions in binary form must reproduce the above copyright notice, this list of
    conditions and the following disclaimer in the documentation and/or other materials
    provided with the distribution.
 3. Neither the name of Live Media nor the names of its contributors may be used to endorse
    or promote products derived from this software without specific prior written permission.

THIS SOFTWARE IS PROVIDED BY THE COPYRIGHT HOLDERS AND CONTRIBUTORS "AS IS" AND ANY EXPRESS OR
IMPLIED WARRANTIES, INCLUDING, BUT NOT LIMITED TO, THE IMPLIED WARRANTIES OF MERCHANTABILITY
AND FITNESS FOR A PARTICULAR PURPOSE ARE DISCLAIMED. IN NO EVENT SHALL THE COPYRIGHT HOLDER
OR CONTRIBUTORS BE LIABLE FOR ANY DIRECT, INDIRECT, INCIDENTAL, SPECIAL, EXEMPLARY, OR
CONSEQUENTIAL DAMAGES (INCLUDING, BUT NOT LIMITED TO, PROCUREMENT OF SUBSTITUTE GOODS OR
SERVICES; LOSS OF USE, DATA, OR PROFITS; OR BUSINESS INTERRUPTION) HOWEVER CAUSED AND ON
ANY THEORY OF LIABILITY, WHETHER IN CONTRACT, STRICT LIABILITY, OR TORT (INCLUDING
NEGLIGENCE OR OTHERWISE) ARISING IN ANY WAY OUT OF THE USE OF THIS SOFTWARE, EVEN IF
ADVISED OF THE POSSIBILITY OF SUCH DAMAGE.

---------------------------------------------------------------------

Argot Compiler
--------------

The Argot Compiler provides a method to create Argot Dictionary files
from a text based description.  This is intended to be a temporary
solution for creating dictionary files until an editor is available
for directly editing dictionary files themselves.

Argot provides binary meta data able to describe, read and write any 
well structured binary data.  Argot's ability to work directly with 
binary data makes it an ideal solution where bandwidth and computing 
power need to be conserved. With a unique dictionary and data mapping 
system, it can be used to build flexible heterogenous distributed 
systems. It is a perfect alternative to XML where speed, data size, 
and data integrity are required.

Please read the Argot Programmer's Guide available in the doc
directory of the distribution.


Argot Compiler
--------------

The Argot Compiler is required to create Argot dictionary files.  
The ac.bat file in the bin directory requires the ARGOT_HOME environment 
variable is defined and set to the base directory of the Argot 
installation.

The Argot Compiler is currently only available in Java. 

Please view the example .argot files to see how argot is specified.


Version History
---------------

1.3.b4 - 9 June 2013

- Modified simple definition to allow any expression.  Specifically change
  to allow MetaAbstract to be defined.

1.3.b3 - 1 June 2013

- Improved error reporting.
- Updated to match Argot 1.3.b1
- Added ability to optionally load meta_extensions.dictionary
- Added ArgotCompilerLoader to allow compiling argot files in programs.
- Added cluster and definition syntax to compiler to make it easier to define definitions.
- Update website in title.
- Collapse meta_extension and dictionary dictionaries into meta dictionary.
- Attempt to load argot files using filename is resource load fails.
- Remote ability to automatically load remote dictionary. 
- Added ability to load and perform library bind.

1.2.2 - 17 January 2008

- Updated to match Argot version 1.2.2.
- Fixed bug in Argot TypeMap to ensure dictionaries are written
  in the order they are defined.  This ensures meta.map does
  not come before meta.abstract types.

1.2.1a - 14th October 2007

- Updated to match Argot version 1.2.1a.
- Catch throwable in Ant task.

1.2.1 - 4 October 2007

- Added Argot compiler ANT task.
- Change compiler to use Argot TypeLibraryLoader method for
  loading dictionary files.
- Allow dictionary files to be loaded from directories or
  jar files.
- Allow ANT task to not load common and remote core dictionaries.
  The meta.dictionary and dictionary.dictionary are always loaded.

1.2.0 - 20 December 2005

- Initial shared source release in C, Java and C#.

