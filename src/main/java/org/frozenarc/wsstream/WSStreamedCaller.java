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

    public Connection prepare() throws WSStreamException {
        HttpURLConnection con;
        OutputStream outputStream = null;
        InputStream responseStream = null;
        InputStream errorStream = null;
        try {
            URL url = new URL(this.url);
            con = (HttpURLConnection) url.openConnection();
            configurator.configure(con);
            con.connect();
            if (con.getDoOutput()) {
                outputStream = con.getOutputStream();
            }
            return new Connection(con, outputStream);
        } catch (Exception ex) {
            throw new WSStreamException("Exception while calling api", ex);
        }
    }

    public void call() throws WSStreamException {
        HttpURLConnection con = null;
        OutputStream outputStream = null;
        InputStream responseStream = null;
        InputStream errorStream = null;
        try {
            URL url = new URL(this.url);
            con = (HttpURLConnection) url.openConnection();
            configurator.configure(con);
            con.connect();
            if (con.getDoOutput()) {
                outputStream = con.getOutputStream();
                requestWriter.write(outputStream);
                outputStream.close();
            }
            int resCode = con.getResponseCode();
            if (resCode < 400) {
                responseStream = con.getInputStream();
            } else {
                errorStream = con.getErrorStream();
            }
            responseReader.read(resCode, responseStream, errorStream);

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
                if (con != null) {
                    con.disconnect();
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

        public Builder url(String url) throws WSStreamException {
            if (!url.startsWith("http")) {
                throw new WSStreamException("URL doesn't start with `http` protocol");
            }
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
