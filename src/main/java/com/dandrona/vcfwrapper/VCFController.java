package com.dandrona.vcfwrapper;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.multipart.MultipartFile;

@RestController
public class VCFController {
    @Autowired
    VCFService vcfService;

    @PostMapping("/handleFileUpload")
    public String handleFileUpload(@RequestParam("file") MultipartFile file) {
        return vcfService.handleFileUpload(file);
    }
}
