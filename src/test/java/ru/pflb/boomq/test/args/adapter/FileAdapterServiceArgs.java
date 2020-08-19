package ru.pflb.boomq.test.args.adapter;

import org.junit.jupiter.params.provider.Arguments;
import ru.pflb.boomq.test.args.provider.ContentProvider;

import java.util.stream.Stream;

public class FileAdapterServiceArgs {

    public static Stream<Arguments> getParams() {
        return Stream.of(
                Arguments.of(ContentProvider.getBucketUri(),
                        ContentProvider.getBucketName(),
                        ContentProvider.getContent())
        );
    }
}