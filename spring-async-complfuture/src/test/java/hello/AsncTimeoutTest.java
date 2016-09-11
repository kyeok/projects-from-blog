package hello;

import org.apache.log4j.Logger;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.junit4.SpringRunner;
import org.springframework.util.StopWatch;

/**
 * Created by Harley on 2016. 9. 11..
 */
@RunWith(SpringRunner.class)
@SpringBootTest
public class AsncTimeoutTest {
    private Logger log = Logger.getLogger(this.getClass());
    final StopWatch stopWatch = new StopWatch("AsncTimeoutTest");

    @Autowired
    private AsyncSampleService asyncSampleService;

    @Test
    public void testServe() throws InterruptedException {
        stopWatch.start("serve");
//        asyncSampleService.serve1();
//        asyncSampleService.serve2();
//        asyncSampleService.serve3();
//        asyncSampleService.serve4();
//        asyncSampleService.serve5();
//        asyncSampleService.serve6_withTimeout();
        asyncSampleService.serve7_within();
        stopWatch.stop();
        log.info(stopWatch.prettyPrint());
    }


}
