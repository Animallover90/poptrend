package co.kr.poptrend.error;

import lombok.Getter;

@Getter
public class ModelCustomException extends RuntimeException{

    private Throwable throwable;
    private final String url;
    private ErrorCode errorCode;

    public ModelCustomException(Throwable throwable, String url) {
        this.throwable = throwable;
        this.url = url;
    }

    public ModelCustomException(CustomException customException, String url) {
        this.throwable = customException.getThrowable();
        this.errorCode = customException.getErrorCode();
        this.url = url;
    }

    public ModelCustomException(String url) {
        this.url = url;
    }

}
