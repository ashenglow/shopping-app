package test.shop.web.controller;

import lombok.Data;
import test.shop.upload.file.UploadFile;

@Data
public class UploadFileDto {

    private String originalFilename;
    private String storeFileName;


    public UploadFileDto(UploadFile uploadFile) {
        originalFilename = uploadFile.getOriginalFilename();
        storeFileName = uploadFile.getStoreFileName();

    }

     public UploadFile DtoToUploadFile(UploadFileDto dto) {
        return new UploadFile(dto.getOriginalFilename(), dto.getStoreFileName());
    }






}
