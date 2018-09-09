package com.sapelkinav.bear.service;

import net.kodehawa.lib.imageboards.DefaultImageBoards;
import net.kodehawa.lib.imageboards.entities.impl.Rule34Image;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.stream.Collectors;

@Service
public class BearService {
    private static final Logger logger = LoggerFactory.getLogger(BearService.class);


    public List<String> searchImages(String tags, int page)  {

        logger.info("Looking up " );
        // Artificial delay of 1s for demonstration purposes

        return DefaultImageBoards.RULE34.
                search(page, 5, tags)
                .blocking()
                .stream()
                .map(Rule34Image::getURL)
                .collect(Collectors.toList());

    }
    public List<String> getRandomImages()  {

        logger.info("Looking up " );
        // Artificial delay of 1s for demonstration purposes


        return DefaultImageBoards.RULE34.get(10).blocking().stream()
                .map(Rule34Image::getURL).collect(Collectors.toList());

    }



}
