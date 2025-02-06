package test.shop.infrastructure.testing.bulk;

import com.fasterxml.jackson.core.JsonProcessingException;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.boot.CommandLineRunner;
import org.springframework.context.annotation.Profile;
import org.springframework.stereotype.Component;
import test.shop.application.dto.request.MemberJoinRequestDto;
import test.shop.application.dto.response.MemberLoginDto;
import test.shop.application.dto.response.UserModelDto;
import test.shop.domain.value.Address;
import test.shop.infrastructure.security.service.AuthService;

import java.io.BufferedWriter;
import java.io.FileWriter;
import java.io.IOException;
import java.util.ArrayList;
import java.util.List;
import java.util.UUID;

@Component
@Profile("test")
@RequiredArgsConstructor
@Slf4j
public class BulkUserInit implements CommandLineRunner {
    private static final int USER_COUNT = 100;
    private static final String CSV_FILE_PATH = "src/test/resources/jmeter/tokens.csv";

    private final AuthService authService;
    @Override
    public void run(String... args) throws Exception {
        try {
            List<String> tokenList = createUsersAndTokens();
            // save tokens to CSV
            writeTokensToCSV(tokenList);
            log.info("Successfully created {} users and saved tokens", USER_COUNT);
        }catch (Exception e){
            log.error("Failed to initialize bulk users", e);
        }
    }

    private List<String> createUsersAndTokens() throws JsonProcessingException {
        List<String> tokenList = new ArrayList<>();
        for(int i = 0; i < USER_COUNT; i++) {
            String username = "user" + i + "_" + UUID.randomUUID();

            // register user
            MemberJoinRequestDto requestDto = buildMemberRequestDto(username);
            authService.register(requestDto);

            // login and get token
            UserModelDto loginDto = authService.login(new MemberLoginDto(username, "password1234"));
            String accessToken = loginDto.getAccessToken();

            tokenList.add(accessToken);
        }
        return tokenList;
    }

    private MemberJoinRequestDto buildMemberRequestDto(String username){
        return MemberJoinRequestDto.builder()
                .userId(username)
                .password("password1234")
                .nickname(username)
                .email(username + "@test.com")
                .address(new Address("06265", "서울 강남구 강남대로 272 (도곡푸르지오)", "112"))
                .build();
    }

    private void writeTokensToCSV(List<String> tokens){
        try {
            BufferedWriter writer = new BufferedWriter(new FileWriter(CSV_FILE_PATH));
            for (String token : tokens) {
                writer.write(token);
                writer.newLine();
            }
            log.info("Access Token 저장 완료: " + CSV_FILE_PATH);
        }catch (IOException e){
            log.error("CSV 저장 실패", e);
            throw new RuntimeException("Failed to write tokens to CSV",e);
        }
    }
}
