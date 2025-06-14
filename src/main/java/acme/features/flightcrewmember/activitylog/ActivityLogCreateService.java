
package acme.features.flightcrewmember.activitylog;

import org.springframework.beans.factory.annotation.Autowired;

import acme.client.components.models.Dataset;
import acme.client.helpers.MomentHelper;
import acme.client.services.AbstractGuiService;
import acme.client.services.GuiService;
import acme.entities.activitylog.ActivityLog;
import acme.entities.flightassignment.FlightAssignment;
import acme.realms.flightcrewmember.FlightCrewMember;

@GuiService
public class ActivityLogCreateService extends AbstractGuiService<FlightCrewMember, ActivityLog> {

	// Internal state ---------------------------------------------------------
	@Autowired
	private ActivityLogRepository repository;


	// AbstractGuiService interface -------------------------------------------
	@Override
	public void authorise() {
		FlightAssignment flightAssignment;
		int flightAssignmentId;
		int flightCrewMemberId;
		boolean status;

		flightAssignmentId = super.getRequest().getData("flightAssignmentId", int.class);
		flightAssignment = this.repository.findFlightAssignmentById(flightAssignmentId);
		flightCrewMemberId = super.getRequest().getPrincipal().getActiveRealm().getId();

		status = flightAssignment != null && flightAssignment.getFlightAssignmentCrewMember().getId() == flightCrewMemberId;

		if (status) {
			String method;
			method = super.getRequest().getMethod();

			if (method.equals("GET"))
				status = true;
			else {
				int version;
				int id;

				version = super.getRequest().getData("version", int.class);
				id = super.getRequest().getData("id", int.class);

				status = id == 0 && version == 0;
			}
		}
		super.getResponse().setAuthorised(status);
	}

	@Override
	public void load() {
		ActivityLog activityLog;
		int flightAssignmentId;
		FlightAssignment flightAssignment;

		flightAssignmentId = super.getRequest().getData("flightAssignmentId", int.class);
		flightAssignment = this.repository.findFlightAssignmentById(flightAssignmentId);

		activityLog = new ActivityLog();
		activityLog.setRegistrationMoment(MomentHelper.getCurrentMoment());
		activityLog.setActivityLogAssignment(flightAssignment);
		activityLog.setPublish(false);

		super.getBuffer().addData(activityLog);
	}

	@Override
	public void bind(final ActivityLog activityLog) {
		super.bindObject(activityLog, "incidentType", "description", "severityLevel");
	}

	@Override
	public void validate(final ActivityLog activityLog) {
		;
	}

	@Override
	public void perform(final ActivityLog activityLog) {
		this.repository.save(activityLog);
	}

	@Override
	public void unbind(final ActivityLog activityLog) {
		Dataset dataset;

		dataset = super.unbindObject(activityLog, "registrationMoment", "incidentType", "description", "severityLevel", "publish", "activityLogAssignment");

		dataset.put("flightAssignmentId", activityLog.getActivityLogAssignment().getId());

		super.getResponse().addData(dataset);
	}
}
