package mover.backend.web.rest.errors;

public final class ErrorConstants {

    public static final String ERR_CONCURRENCY_FAILURE = "error.concurrencyFailure";
    public static final String ERR_ACCESS_DENIED = "error.accessDenied";
    public static final String ERR_VALIDATION = "error.validation";
    public static final String ERR_METHOD_NOT_SUPPORTED = "error.methodNotSupported";
    public static final String ERR_INTERNAL_SERVER_ERROR = "error.internalServerError";
    public static final String ERR_INVALID_CREDENTIALS = "error.invalidCredentials";
    public static final String ERR_TOKEN_EXPIRED = "error.tokenExpired";
    public static final String ERR_VALIDATION_FAILED = "error.validationFailed";

    private ErrorConstants() {
    }
}
