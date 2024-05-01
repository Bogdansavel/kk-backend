package com.example.kkbackend;

import org.junit.jupiter.api.Test;
import org.springframework.boot.test.context.SpringBootTest;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.Comparator;
import java.util.List;


class KkbackendApplicationTests {

    @Test
    void contextLoads() {
        int m = 1;
        List<Integer> price = Arrays.asList(1, 2, 3);
        long cost = 0;
        price.sort(Comparator.reverseOrder());
        for (int i = 0; i < price.size(); i++) {
            double doubleEl = price.get(i).doubleValue();
            long post = 0;
            int o = 0;
            while ((doubleEl >= 1) && (o < m) && (m > 0)) {
                post = (long)Math.floor(doubleEl % 2);
                doubleEl = doubleEl / 2L;
                o++;
            }
            if (post == 1) {
                m = m - o;
                cost += Math.floor(doubleEl);
            }
            price.set(i, 0);
        }

        for (Integer element : price) {
            double doubleEl = element.doubleValue();
            int o = 0;
            while ((doubleEl != 0) && (doubleEl >= 1) && (o < m) && (m > 0)) {
                doubleEl = doubleEl / 2;
                o++;
            }
            m = m - o;
            cost += Math.floor(doubleEl);
        }
    }

}
