package br.arthconf.fortivus.config;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import software.amazon.awssdk.auth.credentials.AwsBasicCredentials;
import software.amazon.awssdk.auth.credentials.StaticCredentialsProvider;
import software.amazon.awssdk.regions.Region;
import software.amazon.awssdk.services.s3.S3Client;

import java.net.URI;

@Configuration
public class S3Config {

    @Value("${fortivus.storage.s3.endpoint}")
    private String endpoint;

    @Value("${fortivus.storage.s3.access-key}")
    private String accessKey;

    @Value("${fortivus.storage.s3.secret-key}")
    private String secretKey;

    @Bean
    public software.amazon.awssdk.auth.credentials.AwsCredentialsProvider awsCredentialsProvider() {
        return software.amazon.awssdk.auth.credentials.AnonymousCredentialsProvider.create();
    }

    @Bean
    public S3Client s3Client(software.amazon.awssdk.auth.credentials.AwsCredentialsProvider credentialsProvider) {
        return S3Client.builder()
                .endpointOverride(URI.create(endpoint))
                .credentialsProvider(credentialsProvider)
                .region(Region.US_EAST_1)
                .forcePathStyle(true)
                .build();
    }
}
