package com.singfung.blackfriday.model;

import lombok.*;
import java.io.Serializable;
import javax.validation.constraints.Min;
import javax.validation.constraints.NotBlank;

@Setter
@Getter
@NoArgsConstructor
@AllArgsConstructor
@ToString
public class Stock implements Serializable
{
	private static final long serialVersionUID = 7843328938127330037L;

	@Min(value = 1, message = "invalid id", groups = {Update.class})
	private int id;
	@Min(value = 1, message = "invalid totalNum", groups = {Insert.class})
	private int totalNum;
	@Min(value = 1, message = "invalid stockNum", groups = {Insert.class})
	private int stockNum;
	@NotBlank(message = "name should not be blank", groups = {Insert.class})
	private String name;
	private int version;
	private String note;

	public interface Update {}

	public interface Insert {}
}