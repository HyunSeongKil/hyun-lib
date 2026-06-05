package dev.hyunlab.hyunlib.misc;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.springframework.beans.BeanUtils;

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

    public Map<String, Object> toMap() {
        Map<String, Object> map = new HashMap<>();
        BeanUtils.copyProperties(this, map);

        return map;
    }
}
