/*
 *  Copyright (C) 2006  Jay Colson
 *
 *  This library is free software; you can redistribute it and/or
 *  modify it under the terms of the GNU Lesser General Public
 *  License as published by the Free Software Foundation; either
 *  version 2.1 of the License, or (at your option) any later version.
 *  
 *  This library is distributed in the hope that it will be useful,
 *  but WITHOUT ANY WARRANTY; without even the implied warranty of
 *  MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the GNU
 *  Lesser General Public License for more details.
 *
 *  You should have received a copy of the GNU Lesser General Public
 *  License along with this library; if not, write to the Free Software
 *  Foundation, Inc., 51 Franklin Street, Fifth Floor, Boston, MA  02110-1301  USA
 *  
 *  A copy of the GNU Lesser General Public License may also be found at 
 *  http://www.gnu.org/licenses/lgpl.txt
 */
package org.synchronoss.utils.cpo;
import java.security.SecureRandom;
import java.net.InetAddress;
import java.math.BigInteger;
import org.apache.log4j.*;

public class GUID  {
  private static GUID guid;
  private SecureRandom seeder;
  SecureRandom sr;
  String guidMidValue;
  private static Category OUT = Category.getInstance(GUID.class);

  private GUID() {
    initGuid();
  }

  private void initGuid() {
    try {
      seeder = SecureRandom.getInstance("SHA1PRNG");
      seeder.generateSeed(20);
      sr = SecureRandom.getInstance("SHA1PRNG");
      byte[] newSeed = new byte[20];
      seeder.nextBytes(newSeed);
      sr.setSeed(newSeed);

      StringBuffer tmpBuffer = new StringBuffer();
      // get the inet address
      InetAddress inet = InetAddress.getLocalHost();
      byte [] bytes = inet.getAddress();
      String hexInetAddress = hexFormat(new BigInteger(bytes).intValue());

      // get the hashcode
      String thisHashCode = hexFormat(this.hashCode());

      /* set up a cached midValue as this is the same per method
      / call as is object specific and is the
      / ...-xxxx-xxxx-xxxx-xxxx.. mid part of the sequence
      */
      tmpBuffer.append("-");
      tmpBuffer.append(hexInetAddress.substring(0,4));
      tmpBuffer.append("-");
      tmpBuffer.append(hexInetAddress.substring(4));
      tmpBuffer.append("-");
      tmpBuffer.append(thisHashCode.substring(0,4));
      tmpBuffer.append("-");
      tmpBuffer.append(thisHashCode.substring(4));
      guidMidValue = tmpBuffer.toString();
    } catch (Exception e) {
      OUT.debug("initGuid: "+e.getMessage());
    }
  }

  static GUID getInstance() {
    if (guid == null)
      guid = new GUID();
    return guid;
  }
  
  public static String getGUID() {
    GUID guid = GUID.getInstance();
    long timeNow = System.currentTimeMillis();
    int timeLow = (int) timeNow & 0xFFFFFFFF;
    int node = guid.sr.nextInt();
    String retVal = (hexFormat(timeLow) + guid.guidMidValue + hexFormat(node));
    OUT.debug("getGUID(): " +retVal);
    return retVal;
  }

  /**
    * Returns an 8 character hexidecimal representation of trgt. If the
    * result is not equal to eight characters leading zeros are prefixed.
    * @return 8 character hex representation of trgt
    */
  private static String hexFormat(int trgt){
    String s = Integer.toHexString(trgt);
    int sz = s.length();

    if (sz==8){
        return s;
    }
    int fill = 8 - sz;
    StringBuffer buf = new StringBuffer();

    for (int i=0;i<fill;++i){
       // add leading zeros
       buf.append('0');
    }
    buf.append(s);
    return buf.toString();
  }
}