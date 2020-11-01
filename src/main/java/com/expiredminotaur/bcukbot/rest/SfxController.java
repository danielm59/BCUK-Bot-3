package com.expiredminotaur.bcukbot.rest;

import com.expiredminotaur.bcukbot.discord.music.SFXHandler;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RestController;

@RestController
public class SfxController
{
    @Autowired
    SFXHandler sfxHandler;

    @PostMapping("/playsfx")
    void playSFX(@RequestBody SfxRequest request)
    {
        sfxHandler.play(request.getSfx(), true);
    }
}
