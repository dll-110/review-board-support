package com.guyazhou.plugin.reviewboard.http;

import cn.hutool.http.ContentType;
import cn.hutool.http.HttpRequest;
import cn.hutool.http.HttpResponse;
import com.guyazhou.plugin.reviewboard.model.DiffVirtualFile;

import java.io.*;
import java.net.*;
import java.nio.charset.StandardCharsets;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.*;

/**
 * HttpClient
 *
 * @author YaZhou.Gu 2017/1/2
 */
public class HttpClient {

    private static final String MULTI_PART_BOUNDARY = "---------MULTIPART";
    private Map<String, String> headers;

    public HttpClient() {
    }

    public HttpClient(Map<String, String> headers) {
        this.headers = headers;
    }

    /**
     * Build a HttpURLConnection instance by url string and reuest method
     *
     * @param urlStr url string
     * @param method request method
     * @return a HttpURLConnection instance
     */
    private HttpURLConnection buildHttpURLConnection(String urlStr, HTTP_METHOD method) {
        URL url;
        try {
            url = new URL(urlStr);
        } catch (MalformedURLException e) {
            throw new RuntimeException("UrlStr is not format properly" + e.getMessage());
        }

        URLConnection urlConnection;
        try {
            urlConnection = url.openConnection();
        } catch (IOException e) {
            throw new RuntimeException("Open url connection error" + e.getMessage());
        }

        if (HTTP_METHOD.POST == method || HTTP_METHOD.PUT == method) {
            urlConnection.setDoOutput(true);    // default: false, set true make it possible to get a output stream
        }

        HttpURLConnection httpURLConnection = (HttpURLConnection) urlConnection;
        try {
            httpURLConnection.setRequestMethod(method.toString());
        } catch (ProtocolException e) {
            throw new RuntimeException("Set http url connection method error" + e.getMessage());
        }

        // set headers
        if (null != headers) {
            for (Map.Entry<String, String> entry : headers.entrySet()) {
                httpURLConnection.setRequestProperty(entry.getKey(), entry.getValue());
            }
        }
        return httpURLConnection;
    }

    /**
     * Build response message from inputstream
     *
     * @param httpURLConnection HttpURLConnection instance
     * @return response message
     */
    private String buildResponseMessage(HttpURLConnection httpURLConnection) {
        InputStream inputStream;
        try {
            inputStream = httpURLConnection.getInputStream();
        } catch (IOException e) {
            throw new RuntimeException("Get inputstram from connection error, " + e.getMessage());
        }
        StringBuilder responseMessageBuilder = new StringBuilder();
        BufferedReader bufferedReader = new BufferedReader(new InputStreamReader(inputStream));
        String line;
        try {
            while ((line = bufferedReader.readLine()) != null) {
                responseMessageBuilder.append(line);
            }
        } catch (IOException e) {
            throw new RuntimeException("Get message from inputstream error" + e.getMessage());
        } finally {
            try {
                bufferedReader.close();
                inputStream.close();
            } catch (IOException e) {
                e.printStackTrace();
            }
        }
        return responseMessageBuilder.toString();
    }

    /**
     * Request a GET http request
     *
     * @param urlStr url
     * @return response messages
     */
    public String get(String urlStr) {
        HttpURLConnection httpURLConnection = this.buildHttpURLConnection(urlStr, HTTP_METHOD.GET);
        // redirect automaticly
        httpURLConnection.setInstanceFollowRedirects(false);

        return this.buildResponseMessage(httpURLConnection);
    }

    /**
     * Post a request to server
     *
     * @param urlStr server url
     * @param params params
     * @return response message
     */
    public String post(String urlStr, Map<String, Object> params) {
        return this.post(urlStr, params, false);
    }

    /**
     * Post a request to server with files
     *
     * @param urlStr      server url
     * @param params      params
     * @param isMultiPart post files if true, default false
     * @return response string
     */
    public String post(String urlStr, Map<String, Object> params, boolean isMultiPart) {
        if (isMultiPart) {
            try {
                return this.postWithMultiplePart(urlStr, params);
            } catch (IOException e) {
                throw new RuntimeException(e);
            }
        } else {
            return this.requestSimply(urlStr, HTTP_METHOD.POST, params);
        }
    }

    public String put(String urlStr, Map<String, Object> params) {
        if (null == urlStr || "".equals(urlStr) || null == params) {
            throw new RuntimeException("Url is empty or paramas is null");
        }
        if (0 == params.size()) {
            return null;
        }
        return this.requestSimply(urlStr, HTTP_METHOD.PUT, params);
    }

    public String delete(String urlStr, Map<String, Object> params) {
        return null;
    }

    private String requestSimply(String urlStr, HTTP_METHOD method, Map<String, Object> params) {

        HttpURLConnection httpURLConnection = this.buildHttpURLConnection(urlStr, method);
        try {
            httpURLConnection.setRequestMethod(method.toString());
        } catch (ProtocolException e) {
            throw new RuntimeException(e);
        }

        httpURLConnection.setRequestProperty("Content-Type", "application/x-www-form-urlencoded");  // TODO refactor

        OutputStream outputStream;
        try {
            outputStream = httpURLConnection.getOutputStream();
        } catch (IOException e) {
            throw new RuntimeException(e);
        }
        String postBody = "";
        for (Map.Entry<String, Object> entry : params.entrySet()) {
            if (!"".equals(postBody)) {
                postBody = postBody.concat("&" + entry.getKey() + "=" + String.valueOf(entry.getValue()));
            } else {
                postBody = entry.getKey() + "=" + String.valueOf(entry.getValue());
            }
        }
        try {
            outputStream.write(postBody.getBytes("UTF-8"));
            outputStream.flush();
            outputStream.close();
        } catch (IOException e) {
            throw new RuntimeException(e);
        }
        return this.buildResponseMessage(httpURLConnection);
    }

    /**
     * Post a http request with multiple part
     *
     * @param urlStr url string
     * @param params http parmas
     * @return response message
     */
    private String postWithMultiplePart(String urlStr, Map<String, Object> params) throws IOException {

        HTTP_METHOD method = HTTP_METHOD.POST;
        HttpURLConnection httpURLConnection = this.buildHttpURLConnection(urlStr, method);
        try {
            httpURLConnection.setRequestMethod(method.toString());
        } catch (ProtocolException e) {
            throw new RuntimeException(e);
        }

        HashMap<String, Object> paramMap = new HashMap<>();
        File file = null;

        Properties props = System.getProperties();
        String userHome = props.getProperty("user.home");
        String month = String.valueOf(Calendar.getInstance().get(Calendar.MONTH));
        String tmpDir = userHome + "/.reviewboard/" + month;
        if (!Files.exists(Paths.get(tmpDir))) {
            new File(tmpDir).mkdirs();
        }
        for (Map.Entry<String, Object> entry : params.entrySet()) {
            if (entry.getValue() instanceof File) {
                file = (File) entry.getValue();
            } else if (entry.getValue() instanceof DiffVirtualFile) {
                DiffVirtualFile vfile = (DiffVirtualFile) entry.getValue();
                Path fpath = Paths.get(tmpDir + "/" + UUID.randomUUID() + ".diff");
                BufferedWriter bfw = Files.newBufferedWriter(fpath, StandardCharsets.UTF_8);
                bfw.write(vfile.getContent());
                bfw.flush();
                bfw.close();
                file = fpath.toFile();
            } else {
                paramMap.put(entry.getKey(), entry.getValue().toString());
            }
        }
        paramMap.put("path", file);

        HttpResponse res = HttpRequest.post(urlStr)
                .header("Content-Type", ContentType.MULTIPART.toString())
                .addHeaders(this.headers)
                .form(paramMap)
                .execute();

        return res.body();
    }

}
