package com.stud.tmdc;

import java.io.IOException;
import java.io.InputStreamReader;
import java.io.OutputStreamWriter;
import java.io.UnsupportedEncodingException;
import java.net.HttpURLConnection;
import java.net.URL;
import java.net.URLConnection;
import java.nio.charset.Charset;
import java.util.Map;

import org.apache.commons.codec.binary.Base64;
import org.apache.http.HttpResponse;
import org.apache.http.client.HttpClient;
import org.apache.http.client.methods.CloseableHttpResponse;
import org.apache.http.client.methods.HttpDelete;
import org.apache.http.client.methods.HttpPost;
import org.apache.http.entity.StringEntity;
import org.apache.http.impl.client.CloseableHttpClient;
import org.apache.http.impl.client.HttpClientBuilder;
import org.apache.http.impl.client.HttpClients;
import org.apache.http.message.BasicHeader;
import org.json.JSONObject;

public class HttpReqResp {
	static String getAction(Map<String, String> requestParams) {
		try {
	    	String stringUrl = "http://localhost:8080/"+requestParams.get("table");
	    	if(requestParams.get("id") != null)
	    		stringUrl = stringUrl + "/" + requestParams.get("id");
	    	
	    	URL url = new URL(stringUrl);
	    	URLConnection uc = url.openConnection();

	    	uc.setRequestProperty("X-Requested-With", "Curl");

	    	String userpass = "username" + ":" + "password";
	    	String basicAuth = "Basic " + new String(new Base64().encode(userpass.getBytes()));
	    	uc.setRequestProperty("Authorization", basicAuth);

	    	InputStreamReader inputStreamReader = new InputStreamReader(uc.getInputStream());
	    	char[] buffer = new char[4096];
	    	StringBuilder sb = new StringBuilder();
	    	for(int len; (len = inputStreamReader.read(buffer)) > 0;)
	    	         sb.append(buffer, 0, len);
	    	
	    	return sb.toString();
		}
		catch(Exception ex) {
			return "Command must be like: /user_get_list or /customer_get_1\n\r"
					+ "As a result you will get a list of users or customer with\n\r"
					+ "id = 1";
		}
	}
	
	static String postAction(Map<String, String> requestParams) {
		try {
	    	String stringUrl = "http://localhost:8080/"+requestParams.get("table");
	    	String username="myusername";
	        String password="mypassword";
	    	JSONObject json = new JSONObject();
	    	
	    	if(requestParams.get("id") != null) {
	    		String[] jsonValues = requestParams.get("id").split(",");
	    		json.put("id",  Integer.parseInt(jsonValues[0]));
	    		
	    		if(requestParams.get("table").equals("user")) {
		    		json.put("login", jsonValues[1])
		    			.put("firstName", jsonValues[2])
		    			.put("lastName", jsonValues[3])
		    			.put("email", jsonValues[4])
		    			.put("password", jsonValues[5]);
	    		}
	    		else if(requestParams.get("table").equals("customer")) {
	    			json.put("firstName", jsonValues[1])
	    				.put("lastName", jsonValues[2]);
	    		}
	    	}
	    	
	    	String jsonData = json.toString();
	    	
	    	HttpReqResp httpPostReq=new HttpReqResp();
	        HttpPost httpPost=httpPostReq.createConnectivity(stringUrl , username, password);
	        
	        return "Response code: " + httpPostReq.executeReq(jsonData, httpPost); 
		}
		catch(Exception ex) {
			return "Command must be like: /customer_post_4,John,Black\n\r"
					+ "As a result you will have customer with:\n\r"
					+ "id = 4\n\r"
					+ "firstName = John\n\r"
					+ "lastName = Black\n\r";
		}
	}
	
	static String putAction(Map<String, String> requestParams) {
		String stringUrl = "http://localhost:8080/"+requestParams.get("table");
		
		JSONObject json = new JSONObject();
    	
    	if(requestParams.get("id") != null) {
    		String[] jsonValues = requestParams.get("id").split(",");
    		json.put("id",  Integer.parseInt(jsonValues[0]));
    		
    		if(requestParams.get("table").equals("user")) {
	    		json.put("login", jsonValues[1])
	    			.put("firstName", jsonValues[2])
	    			.put("lastName", jsonValues[3])
	    			.put("email", jsonValues[4])
	    			.put("password", jsonValues[5]);
    		}
    		else if(requestParams.get("table").equals("customer")) {
    			json.put("firstName", jsonValues[1])
    				.put("lastName", jsonValues[2]);
    		}
    	}
    	
    	String jsonData = json.toString();
		
        try{
        	URL url = new URL(stringUrl);
            HttpURLConnection connection = (HttpURLConnection) url.openConnection();
            connection.setRequestMethod("PUT");
            connection.setDoOutput(true);
            connection.setRequestProperty("Content-Type", "application/json");
            connection.setRequestProperty("Accept", "application/json");
            OutputStreamWriter osw = new OutputStreamWriter(connection.getOutputStream());
            osw.write(jsonData);
            osw.flush();
            osw.close();
            
            return "Response code: " + connection.getResponseCode();
        }
        catch(Exception ex) {
        	return "Command must be like: /customer_put_4,Richard,Hamond\n\r"
					+ "As a result you will update customer with:\n\r"
					+ "id = 4\n\r"
					+ "and set\n\r"
					+ "firstName = Richard\n\r"
					+ "lastName = Hamond\n\r";
        }
     }
	
	static String deleteAction(Map<String, String> requestParams) {
		String stringUrl = "http://localhost:8080/"+requestParams.get("table")+"/"+requestParams.get("id");
		
		CloseableHttpClient httpclient = HttpClients.createDefault();
		HttpDelete httpDelete = new HttpDelete(stringUrl);
		try {
			httpDelete.addHeader(new BasicHeader("Content-Type", "application/json; charset=UTF-8"));
			CloseableHttpResponse response = httpclient.execute(httpDelete);
			response.close();
			
			return "Response code: " + response.getStatusLine().getStatusCode();
		}
		catch(Exception ex) {
			return "Command must be like: /user_delete_4\n\r"
					+ "As a result you will delete user with\n\r"
					+ "id = 4";
		}
	}
	
	HttpPost createConnectivity(String restUrl, String username, String password)
    {
        HttpPost post = new HttpPost(restUrl);
        String auth=new StringBuffer(username).append(":").append(password).toString();
        byte[] encodedAuth = Base64.encodeBase64(auth.getBytes(Charset.forName("US-ASCII")));
        String authHeader = "Basic " + new String(encodedAuth);
        post.setHeader("AUTHORIZATION", authHeader);
        post.setHeader("Content-Type", "application/json");
        post.setHeader("Accept", "application/json");
        post.setHeader("X-Stream" , "true");
        
        return post;
    }
     
    String executeReq(String jsonData, HttpPost httpPost)
    {
    	String responseCode = "";
        try{
        	responseCode = executeHttpRequest(jsonData, httpPost);
        }
        catch (UnsupportedEncodingException e){
            System.out.println("error while encoding api url : "+e);
        }
        catch (IOException e){
            System.out.println("ioException occured while sending http request : "+e);
        }
        catch(Exception e){
            System.out.println("exception occured while sending http request : "+e);
        }
        finally{
            httpPost.releaseConnection();
        }
        
        return responseCode;
    }
     
    String executeHttpRequest(String jsonData,  HttpPost httpPost)  throws UnsupportedEncodingException, IOException
    {
        HttpResponse response=null;
        httpPost.setEntity(new StringEntity(jsonData));
        HttpClient client = HttpClientBuilder.create().build();
        response = client.execute(httpPost);
        
        return ""+response.getStatusLine().getStatusCode();
    }
}