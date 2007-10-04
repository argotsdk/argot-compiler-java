
Copyright 2003-2005 (c) Live Media Pty Ltd. <argot@einet.com.au> 

This software is licensed under the Argot Public License 
which may be found in the file LICENSE distributed 
with this software.

More information about this license can be found at
http://www.einet.com.au/License

The Developer of this software is Live Media Pty Ltd,
PO Box 4591, Melbourne 3001, Australia.  The license is subject 
to the law of Victoria, Australia, and subject to exclusive 
jurisdiction of the Victorian courts.

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

