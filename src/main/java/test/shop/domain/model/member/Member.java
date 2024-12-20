package test.shop.domain.model.member;

import jakarta.persistence.*;
import lombok.*;
import test.shop.application.dto.request.MemberJoinRequestDto;
import test.shop.domain.value.Address;
import test.shop.domain.model.review.Review;
import test.shop.domain.model.order.Order;
import test.shop.application.dto.request.ProfileDto;
import test.shop.application.dto.response.UserModelDto;

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
    private String password;
    @Enumerated(EnumType.STRING)
    private MemberType memberType;

    private String username;
    private String userImg;

    @Embedded
    private Address address;

    @OneToMany(mappedBy = "member", cascade = CascadeType.ALL, orphanRemoval = true)
    private List<Order> orders = new ArrayList<>();

    @OneToMany(mappedBy = "member", cascade = CascadeType.ALL, orphanRemoval = true)
    private List<Review> reviews = new ArrayList<>();


    @Builder
    public Member(String username, String password, MemberType memberType, Address address) {
        this.password = password;
        this.memberType = memberType;
        this.username = username;
        this.address = address;

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

    public void updateMember(String password, String username, Address address) {
        this.password = password;
        this.username = username;
        this.address = address;
    }

    public MemberJoinRequestDto toMemberJoinRequestDto() {
        return MemberJoinRequestDto.builder()
                .id(this.id)
                .username(this.username)
                .memberType(MemberType.USER)
                .password(this.password)
                .address(this.address)
                .build();
    }

    public MemberJoinRequestDto toAdminJoinRequestDto() {
        return MemberJoinRequestDto.builder()
                .id(this.id)
                .username(this.username)
                .memberType(MemberType.ADMIN)
                .password(this.password)
                .address(this.address)
                .build();
    }


    public UserModelDto toUserModelDto(String accessToken) {
        UserModelDto userModelDto = new UserModelDto();
        userModelDto.setId(id);
        userModelDto.setName(username);
        userModelDto.setRole(memberType.name());
        userModelDto.setUserImg(userImg);
        userModelDto.setAccessToken(accessToken);
        return userModelDto;
    }

    public ProfileDto toProfileDto(Long id) {
        ProfileDto profileDto = new ProfileDto();
        profileDto.setId(id);
        profileDto.setUsername(username);
        profileDto.setPassword(password);
        profileDto.setAddress(address);
        return profileDto;
    }


}
