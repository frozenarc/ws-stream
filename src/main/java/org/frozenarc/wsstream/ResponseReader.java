package org.frozenarc.wsstream;

import java.io.InputStream;

/*
 * Author: mpanchal
 * Date: 31-01-2022
 */
public interface ResponseReader {

    void read(int resCode, InputStream responseStream, InputStream errorStream) throws WSStreamException;
}
