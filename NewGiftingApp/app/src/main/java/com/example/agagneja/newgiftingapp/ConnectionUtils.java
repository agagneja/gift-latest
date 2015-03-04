package com.example.agagneja.newgiftingapp;

import android.content.SharedPreferences;
import android.preference.PreferenceManager;
import android.util.Log;
import android.widget.Toast;

import org.apache.http.HeaderElement;
import org.apache.http.HttpEntity;
import org.apache.http.HttpResponse;
import org.apache.http.HttpVersion;
import org.apache.http.NameValuePair;
import org.apache.http.client.HttpClient;
import org.apache.http.client.methods.HttpGet;
import org.apache.http.client.methods.HttpPost;
import org.apache.http.conn.ClientConnectionManager;
import org.apache.http.conn.scheme.PlainSocketFactory;
import org.apache.http.conn.scheme.Scheme;
import org.apache.http.conn.scheme.SchemeRegistry;
import org.apache.http.conn.ssl.SSLSocketFactory;
import org.apache.http.entity.StringEntity;
import org.apache.http.impl.client.DefaultHttpClient;
import org.apache.http.impl.conn.tsccm.ThreadSafeClientConnManager;
import org.apache.http.params.BasicHttpParams;
import org.apache.http.params.HttpConnectionParams;
import org.apache.http.params.HttpParams;
import org.apache.http.params.HttpProtocolParams;
import org.apache.http.protocol.HTTP;
import org.apache.http.util.EntityUtils;
import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.Reader;
import java.io.UnsupportedEncodingException;
import java.security.KeyStore;
import java.text.ParseException;

/**
 * Created by aagnihotri on 12/09/14.
 */
public class ConnectionUtils {

    private static final String API_HOST_STAGE = "https://stage2c7161.qa.paypal.com";

    private static final String API_PORT_STAGE = ":14262";


    private static String X_PAYPAL_SECURITY_CONTEXT ;

    private final static HttpClient httpClient = getNewHttpClient();
    public static String r;

    public static JSONObject createGift(String input) {

        String postPath = buildPathForCreateGift();
        HttpPost httpPost = new HttpPost(postPath);
        Log.d("PATH", postPath);
        X_PAYPAL_SECURITY_CONTEXT = getSecutityContext();
        HttpResponse httpResponse;
        JSONObject response = null;
        try {
            httpPost.setEntity(new StringEntity(input));
            Log.d("INPUT", input);
            httpPost.setHeader("X-PAYPAL-SECURITY-CONTEXT", X_PAYPAL_SECURITY_CONTEXT);
            Log.d("SECURITY_CONTEXT", X_PAYPAL_SECURITY_CONTEXT);
            httpPost.setHeader("Accept", "application/json");
            httpPost.setHeader("Content-Type", "application/json");
            try {
                httpResponse = httpClient.execute(httpPost);
            } catch (IOException e) {
                e.printStackTrace();
                return null;
            }
        } catch (UnsupportedEncodingException e) {
            e.printStackTrace();
            return null;
        }

        if (httpResponse != null) {

            try {
                String resp;
                //response = new JSONObject(EntityUtils.toString(httpResponse.getEntity()));
                HttpEntity entity = httpResponse.getEntity();

                if (entity != null) {

                    InputStream instream = entity.getContent();
                     resp = convertStreamToString(instream);

                    // Closing the input stream will trigger connection release
                    instream.close();
                    response = new JSONObject(resp);
                }

            } catch (IOException e1) {
                e1.printStackTrace();
                return null;
            } catch (JSONException e) {
                e.printStackTrace();
                return null;
            }

        } else {
            try {
                Log.e("FAILED RESPONSE", EntityUtils.toString(httpResponse.getEntity()));
               // int i = httpResponse.getStatusLine().getStatusCode();
                long i = httpResponse.getEntity().getContentLength();
                String b = Long.toString(i);
                Log.e("RESPONSE CODE",b);
            } catch (Exception e) {
                e.printStackTrace();
                return null;
            }
        }

        return response;
    }

    private static String convertStreamToString(InputStream is) {

        BufferedReader reader = new BufferedReader(new InputStreamReader(is));
        StringBuilder sb = new StringBuilder();

        String line = null;
        try {
            while ((line = reader.readLine()) != null) {
                sb.append(line + "\n");
            }
        } catch (IOException e) {
            e.printStackTrace();
        } finally {
            try {
                is.close();
            } catch (IOException e) {
                e.printStackTrace();
            }
        }
        return sb.toString();
    }
    private static String buildPathForCreateGift() {
        StringBuilder stringBuilder = new StringBuilder(API_HOST_STAGE);
        stringBuilder.append(API_PORT_STAGE);
        stringBuilder.append(Endpoints.ENDPOINT_GIFT_CREATE);
        return stringBuilder.toString();
    }

    private static String buildPathForFundingOptions(String giftID) {
        StringBuilder stringBuilder = new StringBuilder(API_HOST_STAGE);
        stringBuilder.append(API_PORT_STAGE);
        stringBuilder.append(Endpoints.ENDPOINT_GIFT_CREATE);
        stringBuilder.append("/");
        stringBuilder.append(giftID);
        stringBuilder.append(Endpoints.SUFFIX_PLAN);
        return stringBuilder.toString();
    }
    private static String buildPathForFulfilmentOptions(String giftID) {
        StringBuilder stringBuilder = new StringBuilder(API_HOST_STAGE);
        stringBuilder.append(API_PORT_STAGE);
        stringBuilder.append(Endpoints.ENDPOINT_GIFT_CREATE);
        stringBuilder.append("/");
        stringBuilder.append(giftID);
        stringBuilder.append(Endpoints.SUFFIX_PAY);
        return stringBuilder.toString();
    }


    private static String buildPathForViewGift(String id)
    {
        StringBuilder stringBuilder = new StringBuilder(API_HOST_STAGE);
        stringBuilder.append(API_PORT_STAGE);
        stringBuilder.append(Endpoints.ENDPOINT_GIFT_CREATE);
        stringBuilder.append("/");
        stringBuilder.append(id);
        return stringBuilder.toString();
    }

    public static HttpClient getNewHttpClient() {
        try {
            KeyStore trustStore = KeyStore.getInstance(KeyStore.getDefaultType());
            trustStore.load(null, null);
            SSLSocketFactory sf = new MySSLSocketFactory(trustStore);
            sf.setHostnameVerifier(SSLSocketFactory.ALLOW_ALL_HOSTNAME_VERIFIER);
            HttpParams params = new BasicHttpParams();

            int timeoutConnection = 50000;
            HttpConnectionParams.setConnectionTimeout(params, timeoutConnection);
            int timeoutSocket = 50000;
            HttpConnectionParams.setSoTimeout(params,timeoutSocket);

            HttpProtocolParams.setVersion(params, HttpVersion.HTTP_1_1);
            HttpProtocolParams.setContentCharset(params, HTTP.UTF_8);
            SchemeRegistry registry = new SchemeRegistry();
            registry.register(new Scheme("http", PlainSocketFactory.getSocketFactory(), 80));
            registry.register(new Scheme("https", sf, 443));
            ClientConnectionManager ccm = new ThreadSafeClientConnManager(params, registry);
            return new DefaultHttpClient(ccm, params);

        } catch (Exception e) {
            return new DefaultHttpClient();
        }

    }

    public static JSONObject fundingOptionsRequest(String id) {
        String postPath = buildPathForFundingOptions(id);
        HttpPost httpPost = new HttpPost(postPath);
        Log.d("PATH", postPath);
        X_PAYPAL_SECURITY_CONTEXT = getSecutityContext();
        HttpResponse httpResponse;
        JSONObject response = null;
        try {
            httpPost.setEntity(new StringEntity(new JSONObject().toString()));
            httpPost.setHeader("X-PAYPAL-SECURITY-CONTEXT", X_PAYPAL_SECURITY_CONTEXT);
            Log.d("SECURITY_CONTEXT", X_PAYPAL_SECURITY_CONTEXT);
            httpPost.setHeader("Accept", "application/json");
            httpPost.setHeader("Content-Type", "application/json");
            try {
                httpResponse = httpClient.execute(httpPost);
            } catch (IOException e) {
                e.printStackTrace();
                return null;
            }
        } catch (UnsupportedEncodingException e) {
            e.printStackTrace();
            return null;
        }

        if (httpResponse != null) {

            try {
                response = new JSONObject(EntityUtils.toString(httpResponse.getEntity()));
            } catch (IOException e1) {
                e1.printStackTrace();
                return null;
            } catch (JSONException e) {
                e.printStackTrace();
                return null;
            }

        } else {
            try {
                Log.e("FAILED RESPONSE", EntityUtils.toString(httpResponse.getEntity()));
            } catch (Exception e) {
                e.printStackTrace();
                return null;
            }
        }

        return response;
    }


    public static JSONObject fulfillmentOptionsRequest(String id, String body) {
        String postPath = buildPathForFulfilmentOptions(id);
        HttpPost httpPost = new HttpPost(postPath);
        Log.e("Here is PATH", postPath);
        Log.e("here is body",body);
        HttpResponse httpResponse;
        JSONObject response = null;
        X_PAYPAL_SECURITY_CONTEXT = getSecutityContext();

        try {
            httpPost.setEntity(new StringEntity(body));
            httpPost.setHeader("X-PAYPAL-SECURITY-CONTEXT", X_PAYPAL_SECURITY_CONTEXT);
            Log.d("SECURITY_CONTEXT", X_PAYPAL_SECURITY_CONTEXT);
            httpPost.setHeader("Accept", "application/json");
            httpPost.setHeader("Content-Type", "application/json");
            try {
                httpResponse = httpClient.execute(httpPost);
            } catch (IOException e) {
                e.printStackTrace();
                return null;
            }
        } catch (UnsupportedEncodingException e) {
            e.printStackTrace();
            return null;
        }

        if (httpResponse != null) {

            try {
               // Log.e("here is the error",EntityUtils.toString(httpResponse.getEntity()));
                response = new JSONObject(EntityUtils.toString(httpResponse.getEntity()));
            } catch (IOException e1) {
                e1.printStackTrace();
                return null;
            } catch (JSONException e) {
                e.printStackTrace();
                return null;
            }

        } else {
            try {
                Log.e("FAILED RESPONSE", EntityUtils.toString(httpResponse.getEntity()));
            } catch (Exception e) {
                e.printStackTrace();
                return null;
            }
        }

        return response;
    }

    public static JSONArray getGifts() {
        //String postPath = buildPathForFundingOptions(id);
        String postPath = "https://stage2c7161.qa.paypal.com:14262/v1/payments/personal-payments/gifts";
        HttpGet httpPost = new HttpGet(postPath);
        String security = getSecutityContext();
        //String security = " {\"scopes\":[\"*\"],\"subjects\":[{\"subject\":{\"id\":\"0\",\"auth_state\":\"LOGGEDIN\",\"account_number\":\"1453948006982829156\",\"auth_claims\":[\"USERNAME\",\"PASSWORD\"]}}],\"actor\":{\"id\":\"0\",\"account_number\":\"1453948006982829156\",\"auth_claims\":[\"USERNAME\",\"PASSWORD\"]}}";
        Log.d("PATH", postPath);
        HttpResponse httpResponse;
        JSONObject response = null;
        JSONArray jar = null;
        // httpPost.setEntity(new StringEntity(body));
            httpPost.setHeader("X-PAYPAL-SECURITY-CONTEXT", security);
            Log.d("SECURITY_CONTEXT", security);
            httpPost.setHeader("Accept", "application/json");
            httpPost.setHeader("Content-Type", "application/json");
            try {
                httpResponse = httpClient.execute(httpPost);
            } catch (IOException e) {
                e.printStackTrace();
                return null;
            }

        if (httpResponse != null && httpResponse.getStatusLine().getStatusCode() == 200) {

            try {
               // String responseBody = getResponseBody(httpResponse);
                String responseBody = EntityUtils.toString(httpResponse.getEntity());
                Log.d("ANU",responseBody);
                r = responseBody;
                print(r);
                jar = new JSONArray(r);
                response = (JSONObject)jar.get(0);

              //  response = new JSONObject(EntityUtils.toString(httpResponse.getEntity()));
               // response = new JSONObject(responseBody);
            } catch (JSONException e) {
                e.printStackTrace();
                return null;
            }
            catch (IOException e)
            {
                e.printStackTrace();
            }



        } else {
            try {
                Log.e("FAILED RESPONSE", EntityUtils.toString(httpResponse.getEntity()));
            } catch (Exception e) {
                e.printStackTrace();
                return null;
            }
        }

        return jar;
    }

    public static JSONObject viewGift(String id) {
        String postPath = buildPathForViewGift(id);
        HttpGet httpPost = new HttpGet(postPath);
        Log.d("PATH", postPath);
        HttpResponse httpResponse;
        JSONObject response = null;

        X_PAYPAL_SECURITY_CONTEXT = getSecutityContext();
            httpPost.setHeader("X-PAYPAL-SECURITY-CONTEXT", X_PAYPAL_SECURITY_CONTEXT);
            Log.d("SECURITY_CONTEXT", X_PAYPAL_SECURITY_CONTEXT);
            httpPost.setHeader("Accept", "application/json");
            httpPost.setHeader("Content-Type", "application/json");
            try {
                httpResponse = httpClient.execute(httpPost);
            } catch (IOException e) {
                e.printStackTrace();
                return null;
            }


        if (httpResponse != null && httpResponse.getStatusLine().getStatusCode() == 200) {

            try {
                response = new JSONObject(EntityUtils.toString(httpResponse.getEntity()));
            } catch (IOException e1) {
                e1.printStackTrace();
                return null;
            } catch (JSONException e) {
                e.printStackTrace();
                return null;
            }

        } else {
            try {
                Log.e("FAILED RESPONSE", EntityUtils.toString(httpResponse.getEntity()));
            } catch (Exception e) {
                e.printStackTrace();
                return null;
            }
        }

        return response;
    }

    public static String getResponseBody(HttpResponse httpResponse)
    {
        String response_text = null;

        HttpEntity entity = null;

        try {

            entity = httpResponse.getEntity();

            response_text = _getResponseBody(entity);

        } catch (ParseException e) {

            e.printStackTrace();

        } catch (IOException e) {

            if (entity != null) {

                try {

                    entity.consumeContent();

                } catch (IOException e1) {

                }

            }

        }

        return response_text;

    }

    public static String _getResponseBody(final HttpEntity entity) throws IOException, ParseException
    {
        if (entity == null) { throw new IllegalArgumentException("HTTP entity may not be null"); }

        InputStream instream = entity.getContent();

        if (instream == null) { return ""; }

        if (entity.getContentLength() > Integer.MAX_VALUE) { throw new IllegalArgumentException(

                "HTTP entity too large to be buffered in memory"); }

        String charset = getContentCharSet(entity);

        if (charset == null) {

            charset = HTTP.DEFAULT_CONTENT_CHARSET;

        }

        Reader reader = new InputStreamReader(instream, charset);

        StringBuilder buffer = new StringBuilder();

        try {

            char[] tmp = new char[1024];

            int l;

            while ((l = reader.read(tmp)) != -1) {

                buffer.append(tmp, 0, l);

            }

        } finally {

            reader.close();

        }

        return buffer.toString();
    }

    public static String getContentCharSet(final HttpEntity entity) throws ParseException {

        if (entity == null) { throw new IllegalArgumentException("HTTP entity may not be null"); }

        String charset = null;

        if (entity.getContentType() != null) {

            HeaderElement values[] = entity.getContentType().getElements();

            if (values.length > 0) {

                NameValuePair param = values[0].getParameterByName("charset");

                if (param != null) {

                    charset = param.getValue();

                }

            }

        }

        return charset;

    }

        public static void print(String str) {
        if(str.length() > 4000) {
            Log.i("hey", str.substring(0, 4000));
            print(str.substring(4000));
        } else
            Log.i("Hey", str);
    }

    public static String getSecutityContext()
    {
        Account account = new Account();
        String num = account.getAccount();
        StringBuffer sb = new StringBuffer("{\"scopes\":[\"*\"],\"subjects\":[{\"subject\":{\"id\":\"0\",\"auth_state\":\"LOGGEDIN\",\"account_number\":\"");
        sb.append(num);
        sb.append("\",\"auth_claims\":[\"USERNAME\",\"PASSWORD\"]}}],\"actor\":{\"id\":\"0\",\"account_number\":\"");
        sb.append(num);
        sb.append("\",\"auth_claims\":[\"USERNAME\",\"PASSWORD\"]}, \"global_session_id\": \"3ilsi34jsi32300Zsdkk23sdlfjlkjsd\"}");
        return  sb.toString();


    }
}
