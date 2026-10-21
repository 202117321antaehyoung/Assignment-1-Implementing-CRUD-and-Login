package edu.classroom.board;

import jakarta.servlet.http.HttpServletRequest;
import org.springframework.stereotype.Service;

import io.jsonwebtoken.Claims;
import io.jsonwebtoken.ExpiredJwtException;
import io.jsonwebtoken.Jwts;
import io.jsonwebtoken.SignatureAlgorithm;
import io.jsonwebtoken.security.Keys;
import org.springframework.beans.factory.annotation.Value;
import java.util.Date;
import java.util.HexFormat;

@Service
public class AuthService {

  @Value("${jwt.secret}")
  private String secret;

  @Value("${jwt.expiration-seconds}")
  private long expirationSeconds;

  public String issueToken(Users.PublicUser user) {
    Date now = new Date();
    // 1. 64자리 16진수 문자열인 secret 키를 32바이트 배열로 파싱 (에러 해결 부분)
    byte[] keyBytes = HexFormat.of().parseHex(secret);

    // 2. JWT 공장을 돌려 진짜 토큰을 만들어 반환
    return Jwts.builder()
            .setSubject(String.valueOf(user.id()))// 누구인지 (sub)
            .setIssuer("webservice-class")            // 누가 발급했는지 (iss)
            .setAudience("board-api")          // 누가 쓸 건지 (aud)
            .setExpiration(new Date(now.getTime() + expirationSeconds * 1000))// 언제 만료되는지 (exp)
            .signWith(Keys.hmacShaKeyFor(keyBytes), SignatureAlgorithm.HS256) // 비밀키로 서명
            .compact();
  }

  public long currentUserId(HttpServletRequest request) {
    String authHeader = request.getHeader("Authorization");

    // 1. 헤더 검증
    if (authHeader == null || !authHeader.startsWith("Bearer ")) {
      throw new ApiError(401, "TOKEN_REQUIRED", "인증 토큰이 필요합니다.");
    }

    String token = authHeader.substring(7);

    try {
      byte[] keyBytes = HexFormat.of().parseHex(secret);

      // 2. JJWT 파서 내장 검증 기능(requireIssuer, requireAudience) 활용
      Claims claims = Jwts.parser()
              .verifyWith(Keys.hmacShaKeyFor(keyBytes))
              .requireIssuer("webservice-class")   // iss 검증
              .requireAudience("board-api") // aud 검증 (JJWT 버전에 맞게 자동 처리)
              .build()
              .parseSignedClaims(token)
              .getPayload();

      // 3. 사용자 ID(sub) 반환
      return Long.parseLong(claims.getSubject());

    } catch (ExpiredJwtException e) {
      throw new ApiError(401, "TOKEN_EXPIRED", "토큰이 만료되었습니다.");
    } catch (Exception e) {
      throw new ApiError(401, "INVALID_TOKEN", "유효하지 않은 토큰입니다.");
    }
  }

  public void assertOwner(long userId, long authorId) {
    if (userId != authorId) {
      throw new ApiError(403, "FORBIDDEN", "본인의 게시글만 수정하거나 삭제할 수 있습니다.");
    }
  }
}