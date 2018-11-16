package com.demo.model;
 
public class HousePriceRecord {
	private Integer id; 
	private String name;
	private String address;
	private String state;
	private String describe;
	private String price;
	private String endPrice;
	private String createTime;
	private String endTime;
	private int isPrice;
	private int isChangePrice;
	private int isChange7DayPrice;
	private String areaName; 
		
	public Integer getId() {
		return id;
	}
	public void setId(Integer id) {
		this.id = id;
	}
	 
	public int getIsChange7DayPrice() {
		return isChange7DayPrice;
	}
	public void setIsChange7DayPrice(int isChange7DayPrice) {
		this.isChange7DayPrice = isChange7DayPrice;
	}
	public int getIsChangePrice() {
		return isChangePrice;
	}
	public void setIsChangePrice(int isChangePrice) {
		this.isChangePrice = isChangePrice;
	}
	public int getIsPrice() {
		return isPrice;
	}
	public void setIsPrice(int isPrice) {
		this.isPrice = isPrice;
	}
	public String getEndPrice() {
		return endPrice;
	}
	public void setEndPrice(String endPrice) {
		this.endPrice = endPrice;
	}
	public String getEndTime() {
		return endTime;
	}
	public void setEndTime(String endTime) {
		this.endTime = endTime;
	}
	public String getCreateTime() {
		return createTime;
	}
	public void setCreateTime(String createTime) {
		this.createTime = createTime;
	}
	public String getAddress() {
		return address;
	}
	/**
	 * @return the name
	 */
	public String getName() {
		return name;
	}
	/**
	 * @param name the name to set
	 */
	public void setName(String name) {
		this.name = name;
	}
	/**
	 * @return the address
	 */
	public String getAdddress() {
		return address;
	}
	/**
	 * @param adddress the address to set
	 */
	public void setAddress(String address) {
		this.address = address;
	}
	/**
	 * @return the state
	 */
	public String getState() {
		return state;
	}
	/**
	 * @param state the state to set
	 */
	public void setState(String state) {
		this.state = state;
	}
	/**
	 * @return the describe
	 */
	public String getDescribe() {
		return describe;
	}
	/**
	 * @param describe the describe to set
	 */
	public void setDescribe(String describe) {
		this.describe = describe;
	}
	/**
	 * @return the price
	 */
	public String getPrice() {
		return price;
	}
	/**
	 * @param price the price to set
	 */
	public void setPrice(String price) {
		this.price = price;
	} 
	
	
	public HousePriceRecord(String name, String address, String state, String describe, String price,String areaName) {
		super();
		this.name = name;
		this.address = address;
		this.state = state;
		this.describe = describe;
		this.price = price;
		this.areaName = areaName;
	}
	public HousePriceRecord() {
		super();
		// TODO Auto-generated constructor stub
	}
	@Override
	public String toString() {
		return "HousePriceRecord [id=" + id + ", name=" + name + ", address=" + address + ", state=" + state
				+ ", describe=" + describe + ", price=" + price + ", endPrice=" + endPrice + ", createTime="
				+ createTime + ", endTime=" + endTime + ", isPrice=" + isPrice + ", isChangePrice=" + isChangePrice
				+ ", isChange7DayPrice=" + isChange7DayPrice + ", areaName=" + areaName + "]";
	}
	
}
