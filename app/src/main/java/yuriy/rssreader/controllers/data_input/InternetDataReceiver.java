package yuriy.rssreader.controllers.data_input;


import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.net.HttpURLConnection;
import java.net.MalformedURLException;
import java.net.URL;
import java.nio.charset.Charset;
import java.nio.charset.IllegalCharsetNameException;


public final class InternetDataReceiver {

    private static final int CONNECT_TIMEOUT = 5000;
    private static final int READ_TIMEOUT = 5000;
    private static final String ENCODING_ATTRIBUTE = "charset=";
    private static final char SEMICOLON = ';';

    public String getTextFromURL(final URL url) throws IOException {
        if (url == null) {
            throw new MalformedURLException();
        }
        return getData(url);
    }

    private String getData(final URL url) throws IOException {
        final StringBuilder data = new StringBuilder();
        HttpURLConnection connection = null;
        BufferedReader bufferedReader = null;

        try {
            connection = (HttpURLConnection) url.openConnection();
            connection.setConnectTimeout(CONNECT_TIMEOUT);
            connection.setReadTimeout(READ_TIMEOUT);
            connection.connect();
            Charset charset;

            final  String contentType = connection.getContentType();
            try {
                charset = getCharset(contentType);
            }catch (IllegalCharsetNameException e){
                charset = Charset.defaultCharset();
            }

            bufferedReader = new BufferedReader(new InputStreamReader(connection.getInputStream(), charset));

            String line;
            while ((line = bufferedReader.readLine()) != null) {
                data.append(line);
            }


        } finally {

            if (connection != null) {
                connection.disconnect();
            }
            if (bufferedReader != null) {
                bufferedReader.close();
            }
        }

        return data.toString();
    }

    private Charset getCharset(final String contentType) {

        if (contentType.contains(ENCODING_ATTRIBUTE)) {
            int index = contentType.indexOf(ENCODING_ATTRIBUTE);
            StringBuilder encoding = new StringBuilder();

            for(index+=ENCODING_ATTRIBUTE.length();index<contentType.length();index++){
                if(contentType.charAt(index)==SEMICOLON){
                    break;
                }
                encoding.append(contentType.charAt(index));
            }

            return Charset.forName(encoding.toString());

        } else {

            return Charset.defaultCharset();
        }
    }
}