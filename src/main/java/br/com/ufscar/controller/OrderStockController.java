package br.com.ufscar.controller;

import java.math.BigDecimal;
import java.util.ArrayList;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import br.com.ufscar.dao.GenericDAO;
import br.com.ufscar.dao.OrderDAO;
import br.com.ufscar.dao.UserDAO;
import br.com.ufscar.entity.Item;
import br.com.ufscar.entity.ItemOrder;
import br.com.ufscar.entity.Orderr;
import br.com.ufscar.entity.User;
import br.com.ufscar.enums.OrderType;

public class OrderStockController {
	
	private GenericDAO dao;
	private OrderDAO orderDAO;
	private UserDAO userDAO;
	private Map<Long, Integer> preOrdem;
	private ItemMovimentationController itemController;
	//TODO: Solicitar valor do pruduto na tela e processar entrada de valor. 
	//Analogia a entrada informando o valor da nota fiscal de compra.
	private BigDecimal valorUnitario = new BigDecimal("1.99");

	public OrderStockController() {
		dao = new GenericDAO();
		orderDAO = new OrderDAO();
		preOrdem = new HashMap<>();
		itemController = new ItemMovimentationController();
		userDAO = new UserDAO();
	}
	
	public void verifyOrder(Orderr order, User userAdmin) {
		if (order != null && order.getItems() != null) {
			for (ItemOrder itemOrder : order.getItems()) {
				Item item = null;
				if(itemOrder.getItem() != null) {
					item = dao.find(Item.class, itemOrder.getItem().getId());
				}
				try {
					itemController.entrada(userAdmin, item, itemOrder.getQuantity(), valorUnitario);
				} catch (Exception e) {
					e.printStackTrace();
					order.setUserAdmin(userAdmin);
					order.setAsRejected();
					dao.update(order);
					return;
				}
			}
			
			order.setUserAdmin(userAdmin);
			order.setAsPacking();
			dao.update(order);
		}
		
	}
	
	public void requireOrderStock(User userAdmin) {
		if (!preOrdem.isEmpty()) {
			List<ItemOrder> itemOrders = new ArrayList<>();
			for (Long id : preOrdem.keySet()) {
				Item item = dao.find(Item.class, id);
				Integer quantity = preOrdem.get(id);
				ItemOrder itemOrder = new ItemOrder();
				itemOrder.setItem(item);
				itemOrder.setQuantity(quantity);
				itemOrders.add(itemOrder);
			}
			
			Orderr order = new Orderr();
			order.setDate(new Date());
			order.setItems(new ArrayList<ItemOrder>());
			order.setItems(itemOrders);
			order.setUserAdmin(userAdmin);
			order.setType(OrderType.STOCK);
			order.setAsSent();
			dao.save(order);
		}
		
	}
	
	public void closeOrder(Orderr order) {
		order.setAsDelivered();
	}

	public void includeItemStock(Item item, Integer quantity) {
		preOrdem.put(item.getId(), quantity);
	}
	
	public List<Orderr> ordersByAdmin(User userAdmin) {
		return orderDAO.listOrderByAdmin(userDAO.findUserByEmail(userAdmin.getEmail()));
	}
	
}
