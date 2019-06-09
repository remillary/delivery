package dev.muskrat.delivery.converter;

import dev.muskrat.delivery.dao.order.Order;
import dev.muskrat.delivery.dao.order.OrderProduct;
import dev.muskrat.delivery.dao.shop.Shop;
import dev.muskrat.delivery.dao.shop.ShopRepository;
import dev.muskrat.delivery.dto.order.OrderCreateDTO;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Component;

import java.util.List;
import java.util.stream.Collectors;

@Component
@RequiredArgsConstructor
public class OrderCreateDTOTOOrderConverter implements ObjectConverter<OrderCreateDTO, Order> {

    private final OrderProductDTOTOOrderProductConverter orderProductDTOTOOrderProductConverter;
    private final ShopRepository shopRepository;

    @Override
    public Order convert(OrderCreateDTO dto) {
        Long shopId = dto.getShopId();
        Shop shop = shopRepository.findById(shopId).get();

        Order order = new Order();
        order.setName(dto.getName());
        order.setComments(dto.getComment());
        order.setPhone(dto.getPhone());
        order.setEmail(dto.getEmail());
        order.setAddress(dto.getAddress());
        order.setShop(shop);

        List<OrderProduct> collect = dto.getProducts().stream()
            .map(orderProductDTOTOOrderProductConverter::convert)
            .collect(Collectors.toList());
        order.setProducts(collect);

        return order;
    }
}
