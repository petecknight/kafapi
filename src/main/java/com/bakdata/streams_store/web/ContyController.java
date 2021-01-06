package com.bakdata.streams_store.web;

import com.bakdata.streams_store.service.ContyService;
import com.bakdata.streams_store.entity.KeyValueBean;
import com.bakdata.streams_store.entity.ProcessorMetadata;
import lombok.AllArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@Slf4j
@RestController
@AllArgsConstructor
@RequestMapping(value="/messages")
public class ContyController {

    private final ContyService contyService;

    @PostMapping(consumes = "application/json")
    public void submitMessage(@RequestBody final KeyValueBean keyValueBean) {
        log.info("post");
        contyService.post(keyValueBean);
    }

    @GetMapping(path = "/{key}", produces="application/json")
    public KeyValueBean valueByKey(@PathVariable final String key) {
       log.info("get by key " + key);
       return contyService.valueByKey(key);
    }

    @GetMapping(path = "/hosts", produces="application/json")
    public List<ProcessorMetadata> processors() {
        log.info("get processors");
        return contyService.processors();
    }
}
