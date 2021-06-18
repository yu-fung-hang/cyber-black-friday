package com.singfung.blackfriday.model;

import lombok.*;

import java.io.Serializable;
import java.sql.Timestamp;

@Setter
@Getter
@NoArgsConstructor
@AllArgsConstructor
@ToString
public class Order implements Serializable
{
	private static final long serialVersionUID = -651642771682545319L;

	private int id;
	private int userId;
	private int productId;
	private int itemsNum;
	private Timestamp orderTime;
	private String note;
}
