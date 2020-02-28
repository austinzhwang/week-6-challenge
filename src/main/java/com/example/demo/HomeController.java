package com.example.demo;

import com.cloudinary.utils.ObjectUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

import java.io.IOException;
import java.util.Map;

@Controller
public class HomeController {
    @Autowired
    CarRepository carRepository;

    @Autowired
    CategoryRepository categoryRepository;

    @Autowired
    CloudinaryConfig cloudc;

    @RequestMapping("/")
    public String index(Model model) {
        model.addAttribute("cars", carRepository.findAll());
        model.addAttribute("categories", categoryRepository.findAll());
        return "index";
    }

    @GetMapping("/add-category")
    public String addTeam(Model model) {
        model.addAttribute("category", new Category());
        return "category-form";
    }

    @PostMapping("/process-category")
    public String processTeam(@ModelAttribute Category category) {
        categoryRepository.save(category);
        return "redirect:/";
    }

    @GetMapping("/add-car")
    public String addCar(Model model) {
        model.addAttribute("categories", categoryRepository.findAll());
        model.addAttribute("car", new Car());
        return "car-form";
    }

    @PostMapping("/process-car")
    public String processCar(@ModelAttribute Car car, @RequestParam("file") MultipartFile file, String img) {
        if (file.isEmpty()) {
            car.setImg(img);
            carRepository.save(car);
            return "redirect:/";
        }
        try {
            Map uploadResult = cloudc.upload(file.getBytes(),
                    ObjectUtils.asMap("resourcetype", "auto"));
            car.setImg(uploadResult.get("url").toString());
            carRepository.save(car);
        } catch (IOException e) {
            e.printStackTrace();
            return "redirect:/add-car";
        }
        return "redirect:/";
    }

    @RequestMapping("/details/{id}")
    public String showCar(@PathVariable("id") long id, Model model) {
        model.addAttribute("car", carRepository.findById(id).get());
        return "car-details";
    }

    @RequestMapping("/update/{id}")
    public String updateCar(@PathVariable("id") long id, Model model) {
        model.addAttribute("car", carRepository.findById(id).get());
        model.addAttribute("categories", categoryRepository.findAll());
        return "car-form";
    }

    @RequestMapping("/delete/{id}")
    public String deleteCar(@PathVariable("id") long id) {
        carRepository.deleteById(id);
        return "redirect:/";
    }
}