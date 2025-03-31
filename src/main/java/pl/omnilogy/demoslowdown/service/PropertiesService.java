package pl.omnilogy.demoslowdown.service;

import lombok.Getter;
import lombok.Setter;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;

@Service
@Getter
@Setter
public class PropertiesService {

    @Value("${application.file.size.limit:0}")
    private Long fileSizeLimit;

    @Value("${application.file.size.listener.enabled:false}")
    private boolean isSizeLimitEnabled;

    @Value("${application.send.size}")
    private Integer sizeInKb;

    @Value("${application.send.interval}")
    private Long sendInterval;
}
