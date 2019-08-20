package httputil;


import com.google.gson.Gson;
import java.io.IOException;
import java.io.UnsupportedEncodingException;
import java.net.URI;
import java.nio.charset.Charset;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import org.apache.commons.codec.binary.StringUtils;
import org.apache.commons.io.IOUtils;
import org.apache.http.Header;
import org.apache.http.HttpHeaders;
import org.apache.http.HttpResponse;
import org.apache.http.NameValuePair;
import org.apache.http.auth.UsernamePasswordCredentials;
import org.apache.http.client.CredentialsProvider;
import org.apache.http.client.HttpClient;
import org.apache.http.client.config.RequestConfig;
import org.apache.http.client.methods.CloseableHttpResponse;
import org.apache.http.client.methods.HttpGet;
import org.apache.http.client.methods.HttpPost;
import org.apache.http.client.utils.URIBuilder;
import org.apache.http.conn.ConnectionKeepAliveStrategy;
import org.apache.http.entity.StringEntity;
import org.apache.http.impl.client.BasicCredentialsProvider;
import org.apache.http.impl.client.CloseableHttpClient;
import org.apache.http.impl.client.HttpClientBuilder;
import org.apache.http.message.BasicHeader;
import org.apache.http.protocol.HttpContext;
import org.testng.annotations.BeforeTest;
import org.testng.annotations.Test;

public class SlackHttpClient {
   private CloseableHttpClient client;
   private Header TOKEN;
   Map<String, String> payload = new HashMap<String, String>();
   
   public SlackHttpClient(int timeout, final long keepAliveDuration, String token) {
      RequestConfig config = RequestConfig.custom()
            .setConnectTimeout(timeout * 1000)
            .setConnectionRequestTimeout(timeout * 1000)
            .setSocketTimeout(timeout * 1000).build();

      ConnectionKeepAliveStrategy myStrategy = new ConnectionKeepAliveStrategy() {
         public long getKeepAliveDuration(HttpResponse response, HttpContext context) {
            return keepAliveDuration;
         }
      };
   
      List<Header> defaultHeaders = new ArrayList<Header>();
      defaultHeaders.add(new BasicHeader(HttpHeaders.ACCEPT, "application/json"));
      defaultHeaders.add(new BasicHeader(HttpHeaders.CONTENT_TYPE, "application/json"));
      defaultHeaders.add(new BasicHeader(HttpHeaders.AUTHORIZATION, "Bearer " + token));
   
      this.client = HttpClientBuilder.create()
            .setDefaultRequestConfig(config).setKeepAliveStrategy(myStrategy).setDefaultHeaders(defaultHeaders).build();

   }

   public String executePost(String uri, Map<String, String> payload){
      CloseableHttpResponse response = null;
      try {
         HttpPost post = new HttpPost(uri);
         post.setEntity(new StringEntity(new Gson().toJson(payload)));

      response = client.execute(post);
      return IOUtils.toString(response.getEntity().getContent(), Charset.forName("UTF-8"));
      } catch (Exception e) {
         e.printStackTrace();
      } finally {
         if(response != null) {
            try {
               response.close();
            } catch (IOException e) {
               e.printStackTrace();
            }
         }
      }
      return null;
   }

   public String executeGet(String url) throws Exception{
      CloseableHttpResponse response = null;
      try {
         HttpGet get = new HttpGet(url);
         response = client.execute(get);
         return IOUtils.toString(response.getEntity().getContent(), Charset.forName("UTF-8"));
      } catch (Exception e) {
         e.printStackTrace();
      } finally {
         if(response != null) {
            try {
               response.close();
            } catch (IOException e) {
               e.printStackTrace();
            }
         }
      }
      return null;
   }

}
