package test.shop.infrastructure.oauth2.error;

public enum OAuth2ErrorType {
    DUPLICATE_EMAIL("DUPLICATE_EMAIL"),
    EMAIL_ALREADY_EXISTS("EMAIL_ALREADY_EXISTS"),
    EMAIL_NOT_FOUND("EMAIL_NOT_FOUND"),
    PROVIDER_MISMATCH("PROVIDER_MISMATCH"),
    INVALID_PROVIDER("INVALID_PROVIDER"),
    MISSING_REQUIRED_INFO("MISSING_REQUIRED_INFO"),
    REGISTRATION_FAILED("REGISTRATION_FAILED"),
    GENERIC_ERROR("GENERIC_ERROR");

    private final String code;

    OAuth2ErrorType(String code) {
        this.code = code;
    }

    public String getCode() {
        return code;
    }
}
