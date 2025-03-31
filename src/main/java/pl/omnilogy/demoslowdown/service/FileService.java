package pl.omnilogy.demoslowdown.service;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;

import java.io.IOException;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.atomic.AtomicLong;

@Service
@Slf4j
@RequiredArgsConstructor
public class FileService {

    private static final Map<Long, byte[]> map = new ConcurrentHashMap<>();
    private final AtomicLong counter = new AtomicLong(1);
    private final PropertiesService propertiesService;

    public void saveFile(MultipartFile file) {
        log.info("Putting file of size {} KB to map", file.getSize() / 1000);
        try {
            map.put(counter.getAndIncrement(), file.getBytes());
        } catch (IOException e) {
            throw new RuntimeException(e);
        }
        long totalSize = 0;
        for (var f : map.values()) {
            totalSize += f.length;
        }
        log.info("Current files size: {}", calculateSize(totalSize));
        if (propertiesService.isSizeLimitEnabled() && totalSize > propertiesService.getFileSizeLimit()) {
            log.info("Map size limit {} exceeded, clearing map", propertiesService.getFileSizeLimit());
            map.clear();
        }
    }

    private String calculateSize(long totalSize) {
        if (totalSize < 1024) {
            return totalSize + " B";
        } else if (totalSize < 1024 * 1024) {
            return totalSize / 1024 + " KB";
        } else {
            return totalSize / (1024 * 1024) + " MB";
        }
    }
}
