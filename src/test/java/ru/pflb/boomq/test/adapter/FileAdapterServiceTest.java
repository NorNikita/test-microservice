package ru.pflb.boomq.test.adapter;

import io.minio.MinioClient;
import org.junit.jupiter.api.extension.ExtendWith;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.MethodSource;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import ru.pflb.boomq.test.adapter.minio.FileAdapterService;
import ru.pflb.boomq.test.config.properties.Minio;
import ru.pflb.boomq.test.model.exception.ReceiveTestParametersException;

import java.io.ByteArrayInputStream;
import java.io.IOException;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
class FileAdapterServiceTest {

    @Mock
    private Minio minio;

    @Mock
    private MinioClient minioClient;

    @InjectMocks
    private FileAdapterService fileAdapterService;

    @ParameterizedTest
    @MethodSource("ru.pflb.boomq.test.args.adapter.FileAdapterServiceArgs#getParams")
    void getFileContentAsStringTest(String bucketUri, String bucketName, String content) throws Exception {
        String path = bucketUri.substring(bucketUri.indexOf(bucketName) + bucketName.length());

        when(minio.getBucketname()).thenReturn(bucketName);
        when(minioClient.getObject(bucketName, path)).thenReturn(new ByteArrayInputStream(content.getBytes()));

        assertEquals(content, fileAdapterService.getFileContentAsString(bucketUri));

        when(minioClient.getObject(bucketName, path)).thenThrow(IOException.class);
        assertThrows(ReceiveTestParametersException.class, () -> fileAdapterService.getFileContentAsString(bucketUri));
    }
}
