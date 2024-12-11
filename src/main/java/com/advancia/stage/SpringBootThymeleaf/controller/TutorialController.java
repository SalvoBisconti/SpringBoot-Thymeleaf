package com.advancia.stage.SpringBootThymeleaf.controller;

import java.util.ArrayList;
import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.ModelAttribute;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;

import com.advancia.stage.SpringBootThymeleaf.entity.Tutorial;
import com.advancia.stage.SpringBootThymeleaf.repository.TutorialRepository;

@Controller
public class TutorialController {

	@Autowired
	private TutorialRepository tutorialRepository;

	@GetMapping("/tutorials")
	public String getAll(Model model, @Param("keyword") String keyword) {
		try {
			List<Tutorial> tutorials = new ArrayList<Tutorial>();

			if (keyword == null) {
				tutorialRepository.findAll().forEach(tutorials::add);
			} else {
				tutorialRepository.findByTitleContainingIgnoreCase(keyword).forEach(tutorials::add);
				model.addAttribute("keyword", keyword);
			}

			model.addAttribute("tutorials", tutorials);
		} catch (Exception e) {
			model.addAttribute("message", e.getMessage());
		}

		return "tutorials";
	}

	@GetMapping("/tutorials/new")
	public String addTutorial(Model model) {
		Tutorial tutorial = new Tutorial();
		tutorial.setPublished(true);

		model.addAttribute("tutorial", tutorial);
		model.addAttribute("pageTitle", "Create new Tutorial");

		return "tutorial_form";
	}

	@PostMapping("/tutorials/save")
	public String saveTutorial(@ModelAttribute Tutorial tutorial, RedirectAttributes redirectAttributes) {
		try {
			tutorialRepository.save(tutorial);

			redirectAttributes.addFlashAttribute("message", "The Tutorial has been saved successfully!");
		} catch (Exception e) {
			redirectAttributes.addFlashAttribute("message", "Error saving tutorial: " + e.getMessage());
		}

		return "redirect:/tutorials";
	}

	@GetMapping("/tutorials/{id}")
	public String editTutorial(@PathVariable("id") Integer id, Model model, RedirectAttributes redirectAttributes) {
		try {
			Tutorial tutorial = tutorialRepository.findById(id)
					.orElseThrow(() -> new IllegalArgumentException("Invalid tutorial ID: " + id));

			model.addAttribute("tutorial", tutorial);
			model.addAttribute("pageTitle", "Edit Tutorial (ID: " + id + ")");
		} catch (Exception e) {
			redirectAttributes.addFlashAttribute("message", "Error: " + e.getMessage());
			return "redirect:/tutorials";
		}

		return "tutorial_form";
	}

	@GetMapping("/tutorials/delete/{id}")
	public String deleteTutorial(@PathVariable("id") Integer id, RedirectAttributes redirectAttributes) {
		try {
			tutorialRepository.deleteById(id);

			redirectAttributes.addFlashAttribute("message",
					"The Tutorial with ID=" + id + " has been deleted successfully!");
		} catch (Exception e) {
			redirectAttributes.addFlashAttribute("message", "Error: " + e.getMessage());
		}

		return "redirect:/tutorials";
	}

	@GetMapping("/tutorials/{id}/published/{status}")
	public String updateTutorialPublishedStatus(@PathVariable("id") Integer id,
			@PathVariable("status") boolean published, RedirectAttributes redirectAttributes) {
		try {
			tutorialRepository.updatePublishedStatus(id, published);

			String status = published ? "published" : "disabled";
			String message = "The Tutorial with ID=" + id + " has been " + status + ".";
			redirectAttributes.addFlashAttribute("message", message);
		} catch (Exception e) {
			redirectAttributes.addFlashAttribute("message", "Error: " + e.getMessage());
		}

		return "redirect:/tutorials";
	}
}
