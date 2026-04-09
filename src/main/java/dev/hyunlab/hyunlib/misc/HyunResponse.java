package dev.hyunlab.hyunlib.misc;

import java.util.List;
import java.util.Map;

import jakarta.annotation.Nullable;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;
import lombok.ToString;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
@ToString
public class HyunResponse {
    /** 정상일 때 널값, 오류시 값 존재 */
    @Nullable
    private Integer code;

    /** 정상일 때 널값, 오류시 값 존재 */
    @Nullable
    private String message;

    /**
     * Map<String,Object> or List<Map<String,Object>>
     */
    @Nullable
    private Object data;

    @Nullable
    private Map<String, Object> extra;

    public static HyunResponse ok(Object obj) {
        return HyunResponse.builder()
                .data(obj)
                .build();
    }

    public static HyunResponse ng(String msg) {
        return HyunResponse.builder()
                .code(-1)
                .message(msg)
                .data(null)
                .build();
    }
}
