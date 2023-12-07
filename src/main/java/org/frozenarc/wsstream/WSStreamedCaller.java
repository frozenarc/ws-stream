package org.frozenarc.wsstream;

import java.io.InputStream;
import java.io.OutputStream;
import java.net.HttpURLConnection;
import java.net.URL;
import java.net.URLConnection;

/*
 * Author: mpanchal
 * Date: 02-02-2022
 */
@SuppressWarnings("unused")
public class WSStreamedCaller {

    private final String url;
    private final URLConnConfigurator configurator;

    private final RequestWriter requestWriter;

    private final ResponseReader responseReader;

    private WSStreamedCaller(String url,
                             URLConnConfigurator configurator,
                             RequestWriter requestWriter,
                             ResponseReader responseReader) {
        this.url = url;
        this.configurator = configurator;
        this.requestWriter = requestWriter;
        this.responseReader = responseReader;
    }

    public static Builder builder() {
        return new Builder();
    }

    public void call() throws WSStreamException {
        URLConnection con = null;
        OutputStream outputStream = null;
        InputStream responseStream = null;
        InputStream errorStream = null;
        try {
            URL url = new URL(this.url);
            con = url.openConnection();
            configurator.configure(con);
            con.connect();
            if (con.getDoOutput()) {
                outputStream = con.getOutputStream();
                requestWriter.write(outputStream);
                outputStream.close();
            }
            if (con instanceof HttpURLConnection) {
                HttpURLConnection httpCon = ((HttpURLConnection) con);
                int resCode = ((HttpURLConnection) con).getResponseCode();
                if (resCode < 400) {
                    responseStream = httpCon.getInputStream();
                } else {
                    errorStream = httpCon.getErrorStream();
                }
                responseReader.read(resCode, responseStream, errorStream);
            } else {
                throw new RuntimeException("Other than http protocol isn't handled");
            }
        } catch (Exception ex) {
            throw new WSStreamException("Exception while calling api", ex);
        } finally {
            try {
                if (outputStream != null) {
                    outputStream.close();
                }
                if (responseStream != null) {
                    responseStream.close();
                }
                if (errorStream != null) {
                    errorStream.close();
                }
                if (con instanceof HttpURLConnection) {
                    ((HttpURLConnection) con).disconnect();
                }
            } catch (Exception ex) {
                ex.printStackTrace();
            }
        }
    }

    public static class Builder {
        private String url;
        private URLConnConfigurator configurator;
        private RequestWriter requestWriter;
        private ResponseReader responseReader;

        public Builder url(String url) {
            this.url = url;
            return this;
        }

        public Builder conConfigurator(URLConnConfigurator configurator) {
            this.configurator = configurator;
            return this;
        }

        public Builder req(RequestWriter requestWriter) {
            this.requestWriter = requestWriter;
            return this;
        }

        public Builder res(ResponseReader responseReader) {
            this.responseReader = responseReader;
            return this;
        }

        public WSStreamedCaller build() {
            return new WSStreamedCaller(url, configurator, requestWriter, responseReader);
        }
    }
}
