package at.technikum.httpserver.http;

public enum ContentType {
    PLAIN_TEXT("text/plain"),
    HTML("text/html"),
    JSON("application/json");

    public final String type;

    ContentType(String type) {
        this.type = type;
    }

    public static ContentType fromType(String contentType) {
        for (ContentType type : values()) {
            if (type.type.equals(contentType)) {
                return type;
            }
        }
        return null;
    }
}
