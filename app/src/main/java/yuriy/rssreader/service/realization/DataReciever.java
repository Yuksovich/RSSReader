package yuriy.rssreader.service.realization;


import org.xmlpull.v1.XmlPullParserException;

import java.io.BufferedInputStream;
import java.io.IOException;
import java.net.HttpURLConnection;
import java.net.URL;
import java.nio.charset.Charset;
import java.util.ArrayList;


public final class DataReciever {


    private static final int CONNECT_TIMEOUT = 5000;
    private static final int READ_TIMEOUT = 5000;

    public String getTextFromURL(final URL url) throws IOException, XmlPullParserException {

        final Integer[] recievedData = getData(url);
        final Charset charset = getCharset(recievedData);
        final byte[] outputReadyData = getByteArrayFromRecivedData(recievedData);
        return new String(outputReadyData, charset);

    }

    private Integer[] getData(final URL url) throws IOException {
        HttpURLConnection connection = null;
        BufferedInputStream bufferedInputStream = null;
        ArrayList<Integer> byteData;
        try {
            connection = (HttpURLConnection) url.openConnection();
            connection.setConnectTimeout(CONNECT_TIMEOUT);
            connection.setReadTimeout(READ_TIMEOUT);
            connection.connect();


            byteData = new ArrayList<>();
            bufferedInputStream = new BufferedInputStream(connection.getInputStream());
            int readByte;

            while ((readByte = bufferedInputStream.read()) != -1) {
                byteData.add(readByte);
            }
        } finally {

            if (connection != null) {
                connection.disconnect();
            }
            if (bufferedInputStream != null) {
                bufferedInputStream.close();
            }
        }


        return byteData.toArray(new Integer[byteData.size()]);
    }

    private Charset getCharset(final Integer[] recievedByteData) {
        final String ENCODING_ATTRIBUTE = "encoding";
        final String START_XML_TAG = "<?xml";

        String data = "";

        for (int currentByte : recievedByteData) {
            if ((char) currentByte == '>') {
                data += (char) currentByte;
                break;
            } else {
                data += (char) currentByte;
            }
        }

        if (data.contains(ENCODING_ATTRIBUTE) && data.contains(START_XML_TAG)) {
            int index = data.indexOf(ENCODING_ATTRIBUTE);
            StringBuilder encoding = new StringBuilder();

            while (data.charAt(++index) != '>') {

                if (data.charAt(index) == '"') {
                    while (data.charAt(++index) != '"') {
                        encoding.append(data.charAt(index));
                    }

                }
            }
            return Charset.forName(encoding.toString());

        } else {
            return Charset.defaultCharset();
        }
    }

    private byte[] getByteArrayFromRecivedData(final Integer[] recievedData) {
        byte[] outputBytes = new byte[recievedData.length];

        for (int i = 0; i < recievedData.length; i++) {
            outputBytes[i] = recievedData[i].byteValue();
        }
        return outputBytes;
    }

}