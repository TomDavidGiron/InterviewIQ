package com.cvoptimizer.cv_backend.controller;

import com.cvoptimizer.cv_backend.dto.ManualJobInputDTO;
import com.cvoptimizer.cv_backend.entity.JobDescription;
import com.cvoptimizer.cv_backend.model.ScraperResult;
import com.cvoptimizer.cv_backend.repository.JobDescriptionRepository;
import com.cvoptimizer.cv_backend.scraper.JobScraperRouter;
import com.cvoptimizer.cv_backend.scraper.SsrfGuard;
import jakarta.validation.Valid;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/scrape")
public class JobScraperController {

    private final JobDescriptionRepository jobDescriptionRepository;
    private final JobScraperRouter jobScraperRouter;

    public JobScraperController(JobDescriptionRepository jobDescriptionRepository,
                                JobScraperRouter jobScraperRouter) {
        this.jobDescriptionRepository = jobDescriptionRepository;
        this.jobScraperRouter = jobScraperRouter;
    }

    @GetMapping
    public ScraperResult scrapeJob(@RequestParam("url") String url) {
        SsrfGuard.assertSafe(url);
        return jobScraperRouter.scrape(url);
    }

    @PostMapping("/manual-input")
    public ScraperResult handleManualInput(@Valid @RequestBody ManualJobInputDTO input) {
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