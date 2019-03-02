package com.bbc.automower;

import com.bbc.automower.domain.Lawn;
import com.bbc.automower.error.Error;
import com.bbc.automower.util.Parser;
import lombok.extern.slf4j.Slf4j;

import static java.util.Objects.nonNull;

@Slf4j
public class Main {

    private final static String DEFAULT_FILE_PATH = "META-INF/config/automower.txt";

    public static void main(final String[] args) {
        new Parser()
                .parse(getFilePath(args))
                .map(Lawn::execute)
                .swap()
                .forEach(errors -> errors
                        .map(Error::text)
                        .forEach(log::error));
    }

    private static String getFilePath(final String[] args) {
        return nonNull(args) && args.length == 1 ?
                args[0] :
                DEFAULT_FILE_PATH;
    }

}
