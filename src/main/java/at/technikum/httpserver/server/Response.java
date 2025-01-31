package at.technikum.httpserver.server;

import at.technikum.httpserver.http.ContentType;
import at.technikum.httpserver.http.HttpStatus;

import java.time.ZoneId;
import java.time.ZonedDateTime;
import java.time.format.DateTimeFormatter;

public class Response {
    private int status;
    private String message;
    private String contentType;
    private String content;

    public Response(HttpStatus httpStatus, ContentType contentType, String content) {
        this.status = httpStatus.code;
        this.message = httpStatus.message;
        this.contentType = contentType.type;
        this.content = content;
    }

    public String get() {

        String localDatetime = DateTimeFormatter.RFC_1123_DATE_TIME.format(ZonedDateTime.now(ZoneId.of("UTC")));
        return "HTTP/1.1 " + this.status + " " + this.message + "\r\n" +
                "Cache-Controller: max-age=0\r\n" +
                "Connection: close\r\n" +
                "Date: " + localDatetime + "\r\n" +
                "Expires: " + localDatetime + "\r\n" +
                "Content-Type: " + this.contentType + "\r\n" +
                "Content-Length: " + this.content.length() + "\r\n" +
                "\r\n" +
                this.content;
    }

    public int getStatusCode() {
        return status;
    }

    public HttpStatus getStatus() {
        return HttpStatus.fromCode(status);
    }

    public ContentType getContentType() {
        return ContentType.fromType(contentType);
    }

    public String getBody() {
        return content;
    }
}