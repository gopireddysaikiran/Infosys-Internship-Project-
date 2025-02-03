package Outpatient.example.Intership_Backend.Controller;

import Outpatient.example.Intership_Backend.Advices.ApiError;
import Outpatient.example.Intership_Backend.Entity.Doctor;
import Outpatient.example.Intership_Backend.Entity.Patient;
import Outpatient.example.Intership_Backend.Service.AdminService;
import Outpatient.example.Intership_Backend.Service.DoctorService;
import Outpatient.example.Intership_Backend.Service.PatientService;
import jakarta.validation.Valid;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Optional;

@RestController
@RequestMapping("/api/admin/")
public class AdminController {

    @Autowired
    private AdminService adminService;


    @GetMapping("/doctors")
    public long getDoctorsCount() {
        return adminService.getDoctorsCount();
    }

    @GetMapping("/patients")
    public long getPatientsCount() {
        return adminService.getPatientsCount();
    }

    @GetMapping("/get-welcome-email")
    public ResponseEntity<Map<String, String>> getWelcomeEmail() {
        Map<String, String> response = new HashMap<>();
        response.put("email", adminService.getLoginEmail());
        return ResponseEntity.ok(response);
    }

    @DeleteMapping("/delete")
    public ResponseEntity<String> deleteDoctor(@RequestParam String email) {
        try {
            boolean isDeleted = adminService.deleteDoctorByEmail(email);
            System.out.println(isDeleted);
            if (isDeleted) {
                return ResponseEntity.ok("Doctor deleted successfully.");
            } else {
                return ResponseEntity.status(HttpStatus.NOT_FOUND).body("Doctor not found with email: " + email);
            }
        } catch (Exception e) {
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body("Error deleting doctor.");
        }
    }


    @DeleteMapping("/delete-patient")
    public ResponseEntity<String> deletePatient(@RequestParam String email) {
        try {
            boolean isDeleted = adminService.deletePatientByEmail(email);
            System.out.println(isDeleted);
            if (isDeleted) {
                return ResponseEntity.ok("Patient deleted successfully.");
            } else {
                return ResponseEntity.status(HttpStatus.NOT_FOUND).body("Patient not found with email: " + email);
            }
        } catch (Exception e) {
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body("Error deleting doctor.");
        }
    }

    //admin by me
    @PostMapping("/add-doctor")
    public ResponseEntity<String> addDoctor(@RequestBody Doctor doctor) {
        try {
            boolean isAdded = adminService.addDoctor(doctor);
            if (isAdded) {
                return ResponseEntity.status(HttpStatus.CREATED).body("Doctor added successfully.");
            } else {
                return ResponseEntity.status(HttpStatus.CONFLICT).body("Doctor with email already exists.");
            }
        } catch (Exception e) {
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body("Error adding doctor.");
        }
    }

    @PostMapping("/add-patient")
    public ResponseEntity<String> addPatient(@RequestBody Patient patient) {
        try {
            boolean isAdded = adminService.addPatient(patient);
            if (isAdded) {
                return ResponseEntity.status(HttpStatus.CREATED).body("Patient added successfully.");
            } else {
                return ResponseEntity.status(HttpStatus.CONFLICT).body("Patient with email already exists.");
            }
        } catch (Exception e) {
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body("Error adding patient.");
        }

    }
    //add

    @PutMapping("/edit-patient-profile/{id}")
    public ResponseEntity<ApiError> editPatientProfileByAdmin(
            @PathVariable String id,
            @RequestBody @Valid Patient updatedPatient) {
        try {
            boolean isUpdated = adminService.updatePatientProfileByAdmin(id, updatedPatient);
            if (isUpdated) {
                return ResponseEntity.ok(new ApiError(HttpStatus.OK, "Patient profile updated successfully.", List.of()));
            } else {
                return ResponseEntity.status(HttpStatus.NOT_FOUND)
                        .body(new ApiError(HttpStatus.NOT_FOUND, "Patient not found.", List.of()));
            }
        } catch (Exception e) {
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
                    .body(new ApiError(HttpStatus.INTERNAL_SERVER_ERROR,
                            "Failed to update patient profile: " + e.getMessage(), List.of(e.getMessage())));
        }
    }

    // Endpoint to get doctor details by email
    @GetMapping("/get-doctor/{email}")
    public ResponseEntity<Doctor> getDoctorByEmail(@PathVariable String email) {
        Optional<Doctor> doctorOpt = adminService.getDoctorByEmail(email);

        if (doctorOpt.isPresent()) {
            return ResponseEntity.ok(doctorOpt.get()); // Return the doctor if found
        } else {
            return ResponseEntity.status(HttpStatus.NOT_FOUND).body(null); // Return 404 if not found
        }
    }

    // Endpoint to update doctor profile
    @PutMapping("/edit-doctor-profile/{email}")
    public ResponseEntity<Doctor> updateDoctorProfile(@PathVariable String email, @RequestBody Doctor doctorDetails) {
        Optional<Doctor> updatedDoctorOpt = Optional.ofNullable(adminService.updateDoctorProfile(email, doctorDetails));

        if (updatedDoctorOpt.isPresent()) {
            return ResponseEntity.ok(updatedDoctorOpt.get()); // Return the updated doctor if found
        } else {
            return ResponseEntity.status(HttpStatus.NOT_FOUND).body(null); // Return 404 if doctor not found
        }
    }

    @GetMapping("/appointments-count")
    public long getAppoinmentsCount(){
        return adminService.getAppointmentsCount();
    }





}