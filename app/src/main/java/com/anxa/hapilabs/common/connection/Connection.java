package com.anxa.hapilabs.common.connection;

import java.io.BufferedReader;
import java.io.DataOutputStream;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.UnsupportedEncodingException;
import java.net.HttpURLConnection;
import java.net.URL;
import java.security.KeyStore;
import java.security.NoSuchAlgorithmException;
import java.util.ArrayList;
import java.util.UUID;

import org.apache.http.HttpEntity;
import org.apache.http.HttpResponse;
import org.apache.http.HttpVersion;
import org.apache.http.NameValuePair;
import org.apache.http.client.HttpClient;
import org.apache.http.client.entity.UrlEncodedFormEntity;
import org.apache.http.client.methods.HttpGet;
import org.apache.http.client.methods.HttpPost;
import org.apache.http.conn.params.ConnManagerParams;
import org.apache.http.conn.scheme.PlainSocketFactory;
import org.apache.http.conn.scheme.Scheme;
import org.apache.http.conn.scheme.SchemeRegistry;
import org.apache.http.conn.ssl.SSLSocketFactory;
import org.apache.http.entity.BufferedHttpEntity;
import org.apache.http.entity.StringEntity;
import org.apache.http.impl.client.DefaultHttpClient;
import org.apache.http.impl.conn.tsccm.ThreadSafeClientConnManager;
import org.apache.http.message.BasicNameValuePair;
import org.apache.http.params.BasicHttpParams;
import org.apache.http.params.CoreProtocolPNames;
import org.apache.http.params.HttpConnectionParams;
import org.apache.http.params.HttpParams;
import org.apache.http.params.HttpProtocolParams;
import org.apache.http.protocol.BasicHttpContext;
import org.apache.http.protocol.HTTP;
import org.apache.http.protocol.HttpContext;

import com.hapilabs.R;
import com.anxa.hapilabs.common.connection.WebServices.SERVICES;
import com.anxa.hapilabs.common.handlers.reader.JsonDefaultResponseHandler;
import com.anxa.hapilabs.common.util.AppUtil;
import com.anxa.hapilabs.common.util.ApplicationEx;
import com.anxa.hapilabs.common.util.ImageManager;
import com.anxa.hapilabs.ui.models.PhotoDownloadObj;

import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.os.Environment;
import android.os.Handler;
import android.os.Message;
import android.util.Log;


public class Connection implements Runnable {

    private ArrayList<NameValuePair> cookies;
    private ArrayList<NameValuePair> params;
    private ArrayList<NameValuePair> headers;

    PhotoDownloadObj photoObj;

    public static final int GET = 0;
    public static final int POST = 1;
    public static final int BITMAP = 2;

    public static final int REQUEST_START = 0;
    public static final int REQUEST_ERROR = 1;
    public static final int REQUEST_SUCCESS = 2;

    private static final int TIME_OUT_SOCKET = 15000;
    private static final int TIME_OUT_CONNECTION = 15000;

    private Handler handler;

    private int method;
    private String url;
    private String data;
    public boolean multipost = false;

    private String uniqueID;
    private HttpClient httpClient;
    WebServices webservice;

    String twohypens = "--";
    String lineend = "\r\n";
    String boundary = "12345";
    public Bitmap bmp;
    public Context textContext;

    String path;
    public final static int DEFAULT_EXCEPTION_CONNECTION = 404;

    public final static String OFFLINE_STR = "{\"api_response\":"
            + "{\"message_detail\":\"Your internet appears to be unstable/offline. HAPi.com is unable to connect at this time.\",\"message\":\"failed\" ,\"error_count\":1 }}";

    //JsonDefaultResponseHandler jsonHandler


    public Connection() {
        cookies = new ArrayList<NameValuePair>();
        params = new ArrayList<NameValuePair>();
        headers = new ArrayList<NameValuePair>();
        webservice = new WebServices();
        handler = new Handler();
    }

    public Connection(Handler _handler) {
        cookies = new ArrayList<NameValuePair>();
        params = new ArrayList<NameValuePair>();
        headers = new ArrayList<NameValuePair>();
        handler = _handler;
        webservice = new WebServices();
    }

    public void addParam(String name, String value) {
        params.add(new BasicNameValuePair(name, value));
    }

    public void addHeader(String name, String value) {
        headers.add(new BasicNameValuePair(name, value));
    }

    public void addCookie(String name, String value) {
        cookies.add(new BasicNameValuePair(name, value));
    }

    public void setEntryString(String id) {
        this.uniqueID = id;
    }

    public void create(int method, String url, String data, JsonDefaultResponseHandler jsonHandler) {
        this.method = method;
        this.url = url;
        this.data = data;
        this.handler = jsonHandler;
        ConnectionManager.getInstance().push(this);
    }

    public void create(int method, String url, String data) {
        this.method = method;
        this.url = url;
        this.data = data;
        ConnectionManager.getInstance().push(this);
    }

    public void get(String url) {
        create(GET, url, null);
    }

    public void post(String url, String data) {
        create(POST, url, data);
    }

    public void post(String url) {
        create(POST, url, null);
    }


    public void post(String url, JsonDefaultResponseHandler jsonHandler) {
        create(POST, url, null, jsonHandler);
    }

    public void post(String url, String data, JsonDefaultResponseHandler jsonHandler) {

        create(POST, url, data, jsonHandler);
    }

    public void get(String url, JsonDefaultResponseHandler jsonHandler) {
        create(GET, url, null, jsonHandler);
    }

    public void bitmap(String url) {
        create(BITMAP, url, null);
    }

    @Override
    public void run() {

        serverResponseObj obj = new serverResponseObj();

        if (url == null || "".equals(url)) {
            ConnectionManager.getInstance().didComplete(this);
            handler.sendMessage(Message.obtain(handler, REQUEST_ERROR, R.string.ERRORMESSAGE_INVALID_URL));
            return;
        }
        try {
            handler.sendMessage(Message.obtain(handler, REQUEST_START));
           
            /* Basic http params */
            HttpParams httpParams = new BasicHttpParams();

            ConnManagerParams.setTimeout(httpParams, TIME_OUT_SOCKET);

            HttpConnectionParams.setConnectionTimeout(httpParams, TIME_OUT_CONNECTION);
            HttpConnectionParams.setSoTimeout(httpParams, TIME_OUT_SOCKET);
            HttpConnectionParams.setTcpNoDelay(httpParams, true);
            HttpConnectionParams.setSocketBufferSize(httpParams, 8192);

            HttpProtocolParams.setContentCharset(httpParams, "utf-8");
            HttpProtocolParams.setVersion(httpParams, HttpVersion.HTTP_1_1);

            /* Register schemes, HTTP and HTTPS */
            SchemeRegistry registry = new SchemeRegistry();
            registry.register(new Scheme("http", new PlainSocketFactory(), 80));

            final SSLSocketFactory socketFactory = SSLSocketFactory.getSocketFactory();
            registry.register(new Scheme("https", socketFactory, 443));


             /* Make a thread safe connection manager for the client */
            ThreadSafeClientConnManager manager = new ThreadSafeClientConnManager(httpParams, registry);

            httpClient = new DefaultHttpClient(manager, httpParams);

            
            /*setup userAgent*/
            String defaultagent = System.getProperty("http.agent");
            if (defaultagent == null)
                defaultagent = AppUtil.getDefaultUserAgent();

            httpClient.getParams().setParameter(CoreProtocolPNames.USER_AGENT, ApplicationEx.getInstance().customAgent + " " + defaultagent);

            // Create local HTTP context
            HttpContext localContext = new BasicHttpContext();

            HttpResponse response = null;

            switch (method) {
                case GET:

                    Log.i("INFO", "COMMAND query" + params);
                    String query = formQueryString();
                    HttpGet httpGet = new HttpGet(url + query);
                    Log.i("INFO", "COMMAND GET URL: " + url + query);
                    /*if you need to set cookie parameters this has to be repeated using cookie name value pair*/

                    for (NameValuePair h : headers) {
                        httpGet.setHeader(h.getName(), h.getValue());
                    }
                    response = httpClient.execute(httpGet, localContext);

                    break;

                case POST:

                    Log.i("INFO", "COMMAND POST" + params);



                    String quer1y = formQueryString();

                    if (url.endsWith("?"))
                        url = url + quer1y;
                    else if (quer1y.length() > 0)
                        url = url + "&" + quer1y;

                    if (url.contains("+")) {
                        Log.i("INFO", "contains + ");

                        url = url.replace("+", "%2B");
                    }

                    HttpPost httpPost = new HttpPost(url);

                    Log.i("INFO", "url POST" + url);

                    for (NameValuePair h : headers) {
                        Log.i("INFO", "Header:" + h.getName() + "::" + h.getValue());
                        httpPost.setHeader(h.getName(), h.getValue());
                    }
                    if (!params.isEmpty()) {
                        Log.i("INFO", "Params:" + params);
                        httpPost.setEntity(new UrlEncodedFormEntity(params, HTTP.UTF_8));
                    }
                    Log.i("INFO", "URL:" + url);
                    if (data != null) {

                        if (multipost) { //*UPLOAD IMAGE DIFFERENT CONNECTION IS USED*/
                            obj = multipostConnection();
                        } else {
                            httpPost.setEntity(new StringEntity(data, HTTP.UTF_8));
                            httpPost.getAllHeaders();
                            httpPost.getMethod();
                            httpPost.getParams();
                            try {
                                response = httpClient.execute(httpPost);
                            } catch (java.net.UnknownHostException exception) {
                                exception.printStackTrace();
                                System.out.println("UnknownHostException: api.anxa.com");
                            }
                        }
                    }

                    break;
                case BITMAP:
                    Log.i("bitmap", "handler = " + url);
                    response = httpClient.execute(new HttpGet(url));
                    processBitmapResponse(response.getEntity());
                    return;
            }

            Log.i("INFO", "handler = " + handler);

            /* If there is an json handler, parse the response else return the string back */
            if (handler != null) {
                if (multipost) {
                    /*bitmap processing*/
                    Log.i("INFO", "processBitmapResponse = ");

                    processBitmapResponse(obj, uniqueID);

                } else {
                    /*text processing*/
                    Log.i("INFO", "processJsonResponse = " + response);

                    processJsonResponse(response);

                }
            } else if (method != BITMAP) {
                Log.i("INFO", "processEntity = ");

                processEntity(response);
            }

        } catch (Exception e) {
            e.printStackTrace();
            Log.i("INFO", "Exception = " + e.getMessage());

            obj = new serverResponseObj(DEFAULT_EXCEPTION_CONNECTION, "Your internet appears to be unstable/offline. HAPi.com is unable to connect at this time.", OFFLINE_STR);
            try {
                processJsonResponse(obj, uniqueID);
            } catch (Exception e1) {
            }

        } finally {
            try {
                httpClient.getConnectionManager().closeExpiredConnections();
                httpClient.getConnectionManager().shutdown();
                ConnectionManager.getInstance().didComplete(this);
            } catch (NullPointerException npe) {

            } catch (Exception e) {
            }

        }


    }


    private serverResponseObj multipostConnection() {
        HttpURLConnection connection = null;
        DataOutputStream outputStream = null;

        serverResponseObj response;
        int serverResponseCode;
        String serverResponseMessage = "";
        String resultStr = null;


        File urlServer = saveImage(bmp, textContext);


        int bytesRead, bytesAvailable, bufferSize;
        byte[] buffer;
        int maxBufferSize = 1 * 1024 * 1024;

        try {
            FileInputStream fileInputStream = new FileInputStream(urlServer);
            URL urlURL = new URL(url);
            connection = (HttpURLConnection) urlURL.openConnection();

            // Allow Inputs & Outputs
            connection.setDoInput(true);
            connection.setDoOutput(true);
            connection.setUseCaches(false);

            // Enable POST method
            connection.setRequestMethod("POST");
            connection.setRequestProperty("Accept", "application/json");
            connection.setRequestProperty("Content-Type", "multipart/form-data; boundary=" + boundary);
            outputStream = new DataOutputStream(connection.getOutputStream());
            outputStream.writeBytes(twohypens + boundary + lineend);
            outputStream.writeBytes("Content-Disposition: form-data; name=\"uploadedfile\"; filename=\"" + "anxa.jpg" + "\"" + lineend);
            outputStream.writeBytes("Content-Type: image/png" + lineend);
            outputStream.writeBytes(lineend);

            bytesAvailable = fileInputStream.available();
            bufferSize = Math.min(bytesAvailable, maxBufferSize);
            buffer = new byte[bufferSize];

            // Read file
            bytesRead = fileInputStream.read(buffer, 0, bufferSize);
            while (bytesRead > 0) {
                outputStream.write(buffer, 0, bufferSize);
                bytesAvailable = fileInputStream.available();
                bufferSize = Math.min(bytesAvailable, maxBufferSize);
                bytesRead = fileInputStream.read(buffer, 0, bufferSize);
            }

            outputStream.writeBytes(lineend);
            outputStream.writeBytes(twohypens);
            outputStream.writeBytes(boundary);
            outputStream.writeBytes(twohypens);
            outputStream.writeBytes(lineend);


            // Responses from the server (code and message)


            serverResponseCode = connection.getResponseCode();
            serverResponseMessage = connection.getResponseMessage();

            System.out.println("debugging multipost: code " + serverResponseCode + " :" + serverResponseMessage);

            if (serverResponseCode == 200) {
                InputStream stream = connection.getInputStream();
                InputStreamReader isReader = new InputStreamReader(stream);
                //put output stream into a string
                BufferedReader br = new BufferedReader(isReader);
                String line;
                while ((line = br.readLine()) != null) {
                    resultStr += line;
                }
                br.close();
                fileInputStream.close();
                outputStream.flush();
                outputStream.close();
            }

        } catch (Exception ex) {
            resultStr = OFFLINE_STR;
            serverResponseCode = 404;
        }

        System.out.println("debugging multipost: " + resultStr.length());
        String resultStr2 = resultStr.substring(4, resultStr.length());
        String isNull = resultStr.substring(0, 4);

        if (isNull.equalsIgnoreCase("null")) {
            resultStr = resultStr2;
        }
        response = new serverResponseObj(serverResponseCode, serverResponseMessage, resultStr);

        return response;
    }

    /*for GET method*/
    private String formQueryString() {
        try {
            String combinedParams = "";
            if (!params.isEmpty()) {
                // combinedParams += "?";
                for (NameValuePair p : params) {
                    //  String paramString = p.getName() + "=" + URLEncoder.encode(p.getValue(), "UTF-8");
                    String paramString = p.getName() + "=" + p.getValue();

                    if (combinedParams.length() > 1) {
                        combinedParams += "&" + paramString;
                    } else {
                        combinedParams += paramString;
                    }
                }
            }
            return combinedParams;
        } catch (Exception e) {
            httpClient = null;
            return "";
        }
    }

    private void processBitmapResponse(serverResponseObj obj, String ID) throws Exception {
    
		/* Parse the response BitmapMultipOst Message */
        ((JsonDefaultResponseHandler) handler).setResponseObj(obj.serverResponseCode);
        if (ID != null)
            ((JsonDefaultResponseHandler) handler).start(obj.resultStr, ID);
        else
            ((JsonDefaultResponseHandler) handler).start(obj.resultStr);
    }

    private void processBitmapResponse(HttpResponse response) throws Exception {
        int statusCode = response.getStatusLine().getStatusCode();
        String statusMessage = response.getStatusLine().getReasonPhrase();
        HttpEntity entity = response.getEntity();

        BufferedReader br = new BufferedReader(new InputStreamReader(entity.getContent()));
        String line, result = "";
        while ((line = br.readLine()) != null)
            result += line;

        if (handler != null) {
            Message message = Message.obtain(handler, REQUEST_SUCCESS, new serverResponseObj(statusCode, statusMessage, result));
            handler.sendMessage(message);
        }
    }

    private void processBitmapResponse(HttpEntity entity) {

        BufferedHttpEntity bufHttpEntity;
        try {

            bufHttpEntity = new BufferedHttpEntity(entity);

            BitmapFactory.Options opts = new BitmapFactory.Options();
            opts.inDither = false;                     //Disable Dithering mode
            opts.inPurgeable = true;                   //Tell to gc that whether it needs free memory, the Bitmap can be cleared
            opts.inInputShareable = true;              //Which kind of reference will be used to recover the Bitmap data after being clear, when it will be used in the future
            opts.inSampleSize = 2;

            try {

                ImageManager.getInstance().addImage(photoObj.photoid, BitmapFactory.decodeStream(bufHttpEntity.getContent(), null, opts));
            } catch (java.lang.OutOfMemoryError e) {
                opts.inSampleSize = 2 * 2;
                bufHttpEntity = null;
                entity = null;
                // handler.sendMessage(Message.obtain(handler, REQUEST_ERROR, ""));
            }

        } catch (Exception e) {
        }
        bufHttpEntity = null;
        entity = null;
        System.gc();

        handler.sendMessage(Message.obtain(handler, REQUEST_SUCCESS, ""));


    }

    private void processEntity(HttpResponse response) throws Exception {
        int statusCode = response.getStatusLine().getStatusCode();

        String statusMessage = response.getStatusLine().getReasonPhrase();
        HttpEntity entity = response.getEntity();

        BufferedReader br = new BufferedReader(new InputStreamReader(entity.getContent()));
        String line, result = "";
        while ((line = br.readLine()) != null)
            result += line;

        if (handler != null) {
            Message message = Message.obtain(handler, REQUEST_SUCCESS, result);
            handler.sendMessage(message);
        }


        if (handler != null) {
            ((JsonDefaultResponseHandler) handler).setResultMessage(result);
        }
    }

    private void processJsonResponse(serverResponseObj obj, String ID) {

        ((JsonDefaultResponseHandler) handler).setResponseObj(obj.serverResponseCode);
        ((JsonDefaultResponseHandler) handler).start(obj.resultStr);
    }

    private void processJsonResponse(HttpResponse response) throws Exception {

        String line, result = "";

        int statusCode = response.getStatusLine().getStatusCode();
        String statusMessage = response.getStatusLine().getReasonPhrase();

        HttpEntity entity = response.getEntity();
        BufferedReader br = new BufferedReader(new InputStreamReader(entity.getContent(), HTTP.UTF_8));

        while ((line = br.readLine()) != null)
            result += line;
        
        /* Parse the response Json Message */

        serverResponseObj obj = new serverResponseObj(statusCode, statusMessage, result);
        ((JsonDefaultResponseHandler) handler).setResponseObj(obj.serverResponseCode);
        ((JsonDefaultResponseHandler) handler).start(obj.resultStr);
    }


    public void getBitmap(String url, String clientid, String photoid) {
        photoObj = new PhotoDownloadObj();
        photoObj.clientid = clientid;
        photoObj.photoid = photoid;
        bitmap(url);
    }

    public void forgotServices(String username, String data, Handler responseHandler) {
    }


    public void getSync(String userid, String from, String to, Handler responseHandler) {
        getSync(userid, from, to, responseHandler, SERVICES.GET_SYNC);
    }

    public void getSync(String userid, String from, String to, Handler responseHandler, SERVICES service) {
    }

    public void updateUserProfile(String userid, String data, Handler responseHandler) {
    }

    public void loginServices(String username, String password, String data, Handler responseHandler) {
    }

    public String createSignature(String input) {

        String signature = "";

        String hashInput = input + ApplicationEx.sharedKey;
        try {
            signature = AppUtil.SHA1(hashInput);
        } catch (NoSuchAlgorithmException e) {
        } catch (UnsupportedEncodingException e) {
        }
        return signature;
    }

    public String createSignature(String input, String sharedKey) {
        String signature = "";
        String hashInput = input + sharedKey;

        try {
            signature = AppUtil.SHA1(hashInput);
        } catch (NoSuchAlgorithmException e) {
        } catch (UnsupportedEncodingException e) {
        }
        return signature;
    }

    File saveImage(Bitmap myBitmap, Context context) {


        File myDir = new File(Environment.getExternalStorageDirectory(), context.getPackageName());


        if (!myDir.exists()) {
            myDir.mkdir();
        }

        UUID uniqueKey = UUID.randomUUID();

        String fname = uniqueKey.toString() + "UploadedImage.png";
        File file = new File(myDir, fname);
        if (file.exists()) file.delete();
        try {
            FileOutputStream out = new FileOutputStream(file);
            myBitmap.compress(Bitmap.CompressFormat.JPEG, 60, out);
            out.flush();
            out.close();

        } catch (Exception e) {
            e.printStackTrace();

        }

        return file;
    }


    public class serverResponseObj {

        public int serverResponseCode = 0;
        public String serverResponseMessage = "";
        public String resultStr = "";

        public serverResponseObj() {
            ;

        }

        public serverResponseObj(int serverResponseCode, String serverResponseMessage, String resultStr) {
            this.serverResponseMessage = serverResponseMessage;
            this.resultStr = resultStr;
            this.serverResponseCode = serverResponseCode;
        }
    }

    private SSLSocketFactory newSslSocketFactory() {
        try {
            // Get an instance of the Bouncy Castle KeyStore format
            //KeyStore trusted = KeyStore.getInstance("BKS");
            KeyStore trustStore = KeyStore.getInstance(KeyStore.getDefaultType());
            trustStore.load(null, null);


            // Get the raw resource, which contains the keystore with
            // your trusted certificates (root and any intermediate certs)
            // Pass the keystore to the SSLSocketFactory. The factory is responsible
            // for the verification of the server certificate.

            SSLSocketFactory sf = new SSLSocketFactory(trustStore);
            // Hostname verification from certificate
            //allow all host
            sf.setHostnameVerifier(SSLSocketFactory.ALLOW_ALL_HOSTNAME_VERIFIER);

            return sf;
        } catch (Exception e) {
            throw new AssertionError(e);
        }
    }


}
