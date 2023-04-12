package com.analysetool.api;
import com.analysetool.modells.Company;
import com.analysetool.services.CompanyService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.*;


import org.springframework.web.bind.annotation.CrossOrigin;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;
import java.util.Optional;

@RestController
//@RequestMapping("/company")
public class CompanyController {
    private CompanyService companyService;
    @Autowired
   // private CompanyService companyService;
    public CompanyController(CompanyService companyService){
        this.companyService = companyService;
    }

    @GetMapping("/companycount")
    public String getCount(){
        return  Integer.toString(companyService.findAll().size());

    }


    @GetMapping("/list")
    public String list(Model model) {
        List<Company> companies = companyService.findAll();
        model.addAttribute("companies", companies);
        return "company/list";
    }

    @GetMapping("/{id}")
    public String show(@PathVariable String id, Model model) {
        Optional<Company> company = companyService.findById(id);
        model.addAttribute("company", company.get());
        return "company/show";}


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
