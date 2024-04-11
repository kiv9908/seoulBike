package Test.bw;

import lombok.Data;

@Data
public class Rental_list {
	private String user_id;
	private String bike_id;
	private String rental_place_id;
	private String rental_place_loc;
	private String start_time;
	private String end_time;
}
