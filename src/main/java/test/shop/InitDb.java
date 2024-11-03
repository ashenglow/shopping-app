package test.shop;

import jakarta.annotation.PostConstruct;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Component;
import test.shop.domain.model.item.Category;
import test.shop.domain.model.member.Member;
import test.shop.domain.model.member.MemberType;
import test.shop.domain.value.Address;
import test.shop.application.dto.request.ProductDto;
import test.shop.application.dto.request.ReviewDto;
import test.shop.domain.repository.ItemRepository;
import test.shop.domain.repository.MemberRepository;
import test.shop.domain.repository.ReviewRepository;
import test.shop.application.service.item.ItemService;
import test.shop.application.service.member.MemberService;
import test.shop.application.service.review.ReviewService;

@Component
@RequiredArgsConstructor
public class InitDb {

   private final InitService initService;
    private final MemberRepository memberRepository;
    private final ItemRepository itemRepository;
    private final ReviewRepository reviewRepository;

    @PostConstruct
    public void init() {
        if (isDatabaseEmpty()) {
            initService.memberInit();
            initService.itemInit();
            initService.reviewInit();
        }
    }

    private boolean isDatabaseEmpty() {
        return memberRepository.count() == 0 &&
               itemRepository.count() == 0 &&
               reviewRepository.count() == 0;
    }

    @Component
    @RequiredArgsConstructor
    static class InitService {

        private final MemberService memberService;
        private final ItemService itemService;
        private final ReviewService reviewService;

        public void memberInit() {
           // Admin user
    memberService.join(Member.builder()
        .username("user1")
        .password("1234")
        .memberType(MemberType.ADMIN)
        .address(new Address("Seoul", "Jongno-gu", "03181"))
        .build());

    // Regular users
    memberService.join(Member.builder()
        .username("user2")
        .password("1234")
        .memberType(MemberType.USER)
        .address(new Address("Seoul", "Gangnam-gu", "06001"))
        .build());
     memberService.join(Member.builder()
        .username("soju_lover")
        .password("1234")
        .memberType(MemberType.USER)
        .address(new Address("Seoul", "Yongsan-gu", "06001"))
        .build());

    memberService.join(Member.builder()
        .username("wine_enthusiast")
        .password("user5678")
        .memberType(MemberType.USER)
        .address(new Address("Busan", "Haeundae-gu", "48099"))
        .build());

    memberService.join(Member.builder()
        .username("tradition_seeker")
        .password("user9012")
        .memberType(MemberType.USER)
        .address(new Address("Incheon", "Yeonsu-gu", "21999"))
        .build());

    memberService.join(Member.builder()
        .username("casual_drinker")
        .password("user3456")
        .memberType(MemberType.USER)
        .address(new Address("Daegu", "Suseong-gu", "42188"))
        .build());

    memberService.join(Member.builder()
        .username("soju_expert")
        .password("user7890")
        .memberType(MemberType.USER)
        .address(new Address("Gwangju", "Seo-gu", "61949"))
        .build());

    memberService.join(Member.builder()
        .username("first_timer")
        .password("user2345")
        .memberType(MemberType.USER)
        .address(new Address("Daejeon", "Yuseong-gu", "34126"))
        .build());

    memberService.join(Member.builder()
        .username("beer_novice")
        .password("user6789")
        .memberType(MemberType.USER)
        .address(new Address("Ulsan", "Nam-gu", "44701"))
        .build());

    memberService.join(Member.builder()
        .username("fruit_wine_lover")
        .password("user0123")
        .memberType(MemberType.USER)
        .address(new Address("Sejong", "Hansol-dong", "30151"))
        .build());

    memberService.join(Member.builder()
        .username("wine_connoisseur")
        .password("user4567")
        .memberType(MemberType.USER)
        .address(new Address("Jeju", "Jeju-si", "63122"))
        .build());
        }

        public void itemInit() {
            // TAKJU
    ProductDto makgeolli = ProductDto.builder()
        .name("Jipyeong Makgeolli")
        .price(8000)
        .description("Smooth and slightly sweet traditional rice wine")
        .stockQuantity(200)
        .ratings(5)
        .numOfReviews(50)
        .category(Category.TAKJU)
        .build();

    ProductDto seoullygangju = ProductDto.builder()
        .name("Seoul Lygang-ju")
        .price(12000)
        .description("Premium sparkling makgeolli with a crisp finish")
        .stockQuantity(150)
        .ratings(4)
        .numOfReviews(30)
        .category(Category.TAKJU)
        .build();

    // YAKJU
    ProductDto cheongju = ProductDto.builder()
        .name("Gyeongju Gyodong Beopju")
        .price(25000)
        .description("Clear and refined rice wine with a subtle aroma")
        .stockQuantity(100)
        .ratings(5)
        .numOfReviews(40)
        .category(Category.YAKJU)
        .build();

    ProductDto gukhwaju = ProductDto.builder()
        .name("Baekwha Gukhwaju")
        .price(30000)
        .description("Chrysanthemum-infused traditional medicinal wine")
        .stockQuantity(80)
        .ratings(4)
        .numOfReviews(25)
        .category(Category.YAKJU)
        .build();

    // SOJU
    ProductDto andongSoju = ProductDto.builder()
        .name("Andong Soju")
        .price(18000)
        .description("Strong traditional distilled soju from Andong")
        .stockQuantity(120)
        .ratings(3)
        .numOfReviews(60)
        .category(Category.SOJU)
        .build();

    ProductDto hwayo = ProductDto.builder()
        .name("Hwayo 41")
        .price(35000)
        .description("Premium soju distilled from Korean rice")
        .stockQuantity(90)
        .ratings(5)
        .numOfReviews(45)
        .category(Category.SOJU)
        .build();

    // BEER
    ProductDto jeju = ProductDto.builder()
        .name("Jeju Wit Ale")
        .price(4000)
        .description("Craft beer made with Jeju tangerines")
        .stockQuantity(300)
        .ratings(2)
        .numOfReviews(70)
        .category(Category.BEER)
        .build();

    ProductDto gompyo = ProductDto.builder()
        .name("Gompyo Wheat Beer")
        .price(3500)
        .description("Light and refreshing beer made with Korean wheat")
        .stockQuantity(250)
        .ratings(3)
        .numOfReviews(55)
        .category(Category.BEER)
        .build();

    // WINE
    ProductDto bokbunja = ProductDto.builder()
        .name("Sansawon Bokbunja")
        .price(22000)
        .description("Sweet and fruity wine made from Korean black raspberries")
        .stockQuantity(110)
        .ratings(4)
        .numOfReviews(35)
        .category(Category.WINE)
        .build();

    ProductDto omija = ProductDto.builder()
        .name("Seolwangsan Omija Wine")
        .price(28000)
        .description("Unique wine made from the five-flavor berry of Mt. Seolwang")
        .stockQuantity(95)
        .ratings(3)
        .numOfReviews(20)
        .category(Category.WINE)
        .build();

    // Save all items
    itemService.saveItem(makgeolli);
    itemService.saveItem(seoullygangju);
    itemService.saveItem(cheongju);
    itemService.saveItem(gukhwaju);
    itemService.saveItem(andongSoju);
    itemService.saveItem(hwayo);
    itemService.saveItem(jeju);
    itemService.saveItem(gompyo);
    itemService.saveItem(bokbunja);
    itemService.saveItem(omija);
        }

        public void reviewInit() {
            // Reviews for Jipyeong Makgeolli
    ReviewDto review1 = ReviewDto.builder()
        .username("wine_enthusiast")
        .userId(1L)
        .rating(5)
        .comment("Smooth and delicious, perfect with Korean BBQ!")
        .productId(1L)
        .build();

    ReviewDto review2 = ReviewDto.builder()
        .username("tradition_seeker")
        .userId(2L)
        .rating(4)
        .comment("Interesting flavor, but a bit too sweet for my taste.")
        .productId(1L)
        .build();

    // Reviews for Gyeongju Gyodong Beopju
    ReviewDto review3 = ReviewDto.builder()
        .username("tradition_seeker")
        .userId(3L)
        .rating(5)
        .comment("Exquisite yakju with a rich history. A must-try!")
        .productId(3L)
        .build();

    ReviewDto review4 = ReviewDto.builder()
        .username("casual_drinker")
        .userId(4L)
        .rating(4)
        .comment("Smooth and easy to drink. Great for special occasions.")
        .productId(3L)
        .build();

    // Reviews for Andong Soju
    ReviewDto review5 = ReviewDto.builder()
        .username("soju_expert")
        .userId(5L)
        .rating(5)
        .comment("The best traditional soju I've ever had. Strong but smooth.")
        .productId(5L)
        .build();

    ReviewDto review6 = ReviewDto.builder()
        .username("first_timer")
        .userId(6L)
        .rating(3)
        .comment("Whoa, this is strong! Definitely an acquired taste.")
        .productId(5L)
        .build();

    // Reviews for Jeju Wit Ale
    ReviewDto review7 = ReviewDto.builder()
        .username("craft_beer_fan")
        .userId(7L)
        .rating(4)
        .comment("Refreshing citrus notes. Perfect for summer!")
        .productId(7L)
        .build();

    ReviewDto review8 = ReviewDto.builder()
        .username("beer_novice")
        .userId(8L)
        .rating(5)
        .comment("I don't usually like beer, but this one is fantastic!")
        .productId(7L)
        .build();

    // Reviews for Sansawon Bokbunja
    ReviewDto review9 = ReviewDto.builder()
        .username("fruit_wine_lover")
        .userId(9L)
        .rating(5)
        .comment("Delightfully sweet and fruity. A perfect dessert wine.")
        .productId(9L)
        .build();

    ReviewDto review10 = ReviewDto.builder()
        .username("wine_connoisseur")
        .userId(10L)
        .rating(4)
        .comment("Unique flavor profile. Interesting representation of Korean wine.")
        .productId(9L)
        .build();

    // Save all reviews
    reviewService.saveReview(review1, 1L);
    reviewService.saveReview(review2, 1L);
    reviewService.saveReview(review3, 3L);
    reviewService.saveReview(review4, 3L);
    reviewService.saveReview(review5, 5L);
    reviewService.saveReview(review6, 5L);
    reviewService.saveReview(review7, 7L);
    reviewService.saveReview(review8, 7L);
    reviewService.saveReview(review9, 9L);
    reviewService.saveReview(review10, 9L);
        }
    }
}
