package Outpatient.example.Intership_Backend.Service;

import Outpatient.example.Intership_Backend.DTO.LoginRequest;
import Outpatient.example.Intership_Backend.Entity.Doctor;
import Outpatient.example.Intership_Backend.Entity.Patient;
import Outpatient.example.Intership_Backend.Entity.User;
import Outpatient.example.Intership_Backend.Repository.*;
import jakarta.transaction.Transactional;
import lombok.Data;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.stereotype.Service;

import java.util.Optional;

@Service
@Transactional
@Data
public class AdminService {
    String loginEmail;

    @Autowired
    private DoctorRepository doctorRepository;

    @Autowired
    private UserRepo userRepo;

    @Autowired
    private AppointmentRepository appointmentRepository;

    @Autowired
    private PrescriptionRepository prescriptionRepository;

    @Autowired
    private PatientRepository patientRepository;


    @Autowired
    private BCryptPasswordEncoder passwordEncoder;


    private String generateDefaultPassword() {
        return passwordEncoder.encode("defaultPassword123");
    }

    public boolean deleteDoctorByEmail(String email) {
        Optional<User> user = userRepo.findByEmail(email);
        if (user.isEmpty()) {
            return false; // Doctor not found
        }

        try {
            if (doctorRepository.existsByEmail(email)) {
                userRepo.deleteByEmail(email);
                appointmentRepository.deleteByDoctorEmail(email);
                doctorRepository.deleteById(email);

                return true;
            }
        } catch (Exception e) {
            e.printStackTrace(); // Log the error for debugging
            return false; // Return false if any error occurs during the deletion process
        }

        return false;
    }

    public void loginAdmin(LoginRequest loginRequest) {
        loginEmail = loginRequest.getEmail();
    }


    public boolean deletePatientByEmail(String email) {
        Optional<User> user = userRepo.findByEmail(email);
        if (user.isEmpty()) {
            return false;
        }

        try {
            if (patientRepository.existsByEmail(email)) {
                userRepo.deleteByEmail(email);
                appointmentRepository.deleteByPatientEmail(email);
                patientRepository.deleteById(email);

                return true;
            }
        } catch (Exception e) {
            e.printStackTrace();
            return false; // Return false if any error occurs during the deletion process
        }

        return false;
    }

    public long getDoctorsCount() {
        return doctorRepository.count();
    }

    public long getPatientsCount() {
        return patientRepository.count();
    }


    public boolean addDoctor(Doctor doctor) {
        try {
            if (doctorRepository.existsByEmail(doctor.getEmail())) {
                return false; // Doctor already exists
            }
            doctorRepository.save(doctor);

            // Create login credentials
            User user = new User();
            user.setUsername(doctor.getDoctorName());
            user.setEmail(doctor.getEmail());
            user.setPassword(generateDefaultPassword());
            user.setRole("DOCTOR");
            userRepo.save(user);

            return true; // Doctor added successfully
        } catch (Exception e) {
            e.printStackTrace();
            return false;
        }

    }
    public boolean addPatient(Patient patient) {
        try {
            if (patientRepository.existsByEmail(patient.getEmail())) {
                return false; // Patient already exists
            }
            // Save patient details
            patientRepository.save(patient);

            // Create login credentials
            User user = new User();
            user.setUsername(patient.getPatientName()); // Use the patient's name
            user.setEmail(patient.getEmail());
            user.setPassword(generateDefaultPassword()); // Generate default password
            user.setRole("USER");
            userRepo.save(user);

            return true; // Patient added successfully
        } catch (Exception e) {
            e.printStackTrace();
            return false;
        }
    }
    //add
    public boolean updatePatientProfileByAdmin(String id, Patient updatedPatient) {
        Patient existingPatient = patientRepository.findById(id).orElse(null);
        if (existingPatient == null) {
            return false; // Patient not found
        }

        // Update patient details
        existingPatient.setPatientName(updatedPatient.getPatientName());
        existingPatient.setMobileNo(updatedPatient.getMobileNo());
        existingPatient.setBloodGroup(updatedPatient.getBloodGroup());
        existingPatient.setGender(updatedPatient.getGender());
        existingPatient.setAge(updatedPatient.getAge());
        existingPatient.setAddress(updatedPatient.getAddress());

        patientRepository.save(existingPatient);
        return true;
    }

    // Method to get doctor details by email

    public Optional<Doctor> getDoctorByEmail(String email) {
        return Optional.ofNullable(doctorRepository.findByEmail(email)); // Returns Optional<Doctor>
    }
    // Method to update doctor profile
    public Doctor updateDoctorProfile(String email, Doctor doctorDetails) {
        Optional<Doctor> existingDoctorOpt = Optional.ofNullable(doctorRepository.findByEmail(email));
        if (existingDoctorOpt.isPresent()) {
            Doctor existingDoctor = existingDoctorOpt.get();
            existingDoctor.setDoctorName(doctorDetails.getDoctorName());
            existingDoctor.setSpeciality(doctorDetails.getSpeciality());
            existingDoctor.setMobileNo(doctorDetails.getMobileNo());
            existingDoctor.setGender(doctorDetails.getGender());
            existingDoctor.setHospitalName(doctorDetails.getHospitalName());
            existingDoctor.setLocation(doctorDetails.getLocation());
            existingDoctor.setChargedPerVisit(doctorDetails.getChargedPerVisit());
            // Save the updated doctor profile


            return doctorRepository.save(existingDoctor);
        }
        return null;
    }

    public long getAppointmentsCount() {
        return patientRepository.count();
    }

}

