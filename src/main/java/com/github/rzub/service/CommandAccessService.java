package com.github.rzub.service;

import com.github.rzub.model.CommandAccessModel;
import net.dv8tion.jda.api.entities.Member;
import org.springframework.stereotype.Service;

import java.util.Optional;

@Service
public class CommandAccessService {
    private final CommandAccessModel commandAccessModel;

    public CommandAccessService(CommandAccessModel commandAccessModel) {
        this.commandAccessModel = commandAccessModel;
    }

    public boolean hasAccess(String command, Member member){
        Optional<CommandAccessModel.AccessModel> accessModelOptional = commandAccessModel.getCommands()
                .stream().filter(accessModel -> accessModel.getId().equals(command))
                .findAny();
        if (!accessModelOptional.isPresent())
            return false;

        if (accessModelOptional.get().isPublicCommand())
            return true;

        boolean isInRoles = member.getRoles().stream().anyMatch(
                role -> accessModelOptional.get().getRoles().stream().anyMatch(cmdRole -> cmdRole.equals(role.getId()))
        );

        boolean isInUsers = accessModelOptional.get().getUsers().stream().anyMatch(s -> s.equals(member.getId()));

        return isInUsers || isInRoles;
    }
}
