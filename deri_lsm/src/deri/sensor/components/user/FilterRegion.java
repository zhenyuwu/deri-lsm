package deri.sensor.components.user;

import java.util.ArrayList;
import java.util.Collection;
import java.util.Collections;
import java.util.List;
import java.util.Map;
import java.util.Set;

import org.zkoss.zk.ui.Component;
import org.zkoss.zk.ui.event.Event;
import org.zkoss.zk.ui.event.EventListener;
import org.zkoss.zk.ui.event.Events;
import org.zkoss.zul.Caption;
import org.zkoss.zul.Checkbox;
import org.zkoss.zul.Groupbox;
import org.zkoss.zul.Tree;
import org.zkoss.zul.Treecell;
import org.zkoss.zul.Treechildren;
import org.zkoss.zul.Treefoot;
import org.zkoss.zul.Treefooter;
import org.zkoss.zul.Treeitem;
import org.zkoss.zul.Treerow;

import deri.sensor.components.Map.Country_Continent_Util;
import deri.sensor.components.Map.GMaps;

/**
 * @author Hoan Nguyen Mau Quoc
 */
public class FilterRegion extends Groupbox {
	private static final long serialVersionUID = -2107372578506485346L;
	private Tree tree;
	private GMaps map;
	private Checkbox checkbox = new Checkbox("ALL");
	
	public FilterRegion(Component parent){
		super();
		this.setId("filterRegion");
		this.setParent(parent);
		
		this.setMold("3d");
		this.setOpen(false);
		Caption caption = new Caption();
		caption.setLabel("Filter by region");//
		caption.setParent(this);
		
		initTree();
		initilazeMap();
		
	}
	
	private void initTree(){
		this.tree = new Tree();
		tree.setParent(this);
		tree.setMultiple(true);
		tree.setCheckmark(true);
		tree.setRows(10);
	}
	
	private void initilazeMap(){
		this.map = (GMaps)this.getParent().getParent().getFellowIfAny("map");
	}
	
	public void addRegionFilter(){
		Treechildren children = new Treechildren();
		children.setParent(tree);
		
		Map<String,List<String>> continents_countries_map = Country_Continent_Util.continents_countries_map;
		for(String continent : continents_countries_map.keySet()){
			Treeitem continentItem = this.addTreeItem(continent, children, true);
			Treechildren continentChildren = new Treechildren();
			continentChildren.setParent(continentItem);
			
			List<String> countries = continents_countries_map.get(continent);
			Collections.sort(countries);
			for(String country : countries){
				this.addTreeItem(country, continentChildren, true);
			}
		}
		
		Treefoot treefoot = new Treefoot();
		treefoot.setParent(tree);
		Treefooter treefooter = new Treefooter();
		treefooter.setParent(treefoot);
		
		checkbox.setChecked(true);
		checkbox.setParent(treefooter);
		checkbox.addEventListener(Events.ON_CHECK, new EventListener(){
			@Override
			public void onEvent(Event event) throws Exception {
				selectAllItems(checkbox.isChecked());
				map.reshowMarkersWith3Filters();
			}
		});
	}
	
	private Treeitem addTreeItem(String label, Component parent, boolean isChecked){
		Treeitem item = new Treeitem();
		item.setParent(parent);
		item.setCheckable(true);
		item.setOpen(false);
		item.setSelected(isChecked);
		
		
		Treerow row = new Treerow();
		row.setParent(item);
		row.setAttribute("containItem", item);
		row.addEventListener(Events.ON_CLICK, new EventListener(){
			@Override
			public void onEvent(Event event) throws Exception {
				Treerow row = (Treerow)event.getTarget();
				Treeitem containItem = (Treeitem)row.getAttribute("containItem");
				addTreeFilterListener(containItem);
			}
		});
		Treecell cell = new Treecell();
		cell.setParent(row);
		cell.setLabel(label);
		
		return item;
	}
	
	private void addTreeFilterListener(Treeitem containItem){
		int level = containItem.getLevel();
		if(level == 0){
			if(containItem.isSelected()){
				setParentSelected(containItem, true);
			}else{
				setParentSelected(containItem, false);
			}
		}else if(level == 1){
			if(isAllFellowsChecked(containItem)){
				containItem.getParentItem().setSelected(true);
			}else{
				containItem.getParentItem().setSelected(false);
			}
		}
		
		
		if(this.getSelectedRegionAfterProcess().size() == 7){
			checkbox.setChecked(true);
		}else{
			checkbox.setChecked(false);
		}
		map.reshowMarkersWith3Filters();
	}
	
	@SuppressWarnings("unchecked")
	private void setParentSelected(Treeitem parentItem, boolean isChecked){
		Treechildren children = parentItem.getTreechildren();
		if(children != null){
			Collection<Treeitem> items = children.getItems();
			if(items != null){
				for(Treeitem item : items){
					item.setSelected(isChecked);
				}
			}
		}
	}
	
	@SuppressWarnings("unchecked")
	private boolean isAllFellowsChecked(Treeitem item){
		boolean allChecked = true;
		Treeitem parentItem = item.getParentItem();
		Treechildren children = parentItem.getTreechildren();
		Collection<Treeitem> items = children.getItems();
		if(items != null){
			for(Treeitem list_item : items){
				if(list_item.isSelected() == false){
					allChecked = false;
					break;
				}
			}
		}
		return allChecked;
	}

	public List<String> getSelectedRegionAfterProcess(){
		List<String> regions = getSelectedCountries();
		processCheckedCountries(regions);
		return regions;
	}
	
	@SuppressWarnings("unchecked")
	public List<String> getSelectedCountries(){
		Set<Treeitem> selectedItems = tree.getSelectedItems();
		List<String> selectedItemsLabel = new ArrayList<String>();
		for(Treeitem item : selectedItems){
			selectedItemsLabel.add(item.getLabel());
		}
		
		return selectedItemsLabel;
	}
	
	private void processCheckedCountries(List<String> countries){
		if(countries != null && countries.size() > 0){
			for(String continent : Country_Continent_Util.continents_list){
				if(countries.contains(continent)){
					List<String> countries_in_the_continent = Country_Continent_Util.continents_countries_map.get(continent);
					for(String country_in_the_continent : countries_in_the_continent){
						countries.remove(country_in_the_continent);
					}
				}
			}
		}
	}

	@SuppressWarnings("unchecked")
	private void selectAllItems(boolean isChecked){
		Collection<Treeitem> items = tree.getItems();
		for(Treeitem item : items){
			item.setSelected(isChecked);
		}
	}

	@SuppressWarnings("unchecked")
	private Treeitem getTreeitem(String label){
		Collection<Treeitem> items = tree.getItems();
		for(Treeitem item : items){
			if(item.getLabel().trim().toLowerCase().equals(label.trim().toLowerCase())){
				return item;
			}
		}
		return null;
	}
	
	@SuppressWarnings("unchecked")
	public void setItemsSelected(List<String> countries){
		if(countries != null && countries.size() > 0){
			this.selectAllItems(false);
			List<String> continents = Country_Continent_Util.RemoveContinents(countries);
			for(String continent : continents){
				Treeitem continentItem = getTreeitem(continent);
				continentItem.setSelected(true);
				Treechildren continentChildren = continentItem.getTreechildren();
				Collection<Treeitem> countryItems = continentChildren.getItems();
				for(Treeitem item : countryItems){
					item.setSelected(true);
				}
			}
			
			for(String country : countries){
				Treeitem countryItem = getTreeitem(country);
				countryItem.setSelected(true);
			}
		}
	}
}
