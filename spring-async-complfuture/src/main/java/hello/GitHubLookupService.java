package hello;

import org.apache.log4j.Logger;
import org.springframework.scheduling.annotation.Async;
import org.springframework.scheduling.annotation.EnableAsync;
import org.springframework.stereotype.Service;
import org.springframework.web.client.RestTemplate;

import java.util.concurrent.CompletableFuture;

@Service
@EnableAsync
public class GitHubLookupService {
    private Logger log = Logger.getLogger(this.getClass());
    private RestTemplate restTemplate = new RestTemplate();

    @Async
    public CompletableFuture<User> findUser(String user) throws InterruptedException {
        log.info("Looking up? " + user);
        User results = restTemplate.getForObject("https://api.github.com/users/" + user, User.class);
        // Artificial delay of 1s for demonstration purposes
        return CompletableFuture.completedFuture(results);
    }


}

