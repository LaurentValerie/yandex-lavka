package ru.yandex.yandexlavka.business.dtos;

import com.fasterxml.jackson.annotation.JsonFormat;

@JsonFormat(shape = JsonFormat.Shape.STRING)
public enum CourierType {
    FOOT, BIKE, AUTO
}
//public enum CourierType {
//    FOOT {
//        @Override
//        public int getMaxWeight() {
//            return 10;
//        }
//    },
//    BIKE {
//        @Override
//        public int getMaxWeight() {
//            return 20;
//        }
//    },
//    AUTO {
//        @Override
//        public int getMaxWeight() {
//            return 40;
//        }
//    };
//
//    public abstract int getMaxWeight();
//}
