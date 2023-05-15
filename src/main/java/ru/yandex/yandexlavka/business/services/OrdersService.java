package ru.yandex.yandexlavka.business.services;

import jakarta.transaction.Transactional;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import ru.yandex.yandexlavka.business.dtos.CourierType;
import ru.yandex.yandexlavka.business.dtos.OrderDTO;
import ru.yandex.yandexlavka.business.entities.AssignedOrdersGroup;
import ru.yandex.yandexlavka.business.entities.Courier;
import ru.yandex.yandexlavka.business.entities.Order;
import ru.yandex.yandexlavka.business.services.mappers.DtoToOrderMapper;
import ru.yandex.yandexlavka.business.services.mappers.OrderToDtoMapper;
import ru.yandex.yandexlavka.persistance.CouriersRepository;
import ru.yandex.yandexlavka.persistance.OrdersRepository;

import java.time.LocalTime;
import java.util.*;

@Service
public class OrdersService {
    private final CouriersRepository couriersRepository;
    private final OrdersRepository ordersRepository;
    private final OrderToDtoMapper orderToDtoMapper;
    private final DtoToOrderMapper dtoToOrderMapper;

    @Autowired
    public OrdersService(CouriersRepository couriersRepository, OrdersRepository ordersRepository,
                         OrderToDtoMapper orderToDtoMapper, DtoToOrderMapper dtoToOrderMapper) {
        this.couriersRepository = couriersRepository;
        this.ordersRepository = ordersRepository;
        this.orderToDtoMapper = orderToDtoMapper;
        this.dtoToOrderMapper = dtoToOrderMapper;
    }

    @Deprecated
    public Optional<OrderDTO> saveOrUpdate(OrderDTO orderDTO) {
        Order order = dtoToOrderMapper.toOrder(orderDTO);
        var saved = ordersRepository.save(order);
        return Optional.ofNullable(orderToDtoMapper.toDto(saved));
    }

    @Transactional
    public List<OrderDTO> saveOrUpdateAll(List<OrderDTO> orderDTOs) {
        List<Order> orders = dtoToOrderMapper.toOrders(orderDTOs);
        orders = (List<Order>) ordersRepository.saveAll(orders);
        return orderToDtoMapper.toDtos(orders);
    }

    public Optional<OrderDTO> getOrderById(Long id) {
        return ordersRepository.findById(id).map(orderToDtoMapper::toDto);
    }

    public List<OrderDTO> getOrders(int limit, int offset) {
        // Более классическая версия offset, сдвиг по страницам
//        Pageable pageable = PageRequest.of(offset, limit);
//        List<Order> orders = ordersRepository.findAll(pageable).getContent();

        // По заданию offset - Количество заказов, которое нужно пропустить для отображения текущей страницы
        List<Order> orders = ordersRepository.findOrdersWithLimitAndOffset(offset, limit);
        return orderToDtoMapper.toDtos(orders);
    }

    @Deprecated
    public void assignOrdersToCouriers() {
        // Используем локальную мапу так как нам не требуется сохранять информацию о составлении групп заказов
        Map<Long, GroupOrdersInfo> assignanceInfoMap = new HashMap<>();
        List<Order> orders = ordersRepository.findByAssignedCourierIsNullOrderByDeliveryStartAsc();
        for (Order order : orders) {
            boolean isAssigned = false;
            // Выбираем курьеров работающих в этом районе, начиная с курьеров с большей грузоподъемностью (по их типу)
            List<Courier> couriers = couriersRepository.findByRegionsContainingOrderByCourierTypeDescAndOrderByStartTimeList(order.getRegions());
            for (Courier courier : couriers) {
                CourierType courierType = courier.getCourierType();
                long courierId = courier.getId();
                // Берем информацию о группах заказов текущего курьера
                GroupOrdersInfo groupOrdersInfo;
                if (assignanceInfoMap.containsKey(courierId)) {
                    groupOrdersInfo = assignanceInfoMap.get(courierId);
                } else { // Или создаем новую
                    groupOrdersInfo = new GroupOrdersInfo();
                    assignanceInfoMap.put(courierId, groupOrdersInfo);
                }
                // Итерируемся по интервалам работы курьера которые подходят для этого заказа
                for (Integer timeDurIndex: getRightTimeIntervals(courier, order)) {
                    // СОЗДАТЬ НОВОЕ
                    LocalTime courierStartTime = courier.getStartTimeList().get(timeDurIndex);
                    LocalTime courierEndTime = courier.getEndTimeList().get(timeDurIndex);
                    // Берем группы заказов для этого интервала времени
                    List<AssignedOrdersGroup> assignedOrdersGroups = groupOrdersInfo.assignedOrdersGroups.get(timeDurIndex);
                    // Берем соответствущие каждой группе регионы доставки
                    List<List<Integer>> groupsRegions = groupOrdersInfo.groupsRegions.get(timeDurIndex);
                    // Берем соответсвующее каждой группе времена окончаний доставки
                    List<LocalTime> endGroupTimes = groupOrdersInfo.endGroupTime.get(timeDurIndex);
                    // Берем соответсвующее каждой группе текущий вес
                    List<Float> curGroupWeight = groupOrdersInfo.curGroupWeight.get(timeDurIndex);
                    for (int i = 0; i < assignedOrdersGroups.size(); i++) {
                        AssignedOrdersGroup curGroup = assignedOrdersGroups.get(i);
                        List<Integer> curRegions = groupsRegions.get(i);
                        LocalTime curEndTime = endGroupTimes.get(i);
                        Float curWeight = curGroupWeight.get(i);
                        // Проверяем может ли курьер взять еще заказ в группу
                        if (curGroup.getOrdersIds().size() == courierType.getMaxQuantity()) continue;
                        // Проверяем может ли курьер взять заказ по весу
                        if (curWeight + order.getWeight() > courierType.getMaxWeight()) continue;
                        // Если курьер доставляет первый заказ или первый заказ в новый район это занимает больше времени
                        if (curGroup.getOrdersIds().size() == 0
                                || isSameRegion(curRegions, order.getRegions())) {
                            LocalTime newEndGroupTime = curEndTime.plusMinutes(courierType.getTimeToVisitFirstPoint());
                            if (newEndGroupTime.isBefore(courierEndTime)) {
                                // Check if all
                                curGroup.getOrdersIds().add(order.getId());
                                endGroupTimes.set(i, newEndGroupTime);
                                curGroupWeight.set(i, curWeight + order.getWeight());
                                curRegions.add(order.getRegions());
                                isAssigned = true;
                            }
                        } else {
                            LocalTime newEndGroupTime = curEndTime.plusMinutes(courierType.getTimeToVisitNextPoint());
                            if (newEndGroupTime.isBefore(courierEndTime)) {
                                // Check if all
                                curGroup.getOrdersIds().add(order.getId());
                                endGroupTimes.set(i, newEndGroupTime);
                                curGroupWeight.set(i, curWeight + order.getWeight());
                                curRegions.add(order.getRegions());
                                isAssigned = true;
                            }
                        }
                    }
                    if (!isAssigned) {
                        var lastGroup = assignedOrdersGroups.get(assignedOrdersGroups.size() - 1);
                        var lastTime = endGroupTimes.get(endGroupTimes.size() - 1);
                        LocalTime newEndGroupTime = lastTime.plusMinutes(courierType.getTimeToVisitFirstPoint());
                        if (newEndGroupTime.isBefore(courierEndTime)) {
                            // Создать новую assignedOrderGroup
                            var newGroup = new AssignedOrdersGroup();

                            newGroup.setCourier_id(courierId);
                            List<Long> newOrderList = new ArrayList<>();
                            newOrderList.add(order.getId());
                            newGroup.setOrdersIds(newOrderList);
                            assignedOrdersGroups.add(newGroup);
                        }
                    }
                    // Заказ не получилось добавить в существующие группы, пробуем создать новую
                    // check time for first order and if ok create new group
                    // don't forget to add this group to groupOrderInfo and to courier
                }
                // Если заказ назначен курьеру берем следующий
                if (isAssigned) break;
            }
        }
    }

    private class GroupOrdersInfo {
        private long courierId;
        private final List<List<AssignedOrdersGroup>> assignedOrdersGroups;
        private final List<List<List<Integer>>> groupsRegions;
        private final List<List<Float>> curGroupWeight;
        private final List<List<LocalTime>> endGroupTime;

        public GroupOrdersInfo() {
            this.assignedOrdersGroups = new ArrayList<>();
            this.groupsRegions = new ArrayList<>();
            this.curGroupWeight = new ArrayList<>();
            this.endGroupTime = new ArrayList<>();
        }
    }

    private List<Integer> getRightTimeIntervals(Courier courier, Order order) {
        List<Integer> res = new ArrayList<>();
        int courierTimePtr = 0;
        int orderTimePtr = 0;
        while (courierTimePtr < courier.getStartTimeList().size() || orderTimePtr < order.getDeliveryStart().size()) {
            if (order.getDeliveryEnd().get(orderTimePtr).isBefore(courier.getStartTimeList().get(courierTimePtr))) {
                orderTimePtr++;
                continue;
            }
            if (courier.getEndTimeList().get(courierTimePtr).isBefore(order.getDeliveryStart().get(orderTimePtr))) {
                courierTimePtr++;
                continue;
            }
            res.add(courierTimePtr);
        }
        return res;
    }

    private boolean isSameRegion(List<Integer> prevRegion, int newRegion) {
        return prevRegion.get(prevRegion.size() - 1) == newRegion;
    }
}
