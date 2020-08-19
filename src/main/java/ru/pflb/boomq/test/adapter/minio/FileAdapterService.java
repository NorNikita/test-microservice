package ru.pflb.boomq.test.adapter.minio;

import io.minio.MinioClient;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.util.StreamUtils;
import ru.pflb.boomq.test.adapter.IFileAdapterService;
import ru.pflb.boomq.test.config.properties.Minio;
import ru.pflb.boomq.test.config.properties.TestServiceProperties;
import ru.pflb.boomq.test.model.exception.ReceiveTestParametersException;

import java.io.InputStream;
import java.nio.charset.StandardCharsets;

@Slf4j
@Service
public class FileAdapterService implements IFileAdapterService {

    private Minio minio;
    private MinioClient minioClient;

    public FileAdapterService(Minio minio, MinioClient minioClient) {
        this.minio = minio;
        this.minioClient = minioClient;
    }

    @Override
    public String getFileContentAsString(String bucketUri) throws ReceiveTestParametersException {
        log.info("get file content as string. bucketUri: {}", bucketUri);

        String bucketName = minio.getBucketname();
        String path = bucketUri.substring(bucketUri.indexOf(bucketName) + bucketName.length());

        try(InputStream inputStream = minioClient.getObject(bucketName, path)) {
            return StreamUtils.copyToString(inputStream, StandardCharsets.UTF_8);
        } catch (Exception exc) {
            log.error("error get file content as String! bucketUri: {}. message: {}", bucketUri, exc.getMessage());

            throw new ReceiveTestParametersException("Can not get file from minio: " + minio.getEndpoint());
        }
    }
}

