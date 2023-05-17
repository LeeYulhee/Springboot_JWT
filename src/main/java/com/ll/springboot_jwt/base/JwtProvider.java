package com.ll.springboot_jwt.base;

import com.ll.springboot_jwt.util.Ut;
import io.jsonwebtoken.Jwts;
import io.jsonwebtoken.SignatureAlgorithm;
import io.jsonwebtoken.security.Keys;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;

import javax.crypto.SecretKey;
import java.util.Base64;
import java.util.Date;
import java.util.Map;

@Component
// 스프링 프레임워크에서 사용하는 어노테이션으로 이 클래스가 컴포넌트임을 나타냄.
// 스프링이 이 클래스를 자동으로 빈으로 등록하게 됨.
public class JwtProvider {

    private SecretKey cachedSecretKey;
    // 생성한 비밀키를 저장할 변수

    @Value("${custom.jwt.secretKey}")
    private String secretKeyPlain;
    // application.properties 파일에서 custom.jwt.secretKey 값을 읽어와 secretKeyPlain 변수에 저장
    // @Value 어노테이션을 사용하여 프로퍼티 값을 주입 받음

    private SecretKey _getSecretKey() {
        String keyBase64Encoded = Base64.getEncoder().encodeToString(secretKeyPlain.getBytes());
        return Keys.hmacShaKeyFor(keyBase64Encoded.getBytes());
    }
    // 비밀키를 생성하는 비공개 메서드
    // Base64 인코딩을 사용하여 문자열을 바이트 배열로 변환하고 이를 다시 문자열로 변환
    // 이 문자열을 바이트 배열로 변환한 다음, 이를 사용하여 HMAC SHA 키를 생성

    public SecretKey getSecretKey() {
        if (cachedSecretKey == null) cachedSecretKey = _getSecretKey();

        return cachedSecretKey;
    }
    // 비밀키를 반환하는 공개 메서드
    // 비밀키가 아직 생성되지 않은 경우 비공개 메서드를 사용하여 생성하고 반환

    public String genToken(Map<String, Object> claims, int seconds) {
        long now = new Date().getTime();
        Date accessTokenExpiresIn = new Date(now + 1000L * seconds);

        return Jwts.builder()
                .claim("body", Ut.json.toStr(claims))
                .setExpiration(accessTokenExpiresIn)
                .signWith(getSecretKey(), SignatureAlgorithm.HS512)
                .compact();
    }
    // JWT 토큰을 생성하는 메서드
    // 사용자가 제공하는 클레임을 받아 이를 JWT 토큰으로 변환
    // 토큰은 사용자가 지정한 시간(초 단위) 후에 만료

    public boolean verify(String token) {
        try {
            Jwts.parserBuilder()
                    .setSigningKey(getSecretKey())
                    .build()
                    .parseClaimsJws(token);
        } catch (Exception e) {
            return false;
        }

        return true;
    }
    // JWT 토큰의 유효성을 검사하는 메서드
    // 토큰을 파싱하려고 시도하고, 실패하면 false를 반환

    public Map<String, Object> getClaims(String token) {
        String body = Jwts.parserBuilder()
                .setSigningKey(getSecretKey())
                .build()
                .parseClaimsJws(token)
                .getBody()
                .get("body", String.class);

        return Ut.json.toMap(body);
    }
    // JWT 토큰에서 클레임을 추출하는 메서드
    // 토큰을 파싱하고, "body" 클레임을 추출하여 이를 Map 객체로 변환
}
