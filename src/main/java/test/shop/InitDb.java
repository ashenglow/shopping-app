package test.shop;

import jakarta.annotation.PostConstruct;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Component;
import test.shop.domain.*;
import test.shop.domain.item.Category;
import test.shop.web.dto.ProductDto;
import test.shop.web.dto.ReviewDto;
import test.shop.web.service.ItemService;
import test.shop.web.service.MemberService;
import test.shop.web.service.ReviewService;

@Component
@RequiredArgsConstructor
public class InitDb {

    private final InitService initService;

    @PostConstruct
    public void init() {
        initService.memberInit();
        initService.itemInit();
        initService.reviewInit();
    }

    @Component
    @RequiredArgsConstructor
    static class InitService {

        private final MemberService memberService;
        private final ItemService itemService;
        private final ReviewService reviewService;

        public void memberInit() {
            memberService.join(Member.builder().username("user1").password("1234").memberType(MemberType.ADMIN).address(new Address("Seoul", "Gangnam", "123-123")).build());
            memberService.join(Member.builder().username("user2").password("1234").memberType(MemberType.USER).address(new Address("Seoul", "Gangbuk", "123-123")).build());
        }

        public void itemInit() {
            ProductDto item1 = test.shop.web.dto.ProductDto.builder().name("Makgeolli").price(10000).description("Korean Traditional Wine").stockQuantity(100).ratings(4).numOfReviews(10).category(Category.TAKJU).build();
            ProductDto item2 = test.shop.web.dto.ProductDto.builder().name("Andong Soju").price(15000).description("Korean Traditional Wine").stockQuantity(80).ratings(4).numOfReviews(5).category(Category.SOJU).build();
            itemService.saveItem(item1);
            itemService.saveItem(item2);
        }

        public void reviewInit(){

            ReviewDto review1 = ReviewDto.builder().username("user1").userId(1L).rating(5).comment("Good").productId(1L).build();
            ReviewDto review2 = ReviewDto.builder().username("user2").userId(2L).rating(4).comment("Not bad").productId(2L).build();
            reviewService.saveReview(review1, 1L);
            reviewService.saveReview(review2, 2L);
        }


    }
}
