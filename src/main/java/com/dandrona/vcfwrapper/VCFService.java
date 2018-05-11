package com.dandrona.vcfwrapper;

import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;

@Service("vcfService")
public class VCFService {

    public String handleFileUpload(MultipartFile file) {
        return MainApplication.getInstance().handleFileUpload(file);
    }
}