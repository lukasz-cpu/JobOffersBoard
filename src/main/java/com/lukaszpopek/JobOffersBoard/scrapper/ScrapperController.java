package com.lukaszpopek.JobOffersBoard.scrapper;


import com.lukaszpopek.JobOffersBoard.scrapper.model.JobLink;
import com.lukaszpopek.JobOffersBoard.scrapper.repository.JobLinkRepository;
import com.lukaszpopek.JobOffersBoard.scrapper.repository.JobOfferRepository;
import com.lukaszpopek.JobOffersBoard.scrapper.scrapping.service.ScrappingService;
import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.Set;
import java.util.SortedSet;
import java.util.TreeSet;
import lombok.AllArgsConstructor;
import lombok.RequiredArgsConstructor;
import lombok.SneakyThrows;
import lombok.extern.slf4j.Slf4j;
import org.jsoup.Jsoup;
import org.jsoup.nodes.Document;
import org.jsoup.nodes.Element;
import org.jsoup.select.Elements;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.dao.DataIntegrityViolationException;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RestController;

@Slf4j
@RestController
@AllArgsConstructor
@RequiredArgsConstructor
public class ScrapperController {

  @Autowired
  JobLinkRepository jobLinkRepository;

  @Autowired
  JobOfferRepository jobOfferRepository;

  @Autowired
  ScrappingService scrappingService;

  @Value("${job.word}")
  private String jobWord;


  private static boolean isInteger(String s) {
    if (s.isEmpty()) {
      return false;
    }
    for (int i = 0; i < s.length(); i++) {
      if (i == 0 && s.charAt(i) == '-') {
        if (s.length() == 1) {
          return false;
        } else {
          continue;
        }
      }
      if (Character.digit(s.charAt(i), 10) < 0) {
        return false;
      }
    }
    return true;
  }

  @SneakyThrows
  @GetMapping("/processLink")
  public ResponseEntity<?> processLink() {

    List<String> links = jobLinkRepository.findAll().stream().map(JobLink::getJobLink).toList();

    for (String link : links) {
      try {
        Thread.sleep(3000);
        Document doc = Jsoup.connect(link).userAgent("Mozilla").timeout(10000).get();
        scrappingService.saveJobOffer(link, doc);
      } catch (Exception ex) {
        log.error("Exception during processing link: {}", link);
      }
    }

    return ResponseEntity.ok().build();
  }

  @GetMapping("/loadLinks")
  public ResponseEntity<?> loadLinks() throws InterruptedException {

    List<String> allLinks = getAllLinks();

    for (String singleLink : allLinks) {

      try {
        scrappingService.saveLink(singleLink);

      } catch (DataIntegrityViolationException exception) {
        log.error("Error during saving the link: {}", exception.getMessage());
      }
    }
    return ResponseEntity.ok().build();
  }

  private List<String> getAllLinks() throws InterruptedException {
    int lastPage = getLastPage();
    List<String> allLinks = new ArrayList<>();
    for (int i = 1; i <= lastPage; i++) {
      List<String> linksForPage = getLinksForPage(i);
      allLinks.addAll(linksForPage);
      Thread.sleep(1500);
    }
    return allLinks;
  }

  @SneakyThrows
  private List<String> getLinksForPage(int page) {
    Set<String> result = new HashSet<>();
    var firstPage = Jsoup.connect("https://nofluffjobs.com/pl/praca-zdalna/"
        + jobWord
        + "?page=" + page).get();

    var elementById = firstPage.getElementsByAttributeValueStarting("id", "nfjPostingListItem");

    for (Element element : elementById) {
      String eachHtmlHref = element.outerHtml();
      Document eachHtmlDocument = Jsoup.parse(eachHtmlHref);
      Elements link = eachHtmlDocument.getElementsByTag("a");
      String url = link.attr("href");
      String resultUrl = "https://nofluffjobs.com" + url;
      result.add(resultUrl);
    }

    return new ArrayList<>(result);
  }

  @SneakyThrows
  private int getLastPage() {

    Document doc = Jsoup.connect("https://nofluffjobs.com/pl/praca-zdalna/" + jobWord + "?page=1")
        .get();
    String paginationContent = doc.getElementsByTag("nfj-pagination").html();

    Document documentOfLinks = Jsoup.parse(paginationContent);

    Elements hyperLinksElements = documentOfLinks.getElementsByTag("a");

    SortedSet<Integer> integers = new TreeSet<>();

    for (Element hyperLinksElement : hyperLinksElements) {
      String text = hyperLinksElement.text();
      if (isInteger(text)) {
        integers.add(Integer.valueOf(text));
      }
    }

    return !integers.isEmpty() ? integers.last() : 0;
  }

}

