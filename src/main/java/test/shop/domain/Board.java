package test.shop.domain;

import jakarta.persistence.*;
import lombok.Getter;
import lombok.Setter;
import test.shop.upload.file.UploadFile;
import test.shop.web.BoardForm;

import java.util.ArrayList;
import java.util.List;

@Entity
@Table(name = "board")
@Getter @Setter
public class Board extends BaseEntity{

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "board_id")
    private Long id;

    private String title;

    private String content;

    @OneToMany(mappedBy = "board", cascade = CascadeType.ALL, orphanRemoval = true)
    private List<UploadFile> uploadFiles = new ArrayList<>();


    public Board() {
    }

    public Board(String title, String content) {
        this.title = title;
        this.content = content;
    }

    public void update(String title, String content) {
        this.title = title;
        this.content = content;
    }

    public void removeFile(UploadFile uploadFile) {
        uploadFiles.remove(uploadFile);
        uploadFile.setBoard(null);
    }

    public void addFile(UploadFile uploadFile) {
        uploadFiles.add(uploadFile);
        uploadFile.setBoard(this);
    }




}
