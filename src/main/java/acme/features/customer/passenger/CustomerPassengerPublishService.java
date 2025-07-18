
package acme.features.customer.passenger;

import org.springframework.beans.factory.annotation.Autowired;

import acme.client.components.models.Dataset;
import acme.client.services.AbstractGuiService;
import acme.client.services.GuiService;
import acme.entities.passenger.Passenger;
import acme.realms.customer.Customer;

@GuiService
public class CustomerPassengerPublishService extends AbstractGuiService<Customer, Passenger> {

	//Internal state --------------------------------------------

	@Autowired
	private CustomerPassengerRepository repository;

	//AbstractGuiService interface -------------------------------


	@Override
	public void authorise() {
		Passenger passenger;
		int passengerId;
		int customerId;
		boolean status;

		passengerId = super.getRequest().getData("id", int.class);
		passenger = this.repository.findPassengerById(passengerId);
		customerId = super.getRequest().getPrincipal().getActiveRealm().getId();

		status = passenger != null && !passenger.isPublish() && passenger.getCustomer().getId() == customerId;

		super.getResponse().setAuthorised(status);
	}

	@Override
	public void load() {
		int id = super.getRequest().getData("id", int.class);
		Passenger passenger = this.repository.findPassengerById(id);

		super.getBuffer().addData(passenger);
	}

	@Override
	public void bind(final Passenger passenger) {
		super.bindObject(passenger, "fullName", "email", "passportNumber", "dateOfBirth", "specialNeeds");
	}

	@Override
	public void validate(final Passenger passenger) {

	}

	@Override
	public void perform(final Passenger passenger) {
		passenger.setPublish(true);
		this.repository.save(passenger);
	}

	@Override
	public void unbind(final Passenger passenger) {
		Dataset dataset;
		dataset = super.unbindObject(passenger, "fullName", "email", "passportNumber", "dateOfBirth", "specialNeeds", "publish");

		super.getResponse().addData(dataset);
	}

}
