package top.mappland.chat.util;

import io.jsonwebtoken.Claims;
import io.jsonwebtoken.ExpiredJwtException;
import io.jsonwebtoken.JwtException;
import io.jsonwebtoken.Jwts;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;
import top.mappland.chat.config.JwtConfig;
import top.mappland.chat.model.vo.Response;

import javax.annotation.PostConstruct;
import javax.crypto.SecretKey;
import javax.crypto.spec.SecretKeySpec;
import java.util.Base64;
import java.util.Date;

@Component
public class JwtUtils {

    private static JwtConfig jwtConfig;

    @Autowired
    private JwtConfig config;

    @PostConstruct
    public void init() {
        jwtConfig = config;
    }

    private static SecretKey getSecretKey() {
        byte[] decodedKey = Base64.getDecoder().decode(jwtConfig.getSecret());
        return new SecretKeySpec(decodedKey, 0, decodedKey.length, "HmacSHA512");
    }

    /**
     * 生成JWT Token
     *
     * @param username 用户名
     * @param uid      用户ID
     * @return 生成的Token
     */
    public static String generateToken(String username, Long uid) {
        return Jwts.builder()
                .setSubject(username)
                .claim("uid", uid)
                .setIssuedAt(new Date())
                .setExpiration(new Date(System.currentTimeMillis() + 86400000)) // 1天有效期
                .signWith(getSecretKey())
                .compact();
    }

    /**
     * 验证并解析JWT Token
     *
     * @param token Token
     * @return 解析后的Claims
     */
    public static Claims parseToken(String token) {
        return Jwts.parserBuilder()
                .setSigningKey(getSecretKey())
                .build()
                .parseClaimsJws(token)
                .getBody();
    }

    public static <T> Response<T> validateJwt(String token, Long expectedUid) {
        try {
            Claims claims = parseToken(token);
            Long tokenUid = claims.get("uid", Long.class);

            if (!tokenUid.equals(expectedUid)) {
                return Response.error(401, "Token的UID与当前用户UID不符合");
            }
            return Response.success(null);
        } catch (ExpiredJwtException e) {
            return Response.error(401, "Token已过期");
        } catch (JwtException e) {
            return Response.error(400, "Token解析失败");
        }
    }
}
