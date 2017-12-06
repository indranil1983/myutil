// Demonstrate URLConnection. 
import java.net.*; 
import java.io.*; 
import java.util.Date; 
import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.StringReader;
import java.net.HttpURLConnection;
import java.net.MalformedURLException;
import java.net.URL;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Date;
import java.util.Enumeration;
import java.util.HashMap;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;
import java.util.Properties;
import java.util.Set;
class UCDemo 
{ 
public static void main(String args[]) throws Exception { 
int c; 
HttpURLConnection conn=null;
System.setProperty("java.net.preferIPv4Stack" , "true");
Proxy proxy = new Proxy(Proxy.Type.HTTP, new InetSocketAddress("10.7.0.140", 8081));
URL url = new URL("https://rally1.rallydev.com/slm/webservice/v2.0/portfolioitem/feature?workspace=https://rally1.rallydev.com/slm/webservice/v2.0/workspace/50775741420&query=((State.Name%20%3D%20%22Implementing%22)OR(State.Name%20%3D%20%22Ready%22))&fetch=FormattedID&start=1&pagesize=1000");
			conn = (HttpURLConnection) url.openConnection(proxy);
			conn.setRequestMethod("GET");
			conn.setConnectTimeout(5000);
			conn.setRequestProperty("Accept", "application/json");
			conn.setRequestProperty("Authorization", "Basic Y2lfdXNlckBlcmljc3Nvbi5jb206Y2lfdXNlcjAx");
			conn.setRequestProperty("Content-Type", "application/json");
			conn.setDoOutput(true);
			conn.setDoInput(true);
			conn.setUseCaches(false);
			System.out.println(conn.toString());
			

			BufferedReader br = new BufferedReader(new InputStreamReader((conn.getInputStream())));

			String output;
			StringBuilder strBuild = new StringBuilder();
			while ((output = br.readLine()) != null) {
				strBuild.append(output);				
			}

			//logger.debug(strBuild.toString());
			System.out.println(strBuild);
} 
}