/*
Hans Kanders
*/

package netninny;

import java.util.regex.*;

public class HttpMessage
{
      String header;
      byte[] body;

      public HttpMessage(String header, byte[] body)
      {
	    this.header = header;
	    this.body = body;
      }

      public HttpMessage(String header)
      {
	    this.header = header;
      }

      //Only used for verbose output 
      public void printBody()
      {
	    for(int i = 0; i < this.body.length; i++)
	    {
		  System.out.println(this.body[i]);
	    }
      }

      private String getField(String field)
      {
	    Pattern pattern = Pattern.compile(field + ": (.*?)\r\n");
	    Matcher matcher = pattern.matcher(this.header);
	    if (matcher.find())
	    {
		  return matcher.group(1);
	    }
	    else return null;
      }

      public void setField(String field, String newValue)
      {
	    this.header =
		  this.header.replaceFirst(field + ": (.*?)\r\n", field + ": " + newValue);
      }

      public String getAdress()
      {
	    return this.getField("Host");
      }

      public int getContentLength()
      {
	    String tmp = this.getField("Content-Length");
	    if (tmp == null)
		  return 0;
	    else
		  return Integer.parseInt(tmp);
      }

      public void setConnectionClose()
      {
	    this.setField("Connection", "Close\r\n");
      }

      public void setConnectionKeepAlive(int param)
      {
	    this.setField("Connection", "Keep-Alive\r\nKeep-Alive: timeout=" + Integer.toString(param) + "\r\n");
      }
}
