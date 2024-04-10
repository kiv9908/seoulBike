package Test.bw;

import java.util.Date;

import lombok.Data;

@Data
public class Rental_list {
	private String user_id;
	private String bike_id;
	private String rental_place_id;
	private Date start_time;
	private Date end_time;
}
