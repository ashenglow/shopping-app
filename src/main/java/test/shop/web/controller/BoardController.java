package test.shop.web.controller;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.core.io.Resource;
import org.springframework.core.io.UrlResource;
import org.springframework.http.HttpHeaders;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.util.ObjectUtils;
import org.springframework.validation.BindingResult;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.util.UriUtils;
import test.shop.upload.file.StoreFile;
import test.shop.web.BoardForm;
import test.shop.web.service.BoardService;

import java.io.IOException;
import java.net.MalformedURLException;
import java.nio.charset.StandardCharsets;
import java.util.List;


@Controller
@Slf4j
@RequiredArgsConstructor
public class BoardController {

    private final BoardService boardService;
    private final StoreFile storeFile;



    @GetMapping("/board/write")
    public String boardWriteForm(Model model) {

        model.addAttribute("board", new BoardForm());
        return "board";
    }

    @PostMapping("/board/write")
    public String boardWrite(BoardForm form, BindingResult result) throws IOException {


        if (result.hasErrors()) {
            return "board";
        }

        boardService.write(form);
        log.info("form={}", form);
        return "redirect:/boards";
    }

    @GetMapping("/board/delete")
    public String boardDelete(@RequestParam("id") Long id) {
        boardService.delete(id);
        return "redirect:/boards";
    }


    @GetMapping("/boards")
    public String list(Model model) {
        model.addAttribute("boards", boardService.findBoards());
        return "boardList";
    }

    @GetMapping("/boards/{id}/detail")
    public String boardDetail(@PathVariable("id") Long id, Model model) {
        model.addAttribute("board", boardService.findOne(id));
        return "boardDetail";
    }

    @GetMapping("/board/{id}/edit")
    public String boardEditForm(@PathVariable("id") Long id, Model model) {
        List<UploadFileDto> files = boardService.previewFile(id);
        if (!ObjectUtils.isEmpty(files)) {
        model.addAttribute("files", files);
        }
        model.addAttribute("board", boardService.editForm(id));
        log.info("ok");
        return "boardEditForm";
    }

    @PostMapping("/board/{id}/edit")
    public String boardEdit(@PathVariable Long id, BoardForm form) throws IOException {

        boardService.update(id,form);
        log.info("ok");

        return "redirect:/boards";
    }

    @ResponseBody
    @GetMapping("/images/{filename}")
    public Resource downloadImage(@PathVariable String filename) throws MalformedURLException {
        return new UrlResource("file:" + storeFile.getFullPath(filename));
    }
//
//    @GetMapping("/attach/{id}")
//    public ResponseEntity<Resource> downloadAttach(@PathVariable Long id) throws MalformedURLException {
//        UploadFileDto attachFile = boardService.findOne(id).getAttachFile();
//        UrlResource resource = new UrlResource("file:" + storeFile.getFullPath(attachFile.getStoreFileName()));
//
//        String encodedUploadFileName = UriUtils.encode(attachFile.getOriginalFilename(), StandardCharsets.UTF_8);
//        String contentDisposition = "attachment; filename=\"" +
//                encodedUploadFileName + "\"";
//        return ResponseEntity.ok()
//                .header(HttpHeaders.CONTENT_DISPOSITION, contentDisposition)
//                .body(resource);
//
//        List<UploadFileDto> imageFiles = boardService.findOne(id).getImageFiles();
//        for (UploadFileDto imageFile : imageFiles) {
//            UrlResource resource = new UrlResource("file:" + storeFile.getFullPath(filename));
//
//        }
//
//    }
}
