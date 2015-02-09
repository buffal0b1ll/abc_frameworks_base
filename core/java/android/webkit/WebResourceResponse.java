/*
 * Copyright (C) 2010 The Android Open Source Project
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *      http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */

package android.webkit;

import java.io.InputStream;
import java.io.StringBufferInputStream;
import java.util.Map;

/**
 * Encapsulates a resource response. Applications can return an instance of this
 * class from {@link WebViewClient#shouldInterceptRequest} to provide a custom
 * response when the WebView requests a particular resource.
 */
public class WebResourceResponse extends WebResourceResponseBase {
    private String mMimeType;
    private String mEncoding;
    private int mStatusCode;
    private String mReasonPhrase;
    private Map<String, String> mResponseHeaders;
    private InputStream mInputStream;

    /**
     * Constructs a resource response with the given MIME type, encoding, and
     * input stream. Callers must implement
     * {@link InputStream#read(byte[]) InputStream.read(byte[])} for the input
     * stream.
     *
     * @param mimeType the resource response's MIME type, for example text/html
     * @param encoding the resource response's encoding
     * @param data the input stream that provides the resource response's data. Must not be a
     *             StringBufferInputStream.
     */
    public WebResourceResponse(String mimeType, String encoding,
            InputStream data) {
        mMimeType = mimeType;
        mEncoding = encoding;
        setData(data);
    }

    /**
     * Constructs a resource response with the given parameters. Callers must
     * implement {@link InputStream#read(byte[]) InputStream.read(byte[])} for
     * the input stream.
     *
     * @param mimeType the resource response's MIME type, for example text/html
     * @param encoding the resource response's encoding
     * @param statusCode the status code needs to be in the ranges [100, 299], [400, 599].
     *                   Causing a redirect by specifying a 3xx code is not supported.
     * @param reasonPhrase the phrase describing the status code, for example "OK". Must be non-null
     *                     and not empty.
     * @param responseHeaders the resource response's headers represented as a mapping of header
     *                        name -> header value.
     * @param data the input stream that provides the resource response's data. Must not be a
     *             StringBufferInputStream.
     */
    public WebResourceResponse(String mimeType, String encoding, int statusCode,
            String reasonPhrase, Map<String, String> responseHeaders, InputStream data) {
        this(mimeType, encoding, data);
        setStatusCodeAndReasonPhrase(statusCode, reasonPhrase);
        setResponseHeaders(responseHeaders);
    }

    /**
     * Sets the resource response's MIME type, for example &quot;text/html&quot;.
     *
     * @param mimeType The resource response's MIME type
     */
    public void setMimeType(String mimeType) {
        mMimeType = mimeType;
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public String getMimeType() {
        return mMimeType;
    }

    /**
     * Sets the resource response's encoding, for example &quot;UTF-8&quot;. This is used
     * to decode the data from the input stream.
     *
     * @param encoding The resource response's encoding
     */
    public void setEncoding(String encoding) {
        mEncoding = encoding;
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public String getEncoding() {
        return mEncoding;
    }

    /**
     * Sets the resource response's status code and reason phrase.
     *
     * @param statusCode the status code needs to be in the ranges [100, 299], [400, 599].
     *                   Causing a redirect by specifying a 3xx code is not supported.
     * @param reasonPhrase the phrase describing the status code, for example "OK". Must be non-null
     *                     and not empty.
     */
    public void setStatusCodeAndReasonPhrase(int statusCode, String reasonPhrase) {
        if (statusCode < 100)
            throw new IllegalArgumentException("statusCode can't be less than 100.");
        if (statusCode > 599)
            throw new IllegalArgumentException("statusCode can't be greater than 599.");
        if (statusCode > 299 && statusCode < 400)
            throw new IllegalArgumentException("statusCode can't be in the [300, 399] range.");
        if (reasonPhrase == null)
            throw new IllegalArgumentException("reasonPhrase can't be null.");
        if (reasonPhrase.trim().isEmpty())
            throw new IllegalArgumentException("reasonPhrase can't be empty.");
        for (int i = 0; i < reasonPhrase.length(); i++) {
            int c = reasonPhrase.charAt(i);
            if (c > 0x7F) {
                throw new IllegalArgumentException(
                        "reasonPhrase can't contain non-ASCII characters.");
            }
        }
        mStatusCode = statusCode;
        mReasonPhrase = reasonPhrase;
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public int getStatusCode() {
        return mStatusCode;
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public String getReasonPhrase() {
        return mReasonPhrase;
    }

    /**
     * Sets the headers for the resource response.
     *
     * @param headers Mapping of header name -> header value.
     */
    public void setResponseHeaders(Map<String, String> headers) {
        mResponseHeaders = headers;
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public Map<String, String> getResponseHeaders() {
        return mResponseHeaders;
    }

    /**
     * Sets the input stream that provides the resource response's data. Callers
     * must implement {@link InputStream#read(byte[]) InputStream.read(byte[])}.
     *
     * @param data the input stream that provides the resource response's data. Must not be a
     *             StringBufferInputStream.
     */
    public void setData(InputStream data) {
        // If data is (or is a subclass of) StringBufferInputStream
        if (data != null && StringBufferInputStream.class.isAssignableFrom(data.getClass())) {
            throw new IllegalArgumentException("StringBufferInputStream is deprecated and must " +
                "not be passed to a WebResourceResponse");
        }
        mInputStream = data;
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public InputStream getData() {
        return mInputStream;
    }
}
