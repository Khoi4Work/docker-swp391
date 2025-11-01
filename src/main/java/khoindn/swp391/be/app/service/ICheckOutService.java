package khoindn.swp391.be.app.service;

import khoindn.swp391.be.app.model.Request.CheckInRequest;
import khoindn.swp391.be.app.model.Request.CheckOutRequest;
import khoindn.swp391.be.app.model.Response.CheckInResponse;
import khoindn.swp391.be.app.model.Response.CheckOutResponse;

public interface ICheckOutService {
    CheckOutResponse processCheckOut(int scheduleId, CheckOutRequest req);

}
