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
            initService.dbInit1();
        }
    }

    public void reinitialize(){
        memberRepository.deleteAll();
        itemRepository.deleteAll();
        reviewRepository.deleteAll();

        init();
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


        public void dbInit1() {
            // Admin user
            MemberJoinRequestDto adminUser = createAdminJoinRequestDto("user1","user1", "user1@gmail.com", "1234", new Address("03175", "서울 종로구 경희궁길 6", "6"));
            // Regular users
            List<MemberJoinRequestDto> initialUsers = Arrays.asList(
                    createMemberJoinRequestDto("user2", "user2", "user2@gmail.com", "1234", new Address("06265", "서울 강남구 강남대로 272 (도곡푸르지오)", "112")),
                    createMemberJoinRequestDto("soju_lover", "soju_lover","soju_lover@gmail.com","1234", new Address("04398", "서울 용산구 녹사평대로 66 (용산푸르지오파크타운)", "2")),
                    createMemberJoinRequestDto("wine_enthusiast", "wine_enthusiast","wine_enthusiast@gmail.com", "user5678", new Address("48060", "부산 해운대구 APEC로 17 (센텀리더스마크)", "3")),
                    createMemberJoinRequestDto("tradition_seeker","tradition_seeker",  "tradition_seeker@gmail.com","user9012", new Address("21999", "인천 연수구 갯벌로 27 (인천대학교 이노베이션센터)", "4")),
                    createMemberJoinRequestDto("casual_drinker", "casual_drinker","casual_drinker@gmail.com","user3456", new Address("42222", "대구 수성구 가창로221길 36-1 (드림팰리스)", "5")),
                    createMemberJoinRequestDto("soju_expert", "soju_expert","soju_expert@gmail.com","user7890", new Address("62070", "광주 서구 개금길 27-13", "6")),
                    createMemberJoinRequestDto("first_timer", "first_timer","first_timer@gmail.com","user2345", new Address("34128", "대전 유성구 가정로 8", "7")),
                    createMemberJoinRequestDto("beer_novice","beer_novice","beer_novice@gmail.com", "user6789", new Address("44717", "울산 남구 갈밭로 4", "8")),
                    createMemberJoinRequestDto("fruit_wine_lover", "fruit_wine_lover","fruit_wine_lover@gmail.com","user0123", new Address("30130", "세종특별자치시 나리로 57 (첫마을7단지)", "9")),
                    createMemberJoinRequestDto("wine_connoisseur","wine_connoisseur",  "wine_connoisseur@gmail.com", "user4567", new Address("63275", "제주특별자치도 제주시 가령골길 1", "10"))
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
            /**
             * Create & Save Items
             */

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

            /**
             * Create & save Reviews
             */

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
        private MemberJoinRequestDto createMemberJoinRequestDto(String userId, String nickname, String email, String password, Address address) {
            return MemberJoinRequestDto.builder()
                    .userId(userId)
                    .nickname(nickname)
                    .email(email)
                    .password(password)
                    .memberType(MemberType.USER)
                    .address(address)
                    .build();

        }

        private MemberJoinRequestDto createAdminJoinRequestDto(String userId, String nickname, String email, String password, Address address) {
            return MemberJoinRequestDto.builder()
                    .userId(userId)
                    .nickname(nickname)
                    .email(email)
                    .password(password)
                    .memberType(MemberType.ADMIN)
                    .address(address)
                    .build();





        }


        private ProductDto createProduct(String name, int price, int stock,
                                         String description, Category category, double ratings, int numOfReviews, String url) {
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

        private ReviewDto createReview(Long productId, Long memberId, String nickname,
                                       double rating, String comment) {
            return ReviewDto.builder()
                    .productId(productId)
                    .memberId(memberId)
                    .nickname(nickname)
                    .rating(rating)
                    .comment(comment)
                    .build();

        }
    }

}
