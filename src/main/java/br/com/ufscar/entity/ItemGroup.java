package br.com.ufscar.entity;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.List;

import javax.persistence.CascadeType;
import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;
import javax.persistence.NamedQuery;
import javax.persistence.OneToMany;

@Entity
@NamedQuery(name = "ItemGroup.findItemGroupByName", query = "SELECT ig FROM ItemGroup ig WHERE UPPER(ig.name) = UPPER(:name)")
public class ItemGroup implements Serializable {
	private static final long serialVersionUID = 1L;

	public static final String FIND_BY_NAME = "ItemGroup.findItemGroupByName";

	@Id
	@GeneratedValue(strategy = GenerationType.AUTO)
	private Long id;

	@Column(unique = true)
	private String name;
	
//	@OneToMany(mappedBy="item",cascade=CascadeType.PERSIST)
//	private List<Item> itens = new ArrayList<Item>();
	
	public Long getId() {
		return id;
	}

	public void setId(Long id) {
		this.id = id;
	}

	public String getName() {
		return name;
	}

	public void setName(String name) {
		if (name!=null && !name.isEmpty())
			this.name = name.toUpperCase();
	}

}