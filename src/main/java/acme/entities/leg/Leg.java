
package acme.entities.leg;

import java.util.Date;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.Index;
import javax.persistence.ManyToOne;
import javax.persistence.Table;
import javax.persistence.Temporal;
import javax.persistence.TemporalType;
import javax.persistence.Transient;
import javax.validation.Valid;

import acme.client.components.basis.AbstractEntity;
import acme.client.components.mappings.Automapped;
import acme.client.components.validation.Mandatory;
import acme.client.components.validation.ValidMoment;
import acme.client.helpers.MomentHelper;
import acme.constraints.ValidFlightNumber;
import acme.constraints.ValidLeg;
import acme.entities.aircraft.Aircraft;
import acme.entities.airport.Airport;
import acme.entities.flight.Flight;
import lombok.Getter;
import lombok.Setter;

@Entity
@Getter
@Setter
@ValidLeg
@Table(indexes = {
	@Index(columnList = "flight_id,publish"), 
	@Index(columnList = "flight_id,departure"),
	@Index(columnList = "flight_id,arrival"),
	@Index(columnList = "flight_id,publish,arrival,departure"),
	@Index(columnList = "deployed_aircraft_id,publish,arrival,departure"),
	@Index(columnList = "flightNumber")
})
public class Leg extends AbstractEntity {

	// Serialisation version --------------------------------------------

	private static final long	serialVersionUID	= 1L;

	// Attributes -------------------------------------------------------

	@Mandatory
	@ValidFlightNumber
	@Column(unique = true)
	private String				flightNumber;

	@Mandatory
	@ValidMoment
	@Temporal(TemporalType.TIMESTAMP)
	private Date				departure;

	@Mandatory
	@ValidMoment
	@Temporal(TemporalType.TIMESTAMP)
	private Date				arrival;

	@Mandatory
	@Valid
	@Automapped
	private LegStatus			status;

	@Mandatory
	//@Valid
	@Automapped
	private boolean				publish;

	//Derived attributes-------------------------------------------------


	@Transient
	public Double getDurationInHours() {
		if (this.departure != null && this.arrival != null) {
			double durationInH = MomentHelper.computeDuration(this.departure, this.arrival).toMinutes() / 60.0;
			return durationInH;
		} else
			return null;
	}

	// Relationships -----------------------------------------------------


	@Mandatory
	@Valid
	@ManyToOne(optional = false)
	private Aircraft	deployedAircraft;

	@Mandatory
	@Valid
	@ManyToOne(optional = false)
	private Airport		departureAirport;

	@Mandatory
	@Valid
	@ManyToOne(optional = false)
	private Airport		arrivalAirport;

	@Mandatory
	@Valid
	@ManyToOne(optional = false)
	private Flight		flight;

}
