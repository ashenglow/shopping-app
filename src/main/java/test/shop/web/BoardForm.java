package test.shop.web;

import lombok.Getter;
import lombok.Setter;
import org.springframework.web.multipart.MultipartFile;
import test.shop.domain.Board;

import java.util.List;

@Getter
@Setter
public class BoardForm {


    private Long id;
    private String title;
    private String content;
    private List<MultipartFile> imageFiles;

    public BoardForm() {
    }

    public BoardForm(Board board) {
        this.id = board.getId();
        this.title = board.getTitle();
        this.content = board.getContent();
    }

    public BoardForm(Long id, String title, String content) {
        this.id = id;
        this.title = title;
        this.content = content;
    }
}
