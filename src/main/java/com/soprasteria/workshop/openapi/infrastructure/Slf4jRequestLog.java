package com.soprasteria.workshop.openapi.infrastructure;

import jakarta.servlet.http.HttpServletRequest;
import org.eclipse.jetty.server.Request;
import org.eclipse.jetty.server.RequestLog;
import org.eclipse.jetty.server.Response;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.slf4j.Marker;
import org.slf4j.MarkerFactory;

import java.util.Optional;

public class Slf4jRequestLog implements RequestLog {
    private static final Logger REQUEST_LOG = LoggerFactory.getLogger("HTTP");
    private static final Marker HTTP = MarkerFactory.getMarker("HTTP");
    private static final Marker HTTP_ERROR = MarkerFactory.getMarker("HTTP_ERROR");
    private static final Marker ASSET = MarkerFactory.getMarker("HTTP_ASSET");
    private static final Marker JSON = MarkerFactory.getMarker("HTTP_JSON");
    private static final Marker XML = MarkerFactory.getMarker("HTTP_XML");
    private static final Marker REDIRECT = MarkerFactory.getMarker("HTTP_REDIRECT");
    private static final Marker NOT_MODIFIED = MarkerFactory.getMarker("HTTP_NOT_MODIFIED");
    private static final Marker STATUS = MarkerFactory.getMarker("HTTP_STATUS_REQUEST");
    
    static {
        HTTP_ERROR.add(HTTP);
        ASSET.add(HTTP);
        JSON.add(HTTP);
        REDIRECT.add(HTTP);
        NOT_MODIFIED.add(HTTP);
        NOT_MODIFIED.add(ASSET);
        STATUS.add(HTTP);
    }

    @Override
    public void log(Request req, Response response) {
        String request = getRequest(req);
        String remoteAddress = Optional.ofNullable(req.getHeader("X-Forwarded-For")).orElse(req.getRemoteAddr());
        String remoteUser = req.getRemoteUser();
        long bytesWritten = response.getHttpChannel().getBytesWritten();
        long latency = System.currentTimeMillis() - req.getTimeStamp();

        if (response.getStatus() >= 500) {
            Object errorMessage = req.getAttribute("javax.servlet.error.message");
            REQUEST_LOG.warn(HTTP_ERROR, "\"{}\" {} remoteAddress=\"{}\" remoteUser=\"{}\" bytesWritten={} latency={} errorMessage=\"{}\"",
                    request, response.getStatus(), remoteAddress, remoteUser, bytesWritten, latency, errorMessage);
        } else if (response.getStatus() >= 400) {
            Object errorMessage = req.getAttribute("javax.servlet.error.message");
            REQUEST_LOG.info(HTTP_ERROR, "\"{}\" {} remoteAddress=\"{}\" remoteUser=\"{}\" bytesWritten={} latency={} errorMessage=\"{}\"",
                    request, response.getStatus(), remoteAddress, remoteUser, bytesWritten, latency, errorMessage);
        } else if (response.getStatus() == 301 || response.getStatus() == 302 || response.getStatus() == 303 || response.getStatus() == 307 || response.getStatus() == 308) {
            String location = response.getHeader("Location");
            REQUEST_LOG.info(REDIRECT, "\"{}\" {} remoteAddress=\"{}\" remoteUser=\"{}\" bytesWritten={} latency={} location={}",
                    request, response.getStatus(), remoteAddress, remoteUser, bytesWritten, latency, location);
        } else if (response.getStatus() == 304) {
            REQUEST_LOG.debug(NOT_MODIFIED, "\"{}\" {} remoteAddress=\"{}\" remoteUser=\"{}\" bytesWritten={} latency={}",
                    request, response.getStatus(), remoteAddress, remoteUser, bytesWritten, latency);
        } else if (response.getStatus() >= 100) {
            if (req.getRequestURI().equals("/status")) {
                REQUEST_LOG.trace(STATUS, "\"{}\" {} remoteAddress=\"{}\" remoteUser=\"{}\" bytesWritten={} latency={}",
                        request, response.getStatus(), remoteAddress, remoteUser, bytesWritten, latency);
            } else {
                String contentType = Optional.ofNullable(response.getContentType()).orElse("");
                Marker marker = HTTP;
                boolean isAsset = contentType.startsWith("image/") || contentType.startsWith("font/") || contentType.startsWith("text/html") || contentType.startsWith("text/css") || contentType.startsWith("application/javascript");
                boolean isJson = contentType.startsWith("application/json");
                boolean isXml = contentType.startsWith("application/xml");
                if (isAsset) {
                    marker = ASSET;
                } else if (isJson) {
                    marker = JSON;
                } else if (isXml) {
                    marker = XML;
                }
                REQUEST_LOG.debug(marker, "\"{}\" {} remoteAddress=\"{}\" remoteUser=\"{}\" bytesWritten={} latency={} contentType={}",
                        request, response.getStatus(), remoteAddress, remoteUser, bytesWritten, latency, contentType);
            }
        } else {
            REQUEST_LOG.error(HTTP, "\"{}\" {} remoteAddress=\"{}\" remoteUser=\"{}\" bytesWritten={} latency={}",
                    request, response.getStatus(), remoteAddress, remoteUser, bytesWritten, latency);
        }
    }

    public static String getRequest(HttpServletRequest request) {
        return request.getMethod() + " " + getRequestUrl(request);
    }

    public static String getRequestUrl(HttpServletRequest req) {
        return getOrigin(req) + req.getRequestURI() + (req.getQueryString() != null ? "?" + req.getQueryString() : ""   );
    }

    public static String getOrigin(HttpServletRequest req) {
        String host = Optional.ofNullable(first(req.getHeader("X-Forwarded-Host")))
                .orElseGet(() -> req.getServerName() + (getServerPort(req) != getDefaultPort(req) ?  ":" + getServerPort(req) : ""));
        return getScheme(req) + "://" + host;
    }

    private static int getServerPort(HttpServletRequest req) {
        return Optional.ofNullable(req.getHeader("X-Forwarded-Port"))
                .map(Integer::parseInt)
                .orElseGet(() -> {
                    int port = req.getServerPort();
                    return port == 80 || port == 443 ? getDefaultPort(req) : port;
                });
    }

    private static int getDefaultPort(HttpServletRequest req) {
        return getScheme(req).equals("https") ? 443 : (getScheme(req).equals("http") ? 80 : -1);
    }

    private static String getScheme(HttpServletRequest req) {
        return Optional.ofNullable(first(req.getHeader("X-Forwarded-Proto"))).orElse(req.getScheme());
    }

    private static String first(String header) {
        return header != null ? header.split(",\\s* ")[0] : null;
    }

}