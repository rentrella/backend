package com.example.rentrella.auth.service;

import com.example.rentrella.auth.domain.User;
import com.example.rentrella.auth.dto.DataGsmStudent;
import com.example.rentrella.auth.repository.UserRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

@Service
@RequiredArgsConstructor
@Transactional
public class DataGsmStudentSyncService {

    private static final int DEFAULT_PAGE_SIZE = 300;

    private final DataGsmStudentClient dataGsmStudentClient;
    private final UserRepository userRepository;

    public int syncAllStudents() {
        int savedCount = 0;
        int page = 0;

        while (true) {
            List<DataGsmStudent> students = dataGsmStudentClient.fetchStudents(page, DEFAULT_PAGE_SIZE);
            if (students.isEmpty()) {
                return savedCount;
            }

            for (DataGsmStudent student : students) {
                upsertStudent(student);
                savedCount++;
            }

            if (students.size() < DEFAULT_PAGE_SIZE) {
                return savedCount;
            }

            page++;
        }
    }

    public User syncStudentByEmail(String email) {
        DataGsmStudent student = dataGsmStudentClient.fetchStudentByEmail(email);
        return student == null ? null : upsertStudent(student);
    }

    private User upsertStudent(DataGsmStudent student) {
        String dataGsmId = student.id() == null ? null : student.id().toString();
        User user = dataGsmId == null
                ? userRepository.findByEmail(student.email()).orElseGet(User::newDataGsmStudent)
                : userRepository.findByDataGsmId(dataGsmId)
                        .or(() -> userRepository.findByEmail(student.email()))
                        .orElseGet(User::newDataGsmStudent);

        user.synchronizeStudentProfile(
                dataGsmId,
                student.name(),
                student.email(),
                student.studentNumberAsString(),
                student.grade(),
                student.classNum(),
                student.number()
        );
        return userRepository.save(user);
    }
}
