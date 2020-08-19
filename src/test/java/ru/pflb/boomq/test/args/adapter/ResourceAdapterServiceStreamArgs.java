package ru.pflb.boomq.test.args.adapter;

import org.junit.jupiter.params.provider.Arguments;
import ru.pflb.boomq.model.test.TestState;
import ru.pflb.boomq.test.args.provider.*;

import java.util.stream.Stream;

public class ResourceAdapterServiceStreamArgs {

    public static Stream<Arguments> getArgsFor_allocateResources() {
        return Stream.of(
                Arguments.of(
                        TestProvider.getTest(TestState.INITIALIZATION),
                        ResourceProvider.getListFreeResources(),
                        FreeResourceDtoProvider.getListFreeResourcesDto(),
                        UsedResourceProvider.getListUsedResource()
                )
        );
    }

    public static Stream<Arguments> getArgsFor_createServer() {
        return Stream.of(
                Arguments.of(
                        ResourceProvider.getResource(),
                        ServerProvider.getServer()
                )
        );
    }
}