package ru.yandex.yandexlavka.business.dtos;

import com.fasterxml.jackson.annotation.JsonFormat;

@JsonFormat(shape = JsonFormat.Shape.STRING)
//public enum CourierType {
//    FOOT, BIKE, AUTO
//}
public enum CourierType {
    FOOT {
        @Override
        public int getMaxWeight() {
            return 10;
        }

        @Override
        public int getMaxQuantity() {
            return 2;
        }

        @Override
        public int getTimeToVisitFirstPoint() {
            return 25;
        }

        @Override
        public int getTimeToVisitNextPoint() {
            return 10;
        }
    },
    BIKE {
        @Override
        public int getMaxWeight() {
            return 20;
        }

        @Override
        public int getMaxQuantity() {
            return 4;
        }

        @Override
        public int getTimeToVisitFirstPoint() {
            return 12;
        }

        @Override
        public int getTimeToVisitNextPoint() {
            return 8;
        }
    },
    AUTO {
        @Override
        public int getMaxWeight() {
            return 40;
        }

        @Override
        public int getMaxQuantity() {
            return 7;
        }

        @Override
        public int getTimeToVisitFirstPoint() {
            return 8;
        }

        @Override
        public int getTimeToVisitNextPoint() {
            return 4;
        }
    };

    public abstract int getMaxWeight();
    public abstract int getMaxQuantity();
    public abstract int getTimeToVisitFirstPoint();
    public abstract int getTimeToVisitNextPoint();
}
