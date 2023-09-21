package co.kr.poptrend.sessionRedis;

import lombok.RequiredArgsConstructor;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.stereotype.Service;

import java.util.concurrent.TimeUnit;

@Service
@RequiredArgsConstructor
public class RedisUtil {

    private final RedisTemplate<String, Object> redisTemplate;

    public void setDTOOps(String key, RedisDTO value, long ttl, TimeUnit unit){
        redisTemplate.opsForValue().set(key, value, ttl, unit);
    }

    public RedisDTO getDTOOps(String key){
        return (RedisDTO) redisTemplate.opsForValue().get(key);
    }

    public void deleteDTOOps(String key){
        redisTemplate.delete(key);
    }
}
