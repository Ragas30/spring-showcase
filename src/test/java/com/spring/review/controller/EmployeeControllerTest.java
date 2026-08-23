package com.spring.review.controller;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.datatype.jsr310.JavaTimeModule;
import com.spring.review.bean.employee.CreateEmployeeRequest;
import com.spring.review.bean.employee.EmployeeSearchRequest;
import com.spring.review.bean.employee.UpdateEmployeeRequest;
import com.spring.review.common.ErrorCode;
import com.spring.review.common.PageResponse;
import com.spring.review.entity.EmployeeStatus;
import com.spring.review.entity.Gender;
import com.spring.review.entityView.EmployeeView;
import com.spring.review.exception.BusinessException;
import com.spring.review.exception.GlobalExceptionHandler;
import com.spring.review.service.EmployeeService;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.http.MediaType;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.setup.MockMvcBuilders;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.List;

import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.Mockito.doNothing;
import static org.mockito.Mockito.when;
import static org.mockito.Mockito.mock;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

@ExtendWith(MockitoExtension.class)
class EmployeeControllerTest {

    private MockMvc mockMvc;
    private ObjectMapper objectMapper;

    @Mock
    private EmployeeService employeeService;

    @InjectMocks
    private EmployeeController employeeController;

    @BeforeEach
    void setUp() {
        objectMapper = new ObjectMapper();
        objectMapper.registerModule(new JavaTimeModule());
        mockMvc = MockMvcBuilders.standaloneSetup(employeeController)
                .setControllerAdvice(new GlobalExceptionHandler())
                .build();
    }

    private CreateEmployeeRequest buildCreateRequest() {
        CreateEmployeeRequest request = new CreateEmployeeRequest();
        request.setFullName("Nguyen Van A");
        request.setEmail("nguyenvana@example.com");
        request.setPhoneNumber("0123456789");
        request.setGender(Gender.MALE);
        request.setBirthDate(LocalDate.of(1995, 5, 15));
        request.setHireDate(LocalDate.of(2024, 1, 1));
        request.setStatus(EmployeeStatus.ACTIVE);
        request.setDepartmentId(1L);
        request.setPositionId(1L);
        request.setManagerId(null);
        request.setPhotoUrl(null);
        return request;
    }

    private UpdateEmployeeRequest buildUpdateRequest() {
        UpdateEmployeeRequest request = new UpdateEmployeeRequest();
        request.setFullName("Nguyen Van A Updated");
        request.setEmail("nguyenvanaupdated@example.com");
        request.setPhoneNumber("0987654321");
        request.setGender(Gender.MALE);
        request.setBirthDate(LocalDate.of(1995, 5, 15));
        request.setHireDate(LocalDate.of(2024, 1, 1));
        request.setStatus(EmployeeStatus.ACTIVE);
        request.setDepartmentId(1L);
        request.setPositionId(1L);
        request.setManagerId(null);
        request.setPhotoUrl(null);
        return request;
    }

    private EmployeeView buildEmployeeView(Long id) {
        EmployeeView view = mock(EmployeeView.class);
        when(view.getId()).thenReturn(id);
        when(view.getEmployeeCode()).thenReturn("EMP001");
        when(view.getFullName()).thenReturn("Nguyen Van A");
        when(view.getEmail()).thenReturn("nguyenvana@example.com");
        when(view.getPhoneNumber()).thenReturn("0123456789");
        when(view.getGender()).thenReturn(Gender.MALE);
        when(view.getBirthDate()).thenReturn(LocalDate.of(1995, 5, 15));
        when(view.getHireDate()).thenReturn(LocalDate.of(2024, 1, 1));
        when(view.getStatus()).thenReturn(EmployeeStatus.ACTIVE);
        when(view.getDepartmentName()).thenReturn("Engineering");
        when(view.getPositionName()).thenReturn("Developer");
        when(view.getManagerId()).thenReturn(null);
        when(view.getManagerName()).thenReturn(null);
        when(view.getPhotoUrl()).thenReturn(null);
        when(view.getCreatedAt()).thenReturn(LocalDateTime.of(2024, 1, 1, 8, 0));
        when(view.getUpdatedAt()).thenReturn(LocalDateTime.of(2024, 1, 1, 8, 0));
        return view;
    }

    @Test
    void createEmployee_validRequest_returns200() throws Exception {
        EmployeeView view = buildEmployeeView(1L);
        when(employeeService.createEmployee(any(CreateEmployeeRequest.class))).thenReturn(view);

        mockMvc.perform(post("/api/employees")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(buildCreateRequest())))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.code").value(ErrorCode.SUCCESS.name()));
    }

    @Test
    void createEmployee_duplicateEmail_returns409() throws Exception {
        when(employeeService.createEmployee(any(CreateEmployeeRequest.class)))
                .thenThrow(new BusinessException(ErrorCode.EMAIL_ALREADY_EXISTS, "Email already exists"));

        mockMvc.perform(post("/api/employees")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(buildCreateRequest())))
                .andExpect(status().isConflict())
                .andExpect(jsonPath("$.code").value(ErrorCode.EMAIL_ALREADY_EXISTS.name()));
    }

    @Test
    void createEmployee_missingFullName_returns400() throws Exception {
        CreateEmployeeRequest request = new CreateEmployeeRequest();
        request.setEmail("test@example.com");

        mockMvc.perform(post("/api/employees")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(request)))
                .andExpect(status().isBadRequest())
                .andExpect(jsonPath("$.code").value("VALIDATION_ERROR"));
    }

    @Test
    void getEmployeeById_existingId_returns200() throws Exception {
        EmployeeView view = buildEmployeeView(1L);
        when(employeeService.getEmployeeById(1L)).thenReturn(view);

        mockMvc.perform(get("/api/employees/1"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.code").value(ErrorCode.SUCCESS.name()))
                .andExpect(jsonPath("$.data.fullName").value("Nguyen Van A"));
    }

    @Test
    void getEmployeeById_notFound_returns404() throws Exception {
        when(employeeService.getEmployeeById(999L))
                .thenThrow(new BusinessException(ErrorCode.EMPLOYEE_NOT_FOUND, "Employee not found"));

        mockMvc.perform(get("/api/employees/999"))
                .andExpect(status().isNotFound())
                .andExpect(jsonPath("$.code").value(ErrorCode.EMPLOYEE_NOT_FOUND.name()));
    }

    @Test
    void getEmployees_returnsPageResponse() throws Exception {
        EmployeeView view = buildEmployeeView(1L);
        PageResponse<EmployeeView> pageResponse = PageResponse.<EmployeeView>builder()
                .content(List.of(view))
                .totalElements(1L)
                .page(0)
                .size(10)
                .totalPages(1)
                .build();
        when(employeeService.getEmployees(any(EmployeeSearchRequest.class))).thenReturn(pageResponse);

        mockMvc.perform(get("/api/employees")
                        .param("page", "0")
                        .param("size", "10"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.code").value(ErrorCode.SUCCESS.name()))
                .andExpect(jsonPath("$.data.content[0].fullName").value("Nguyen Van A"))
                .andExpect(jsonPath("$.data.totalElements").value(1));
    }

    @Test
    void updateEmployee_validRequest_returns200() throws Exception {
        EmployeeView view = buildEmployeeView(1L);
        when(employeeService.updateEmployee(eq(1L), any(UpdateEmployeeRequest.class))).thenReturn(view);

        mockMvc.perform(put("/api/employees/1")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(buildUpdateRequest())))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.code").value(ErrorCode.SUCCESS.name()));
    }

    @Test
    void deleteEmployee_existingId_returns200() throws Exception {
        doNothing().when(employeeService).deleteEmployee(1L);

        mockMvc.perform(delete("/api/employees/1"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.code").value(ErrorCode.SUCCESS.name()));
    }
}
