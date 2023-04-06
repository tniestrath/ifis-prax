package com.analysetool.api;
import com.analysetool.modells.Company;
import com.analysetool.services.CompanyService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.Optional;

@Controller
@RequestMapping("/company")
public class CompanyController {

    @Autowired
    private CompanyService companyService;

    @GetMapping("")
    public String list(Model model) {
        List<Company> companies = companyService.findAll();
        model.addAttribute("companies", companies);
        return "company/list";
    }

    @GetMapping("/{id}")
    public String show(@PathVariable String id, Model model) {
        Optional<Company> company = companyService.findById(id);
        model.addAttribute("company", company.get());
        return "company/show";
    }

    @GetMapping("/create")
    public String create(Model model) {
        model.addAttribute("company", new Company());
        return "company/form";
    }

    @GetMapping("/{id}/edit")
    public String edit(@PathVariable String id, Model model) {
        Optional<Company> company = companyService.findById(id);
        model.addAttribute("company", company.get());
        return "company/form";
    }

    @PostMapping("")
    public String save(@ModelAttribute Company company) {
        companyService.save(company);
        return "redirect:/company/" + company.getId();
    }

    @GetMapping("/{id}/delete")
    public String delete(@PathVariable String id) {
        companyService.deleteById(id);
        return "redirect:/company";
    }
}
