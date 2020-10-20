package com.expiredminotaur.bcukbot.rest;

import com.expiredminotaur.bcukbot.discord.music.SFXHandler;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
public class sfxController
{
    @Autowired
    SFXHandler sfxHandler;

    @PostMapping("/playsfx/{sfx}")
    void playSFX(@PathVariable String sfx)
    {
        sfxHandler.play(sfx);
    }
}
