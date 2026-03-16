package com.cvoptimizer.cv_backend.controller;

import com.cvoptimizer.cv_backend.dto.ManualJobInputDTO;
import com.cvoptimizer.cv_backend.entity.JobDescription;
import com.cvoptimizer.cv_backend.model.ScraperResult;
import com.cvoptimizer.cv_backend.repository.JobDescriptionRepository;
import com.cvoptimizer.cv_backend.scraper.JobScraperRouter;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/scrape")
@CrossOrigin(origins = "*") // optional for frontend CORS support
public class JobScraperController {

    @Autowired
    private JobDescriptionRepository jobDescriptionRepository;

    @GetMapping
    public ScraperResult scrapeJob(@RequestParam("url") String url) {
        return JobScraperRouter.scrape(url);
    }

    @PostMapping("/manual-input")
    public ScraperResult handleManualInput(@RequestBody ManualJobInputDTO input) {
        // Save job input to DB
        JobDescription job = new JobDescription();
        job.setTitle(input.getTitle());
        job.setCompany(input.getCompany());
        job.setLocation(input.getLocation());
        job.setDescription(input.getDescription());
        job.setRequirements(input.getRequirements());

        jobDescriptionRepository.save(job);

        return new ScraperResult(
                job.getTitle(),
                job.getCompany(),
                job.getLocation(),
                job.getDescription(),
                job.getRequirements()
        );
    }

    @GetMapping("/manual-inputs")
    public List<JobDescription> getAllManualJobDescriptions() {
        return jobDescriptionRepository.findAll();
    }

}
