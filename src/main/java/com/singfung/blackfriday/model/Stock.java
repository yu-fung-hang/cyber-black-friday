package com.singfung.blackfriday.model;

import lombok.*;
import java.io.Serializable;

@Setter
@Getter
@NoArgsConstructor
@AllArgsConstructor
@ToString
public class Stock implements Serializable
{
	private static final long serialVersionUID = 7843328938127330037L;

	private int id;
	private int totalNum;
	private int stockNum;
	private int version;
	private String note;
}