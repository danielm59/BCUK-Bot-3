package com.expiredminotaur.bcukbot.twitch;

import com.expiredminotaur.bcukbot.sql.twitch.bannedphrase.BannedPhrase;
import com.expiredminotaur.bcukbot.sql.twitch.bannedphrase.BannedPhraseRepository;
import com.expiredminotaur.bcukbot.twitch.command.chat.TwitchCommandEvent;
import com.expiredminotaur.bcukbot.twitch.command.chat.TwitchPermissions;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

@Component
public class BanHandler
{
    @Autowired
    private BannedPhraseRepository bannedPhraseRepository;

    public boolean checkBannedPhrases(TwitchCommandEvent event)
    {
        if (TwitchPermissions.subPlus(event))
            return false;
        else
        {
            for (BannedPhrase bannedPhrase : bannedPhraseRepository.findAll())
            {
                if (event.getOriginalMessage().toLowerCase().contains(bannedPhrase.getPhrase().toLowerCase()))
                {
                    event.respond("/ban " + event.getEvent().getUser().getName() + " Banned Phrase: " + bannedPhrase.getPhrase());
                    return true;
                }
            }
        }
        return false;
    }
}
