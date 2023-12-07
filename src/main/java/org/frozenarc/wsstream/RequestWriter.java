package org.frozenarc.wsstream;

import java.io.OutputStream;

/*
 * Author: mpanchal
 * Date: 31-01-2022
 */
public interface RequestWriter {

    void write(OutputStream requestStream) throws WSStreamException;
}
