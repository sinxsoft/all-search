package com.anqi.es.controller;

import com.anqi.es.highclient.RestHighLevelClientService;
import org.elasticsearch.action.index.IndexResponse;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.ResponseBody;
import org.springframework.web.bind.annotation.RestController;

import javax.servlet.http.HttpServletResponse;
import java.io.IOException;

/**
 * @author anqi
 */
@RestController
public class EsController {

    @Autowired
    RestHighLevelClientService service;

    @GetMapping("/es")
	@ResponseBody
    public Object  testHigh(HttpServletResponse httpServletResponse) throws IOException{
        String source = "{\n" +
                "  \"name\" : \"耐苦无领运动半袖\",\n" +
                "  \"price\" : 300,\n" +
                "  \"num\" : 800,\n" +
                "  \"date\" : \"2019-07-28\"\n" +
                "}";
        //service.createIndex("idx_clouthing", null, null);
        IndexResponse response = service.addDoc("idx_clouthing","11111", source);

        return response;
    }
    
    @GetMapping("/create/{name}")
	@ResponseBody
    public Object create(@PathVariable String name, HttpServletResponse httpServletResponse) throws IOException{
      return  service.createIndex(name, null, null);
    }
}
