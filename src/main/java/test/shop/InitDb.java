package test.shop;

import jakarta.annotation.PostConstruct;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Component;
import test.shop.domain.*;
import test.shop.domain.item.Album;
import test.shop.domain.item.Book;
import test.shop.web.form.item.BookForm;
import test.shop.web.service.ItemService;
import test.shop.web.service.MemberService;
import test.shop.web.service.OrderService;

@Component
@RequiredArgsConstructor
public class InitDb {

    private final InitService initService;

    @PostConstruct
    public void init() {
        initService.dbInit1();
        initService.dbInit2();
    }

    @Component
    @RequiredArgsConstructor
    static class InitService {

        private final MemberService memberService;
        private final ItemService itemService;

        public void dbInit1() {
            memberService.join(Member.builder().name("userA").address(new Address("서울", "1", "1111")).build());
            memberService.join(Member.builder().name("userB").address(new Address("부산", "2", "2222")).build());
        }

        public void dbInit2() {
            itemService.saveItem(BookForm.builder().name("JPA1 Book").price(10000).stockQuantity(100).author("kim").isbn("123456").build());
            itemService.saveItem(BookForm.builder().name("JPA2 Book").price(20000).stockQuantity(100).author("lee").isbn("123356").build());

        }
    }
}
