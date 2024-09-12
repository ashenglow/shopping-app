//package test.shop.web.service;
//
//import lombok.RequiredArgsConstructor;
//import lombok.extern.slf4j.Slf4j;
//import org.springframework.stereotype.Service;
//import org.springframework.transaction.annotation.Transactional;
//import org.springframework.util.ObjectUtils;
//import org.springframework.web.multipart.MultipartFile;
//import test.shop.domain.Board;
//import test.shop.upload.file.UploadFile;
//import test.shop.web.dto.BoardForm;
//import test.shop.web.dto.BoardDto;
//import test.shop.web.dto.UploadFileDto;
//import test.shop.web.repository.BoardRepository;
//import test.shop.web.repository.FileRepository;
//
//import java.io.IOException;
//import java.util.Collections;
//import java.util.List;
//import java.util.Optional;
//import java.util.stream.Collectors;
//
//@Service
//@Transactional
//@RequiredArgsConstructor
//@Slf4j
//public class BoardService {
//
//
//    private final BoardRepository boardRepository;
//    private final FileRepository fileRepository;
//    private final FileService fileService;
//
//
//    public void write(BoardForm form) throws IOException {
//
//        Board board = new Board(form.getTitle(), form.getContent());
//        List<MultipartFile> imageFiles = form.getImageFiles();
//        //파일 저장
//            if (!ObjectUtils.isEmpty(imageFiles)) {
//            List<UploadFile> uploadFiles = fileService.createUploadFile(imageFiles);
//            for (UploadFile uploadFile : uploadFiles) {
//                fileService.associateFileWithBoard(board, uploadFile);
//            }
//            }
//            boardRepository.save(board);
//
//    }
//
//    public List<BoardDto> findBoards() {
//        List<Board> boards = boardRepository.findAll();
//        return boards.stream()
//                .map(BoardDto::new)
//                .collect(Collectors.toList());
//    }
//    public BoardDto findOne(Long id) {
//        Board board = boardRepository.findBoardDistinctById(id);
//        return new BoardDto(board);
//    }
//
//    public String delete(Long id) {
//        boardRepository.deleteById(id);
//        return "ok";
//    }
//
//    public BoardForm editForm(Long id) {
//        Board board = boardRepository.findById(id).orElseThrow();
//        return new BoardForm(board);
//    }
//
//    public List<UploadFileDto> previewFile(Long id) {
//
//        List<UploadFile> files = fileRepository.findUploadFilesDistinctByBoard_Id(id);
//            return Optional.ofNullable(files)
//                    .orElseGet(Collections::emptyList).stream()
//                    .map(UploadFileDto::new)
//                    .toList();
//    }
//
//
//    public String update(Long id, BoardForm form) throws IOException {
//
//        List<MultipartFile> imageFiles = form.getImageFiles();
//        Board board = boardRepository.findBoardDistinctById(id);
//        if (!ObjectUtils.isEmpty(imageFiles)) {
//            List<UploadFile> uploadFiles = fileService.createUploadFile(imageFiles);
//            fileService.modifyFile(board, uploadFiles);
//        }
//        board.update(form.getTitle(), form.getContent());
//
//        return "ok";
//    }
//
//
//
//
//
//
//
//}
