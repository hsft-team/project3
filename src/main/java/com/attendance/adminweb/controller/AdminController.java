package com.attendance.adminweb.controller;

import com.attendance.adminweb.model.CompanyLocationForm;
import com.attendance.adminweb.model.EmployeeForm;
import com.attendance.adminweb.service.AdminService;
import jakarta.validation.Valid;
import java.security.Principal;
import org.springframework.dao.DataIntegrityViolationException;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.validation.BindingResult;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.ModelAttribute;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;

@Controller
public class AdminController {

    private final AdminService adminService;

    public AdminController(AdminService adminService) {
        this.adminService = adminService;
    }

    @GetMapping("/")
    public String root() {
        return "redirect:/dashboard";
    }

    @GetMapping("/dashboard")
    public String dashboard(Model model, Principal principal) {
        model.addAttribute("summary", adminService.getTodaySummary(principal.getName()));
        model.addAttribute("recentAttendances", adminService.getTodayAttendances(principal.getName()));
        return "dashboard";
    }

    @GetMapping("/employees")
    public String employees(@RequestParam(required = false) Long editId, Model model, Principal principal) {
        model.addAttribute("employees", adminService.getEmployees(principal.getName()));
        if (!model.containsAttribute("employeeForm")) {
            model.addAttribute("employeeForm", editId == null
                    ? adminService.getEmployeeFormForCreate()
                    : adminService.getEmployeeFormForEdit(principal.getName(), editId));
        }
        model.addAttribute("editing", editId != null);
        return "employees";
    }

    @GetMapping("/settings/location")
    public String companyLocation(Model model, Principal principal) {
        if (!model.containsAttribute("locationForm")) {
            model.addAttribute("locationForm", adminService.getCompanyLocationForm(principal.getName()));
        }
        model.addAttribute("location", adminService.getCompanyLocation(principal.getName()));
        return "location-settings";
    }

    @PostMapping("/settings/location")
    public String updateCompanyLocation(@Valid @ModelAttribute("locationForm") CompanyLocationForm form,
                                        BindingResult bindingResult,
                                        RedirectAttributes redirectAttributes,
                                        Principal principal) {
        if (bindingResult.hasErrors()) {
            redirectAttributes.addFlashAttribute("org.springframework.validation.BindingResult.locationForm", bindingResult);
            redirectAttributes.addFlashAttribute("locationForm", form);
            return "redirect:/settings/location";
        }

        adminService.updateCompanyLocation(principal.getName(), form);
        redirectAttributes.addFlashAttribute("message", "회사 위치가 저장되었습니다.");
        return "redirect:/settings/location";
    }

    @PostMapping("/employees")
    public String createEmployee(@Valid @ModelAttribute("employeeForm") EmployeeForm form,
                                 BindingResult bindingResult,
                                 RedirectAttributes redirectAttributes,
                                 Principal principal) {
        validateCreateEmployeeForm(form, bindingResult);
        if (bindingResult.hasErrors()) {
            redirectAttributes.addFlashAttribute("org.springframework.validation.BindingResult.employeeForm", bindingResult);
            redirectAttributes.addFlashAttribute("employeeForm", form);
            redirectAttributes.addFlashAttribute("editing", false);
            return "redirect:/employees";
        }

        try {
            adminService.createEmployee(principal.getName(), form);
            redirectAttributes.addFlashAttribute("message", "직원이 등록되었습니다.");
        } catch (IllegalArgumentException | DataIntegrityViolationException exception) {
            redirectAttributes.addFlashAttribute("employeeErrorMessage", exception.getMessage());
            redirectAttributes.addFlashAttribute("employeeForm", form);
        }

        return "redirect:/employees";
    }

    @PostMapping("/employees/{employeeId}/update")
    public String updateEmployee(@PathVariable Long employeeId,
                                 @Valid @ModelAttribute("employeeForm") EmployeeForm form,
                                 BindingResult bindingResult,
                                 RedirectAttributes redirectAttributes,
                                 Principal principal) {
        validateUpdateEmployeeForm(form, bindingResult);
        if (bindingResult.hasErrors()) {
            redirectAttributes.addFlashAttribute("org.springframework.validation.BindingResult.employeeForm", bindingResult);
            redirectAttributes.addFlashAttribute("employeeForm", form);
            redirectAttributes.addFlashAttribute("editing", true);
            return "redirect:/employees?editId=" + employeeId;
        }

        try {
            adminService.updateEmployee(principal.getName(), employeeId, form);
            redirectAttributes.addFlashAttribute("message", "직원 정보가 수정되었습니다.");
            return "redirect:/employees";
        } catch (IllegalArgumentException | DataIntegrityViolationException exception) {
            redirectAttributes.addFlashAttribute("employeeErrorMessage", exception.getMessage());
            redirectAttributes.addFlashAttribute("employeeForm", form);
            redirectAttributes.addFlashAttribute("editing", true);
            return "redirect:/employees?editId=" + employeeId;
        }
    }

    @PostMapping("/employees/{employeeId}/delete")
    public String deleteEmployee(@PathVariable Long employeeId,
                                 RedirectAttributes redirectAttributes,
                                 Principal principal) {
        try {
            adminService.deleteEmployee(principal.getName(), employeeId);
            redirectAttributes.addFlashAttribute("message", "직원이 삭제되었습니다.");
        } catch (IllegalArgumentException | DataIntegrityViolationException exception) {
            redirectAttributes.addFlashAttribute("employeeErrorMessage", exception.getMessage());
        }
        return "redirect:/employees";
    }

    private void validateCreateEmployeeForm(EmployeeForm form, BindingResult bindingResult) {
        if (form.getPassword() == null || form.getPassword().isBlank()) {
            bindingResult.rejectValue("password", "required", "비밀번호를 입력해 주세요.");
            return;
        }
        if (form.getPassword().length() < 8) {
            bindingResult.rejectValue("password", "length", "비밀번호는 8자 이상이어야 합니다.");
        }
    }

    private void validateUpdateEmployeeForm(EmployeeForm form, BindingResult bindingResult) {
        if (form.getPassword() != null && !form.getPassword().isBlank() && form.getPassword().length() < 8) {
            bindingResult.rejectValue("password", "length", "비밀번호는 8자 이상이어야 합니다.");
        }
    }
}
