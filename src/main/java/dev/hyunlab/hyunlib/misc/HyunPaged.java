package dev.hyunlab.hyunlib.misc;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class HyunPaged<T> {
    private String code;

    private String message;

    private int page;

    private int size;

    private long total;

    private List<T> data;

    public Map<String, Object> toMutableMap() {
        Map<String, Object> map = new HashMap<>();
        map.put("code", code);
        map.put("message", message);
        map.put("page", page);
        map.put("size", size);
        map.put("total", total);
        map.put("data", data);

        return map;
    }
}
