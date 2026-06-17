package br.arthconf.fortivus.service;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;
import software.amazon.awssdk.core.sync.RequestBody;
import software.amazon.awssdk.services.s3.S3Client;
import software.amazon.awssdk.services.s3.model.ObjectCannedACL;
import software.amazon.awssdk.services.s3.model.PutObjectRequest;

import java.io.IOException;
import java.util.UUID;

@Service
@RequiredArgsConstructor
@Slf4j
public class S3StorageService implements FileStorageService {

    private final S3Client s3Client;

    @Value("${fortivus.storage.s3.bucket}")
    private String bucketName;

    @Value("${fortivus.storage.s3.endpoint}")
    private String endpoint;

    @jakarta.annotation.PostConstruct
    public void init() {
        try {
            s3Client.createBucket(builder -> builder.bucket(bucketName));
            log.info("Bucket '{}' verificado/criado no SeaweedFS", bucketName);
        } catch (Exception e) {
            // Ignora se o bucket já existir ou se houver erro de permissão inicial
            log.warn("Aviso ao inicializar bucket no SeaweedFS: {}", e.getMessage());
        }
    }

    @Override
    public String upload(MultipartFile file, String directory) {
        if (file == null || file.isEmpty()) {
            return null;
        }

        String fileName = UUID.randomUUID() + "_" + file.getOriginalFilename();
        String key = directory + "/" + fileName;

        try {
            log.info("Iniciando upload para SeaweedFS: {} ({} bytes)", key, file.getSize());
            
            byte[] bytes = file.getBytes();
            
            PutObjectRequest putObjectRequest = PutObjectRequest.builder()
                    .bucket(bucketName)
                    .key(key)
                    .contentType(file.getContentType())
                    .contentLength((long) bytes.length)
                    .build();

            s3Client.putObject(putObjectRequest, RequestBody.fromBytes(bytes));

            String finalUrl = endpoint + "/" + bucketName + "/" + key;
            log.info("Upload concluído com sucesso. URL: {}", finalUrl);
            return finalUrl;

        } catch (IOException e) {
            log.error("Erro ao fazer upload de arquivo para o S3/SeaweedFS", e);
            throw new RuntimeException("Falha no upload do arquivo", e);
        }
    }

    @Override
    public void delete(String fileUrl) {
        if (fileUrl == null || fileUrl.isEmpty()) {
            return;
        }

        try {
            // Extrai a chave da URL (presumindo o formato retornado no upload)
            String key = fileUrl.replace(endpoint + "/" + bucketName + "/", "");
            
            s3Client.deleteObject(builder -> builder.bucket(bucketName).key(key));
        } catch (Exception e) {
            log.error("Erro ao deletar arquivo no S3/SeaweedFS: {}", fileUrl, e);
        }
    }
}
