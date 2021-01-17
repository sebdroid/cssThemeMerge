package cssThemeMerge;

import static org.junit.Assert.assertEquals;

import org.junit.Test;

import picocli.CommandLine;

public class AppTest {

    @Test
    public void testCSS() {
        App app = new App();
        CommandLine cmd = new CommandLine(app);
        assertEquals(0, cmd.execute("-d=src/test/resources/default.css","-a=src/test/resources/alternative.css","-t=light"));
    }
}
