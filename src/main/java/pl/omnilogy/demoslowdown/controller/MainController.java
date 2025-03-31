package pl.omnilogy.demoslowdown.controller;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.boot.logging.LogLevel;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.multipart.MultipartFile;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;
import pl.omnilogy.demoslowdown.service.FileService;
import pl.omnilogy.demoslowdown.service.PropertiesService;
import pl.omnilogy.demoslowdown.utils.DynamicTaskScheduler;
import pl.omnilogy.demoslowdown.utils.LoggingLevelChanger;


@Controller
@RequiredArgsConstructor
@Slf4j
public class MainController {

    private final FileService fileService;
    private final PropertiesService propertiesService;
    private final DynamicTaskScheduler dynamicTaskScheduler;
    private final LoggingLevelChanger loggingLevelChanger;

    @GetMapping("/")
    public String home(Model model) {
        model.addAttribute("sendInterval", propertiesService.getSendInterval());
        model.addAttribute("sendSize", propertiesService.getSizeInKb());
        model.addAttribute("fileSizeLimit", propertiesService.getFileSizeLimit());
        model.addAttribute("sizeListenerEnabled", propertiesService.isSizeLimitEnabled());
        model.addAttribute("loggingLevelString", loggingLevelChanger.getRootLogLevel().toString());
        return "index";
    }

    @PostMapping("/upload")
    public String uploadFile(@RequestParam("file") MultipartFile file, RedirectAttributes redirectAttributes) {
        if (file.isEmpty()) {
            redirectAttributes.addFlashAttribute("message", "Please select a file to upload");
            return "redirect:/";
        }
        fileService.saveFile(file);

        return "redirect:/";
    }

    @PostMapping("/updateProperties")
    public String updateProperties(@RequestParam Long sendInterval,
                                   @RequestParam Integer sendSize,
                                   @RequestParam Long fileSizeLimit,
                                   @RequestParam boolean sizeListenerEnabled,
                                   @RequestParam LogLevel loggingLevel,
                                   RedirectAttributes redirectAttributes) {
        log.info("Updating properties: sendInterval={}, sendSize={}, fileSizeLimit={}, sizeListenerEnabled={}",
                sendInterval, sendSize, fileSizeLimit, sizeListenerEnabled);
        propertiesService.setSizeInKb(sendSize);
        propertiesService.setFileSizeLimit(fileSizeLimit);
        propertiesService.setSizeLimitEnabled(sizeListenerEnabled);
        dynamicTaskScheduler.changeRate(sendInterval);
        loggingLevelChanger.changeRootLogLevel(loggingLevel);

        redirectAttributes.addFlashAttribute("message", "Properties updated successfully");
        return "redirect:/";
    }
}