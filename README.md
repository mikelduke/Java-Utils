mdp3 Java Util
========

Various util classes for java projects.

Created by Mikel Duke http://www.mikelduke.com
  
Code https://github.com/mikelduke/
  
Released under GPLv2

******************************************************************************

  ConsoleReader
  
  The ConsoleReader class easily adds a threaded BufferedInput Console reader 
  to a Java app, to enable simple call backs to methods on the implementing 
  object. 
  
  All methods used by the lookups should implement all Strings in the parameter 
  list and do type conversion within the callback methods.
  
  Example:
  
  For some object o with methods:
  
  ConsoleReader cr = new ConsoleReader("App Name", o);
  
  Will instantiate and start an instance of the ConsoleReader which will call 
  back to methods on o when the method names and parameter counts match, 
  parameter types are unchecked and should all be String.
  