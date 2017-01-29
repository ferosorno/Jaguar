/*#################################
* class:  ExitTrappedException.java
* author: Fernando Osorno-Gutierrez
* date:   29 Aug 2014
* #################################
**********************************/

package uk.ac.man.jaguar.util;

import java.security.Permission;

public class ExitTrappedException extends SecurityException { 

public static void forbidSystemExitCall() {
  final SecurityManager securityManager = new SecurityManager() {
    public void checkPermission( Permission permission ) {
      if( "exitVM".equals( permission.getName() ) ) {
        throw new ExitTrappedException() ;
      }
    }
  } ;
  System.setSecurityManager( securityManager ) ;
}

public static void enableSystemExitCall() {
  System.setSecurityManager( null ) ;
  


}

}