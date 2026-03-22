package dev.hyunlab.hyunlib.misc;

import java.util.List;

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
public class HyunResponse<T> {
    /** 정상일 때 널값, 오류시 값 존재 */
    @Nullable
    private Integer code;

    /** 정상일 때 널값, 오류시 값 존재 */
    @Nullable
    private String message;

    @Nullable
    private T data;

    @Nullable
    private List<T> datas;

    public static <T> HyunResponse<T> ok() {
        return HyunResponse.<T>builder()
                .build();
    }

    @SuppressWarnings("unchecked")
    public static <T> HyunResponse<T> ok(Object data) {
        if (data instanceof List) {
            return HyunResponse.<T>builder()
                    .datas((List<T>) data)
                    .build();
        } else {
            return HyunResponse.<T>builder()
                    .data((T) data)
                    .build();
        }
    }

    @Deprecated(since = "20260321", forRemoval = true)
    public static <T> HyunResponse<T> data(T data) {
        return HyunResponse.<T>builder()
                .code(0)
                .message("ok")
                .data(data)
                .build();
    }

    @Deprecated(since = "20260321", forRemoval = true)
    public static <T> HyunResponse<T> datas(List<T> datas) {
        return HyunResponse.<T>builder()
                .code(0)
                .message("ok")
                .datas(datas)
                .build();
    }

    public static HyunResponse<Object> ng(String msg) {
        return HyunResponse.builder()
                .code(-1)
                .message(msg)
                .build();
    }
}
