package betaAPI.demo;

import java.io.InputStream;
import java.io.Serializable;

import org.apache.commons.httpclient.Header;

public class HttpResponseBean implements Serializable {
    private static final long serialVersionUID = -3618237066933086982L;
    private int statusCode;
    private Header contentLength;
    private Header contentEncoding;
    private InputStream inputStream;
    private Header contentDisposition;

    public Header getContentLength() {
        return contentLength;
    }

    public void setContentLength(Header contentLength) {
        this.contentLength = contentLength;
    }

    public InputStream getInputStream() {
        return inputStream;
    }

    public void setInputStream(InputStream inputStream) {
        this.inputStream = inputStream;
    }

    public Header getContentEncoding() {
        return contentEncoding;
    }

    public void setContentEncoding(Header contentEncoding) {
        this.contentEncoding = contentEncoding;
    }

    public int getStatusCode() {
        return statusCode;
    }

    public void setStatusCode(int statusCode) {
        this.statusCode = statusCode;
    }

    public Header getContentDisposition() {
        return contentDisposition;
    }

    public void setContentDisposition(Header contentDisposition) {
        this.contentDisposition = contentDisposition;
    }
}
