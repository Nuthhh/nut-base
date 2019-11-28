package com.nut.base.core.util;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;

import javax.net.ssl.*;
import java.io.BufferedInputStream;
import java.io.File;
import java.io.IOException;
import java.io.RandomAccessFile;
import java.net.HttpURLConnection;
import java.net.URL;
import java.nio.charset.StandardCharsets;
import java.security.cert.CertificateException;
import java.security.cert.X509Certificate;

/**
 * @Auther: han jianguo
 * @Date: 2019/11/20 09:52
 * @Description:
 **/
public class DownloadUtil {

    private static Log log = LogFactory.getLog(DownloadUtil.class);

    private static TrustManager[] tm = {new TrustAnyTrustManager()}; // 创建SSLContext对象，使用我们自己创建的信任管理器初始化
    private static SSLContext sc;

    static {
        try {
            sc = SSLContext.getInstance("SSL", "SunJSSE");
            sc.init(null, tm, new java.security.SecureRandom());
        } catch (Exception e) {
            log.error("下载工具类初始化错误：" + e.toString());
        }
    }


    public static void main(String[] args) {
        String url = "https://dldir1.qq.com/qqfile/qq/TIM2.3.2/21173/TIM2.3.2.21173.exe";
        String location = "D:\\";
       // downloadByUrl(url, location);
        getDownloadData(url);
    }


    public static boolean downloadByUrl(String url, String location) {
        DownloadData data = getDownloadData(url);
        if (!data.isDownload || data.fileSize <= 0) {
            return false;
        }

        location += data.fileName; // 文件存放的本地路径

        int startPos = 0; // 文件下载开始位置
        if (data.isSupport) {
            File file = new File(location);
            if (file.exists() && file.isFile()) {
                if (file.length() >= 2048) {
                    startPos = (int) (file.length() - 2048);
                } else {
                    file.delete();
                }
            }
        }
        return download(url, location, startPos, data.fileSize);
    }

    private static DownloadData getDownloadData(String fileUrl) {
        DownloadData data = new DownloadData();
        HttpURLConnection conn = getConn(fileUrl);
        try {
            conn.connect();
            int code = conn.getResponseCode();
            if ((code == 301 || code == 302) && conn.getHeaderField("Location") != null) {
                data = getDownloadData(conn.getHeaderField("Location"));
                return data;
            }
            if (code != 200 && code != 202) {
                log.error("文件访问错误");
                return data;
            }

            String fileName = "";
            String newUrl = conn.getURL().getFile();
            if (StringUtil.isNotEmpty(newUrl)) {
                newUrl = java.net.URLDecoder.decode(newUrl, "UTF-8");
                int pos = newUrl.indexOf('?');
                if (pos > 0) {
                    newUrl = newUrl.substring(0, pos);
                }
                pos = newUrl.lastIndexOf('/');
                fileName = newUrl.substring(pos + 1);
            }

            if (StringUtil.isEmpty(fileName) || !fileName.contains(".")) {
                String raw = conn.getHeaderField("Content-Disposition"); // raw = "attachment; filename=abc.jpg"
                if (raw != null && raw.indexOf("=") > 0) {
                    fileName = raw.split("=")[1]; // getting value after '='
                    fileName = fileName.replaceAll("\"", "");
                    fileName = new String(fileName.getBytes(StandardCharsets.ISO_8859_1), StandardCharsets.UTF_8);
                }
            }

            if (StringUtil.isEmpty(fileName)) {
                log.error("文件获取名称错误");
                return data;
            }

            int fileSize = 0;

            if (conn.getHeaderField("Content-Length") != null) {
                fileSize = Integer.parseInt(conn.getHeaderField("Content-Length"));
            }

            if (fileSize <= 2048 && fileName.indexOf('.') <= 0) {
                log.error("文件检测错误");
                return data;
            }

            if (conn.getHeaderField("Accept-Ranges") != null) {
                data.isSupport = true;
            }
            data.fileSize = fileSize;
            data.fileName = fileName;
            data.isDownload = data.fileSize > 0;
        } catch (Exception e) {
            log.error("文件下载错误");
        } finally {
            destroyConn(conn);
        }

        return data;
    }

    private static boolean download(String fileUrl, String fileLocal, int startPos, int fileSize) {

        BufferedInputStream in = null;
        RandomAccessFile realFile = null;
        HttpURLConnection conn = getConn(fileUrl);
        boolean rs = false;
        try {
            if (startPos > 0) {
                conn.setRequestProperty("RANGE", "bytes=" + startPos + "-");
            }
            conn.connect();

            int code = conn.getResponseCode();
            if ((code == 301 || code == 302) && conn.getHeaderField("Location") != null) {
                return download(conn.getHeaderField("Location"), fileLocal, startPos, fileSize);
            }

            in = new BufferedInputStream(conn.getInputStream());

            realFile = new RandomAccessFile(fileLocal, "rwd");
            if (startPos > 0)
                realFile.seek(startPos);

            byte[] temp = new byte[1024];
            int size = 0;
            int currentLen = startPos; // 当前下载进度
            while ((size = in.read(temp)) > 0) {
                realFile.write(temp, 0, size);
                currentLen += size;
                ConsoleProgressBarUtil.show((float) (currentLen * 1.0 / fileSize));
            }
            realFile.close();
            realFile = null;
            rs = true;
        } catch (Exception e) {
            log.error("文件下载异常：" + e.toString());
        }

        try {
            if (in != null)
                in.close();
        } catch (IOException e2) {
            log.error("文件下载异常：" + e2.toString());
        }

        try {
            if (realFile != null)
                realFile.close();
        } catch (IOException e3) {
            log.error("文件下载异常：" + e3.toString());
        }

        destroyConn(conn);

        return rs;
    }

    private static HttpURLConnection getConn(String fileUrl) {
        HttpURLConnection conn = null;
        try {
            URL url = new URL(fileUrl);
            if (fileUrl.startsWith("https://")) {
                HttpsURLConnection conn2 = (HttpsURLConnection) url.openConnection();
                conn2.setSSLSocketFactory(sc.getSocketFactory());
                conn2.setHostnameVerifier(new TrustAnyHostnameVerifier());
                conn = conn2;
                conn.setDoOutput(true);
                conn.setDoInput(true);
            } else {
                conn = (HttpURLConnection) url.openConnection();
            }
            conn.setConnectTimeout(10000);
            conn.setReadTimeout(15000);
            conn.setRequestMethod("GET");
        } catch (Exception e) {
            log.error("文件下载异常：" + e.toString());
        }
        return conn;
    }

    private static void destroyConn(HttpURLConnection conn) {
        if (conn != null) {
            conn.disconnect();
        }
    }

    /**
     * 文件相关信息
     */
    private static class DownloadData {
        private Boolean isDownload = false; // 文件是否可下载
        private Boolean isSupport = false; // 文件是否支持断点续传
        private String fileName; // 文件名称
        private Integer fileSize; // 文件大小
    }

    /**
     * 通过实现X509TrustManager接口，来实现自己的证书信任管理器类
     * 下面的实现意味着信任所有证书，不管是否权威
     */
    private static class TrustAnyTrustManager implements X509TrustManager {
        public void checkClientTrusted(X509Certificate[] chain, String authType) throws CertificateException {
        }

        public void checkServerTrusted(X509Certificate[] chain, String authType) throws CertificateException {
        }

        public X509Certificate[] getAcceptedIssuers() {
            return new X509Certificate[]{};
        }
    }

    /**
     * 使用verify函数校验服务器主机名的合法性，否则会导致恶意程序利用中间人攻击绕过主机名校验
     * 下面的实现意味着默认接受所有域名，存在安全风险
     */
    private static class TrustAnyHostnameVerifier implements HostnameVerifier {
        public boolean verify(String hostname, SSLSession session) {
            return true;
        }
    }

}
