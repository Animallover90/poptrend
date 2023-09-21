package co.kr.poptrend.error;

import lombok.Getter;

@Getter
public class UnexpectedException extends RuntimeException{

    private final Throwable throwable;
    public UnexpectedException(Throwable throwable) {
        this.throwable = throwable;
    }
}
