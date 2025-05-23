package news.app.newsApp.exception;

public class QueryExecutionException extends RuntimeException {
    private final String queryName;
    private final String queryDetails;

    public QueryExecutionException(String message, String queryName, String queryDetails) {
        super(message);
        this.queryName = queryName;
        this.queryDetails = queryDetails;
    }

    public QueryExecutionException(String message, String queryName, String queryDetails, Throwable cause) {
        super(message, cause);
        this.queryName = queryName;
        this.queryDetails = queryDetails;
    }

    public String getQueryName() {
        return queryName;
    }

    public String getQueryDetails() {
        return queryDetails;
    }

    @Override
    public String getMessage() {
        return String.format("Query execution failed: %s\nQuery: %s\nDetails: %s", 
            super.getMessage(), queryName, queryDetails);
    }
} 