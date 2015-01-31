/* 
 * Copyright (C) 2015 Bruce Beisel
 *
 * This program is free software: you can redistribute it and/or modify
 * it under the terms of the GNU General Public License as published by
 * the Free Software Foundation, either version 3 of the License, or
 * (at your option) any later version.
 *
 * This program is distributed in the hope that it will be useful,
 * but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
 * GNU General Public License for more details.
 *
 * You should have received a copy of the GNU General Public License
 * along with this program.  If not, see <http://www.gnu.org/licenses/>.
 */
package com.bdb.weather.collector.socket;

import java.io.IOException;
import java.io.InputStream;
import java.net.Socket;
import java.net.SocketTimeoutException;
import java.nio.ByteBuffer;
import java.nio.charset.Charset;
import java.util.ArrayList;
import java.util.List;
import java.util.logging.Level;
import java.util.logging.Logger;

/**
 * Reads a socket stream and make callbacks to determine tokens and to process said tokens.
 *
 * @author Bruce
 */
public class SocketReader {
    private final Socket socket;
    private final SocketDataProcessor processor;
    private final InputStream inputStream;
    private final byte[] buffer = new byte[16384];
    private final StringBuilder stringBuilder = new StringBuilder();
    private final Charset charset = Charset.forName("us-ascii");
    private static final Logger logger = Logger.getLogger(SocketReader.class.getName());
    
    /**
     * Constructor.
     * 
     * @param socket The socket that is to be read
     * @param processor The client that will parse and process the tokens
     * 
     * @throws IllegalArgumentException socket or processor is null
     * @throws IOException The input stream could not be retrieved from the socket
     */
    public SocketReader(Socket socket, SocketDataProcessor processor) throws IllegalArgumentException, IOException {
        if (socket == null || processor == null)
            throw new IllegalArgumentException("NULL arguments not allowed");

        this.socket = socket;
        this.processor = processor;
        inputStream = socket.getInputStream();
    }

    /**
     * Read data from the socket.
     * 
     * @return false if an error was detected, the caller should close the socket
     */
    public boolean readSocket() {
        try {
            logger.finer("Reading available data");
            List<String> messages = new ArrayList<>(100);

            int nbytes = inputStream.read(buffer);
            if (nbytes < 0)
                return false;
            else if (nbytes == 0)
                return true;

            String readString = new String(buffer, 0, nbytes);
            logger.finer(String.format("Read %d bytes: '%s'", nbytes, readString));
            stringBuilder.append(readString);
            int endToken;
            //
            // Loop until the buffer is empty or there is not a complete token left in the buffer
            //
            do {
                endToken = processor.findToken(stringBuilder.toString());

                if (endToken != SocketDataProcessor.NO_TOKEN_FOUND) {
                    if (endToken >= stringBuilder.length()) {
                        logger.warning("Socket processor indicated that the end of the token was beyond the length of the input buffer");
                        endToken = SocketDataProcessor.NO_TOKEN_FOUND;
                    }
                    else {
                        logger.finer("Token ends at index " + endToken + ". Character = '" + stringBuilder.charAt(endToken) + "'");
                        //
                        // StringBuilder functions operate exclusively so we must say the end is the character after the token
                        // separator
                        //
                        endToken++;
                        String token = stringBuilder.substring(0, endToken - 1);
                        stringBuilder.delete(0, endToken);
                        messages.add(token);
                    }
                }
            } while (endToken != SocketDataProcessor.NO_TOKEN_FOUND);

            if (!messages.isEmpty()) {
                String response = processor.consumeMessages(messages);
                if (response != null) {
                    logger.finer(String.format("Responding with '%s'", response));
                    ByteBuffer responseBuffer = charset.encode(response);
                    socket.getOutputStream().write(responseBuffer.array());
                }
            }

            return true;
        }
        catch (SocketTimeoutException e2) {
            // this exception is OK
            return true;
        }
        catch (IOException e) {
            logger.log(Level.WARNING, "Caught IOException");
            return false;
        }
    }
}