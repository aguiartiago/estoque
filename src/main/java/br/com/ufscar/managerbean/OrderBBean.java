package br.com.ufscar.managerbean;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import javax.annotation.PostConstruct;
import javax.faces.bean.ManagedBean;
import javax.faces.bean.SessionScoped;
import javax.faces.context.FacesContext;
import javax.servlet.http.HttpSession;

import br.com.ufscar.controller.OrderController;
import br.com.ufscar.dao.OrderDAO;
import br.com.ufscar.dao.UserDAO;
import br.com.ufscar.entity.Item;
import br.com.ufscar.entity.ItemOrder;
import br.com.ufscar.entity.Orderr;
import br.com.ufscar.entity.User;


@SessionScoped
@ManagedBean
public class OrderBBean implements Serializable {

	OrderController orderController;
	UserDAO dao;
	OrderDAO orderDAO;
	
	private ItemOrder itemOrder;
	
	private Item item;
	
	private Orderr orderSelected;
	
	private Map<Long, ItemOrder> itemsSelected;

	public OrderBBean() {
		super();
		this.init();
	}
	
	@PostConstruct
	private void init() {
		orderController = new OrderController();
		dao = new UserDAO();
		orderDAO = new OrderDAO();
		itemsSelected = new HashMap<>();
		orderSelected = null;
	}
	
	public List<Orderr> getOrders() {
		return orderController.ordersByClient(getUserLogged());
	}

	public List<Item> getListItems() {
		return dao.findAll(Item.class);
	}

	private User getUserLogged() {
		try {
			return (User) getSession().getAttribute("user");
		} catch (Exception e) {
			e.printStackTrace();
			return null;
		}
	}
	
	private HttpSession getSession() {
		return (HttpSession) FacesContext.getCurrentInstance().getExternalContext().getSession(false);
	}
	
	public void aumenta() {
		itemOrder = itemsSelected.get(item.getId());
		if (itemOrder.getQuantity() == null) {
			itemOrder.setQuantity(1);
		} else {
			Integer qtd = itemOrder.getQuantity();
			itemOrder.setQuantity(++qtd);
		}
	}
	
	public void diminui() {
		itemOrder = itemsSelected.get(item.getId());
		if (itemOrder.getQuantity() != null && itemOrder.getQuantity() > 0 ) {
			Integer qtd = itemOrder.getQuantity();
			itemOrder.setQuantity(--qtd);
		}
	}
	
	public String adiciona() {
		if (itemsSelected.get(item.getId())==null){
			itemOrder = new ItemOrder();
			itemOrder.setItem(item);
			itemsSelected.put(item.getId(), itemOrder);
			
		}
		aumenta();
		return "";
	}
	
	public List<ItemOrder> getItemsPedido() {
		List<ItemOrder> itens = new ArrayList<ItemOrder>(itemsSelected.values());
		return itens;
	}
	
	public String enviarPedido() {
		for (ItemOrder item : itemsSelected.values()) {
			orderController.includeItem(item.getItem(), item.getQuantity());
		}
		orderController.requireOrder(getUserLogged());
		this.init();
		return "/pages/protected/defaultUser/lista-pedidos.xhtml";
	}
	
	public String verificar() {
		orderController.verifyOrder(orderSelected, getUserLogged());
		return "";
	}
	
	
	
	public List<Orderr> getOrdersToVerify() {
		return orderDAO.listOrdersToVerify();
	}
	
	public List<Orderr> getAllOrders() {
		return dao.findAll(Orderr.class);
	}
	
	//getters and setters 

	public Item getItem() {
		return item;
	}

	public void setItem(Item item) {
		this.item = item;
	}

	public Map<Long, ItemOrder> getItemsSelected() {
		return itemsSelected;
	}

	public void setItemsSelected(Map<Long, ItemOrder> itemsSelected) {
		this.itemsSelected = itemsSelected;
	}

	public Orderr getOrderSelected() {
		return orderSelected;
	}

	public void setOrderSelected(Orderr orderSelected) {
		this.orderSelected = orderSelected;
	}
	
	

}