package test.shop;

import jakarta.annotation.PostConstruct;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Component;
import test.shop.domain.*;
import test.shop.domain.item.Album;
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

        public void dbInit1() {
            memberService.join(Member.builder().name("userA").address(new Address("서울", "1", "1111")).build());
            memberService.join(Member.builder().name("userB").address(new Address("부산", "2", "2222")).build());
        }

        public void dbInit2() {
            Album.builder().name("The Astronaut").price(16000).stockQuantity(100).artist("Jin").etc("etc").build();
            Album.builder().name("Moon").price(16000).stockQuantity(100).artist("Jin").etc("etc").build();

        }
    }
}
