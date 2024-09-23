//package test.shop.web.dto;
//
//import lombok.Data;
//import test.shop.domain.Board;
//
//import java.util.List;
//import java.util.stream.Collectors;
//
//@Data
//public class BoardDto {
//
//
//    private Long id;
//
//    private String title;
//
//    private String content;
//
////    private List<UploadFileDto> uploadFiles;
//
//
//    private List<UploadFileDto> imageFiles;
//
//    public BoardDto(Board board) {
//        this.id = board.getId();
//        this.title = board.getTitle();
//        this.content = board.getContent();
//        this.imageFiles = board.getUploadFiles().stream()
//                .map(e -> e.UploadFileToDto(e))
//                .collect(Collectors.toList());
//    }
//
//
//
//}
