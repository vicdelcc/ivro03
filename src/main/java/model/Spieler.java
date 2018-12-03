package model;

import model.hilfsklassen.BaseEntity;
import lombok.Data;
import lombok.NoArgsConstructor;

import javax.persistence.Column;
import javax.persistence.Embedded;
import javax.persistence.Entity;
import javax.persistence.ManyToOne;
import java.util.ArrayList;
import java.util.List;

@Data
@Entity
@NoArgsConstructor
public class Spieler extends BaseEntity {

	@ManyToOne
	private Spielrunde spielrunde;

	@Embedded
	private List<Spielkarte> hand;

	@Column
	private String name;

	@Column
	private boolean spielend;
	
	public Spieler(List<Spielkarte> hand, String name, boolean spielend) {
		this.hand = hand;
		this.name = name;
		this.spielend = spielend;
	}

	public Spieler(String name) {
		this.name = name;
		this.hand = new ArrayList<>();
	}

}
