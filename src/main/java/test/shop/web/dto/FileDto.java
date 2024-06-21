package test.shop.web.dto;

import lombok.Data;

import java.util.List;

@Data
public class FileDto {

    private Long id;
    List<UploadFileDto> imageFiles;

    public FileDto() {
    }

    public FileDto(Long id, List<UploadFileDto> imageFiles) {

        this.imageFiles = imageFiles;
    }
}
