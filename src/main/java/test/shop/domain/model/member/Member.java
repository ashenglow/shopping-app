package test.shop.domain.model.member;

import jakarta.persistence.*;
import lombok.*;
import test.shop.application.dto.request.MemberJoinRequestDto;
import test.shop.domain.value.Address;
import test.shop.domain.model.review.Review;
import test.shop.domain.model.order.Order;
import test.shop.application.dto.request.ProfileDto;
import test.shop.application.dto.response.UserModelDto;

import java.security.PrivateKey;
import java.util.ArrayList;
import java.util.List;

@Entity
@Getter
@RequiredArgsConstructor
public class Member {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "member_id")
    private Long id;
    // Authentication identifiers
    @Column(unique = true)
    private String userId; // login identifier
    @Column(unique = true)
    private String email; // primary identifier for Oauth2


    private String password;
    private String nickname;
    private String userImg;

    @Enumerated(EnumType.STRING)
    private MemberType memberType;

    // Oauth2 fields
    @Column(nullable = true)
    private String provider;
    @Column(nullable = true)
    private String providerId;

    @Embedded
    private Address address;

    @OneToMany(mappedBy = "member", cascade = CascadeType.ALL, orphanRemoval = true)
    private List<Order> orders = new ArrayList<>();

    @OneToMany(mappedBy = "member", cascade = CascadeType.ALL, orphanRemoval = true)
    private List<Review> reviews = new ArrayList<>();


    @Builder
    public Member(String userId, String email, String password, String nickname, MemberType memberType, Address address,
                 String provider, String providerId) {
        this.userId = userId;
        this.email = email;
        this.password = password;
        this.nickname = nickname;
        this.memberType = memberType;
        this.address = address;
        this.provider = provider;  // Default to null
        this.providerId = providerId;

    }

    public void addUserImg(String url){
        this.userImg = url;
    }

    public void addOrder(Order order) {
        orders.add(order);
    }

    public void addReview(Review review) {
        reviews.add(review);
    }

    public void updateMember(String password, String nickname, Address address, String email) {
        this.password = password;
        this.nickname = nickname;
        this.address = address;
        this.email = email;
    }

    public MemberJoinRequestDto toMemberJoinRequestDto() {
        return MemberJoinRequestDto.builder()
                .id(this.id)
                .userId(this.userId)
                .memberType(MemberType.USER)
                .password(this.password)
                .address(this.address)
                .build();
    }

    public MemberJoinRequestDto toAdminJoinRequestDto() {
        return MemberJoinRequestDto.builder()
                .id(this.id)
                .userId(this.userId)
                .memberType(MemberType.ADMIN)
                .password(this.password)
                .address(this.address)
                .build();
    }


    public UserModelDto toUserModelDto(String accessToken) {
        UserModelDto userModelDto = new UserModelDto();
        userModelDto.setId(id);
        userModelDto.setUserId(userId);
        userModelDto.setEmail(email);
        userModelDto.setNickname(nickname);
        userModelDto.setRole(memberType.name());
        userModelDto.setUserImg(userImg);
        userModelDto.setAccessToken(accessToken);
        return userModelDto;
    }

    public ProfileDto toProfileDto(Long id) {
        ProfileDto profileDto = new ProfileDto();
        profileDto.setId(id);
        profileDto.setUserId(userId);
        profileDto.setNickname(nickname);
        profileDto.setEmail(email);
        profileDto.setPassword(password);
        profileDto.setAddress(address);
        return profileDto;
    }


}
