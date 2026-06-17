package br.arthconf.fortivus.service;

import org.springframework.web.multipart.MultipartFile;

public interface FileStorageService {
    String upload(MultipartFile file, String path);
    void delete(String fileUrl);
}
