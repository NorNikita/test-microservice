package ru.pflb.boomq.test.args.provider;

public class ContentProvider {

    public static String getContent() {
        return "stepCount: 1\n" +
                "stepLength: 300\n" +
                "rampUp: 0\n" +
                "usersPerStep: 1\n" +
                "rampDown: 0\n" +
                "groups:\n" +
                "  - name: New group 1\n" +
                "    perc: 100\n" +
                "    requests:\n" +
                "      - body: ''\n" +
                "        extractors: {}\n" +
                "        headers: {}\n" +
                "        method: GET\n" +
                "        params: {}\n" +
                "        perc: 0\n" +
                "        url: 'https://yandex.ru'\n" +
                "parameters: []\n" +
                "testType: STABLE\n" +
                "version: 1.0.0\n";
    }

    public static String getBucketUri() {
        return "mi://boomq/users/300/300/untitled_project_19:39.yaml";
    }

    public static String getBucketName() {
        return "boomq";
    }
}
