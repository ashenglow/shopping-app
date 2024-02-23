package test.shop;

import lombok.RequiredArgsConstructor;
import org.springframework.boot.context.event.ApplicationReadyEvent;
import org.springframework.context.event.EventListener;
import org.springframework.stereotype.Component;
import org.springframework.transaction.annotation.Transactional;
import test.shop.domain.Board;
import test.shop.web.repository.BoardRepository;

//@Component
//@RequiredArgsConstructor
//public class DbInit {
//
//    private final BoardRepository boardRepository;
//
//    @Transactional
//    @EventListener(ApplicationReadyEvent.class)
//    public void init() {
//        boardRepository.save(new Board("test1", "test1"));
//        boardRepository.save(new Board("test2", "test2"));
//        boardRepository.save(new Board("test3", "test3"));
//    }
//}
