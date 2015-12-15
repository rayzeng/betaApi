package betaAPI.demo;

import java.io.BufferedInputStream;
import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.io.File;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;
import java.util.Map;
import java.util.zip.GZIPInputStream;

import org.apache.commons.httpclient.DefaultHttpMethodRetryHandler;
import org.apache.commons.httpclient.Header;
import org.apache.commons.httpclient.HttpClient;
import org.apache.commons.httpclient.HttpMethod;
import org.apache.commons.httpclient.HttpStatus;
import org.apache.commons.httpclient.NameValuePair;
import org.apache.commons.httpclient.methods.GetMethod;
import org.apache.commons.httpclient.methods.InputStreamRequestEntity;
import org.apache.commons.httpclient.methods.PostMethod;
import org.apache.commons.httpclient.methods.RequestEntity;
import org.apache.commons.httpclient.methods.multipart.FilePart;
import org.apache.commons.httpclient.methods.multipart.MultipartRequestEntity;
import org.apache.commons.httpclient.methods.multipart.Part;
import org.apache.commons.httpclient.methods.multipart.StringPart;
import org.apache.commons.httpclient.params.HttpMethodParams;
import org.apache.commons.httpclient.protocol.Protocol;
import org.apache.commons.lang.StringUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 * 
 * @author rayzeng
 * 
 */
public class NetUtil {
	private Logger logger = LoggerFactory.getLogger("");

	public static String POST_METHOD = "POST";
	public static String GET_METHOD = "GET";
	public static final String CODE_UTF8 = "UTF-8";

	// HTTP response code
	public final int HTTP_OK = 200;
	public final int HTTP_REDIRECT = 302;
	public final int HTTP_NOT_FOUND = 404;

	public InputStream getHttpConnection(String url, String methodType,
			NameValuePair[] params, String contentType, String postStreamStr) {
		if (url == null || methodType == null) {
			logger.warn("getHttpConnection end ! para is error!");
			return null;
		}
		logger.info("getHttpConnection start! url: " + url);
		HttpClient httpClient = new HttpClient();

		Protocol myhttps = new Protocol("https",
				new SSLProtocolSocketFactory(), 443);
		Protocol.registerProtocol("https", myhttps);

		HttpMethod method = null;
		InputStream input = null;
		if (POST_METHOD.equals(methodType)) {
			PostMethod postMethod = new PostMethod(url);
			if (params != null) {
				postMethod.setRequestBody(params);
			}
			if (StringUtils.isNotEmpty(postStreamStr)) {
				InputStream postStream = new ByteArrayInputStream(
						postStreamStr.getBytes());
				RequestEntity requestEntity = new InputStreamRequestEntity(
						postStream);
				postMethod.setRequestEntity(requestEntity);
			}
			method = postMethod;
		} else if (GET_METHOD.equals(methodType)) {
			GetMethod getMethod = new GetMethod(url);
			method = getMethod;
		} else {
			GetMethod getMethod = new GetMethod(url);
			method = getMethod;
		}
		if (params != null) {
			for (int i = 0; i < params.length; i++) {
				method.setRequestHeader(params[i].getName(),
						params[i].getValue());
			}
		}
		method.setRequestHeader("Content-Type", contentType);
		method.setRequestHeader("Accept-Encoding", "gzip");
		method.getParams().setParameter(HttpMethodParams.RETRY_HANDLER,
				new DefaultHttpMethodRetryHandler());
		InputStream in = null;
		ByteArrayOutputStream baos = null;
		try {
			method.setRequestHeader(
					"Cookie",
					"vc=vc-66bbb8f4-7d8c-44ed-8fab-4bb68fc66672; pgv_pvi=2366600192; ptui_loginuin=2531469653; RK=zQfnx2jMmW; p_luin=o0024818653; p_lskey=00040000842448938a4ae7547319ad71d883b00e2ba5451eb4ebd41209c7e1e44d184b6afdbcbf80a6da20fc; luin=o0024818653; lskey=000100000784e8ae7edd32e80435096501d42eac53bdb45084160c06b559440091e30b932681bf569d739f6d; ptcz=61459f4116df9a7680380992210c4ef572799d25b54cdf246a10ab26efaec4d3; pt2gguin=o0024818653; JSESSIONID=e1b13b4d-5a2c-4b7d-9936-38dfcd4131ca; pgv_si=s2486140928");
			int statusCode = httpClient.executeMethod(method);
			if (statusCode != HttpStatus.SC_OK) {
				logger.warn("getHttpConnection error! statusCode: "
						+ statusCode);
			} else {
				in = method.getResponseBodyAsStream();
				Header contentEncodingHeader = method
						.getResponseHeader("Content-Encoding");
				if (contentEncodingHeader != null) {
					String conType = contentEncodingHeader.getValue();
					if (conType != null && in != null) {
						if (conType.toLowerCase().indexOf("gzip") != -1) {
							in = new GZIPInputStream(in);
						}
					}
				}
				if (in != null) {
					baos = new ByteArrayOutputStream();
					byte[] byteChunk = new byte[1024 * 16];
					int len = -1;
					while ((len = in.read(byteChunk)) > -1) {
						baos.write(byteChunk, 0, len);
					}
					baos.flush();
					in.close();
					in = null;
					byte[] bytes = baos.toByteArray();
					input = new ByteArrayInputStream(bytes);
				}
				logger.info("getHttpConnection end! url:" + url);
			}
		} catch (Exception e) {
			logger.error("getHttpConnection error!", e);
		} finally {
			if (in != null) {
				try {
					in.close();
				} catch (IOException e) {
					logger.error("getHttpConnection error!", e);
				}
				in = null;
			}

			if (null != baos) {
				try {
					baos.close();
				} catch (IOException e) {
					e.printStackTrace();
				}
			}
			method.releaseConnection();
		}
		return input;
	}

	public void close(InputStream input) {
		if (null != input) {
			try {
				input.close();
			} catch (IOException e) {
				e.printStackTrace();
			}
		}
	}

	public void close(OutputStream output) {
		if (null != output) {
			try {
				output.close();
			} catch (IOException e) {
				e.printStackTrace();
			}
		}
	}

	public HttpResponseBean post(String url, NameValuePair[] params,
			Map<String, Object> postParams, File file)
			throws FileNotFoundException {
		if (url == null) {
			logger.warn("post end ! para is error!");
			return null;
		}
		logger.info("post start! url: " + url);
		HttpClient httpClient = new HttpClient();
		HttpMethod method = null;
		HttpResponseBean rspBean = new HttpResponseBean();

		Protocol myhttps = new Protocol("https",
				new SSLProtocolSocketFactory(), 443);
		Protocol.registerProtocol("https", myhttps);

		PostMethod postMethod = new PostMethod(url);
		List<Part> partlist = new ArrayList<Part>();
		if (postParams != null) {
			Iterator<String> it = postParams.keySet().iterator();
			while (it.hasNext()) {
				String key = it.next();
				StringPart paramPart = new StringPart(key, postParams.get(key)
						.toString(), "UTF-8");
				partlist.add(paramPart);
			}
		}
		if (file != null) {
			FilePart filePart = new FilePart("file", file);
			partlist.add(filePart);
		}
		Part[] parts = partlist.toArray(new Part[0]);

		RequestEntity requestEntity = new MultipartRequestEntity(parts,
				postMethod.getParams());
		postMethod.setRequestEntity(requestEntity);

		method = postMethod;

		if (params != null) {
			for (int i = 0; i < params.length; i++) {
				method.setRequestHeader(params[i].getName(),
						params[i].getValue());
			}
		}
		method.setRequestHeader("Accept-Encoding", "gzip");
		method.getParams().setParameter(HttpMethodParams.RETRY_HANDLER,
				new DefaultHttpMethodRetryHandler());
		InputStream in = null;
		ByteArrayOutputStream baos = null;
		try {
			int statusCode = httpClient.executeMethod(method);
			rspBean.setStatusCode(statusCode);
			in = method.getResponseBodyAsStream();

			Header contentLengthHeader = method
					.getResponseHeader("Content-Length");
			rspBean.setContentLength(contentLengthHeader);

			Header contentEncodingHeader = method
					.getResponseHeader("Content-Encoding");
			if (contentEncodingHeader != null) {
				String conType = contentEncodingHeader.getValue();
				if (conType != null && in != null) {
					if (conType.toLowerCase().indexOf("gzip") != -1) {
						in = new GZIPInputStream(in);
					}
				}
			}
			rspBean.setContentEncoding(contentEncodingHeader);

			if (in != null) {
				baos = new ByteArrayOutputStream();
				int c = -1;
				byte[] buf = new byte[1024 * 16];
				while (in != null && ((c = in.read(buf)) != -1)) {
					baos.write(buf, 0, c);
				}
				baos.flush();
				in.close();
				in = null;
				byte[] bytes = baos.toByteArray();
				rspBean.setInputStream(new ByteArrayInputStream(bytes));
			}
			logger.info("post end! url:" + url);
		} catch (Exception e) {
			logger.error("post error!", e);
		} finally {
			close(in);
			close(baos);

			if (method != null)
				method.releaseConnection();
		}
		return rspBean;
	}
	
	public String readBufferFromStream(InputStream input) {
        if (input == null)
            return null;
        String charSet = "UTF-8";
        BufferedInputStream buffer = new BufferedInputStream(input);
        ByteArrayOutputStream baos = null;
        String str = null;
        try {
            baos = new ByteArrayOutputStream();

            byte[] byteChunk = new byte[1024 * 16];
            int len = -1;
            while ((len = buffer.read(byteChunk)) > -1) {
                baos.write(byteChunk, 0, len);
            }
            baos.flush();
            byte[] bytes = baos.toByteArray();
            str = new String(bytes, charSet);
            if (baos != null) {
                baos.close();
                baos = null;
            }
        } catch (IOException e) {
            logger.error("readBufferFromStream error", e);
        } finally {
            try {
                if (baos != null) {
                    baos.close();
                    baos = null;
                }
            } catch (Exception e) {
                logger.error("readBufferFromStream error!", e);
            }
        }
        return str;
    }
}
