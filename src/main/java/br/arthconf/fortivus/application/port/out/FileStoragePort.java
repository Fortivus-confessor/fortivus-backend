package br.arthconf.fortivus.application.port.out;

import org.springframework.web.multipart.MultipartFile;

public interface FileStoragePort {
    String upload(MultipartFile file, String path);
    void delete(String fileUrl);
}
