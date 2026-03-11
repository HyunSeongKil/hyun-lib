package dev.hyunlab.hyunlib.misc;

import lombok.Data;
import lombok.Builder;
import lombok.NoArgsConstructor;
import lombok.ToString;
import lombok.AllArgsConstructor;
import java.util.List;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
@ToString
public class HyunResponse<T> {
    private int code;
    private String message;
    private T data;
    private List<T> datas;

    public static <T> HyunResponse<T> ok() {
        return HyunResponse.<T>builder()
                .code(0)
                .message("ok")
                .build();
    }

    public static <T> HyunResponse<T> data(T data) {
        return HyunResponse.<T>builder()
                .code(0)
                .message("ok")
                .data(data)
                .build();
    }

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
