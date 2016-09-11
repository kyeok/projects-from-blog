package hello;

import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.junit4.SpringRunner;
import org.springframework.util.StopWatch;

import java.util.concurrent.CompletableFuture;
import java.util.concurrent.ExecutionException;

/**
 * Created by Harley on 2016. 9. 11..
 */
@RunWith(SpringRunner.class)
@SpringBootTest
public class CompletableFutureTest {
    @Autowired
    GitHubLookupService gitHubLookupService;

    final StopWatch stopWatch = new StopWatch("Completable Future Watch");

    @Test
    public void testGitUserApi() throws InterruptedException, ExecutionException {
        stopWatch.start("call apis");
        long start = System.currentTimeMillis();

        // Kick of multiple, asynchronous lookups
        CompletableFuture<User> page1 = gitHubLookupService.findUser("PivotalSoftware");
        CompletableFuture<User> page2 = gitHubLookupService.findUser("CloudFoundry");
        CompletableFuture<User> page3 = gitHubLookupService.findUser("Spring-Projects");
        stopWatch.stop();

        // Wait until they are all done
        //while (!(page1.isDone() && page2.isDone() && page3.isDone())) {
        //  Thread.sleep(10); //10-millisecond pause between each check
        //}

        //wait until all they are completed.
        stopWatch.start("join");
        CompletableFuture.allOf(page1, page2, page3).join();
        //I could join as well if interested.
        stopWatch.stop();


        // Print results, including elapsed time
        System.out.println("Elapsed time: " + (System.currentTimeMillis() - start) + " ms");
        System.out.println(page1.get());
        System.out.println(page2.get());
        System.out.println(page3.get());
    }
}
