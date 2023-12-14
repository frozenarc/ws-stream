package org.frozenarc.wsstream;

import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.net.HttpURLConnection;

/*
 * Author: mpanchal
 * Date: 14-12-2023
 */
public class Connection {

    private final HttpURLConnection connection;
    private final OutputStream reqOutputStream;
    private InputStream resInputStream;
    private InputStream errorInputStream;
    private Integer resCode;

    public Connection(HttpURLConnection connection, OutputStream reqOutputStream) {
        this.connection = connection;
        this.reqOutputStream = reqOutputStream;
    }

    public OutputStream getReqOutputStream() {
        return reqOutputStream;
    }

    public InputStream getResInputStream() {
        return resInputStream;
    }

    public InputStream getErrorInputStream() {
        return errorInputStream;
    }

    public Integer getResCode() {
        return resCode;
    }

    public Connection send(RequestWriter writer) throws WSStreamException {
        writer.write(reqOutputStream);
        return this;
    }

    public Connection receive() throws WSStreamException {
        try {
            resCode = connection.getResponseCode();
            if (resCode < 400) {
                resInputStream = connection.getInputStream();
            } else {
                errorInputStream = connection.getErrorStream();
            }
            return this;
        } catch (IOException ex) {
            throw new WSStreamException(ex);
        }
    }

    public void close() throws WSStreamException {
        try {
            if (reqOutputStream != null) {
                reqOutputStream.close();
            }
            if (resInputStream != null) {
                resInputStream.close();
            }
            if (errorInputStream != null) {
                errorInputStream.close();
            }
            if (connection != null) {
                connection.disconnect();
            }
        } catch (IOException ex) {
            throw new WSStreamException(ex);
        }
    }
}
