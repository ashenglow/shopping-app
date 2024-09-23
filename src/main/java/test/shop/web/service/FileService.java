//package test.shop.web.service;
//
//import lombok.RequiredArgsConstructor;
//import org.springframework.stereotype.Service;
//import org.springframework.transaction.annotation.Transactional;
//import org.springframework.web.multipart.MultipartFile;
//import test.shop.domain.Board;
//import test.shop.upload.file.StoreFile;
//import test.shop.upload.file.UploadFile;
//
//import java.io.IOException;
//import java.util.List;
//
//@Service
//@Transactional
//@RequiredArgsConstructor
//public class FileService {
//
//    private final StoreFile storeFile;
//
//    public List<UploadFile> createUploadFile(List<MultipartFile> imageFiles) throws IOException {
//        return storeFile.storeFiles(imageFiles);
//    }
//
//    public void associateFileWithBoard(Board board, UploadFile uploadFile) {
//        board.addFile(uploadFile);
//    }
//
//    public void modifyFile(Board board, List<UploadFile> modifiedFiles) {
//        List<UploadFile> existingFiles = board.getUploadFiles();
//        existingFiles.clear();
//        for (UploadFile modifiedFile : modifiedFiles) {
//            existingFiles.add(modifiedFile);
//            modifiedFile.setBoard(board);
//        }
//
//    }
//
//
//}
