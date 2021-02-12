package me.knight.kafapi.web;

import me.knight.kafapi.service.MessagesKeyValue;
import me.knight.kafapi.entity.KeyValueBean;
import me.knight.kafapi.entity.ProcessorMetadata;
import lombok.AllArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@Slf4j
@RestController
@AllArgsConstructor
@RequestMapping(value="/messages")
public class MessagesKeyValueController {

    private final MessagesKeyValue messagesKeyValue;

    @PostMapping(consumes = "application/json")
    public void submitMessage(@RequestBody final KeyValueBean keyValueBean) {
        log.info("post");
        messagesKeyValue.post(keyValueBean);
    }

    @GetMapping(path = "/{key}", produces="application/json")
    public KeyValueBean valueByKey(@PathVariable final String key) {
       log.info("get by key " + key);
       return messagesKeyValue.valueByKey(key);
    }

    @GetMapping(path = "/hosts", produces="application/json")
    public List<ProcessorMetadata> processors() {
        log.info("get processors");
        return messagesKeyValue.processors();
    }
}
