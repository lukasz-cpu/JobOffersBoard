package com.lukaszpopek.JobOffersBoard.scrapper.scrapping.service;

import com.lukaszpopek.JobOffersBoard.scrapper.model.SalaryRange;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import lombok.SneakyThrows;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.lang3.StringUtils;
import org.jsoup.Jsoup;
import org.jsoup.nodes.Document;
import org.jsoup.nodes.Element;
import org.jsoup.select.Elements;
import org.springframework.boot.configurationprocessor.json.JSONArray;
import org.springframework.boot.configurationprocessor.json.JSONObject;

@Slf4j
public final class ScrappingHtmlUtils {

  private ScrappingHtmlUtils() {

  }

  @SneakyThrows
  public static List<String> getSeniority(Document document) {
    return Arrays.stream(
        document.getElementsByAttributeValue("id", "posting-seniority").text().split(",")).toList();
  }


  @SneakyThrows
  public static List<String> getNiceToHaveRequirements(Document document) {
    List<String> resultListWithSkills = new ArrayList<>();
    String result = document.getElementsByAttributeValueStarting("id", "posting-requirements")
        .tagName("ul").html();

    Document eachHtmlDocument = Jsoup.parse(result);
    String html = eachHtmlDocument.getElementsByAttributeValue("branch", "nices").html();
    Elements list = Jsoup.parse(html).getElementsByTag("li");

    for (Element element : list) {
      resultListWithSkills.add(StringUtils.stripAccents(element.text()));
    }
    return resultListWithSkills;
  }

  @SneakyThrows
  public static List<String> getMustHaveRequirements(Document document) {
    List<String> resultListWithSkills = new ArrayList<>();
    String result = document.getElementsByAttributeValueStarting("id", "posting-requirements")
        .tagName("ul").html();

    Document eachHtmlDocument = Jsoup.parse(result);
    String html = eachHtmlDocument.getElementsByAttributeValue("branch", "musts").html();
    Elements list = Jsoup.parse(html).getElementsByTag("li");

    for (Element element : list) {
      resultListWithSkills.add(StringUtils.stripAccents(element.text()));
    }
    return resultListWithSkills;
  }

  @SneakyThrows
  public static List<String> getCategories(Document document) {
    List<String> resultListWithCategories = new ArrayList<>();
    Elements elementsByAttribute = document.getElementsByAttribute("commonpostingcattech");
    String html = elementsByAttribute.html();
    Elements list = Jsoup.parse(html).getElementsByTag("a");

    for (Element element : list) {
      resultListWithCategories.add(element.text());
    }

    return resultListWithCategories;
  }


  @SneakyThrows
  public static String getCompanyName(Document document) {
    String script = document.getElementsByTag("nfj-json-ld").html();
    String json = Jsoup.parse(script).getElementsByTag("script").first().data();
    JSONObject jsonObject = new JSONObject(json);
    JSONArray jsonArray = jsonObject.getJSONArray("@graph");
    JSONObject firstElement = jsonArray.getJSONObject(2);
    JSONObject hiringOrganization = firstElement.getJSONObject("hiringOrganization");
    Object name = hiringOrganization.get("name");
    return StringUtils.stripAccents(name.toString());
  }

  @SneakyThrows
  public static String getJobTitle(Document document) {
    String script = document.getElementsByTag("nfj-json-ld").html();
    String json = Jsoup.parse(script).getElementsByTag("script").first().data();
    JSONObject jsonObject = new JSONObject(json);
    JSONArray jsonArray = jsonObject.getJSONArray("@graph");
    JSONObject breadcrumbList = jsonArray.getJSONObject(1);
    JSONArray itemListElement = breadcrumbList.getJSONArray("itemListElement");

    int length = itemListElement.length();
    JSONObject jobPosition = null;
    if (length == 4) {
      jobPosition = itemListElement.getJSONObject(3);
    }
    if (length == 3) {
      jobPosition = itemListElement.getJSONObject(2);
    }

    assert jobPosition != null;
    Object name = jobPosition.get("name");
    return StringUtils.stripAccents(name.toString());
  }

  @SneakyThrows
  public static String getLocation(Document document) {
    String script = document.getElementsByTag("nfj-json-ld").html();
    String json = Jsoup.parse(script).getElementsByTag("script").first().data();
    JSONObject jsonObject = new JSONObject(json);
    JSONArray jsonArray = jsonObject.getJSONArray("@graph");

    String result = "NOT-REMOTE";

    try {
      result = String.valueOf(jsonArray.getJSONObject(2).get("jobLocationType"));
    } catch (Exception ex) {

      log.info(ex.toString());
    }

    return result;
  }

  @SneakyThrows
  public static SalaryRange getSalary(Document document) {

    Element salary = document.getElementsByAttributeValue("class", "salary ng-star-inserted")
        .first();
    Elements h4 = salary.getElementsByTag("h4");

    String text = h4.text();

    if (text.contains("bezp")) {
      return new SalaryRange(0, 0);
    }

    text = text.replace("\u00a0", "").replace("PLN", "").replace(" –", "");

    List<String> salaryRange = Arrays.stream(text.split(" ")).toList();
    if (salaryRange.size() > 1) {

      return new SalaryRange(Integer.parseInt(salaryRange.get(0)),
          Integer.parseInt(salaryRange.get(1)));
    } else {
      return new SalaryRange(0, Integer.parseInt(salaryRange.get(0)));
    }
  }
}
