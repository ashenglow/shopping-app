package test.shop.upload.file;

import jakarta.persistence.*;
import lombok.Getter;
import lombok.Setter;
import test.shop.domain.Board;
import test.shop.web.controller.UploadFileDto;

import java.util.List;

@Entity
@Inheritance(strategy = InheritanceType.JOINED)
@Table(name = "file")
@Getter @Setter
public class UploadFile {

    @Id
    @GeneratedValue
    @Column(name = "file_id")
    private Long id;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "board_id")
    private Board board;

    private String originalFilename;
    private String storeFileName;


    public UploadFile() {
    }


    public UploadFile(String originalFilename, String storeFileName) {
        this.originalFilename = originalFilename;
        this.storeFileName = storeFileName;

    }

     public UploadFileDto UploadFileToDto(UploadFile uploadFile) {
        return new UploadFileDto(uploadFile);
    }



}
