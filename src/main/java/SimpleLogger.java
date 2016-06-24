import java.util.logging.Logger;

public class SimpleLogger {

    private Logger logger;

    // 消される
    public SimpleLogger() {
        logger = Logger.getLogger(this.getClass().getName());
        logger.info("create SimpleLogger");
    }

    // 標準出力への出力に変更される
    public void log(String text) {
        logger.info(text);
    }
}