package jsonparser;

import android.util.Log;

import org.apache.http.client.ClientProtocolException;
import org.apache.http.conn.ConnectTimeoutException;
import org.apache.http.util.ByteArrayBuffer;
import org.w3c.dom.Document;
import org.w3c.dom.Element;
import org.w3c.dom.Node;
import org.w3c.dom.NodeList;
import org.xml.sax.InputSource;
import org.xml.sax.SAXException;

import java.io.BufferedInputStream;
import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.OutputStreamWriter;
import java.io.Reader;
import java.io.StringReader;
import java.io.StringWriter;
import java.io.UnsupportedEncodingException;
import java.io.Writer;
import java.net.HttpURLConnection;
import java.net.MalformedURLException;
import java.net.SocketTimeoutException;
import java.net.URL;
import java.net.URLConnection;

import javax.xml.parsers.DocumentBuilder;
import javax.xml.parsers.DocumentBuilderFactory;
import javax.xml.parsers.ParserConfigurationException;

public class JSONParser {


    public String thePostRequest(String url, String postData) {
        // url = URLEncoder.encode(url);
        String myString = null;
        InputStream myInputStream = null;
        StringBuilder sb = new StringBuilder();
        sb.append(postData);
        URL url2;
        System.out.println("WWWWW--------   " + url + "  " + postData);
        try {
            url2 = new URL(url);
            HttpURLConnection conn = (HttpURLConnection) url2.openConnection();
            conn.setConnectTimeout(20 * 1000);
            conn.setReadTimeout(20 * 1000);
            conn.setDoOutput(true);
            conn.setRequestMethod("POST");
            OutputStreamWriter wr = new OutputStreamWriter(
                    conn.getOutputStream());
            // this is were we're adding post data to the request
            wr.write(sb.toString());
            wr.flush();
            myInputStream = conn.getInputStream();
            BufferedInputStream bis = new BufferedInputStream(myInputStream);
            ByteArrayBuffer baf = new ByteArrayBuffer(100);
            int current = 0;
            while ((current = bis.read()) != -1) {
                baf.append((byte) current);
            }
            myString = new String(baf.toByteArray());
            wr.close();
            conn.disconnect();

        } catch (ConnectTimeoutException e) {
//			Log.e("Asim Timeout Exception: ", e.toString());
            myString = "Error: " + e.toString();
        } catch (SocketTimeoutException ste) {
//			Log.e("Asim Timeout Exception: ", ste.toString());
            myString = "Error: " + ste.toString();
        } catch (UnsupportedEncodingException e) {
//			e.printStackTrace();
            myString = "Error: " + e.toString();
        } catch (ClientProtocolException e) {
//			e.printStackTrace();
            myString = "Error: " + e.toString();
        } catch (IOException e) {
//			e.printStackTrace();
            myString = "Error: " + e.toString();
        }
        return myString;
    }

    // ----------------------------------------------------------------------------

    public String theGetRequest(String url) {
        String contents = "";

        try {
            URLConnection conn = new URL(url).openConnection();
            conn.setConnectTimeout(30000);
            InputStream in = conn.getInputStream();
            contents = convertStreamToStringStyle1(in);
        } catch (MalformedURLException e) {
            Log.v("MALFORMED URL EXCEPTION", e.toString());
        } catch (IOException e) {
            Log.e(e.getMessage(), e.toString());
        }

        return contents;
    }

    private String convertStreamToStringStyle1(InputStream ins)
            throws UnsupportedEncodingException {

        BufferedReader reader = new BufferedReader(new InputStreamReader(ins,
                "UTF-8"));
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
                ins.close();
            } catch (IOException e) {
                e.printStackTrace();
            }
        }
        return sb.toString();
    }

    public static String convertStreamToStringStyle2(InputStream inputStream)
            throws IOException {
        if (inputStream != null) {
            Writer writer = new StringWriter();

            char[] buffer = new char[1024];
            try {
                Reader reader = new BufferedReader(new InputStreamReader(
                        inputStream, "UTF-8"), 1024);
                int n;
                while ((n = reader.read(buffer)) != -1) {
                    writer.write(buffer, 0, n);
                }
            } finally {
                inputStream.close();
            }
            return writer.toString();
        } else {
            return "";
        }

    }


    public Document getDomElement(String xml) {
        Document doc = null;
        DocumentBuilderFactory dbf = DocumentBuilderFactory.newInstance();
        try {

            DocumentBuilder db = dbf.newDocumentBuilder();

            InputSource is = new InputSource();
            is.setCharacterStream(new StringReader(xml));
            doc = db.parse(is);

        } catch (ParserConfigurationException e) {
            Log.e("Error: ", e.getMessage());
            e.printStackTrace();
            return null;
        } catch (SAXException e) {
            Log.e("Error: ", e.getMessage());
            e.printStackTrace();
            return null;
        } catch (IOException e) {
            Log.e("Error: ", e.getMessage());
            e.printStackTrace();
            return null;
        }

        return doc;
    }

    public final String getElementValue(Node elem) {
        Node child;
        if (elem != null) {
            if (elem.hasChildNodes()) {
                for (child = elem.getFirstChild(); child != null; child = child
                        .getNextSibling()) {
                    if (child.getNodeType() == Node.TEXT_NODE) {
                        return child.getNodeValue();
                    }
                }
            }
        }
        return "";
    }

    public String getValue(Element item, String str) {
        NodeList n = item.getElementsByTagName(str);
        return this.getElementValue(n.item(0));
    }

}
