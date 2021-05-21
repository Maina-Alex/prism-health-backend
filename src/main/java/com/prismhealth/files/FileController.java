package com.prismhealth.files;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.core.io.Resource;
import org.springframework.http.HttpHeaders;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;

import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;
import org.springframework.web.servlet.support.ServletUriComponentsBuilder;

import javax.servlet.http.HttpServletRequest;

import com.prismhealth.util.UtilityFunctions;

@RestController
@RequestMapping("/files")
@CrossOrigin
public class FileController {

        @Autowired
        private FileSystemStorageService storageService;

        @GetMapping("/download/{filename:.+}")
        @CrossOrigin
        public ResponseEntity<Resource> downloadFile(@PathVariable String filename, HttpServletRequest request) {

                Resource resource = storageService.loadAsResource(filename);
                String contentType = null;
                try {
                        contentType = request.getServletContext().getMimeType(resource.getFile().getAbsolutePath());
                } catch (Exception ex) {
                        ex.printStackTrace();
                }
                if (contentType == null) {
                        contentType = "application/octet-stream";
                }

                return ResponseEntity.ok().contentType(MediaType.parseMediaType(contentType))
                                .header(HttpHeaders.CONTENT_DISPOSITION,
                                                "attachment; filename=\"" + resource.getFilename() + "\"")
                                .body(resource);
        }

        @PostMapping("/upload")
        @CrossOrigin
        public FileResponse uploadFile(@RequestParam("file") MultipartFile file) {

                storageService.setPrefix(UtilityFunctions.getRandomString());
                String name = storageService.store(file);

                String uri = ServletUriComponentsBuilder.fromCurrentContextPath().path("/files/download/").path(name)
                                .toUriString();

                return new FileResponse(name, uri, file.getContentType(), file.getSize());
        }

}