/*
Hans Kanders
*/

package netninny;

import java.util.regex.*;

/* Contains the header and body of a HTTP-message
   in a user-friendly way */
public class HttpMessage
{
      String header;
      byte[] body;

      /* Constructor */
      public HttpMessage(String header, byte[] body)
      {
	    this.header = header;
	    this.body = body;
      }

      /* Constructor */
      public HttpMessage(String header)
      {
	    this.header = header;
      }

      /* Returns the value of a specified field in the header.
	 Not for external use */
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

      /* Changes a specified field to a new value.
	 Not for external use */
      private void setField(String field, String value)
      {
	    this.header =
		  this.header.replaceFirst(field + ": (.*?)\r\n", field + ": " + value + "\r\n");
      }

      /* Adds a new field to the header.
	 Not for external use */
      private void addField(String field, String value)
      {
	    header
		  = header.substring(0, header.length() - 2);

	    header
		  = header + field + ": " + value + "\r\n\r\n";
      }

      /* Removes a field from header */
      public void removeField(String field)
      {
	    this.header =
		  this.header.replaceFirst(field + ": (.*?)\r\n", "");
      }

      /* Returns value of Host-field */
      public String getAdress()
      {
	    return this.getField("Host");
      }

      /* Returns URL found in request field (GET) */
      public String getUrl()
      {
	    Pattern pattern = Pattern.compile("GET (.*?) HTTP");
	    Matcher matcher = pattern.matcher(this.header);
	    if (matcher.find())
	    {
		  return matcher.group(1);
	    }
	    else return null;
      }

      /* Returns value of Content-type field */
      public String getContentType()
      {
	     return this.getField("Content-Type");
      }      

      /* Returns value of Content-Length field */
      public int getContentLength()
      {
	    String tmp = this.getField("Content-Length");
	    if (tmp == null)
		  return 0;
	    else
		  return Integer.parseInt(tmp);
      }

      /* Returns value of Connection field */
      public String getConnection()
      {
	    return this.getField("Connection");
      }

      /* Returns true if the HTTP-method used is GET */
      public boolean isMethodGet()
      {
	    return header.startsWith("GET");
      }

      /* Returns true if Content-Encoding field exists */
      public boolean isCompressed()
      {
	    return (getField("Content-Encoding") != null);
      }

      /* Changes Connection field to Close */
      public void setConnectionClose()
      {
	    this.setField("Connection", "Close");
      }

      /* Changes Connection to value. If Value == Keep-Alive
	 then Keep-Alive field is added with timeout value */
      public void setConnection(String value, int timeout)
      {
	    this.setField("Connection", value);

	    if (value.toLowerCase().equals("keep-alive") && timeout > 0);
		this.addField("Keep-Alive", Integer.toString(timeout));
      }

      /* Removes any fields involved with cacheing. This 
	 is so response body can be searched for blacklist words */
      public void removeCacheFields()
      {
	    this.removeField("If-Modified-Since");
	    this.removeField("If-None-Match");
	    this.removeField("Cache-Control");
      }
}
