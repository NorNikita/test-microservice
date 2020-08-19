package ru.pflb.boomq.test.adapter;

import ru.pflb.boomq.test.model.exception.ReceiveTestParametersException;

public interface IFileAdapterService {

    String getFileContentAsString(String bucketUri) throws ReceiveTestParametersException;
}
