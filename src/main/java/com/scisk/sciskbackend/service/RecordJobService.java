package com.scisk.sciskbackend.service;

import com.scisk.sciskbackend.dto.*;

public interface RecordJobService {

    void assignRecordJobToEmployee(Long id, Long userId, AssignRecordJobToEmployeeDto assignRecordJobToEmployeeDto);

    void closeRecordJobByAssistant(Long id, CloseRecordJobByAssistantDto closeRecordJobByEmployeeDto);

    void closeRecordJobByChief(Long id, CloseRecordJobByChiefDto closeRecordJobByChiefDto);
}
