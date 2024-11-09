package test.shop;

import com.fasterxml.jackson.core.JsonProcessingException;
import jakarta.annotation.PostConstruct;
import lombok.RequiredArgsConstructor;
import org.springframework.boot.autoconfigure.info.ProjectInfoAutoConfiguration;
import org.springframework.stereotype.Component;
import test.shop.application.dto.request.MemberJoinRequestDto;
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
import test.shop.infrastructure.security.service.AuthService;

import java.util.Arrays;
import java.util.List;

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

        private final AuthService authService;
        private final ItemService itemService;
        private final ReviewService reviewService;


        public void memberInit() {
            // Admin user
            MemberJoinRequestDto adminUser = createAdminJoinRequestDto("user1", "1234", new Address("Seoul", "Jongno-gu", "03181"));
            // Regular users
            List<MemberJoinRequestDto> initialUsers = Arrays.asList(
                    createMemberJoinRequestDto("user2", "1234", new Address("Seoul", "Gangnam-gu", "06001")),
                    createMemberJoinRequestDto("soju_lover", "1234", new Address("Seoul", "Yongsan-gu", "06001")),
                    createMemberJoinRequestDto("wine_enthusiast", "user5678", new Address("Busan", "Haeundae-gu", "48099")),
                    createMemberJoinRequestDto("tradition_seeker", "user9012", new Address("Incheon", "Yeonsu-gu", "21999")),
                    createMemberJoinRequestDto("casual_drinker", "user3456", new Address("Daegu", "Suseong-gu", "42188")),
                    createMemberJoinRequestDto("soju_expert", "user7890", new Address("Gwangju", "Seo-gu", "61949")),
                    createMemberJoinRequestDto("first_timer", "user2345", new Address("Daejeon", "Yuseong-gu", "34126")),
                    createMemberJoinRequestDto("beer_novice", "user6789", new Address("Ulsan", "Nam-gu", "44701")),
                    createMemberJoinRequestDto("fruit_wine_lover", "user0123", new Address("Sejong", "Hansol-dong", "30151")),
                    createMemberJoinRequestDto("wine_connoisseur", "user4567", new Address("Jeju", "Jeju-si", "63122"))
            );
            for (MemberJoinRequestDto dto : initialUsers) {
                try {
                    authService.register(dto);
                }catch(Exception e) {
                    // Add logging here
                    System.err.println("Failed to save member ");
                    e.printStackTrace();
                }
            }
            try {
                authService.adminRegister(adminUser);
            } catch (Exception e) {
                // Add logging here
                System.err.println("Failed to save admin member ");
                e.printStackTrace();
            }


        }
        private MemberJoinRequestDto createMemberJoinRequestDto(String name, String password, Address address) {
            return MemberJoinRequestDto.builder()
                    .username(name)
                    .password(password)
                    .memberType(MemberType.USER)
                    .address(address)
                    .build();

        }

        private MemberJoinRequestDto createAdminJoinRequestDto(String name, String password, Address address) {
            return MemberJoinRequestDto.builder()
                    .username(name)
                    .password(password)
                    .memberType(MemberType.ADMIN)
                    .address(address)
                    .build();

        }

        public void itemInit() {
            List<ProductDto> initialItems = Arrays.asList(
                    createProduct("Jipyeong Makgeolli", 8000, 200,
                            "Smooth and slightly sweet traditional rice wine", Category.TAKJU,5,50, "https://i.ibb.co/TvL4xvn/c6c3e71f-fe4f-4bfb-b80a-d00d349ade54.png"),
                    createProduct("Seoul Lygang-ju", 12000, 150,
                            "Premium sparkling makgeolli with a crisp finish", Category.TAKJU, 4,30,"https://i.ibb.co/BGVMHjh/f14dc02b-186e-4055-8f38-854535936e1b.png"),
                    createProduct("Gyeongju Gyodong Beopju", 25000, 100,
                            "Clear and refined rice wine with a subtle aroma", Category.YAKJU,5,40,
                            "https://i.ibb.co/JyFwjxZ/75c533f7-bb15-475d-9fdd-c1ae53e906f0.png"
                    ),
                    createProduct("Baekwha Gukhwaju", 30000, 80,
                            "Chrysanthemum-infused traditional medicinal wine", Category.YAKJU,4,25,
                            "https://i.ibb.co/qx4FFBh/13121887-8949-4674-92ab-5870baf1e059.png"),
                    createProduct("Andong Soju", 18000, 120,
                            "Strong traditional distilled soju from Andong",Category.SOJU,3,60,
                            "https://i.ibb.co/cNmqQM3/0e42a167-f5f5-4b63-ab9e-9d6eda564f22.png"),
                    createProduct("Hwayo 41", 35000, 90,
                            "Premium soju distilled from Korean rice", Category.SOJU,5,45,
                            "https://i.ibb.co/0KVpdPL/26deb0dd-d6ab-4368-a6cd-a65d44c0f886.png"),
                    createProduct("Jeju Wit Ale", 4000, 300,
                            "Craft beer made with Jeju tangerines",Category.BEER,2,70,
                            "https://i.ibb.co/wpT1Nk1/35cb1586-1a60-4acd-aed7-669a440dbeb3.png"),
                    createProduct("Gompyo Wheat Beer", 3500, 250,
                            "Light and refreshing beer made with Korean wheat",Category.BEER,3,55,
                            "https://i.ibb.co/y0FXsyW/363b74fb-e667-49db-a4cd-09823a5b7139.png"),
                    createProduct("Sansawon Peach", 22000, 110,
                            "Sweet and fruity wine made from Korean peaches",Category.WINE,4,35,
                            "https://i.ibb.co/3SRcGcH/447417d6-a55f-487d-a363-9073679efbef.png"),
                    createProduct("Soole Melon Wine", 28000, 95,
                            "Unique wine made from melon",Category.WINE,3,20,
                            "https://i.ibb.co/KNt0GRs/fe8ad9b4-6841-424d-85f6-c95deb6a5c38.png")



            );

            for (ProductDto dto : initialItems) {
                try {
                    itemService.saveItem(dto);
                } catch (Exception e) {
                    // Add logging here
                    System.err.println("Failed to save item: " + dto.getName());
                    e.printStackTrace();
                }
            }
        }

        private ProductDto createProduct(String name, int price, int stock,
                                         String description, Category category, int ratings, int numOfReviews, String url) {
            ProductDto dto = ProductDto.builder()
                    .name(name)
                    .price(price)
                    .stockQuantity(stock)
                    .description(description)
                    .category(category)
                    .ratings(ratings)
                    .numOfReviews(numOfReviews)
                    .build();

            dto.addImage(url);
            return dto;
        }


        public void reviewInit() {

            List<ReviewDto> initialReviews = Arrays.asList(
                    // Reviews for Jipyeong Makgeolli
                    createReview(1L, 1L, "wine_enthusiast", 5,
                            "Smooth and delicious, perfect with Korean BBQ!"),
                    createReview(1L, 2L, "tradition_seeker", 4,
                            "Interesting flavor, but a bit too sweet for my taste."),
                    // Reviews for Gyeongju Gyodong Beopju
                    createReview(3L, 3L, "tradition_seeker", 5,
                            "Exquisite yakju with a rich history. A must-try!"),
                    createReview(3L, 4L, "casual_drinker", 4,
                            "Smooth and easy to drink. Great for special occasions."),
                    // Reviews for Andong Soju
                    createReview(5L, 5L, "soju_expert", 5,
                            "The best traditional soju I've ever had. Strong but smooth."),
                    createReview(5L, 6L, "first_timer", 3,
                            "Whoa, this is strong! Definitely an acquired taste."),
                    // Reviews for Jeju Wit Ale
                    createReview(7L, 7L, "craft_beer_fan", 4,
                            "Refreshing citrus notes. Perfect for summer!"),
                    createReview(7L, 8L, "beer_novice", 5,
                            "I don't usually like beer, but this one is fantastic!"),
                    // Reviews for Sansawon Bokbunja
                    createReview(9L, 9L, "fruit_wine_lover", 5,
                            "Delightfully sweet and fruity. A perfect dessert wine."),
                    createReview(9L, 10L, "wine_connoisseur", 4,
                            "Unique flavor profile. Interesting representation of Korean wine.")

            );
            for (ReviewDto dto : initialReviews) {
                try {
                    reviewService.saveReview(dto);
                }catch (Exception e) {
                    // Add logging here
                    System.err.println("Failed to save review");
                    e.printStackTrace();
                }

            }

        }


        private ReviewDto createReview(Long productId, Long userId, String username,
                                       int rating, String comment) {
            return ReviewDto.builder()
                    .productId(productId)
                    .userId(userId)
                    .username(username)
                    .rating(rating)
                    .comment(comment)
                    .build();

        }
    }

}
