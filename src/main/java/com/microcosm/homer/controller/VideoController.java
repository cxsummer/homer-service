package com.microcosm.homer.controller;

import lombok.extern.slf4j.Slf4j;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

/**
 * @author caojiancheng
 * @date 2022-04-20 15:19
 */
@Slf4j
@RestController
@RequestMapping("/video")
public class VideoController {

    @GetMapping("/search")
    public String search() {
        log.info("sss");
        return "123";
    }
}
