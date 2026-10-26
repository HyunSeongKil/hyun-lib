package dev.hyunlab.hyunlib.misc;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.OutputStream;
import java.io.PrintStream;

public class Slf4jPrintStream extends PrintStream {

    private static final Logger log = LoggerFactory.getLogger("SMTP_DEBUG");

    public Slf4jPrintStream() {
        super(new OutputStream() {
            @Override
            public void write(int b) {
            }
        });
    }

    @Override
    public void println(String x) {
        log.debug(x);
    }
}