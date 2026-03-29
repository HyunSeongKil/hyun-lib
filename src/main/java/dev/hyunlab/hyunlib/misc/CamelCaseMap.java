package dev.hyunlab.hyunlib.misc;

import org.apache.commons.collections4.map.ListOrderedMap;

public class CamelCaseMap<K, V> extends ListOrderedMap<K, V> {

    private static final long serialVersionUID = 6723434363565852261L;

    @SuppressWarnings("unchecked")
    @Override
    public V put(K key, V value) {
        return super.put((K) convertToCamelCase((String) key), value);
    }

    private String convertToCamelCase(String key) {
        // 예: user_id → userId
        StringBuilder sb = new StringBuilder();
        boolean toUpper = false;
        for (char c : key.toCharArray()) {
            if (c == '_') {
                toUpper = true;
            } else {
                sb.append(toUpper ? Character.toUpperCase(c) : Character.toLowerCase(c));
                toUpper = false;
            }
        }
        return sb.toString();
    }

}