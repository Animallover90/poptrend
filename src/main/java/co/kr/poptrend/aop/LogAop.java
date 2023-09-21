package co.kr.poptrend.aop;

import co.kr.poptrend.error.CustomException;
import co.kr.poptrend.error.ErrorCode;
import co.kr.poptrend.error.ModelCustomException;
import co.kr.poptrend.error.UnexpectedException;
import lombok.extern.slf4j.Slf4j;
import org.aspectj.lang.JoinPoint;
import org.aspectj.lang.ProceedingJoinPoint;
import org.aspectj.lang.annotation.*;
import org.aspectj.lang.reflect.MethodSignature;
import org.slf4j.MDC;
import org.springframework.dao.NonTransientDataAccessException;
import org.springframework.dao.TransientDataAccessException;
import org.springframework.stereotype.Component;

import java.lang.reflect.Method;

@Aspect
@Component
@Slf4j
public class LogAop {

    @Pointcut(value = "execution(* co.kr.poptrend..*Controller.*(..)) || execution(* co.kr.poptrend..*Service.*(..))")
    private void controllerAndService(){}
    @Pointcut(value = "execution(* co.kr.poptrend..*Controller.*(..))")
    private void controller(){}
    @Pointcut(value = "execution(* co.kr.poptrend..*Repository*.*(..))")
    private void repository(){}
    @Pointcut(value = "execution(* co.kr.poptrend..*Scheduler*.*(..))")
    private void scheduler(){}

    @Around(value = "controller() && @annotation(org.springframework.web.bind.annotation.GetMapping)")
    public Object controllerGetTryCatch(ProceedingJoinPoint joinPoint) {
        try {
            return joinPoint.proceed();
        } catch (ModelCustomException mcex) {
            commonWarnLog(mcex, joinPoint);
            throw mcex;
        } catch (TransientDataAccessException trex) {
            commonWarnLog(trex, joinPoint);
            return "/?status=510";
        } catch (Throwable e) {
            commonErrorLog(e, joinPoint);
            return "/error/500";
        }
    }

    @Around(value = "controller() && @annotation(org.springframework.web.bind.annotation.PostMapping)")
    public Object controllerPostTryCatch(ProceedingJoinPoint joinPoint) {
        try {
            return joinPoint.proceed();
        } catch (CustomException cex) {
            commonWarnLog(cex, joinPoint);
            throw cex;
        } catch (TransientDataAccessException trex) {
            commonWarnLog(trex, joinPoint);
            throw new CustomException(ErrorCode.TransientDataAccess);
        } catch (NonTransientDataAccessException notrex) {
            commonErrorLog(notrex, joinPoint);
            throw new CustomException(ErrorCode.NonTransientDataAccess);
        } catch (Throwable e) {
            commonErrorLog(e, joinPoint);
            throw new CustomException(ErrorCode.UnExpectedError);
        }
    }

    // DataAccessException을 추상화하여 각 DB에러에 대한 errorcode기반으로 공통 에러로 변환해서 반환 @Repository가 붙은 클래스에 적용됨
    @Around(value = "repository()")
    public Object execute(ProceedingJoinPoint joinPoint) {
        try {
            return joinPoint.proceed();
        } catch (TransientDataAccessException trex) {
            // DataAccessException을 상속받은 공통적으로 추상화된 클래스로 재시도시 해결 가능한 오류
            throw trex;
        } catch (NonTransientDataAccessException notrex) {
            // DataAccessException을 상속받은 공통적으로 추상화된 클래스로 재시도해도 해결 불가능(문제 해결이 필요)
            throw notrex;
        } catch (Throwable e) {
            // 예상치 못한 예외로 RuntimeException 이외의 가능성에는 Transaction Rollback이 안될 수 있으니. RuntimeError로 바꿔서 전달
            throw new UnexpectedException(e);
        }
    }

    @Before("controllerAndService()")
    public void requestLogging(JoinPoint joinPoint) {
        log.info("ip={}||user_id={}||before_time={}||class_name={}||method_name={}||args={}",
                MDC.get("originIP"), MDC.get("userID"), System.currentTimeMillis(), getClass(joinPoint),
                getMethod(joinPoint).getName(), joinPoint.getArgs());
    }

    @AfterReturning(value = "controllerAndService()", returning = "returnObj")
    public void requestLogging(JoinPoint joinPoint, Object returnObj) {
        log.info("ip={}||user_id={}||after_time={}||class_name={}||method_name={}||return_value={}",
                MDC.get("originIP"), MDC.get("userID"), System.currentTimeMillis(), getClass(joinPoint),
                getMethod(joinPoint).getName(), returnObj);
    }

    @AfterThrowing(value = "scheduler()", throwing = "exception")
    public void schedulerLogging(JoinPoint joinPoint, Exception exception) {
        commonErrorLog(exception, joinPoint);
    }

    private Method getMethod(JoinPoint joinPoint) {
        MethodSignature signature = (MethodSignature) joinPoint.getSignature();
        return signature.getMethod();
    }

    private String getClass(JoinPoint joinPoint) {
        return joinPoint.getSignature().getDeclaringType().getSimpleName();
    }

    public static <T> String getStackTrace(T ex) {
        StackTraceElement[] stackTrace = null;
        if (ex instanceof CustomException && ((CustomException) ex).getThrowable() != null) {
            stackTrace = ((CustomException) ex).getThrowable().getStackTrace();
        } else if (ex instanceof ModelCustomException  && ((ModelCustomException) ex).getThrowable() != null) {
            stackTrace = ((ModelCustomException) ex).getThrowable().getStackTrace();
        } else if (ex instanceof UnexpectedException && ((UnexpectedException) ex).getThrowable() != null) {
            stackTrace = ((UnexpectedException) ex).getThrowable().getStackTrace();
        } else if (ex instanceof Exception) {
            stackTrace = ((Exception) ex).getStackTrace();
        }

        StringBuilder stringBuilder = new StringBuilder();
        for (int i = 0; i < stackTrace.length; i++) {
            if (i == stackTrace.length-1) {
                stringBuilder.append(stackTrace[i]);
            } else {
                stringBuilder.append(stackTrace[i] + "-->");
            }
        }
        return stringBuilder.toString();
    }

    private <T> void commonWarnLog(T ex, JoinPoint joinPoint) {
        String message = getExceptionMessage(ex);
        log.warn("ip={}||user_id={}||after_time={}||class_name={}||method_name={}||args={}||message={}||exception={}",
                MDC.get("originIP"), MDC.get("userID"), System.currentTimeMillis(), getClass(joinPoint),
                getMethod(joinPoint).getName(), joinPoint.getArgs(), message,
                getStackTrace((Exception) ex));
    }

    private <T> void commonErrorLog(T ex, JoinPoint joinPoint) {
        String message = getExceptionMessage(ex);
        log.error("ip={}||user_id={}||after_time={}||class_name={}||method_name={}||args={}||message={}||exception={}",
                MDC.get("originIP"), MDC.get("userID"), System.currentTimeMillis(), getClass(joinPoint),
                getMethod(joinPoint).getName(), joinPoint.getArgs(), message,
                getStackTrace((Exception) ex));
    }

    private <T> String getExceptionMessage(T ex) {
        String message = null;
        if (ex instanceof CustomException && ((CustomException) ex).getThrowable() != null) {
            CustomException customException = (CustomException) ex;
            message = customException.getThrowable().getMessage();
            if (message == null) message = customException.getErrorCode().getMessage();
        } else if (ex instanceof ModelCustomException  && ((ModelCustomException) ex).getThrowable() != null) {
            ModelCustomException modelCustomException = (ModelCustomException) ex;
            message = modelCustomException.getThrowable().getMessage();
            if (message == null) message = modelCustomException.getErrorCode().getMessage();
        } else if (ex instanceof UnexpectedException  && ((UnexpectedException) ex).getThrowable() != null) {
            UnexpectedException unexpectedException = (UnexpectedException) ex;
            message = unexpectedException.getThrowable().getMessage();
        } else if (ex instanceof Exception) {
            message = ((Exception) ex).getMessage();
        }
        return message;
    }
}
