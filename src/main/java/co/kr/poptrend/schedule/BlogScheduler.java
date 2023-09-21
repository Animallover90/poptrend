package co.kr.poptrend.schedule;

import co.kr.poptrend.blog.entity.Blog;
import co.kr.poptrend.blog.service.BlogService;
import co.kr.poptrend.blogRedis.BlogRedisConfig;
import co.kr.poptrend.member.service.MemberService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.json.simple.JSONObject;
import org.json.simple.parser.JSONParser;
import org.json.simple.parser.ParseException;
import org.springframework.data.redis.core.ListOperations;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Component;

import java.time.LocalDateTime;

@Slf4j
@Component
@RequiredArgsConstructor
public class BlogScheduler {

    private final BlogRedisConfig blogRedisConfig;
    private final BlogService blogService;
    private final MemberService memberService;

    @Scheduled(cron = "0 1/3 * * * ?")
    public void insertBlogByRedis() {
        ListOperations<String, Object> listOps = blogRedisConfig.blogRedisTemplate().opsForList();

        Object news = listOps.rightPop("news");
        while (news != null) {
            System.out.println("news = " + news);

            JSONParser jsonParser = new JSONParser();
            JSONObject parse = null;
            try {
                parse = (JSONObject) jsonParser.parse(news.toString());
            } catch (ParseException e) {
                throw new RuntimeException();
            }

            if (!parse.isEmpty() && parse.get("content") != null) {
                Blog blog = Blog.builder()
                        .title(parse.get("title").toString())
                        .content(parse.get("content").toString())
                        .source(parse.get("source").toString())
                        .tag(parse.get("tag").toString())
                        .build();

                blogService.saveBlog(blog);
            }

            news = listOps.rightPop("news");
        }
    }

    @Scheduled(cron = "0 1/5 * * * ?")
    public void delete1dayAgoBlogs() {
        blogService.delete1dayAgoBlogsDataAndBackup(LocalDateTime.now().minusDays(1L));
    }

    @Scheduled(cron = "0 1/5 * * * ?")
    public void delete1dayAgoSearch() {
        blogService.delete1dayAgoSearchDataAndBackup(LocalDateTime.now().minusDays(1L));
    }

    @Scheduled(cron = "0 1/10 * * * ?")
    public void delete1dayAgoInactiveMember() {
        memberService.delete1dayAgoWithdrawMemberAndBackup(LocalDateTime.now().minusDays(1L));
    }
}
