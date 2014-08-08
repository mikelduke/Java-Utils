mdp3 Java Util
========

Various util classes for java projects.

Created by Mikel Duke http://www.mikelduke.com
  
Code https://github.com/mikelduke/
  
Released under GPLv2

Test classes that can be used as examples are located in the test package.

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

******************************************************************************

  Webservice
  
  Simple Java Webservice using the Sun HTTP Server implementation. The 
  necessary packages may not be included in all JVMs. 
  
  This package creates a webserver at the specified port and context, which 
  listens for connections, builds the access URL parameters into a HashMap
  and finally calls the wsAccess callback specified by the WebserviceListener
  interface. The String returned by wsAccess is returned back to the browser.
  
******************************************************************************

  Settings
  
  Java package used to make loading various settings from a text file onto an 
  object easier.
  
  To use instantiate this Settings class using either an Object or a Class 
  definition, depending on whether the destination class is static or not. 
  
  After loadSettings is called with either a File object or String file name,
  the class looks up properties of the class that was passed in earlier, and 
  attempts to load values from the text file onto it.
  
  Supported property types are: int, long, double, bool, and String.
  
  Settings Text File usage:
    # denotes a comment, lines beginning with the symbol are ignored
    Key Value pairs should be denoted use property name: value
    Values are trimmed of whitespace
  