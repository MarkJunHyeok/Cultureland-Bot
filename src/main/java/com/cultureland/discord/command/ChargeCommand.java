package com.cultureland.discord.command;


import com.jagrosh.jdautilities.command.Command;
import com.jagrosh.jdautilities.command.CommandEvent;
import com.jagrosh.jdautilities.command.SlashCommand;
import net.dv8tion.jda.api.EmbedBuilder;
import net.dv8tion.jda.api.entities.Message;
import net.dv8tion.jda.api.entities.MessageEmbed;
import net.dv8tion.jda.api.entities.MessageHistory;
import net.dv8tion.jda.api.events.interaction.SlashCommandEvent;
import net.dv8tion.jda.api.interactions.commands.OptionMapping;
import net.dv8tion.jda.api.interactions.commands.OptionType;
import net.dv8tion.jda.api.interactions.commands.build.OptionData;
import net.dv8tion.jda.api.requests.restaction.interactions.ReplyAction;

import java.awt.*;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

import static com.cultureland.discord.selenium.ChargeSelenium.chargeMain;

public class ChargeCommand extends SlashCommand {



    public ChargeCommand() {

        this.name = "충전";
        this.help = "/충전 닉네임 핀번호 - 해당 계정에 핀번호를 충전합니다..";
        List<OptionData> list = new ArrayList<>();

       list.add(new OptionData(OptionType.STRING, "닉네임", "닉네임을 입력해주세요").setRequired(true));
       list.add(new OptionData(OptionType.STRING, "핀번호", "핀번호를 입력해주세요").setRequired(true));
       this.options = list;
    }


    @Override
    protected void execute(SlashCommandEvent slashCommandEvent) {
        String nickName = slashCommandEvent.getOption("닉네임").getAsString();
        String pinNum = slashCommandEvent.getOption("핀번호").getAsString();


        EmbedBuilder embedBuilder = new EmbedBuilder();
        MessageEmbed resultMessages;
        embedBuilder.setTitle("처리중입니다..");
        resultMessages = embedBuilder.build();
        slashCommandEvent.replyEmbeds(resultMessages).setEphemeral(true).queue();



        int resultMoney = chargeMain(pinNum);
        System.out.println("충전 결과 : " + resultMoney);
        MessageEmbed resultMessage = resultSetting(resultMoney, nickName);
        slashCommandEvent.getHook().editOriginalEmbeds().setEmbeds(resultMessage).queue();


    }

    private MessageEmbed resultSetting(int resultMoney, String nickName){
        EmbedBuilder embedBuilder = new EmbedBuilder();
        MessageEmbed resultMessage;
        if(resultMoney > 0){
            embedBuilder.setTitle("**< 충전 결과 >**");
            String information = "**충전이 완료 되었습니다.**";
            information += "\n\n유저 닉네임 : " + nickName;
            information += "\n처리된 금액 : " + resultMoney + " 원";
            embedBuilder.setDescription(information);
            embedBuilder.setColor(Color.green);
        }else if(resultMoney == -404){
            embedBuilder.setTitle("**< 충전 결과 >**");
            String information = "**서버 상태가 불안정합니다..**";
            String help = "\n\n\n나중에 충전을 다시 시도해주세요.";
            embedBuilder.setDescription(information);
            embedBuilder.setFooter(help);
            embedBuilder.setColor(Color.red);
        }else if (resultMoney == 0 || resultMoney == -1){
            embedBuilder.setTitle("**< 충전 결과 >**");
            String information = "**충전할 수 없는 핀번호입니다.**";
            String help = "\n\n\n충전을 다시 시도해주세요.";
            embedBuilder.setDescription(information);
            embedBuilder.setFooter(help);
            embedBuilder.setColor(Color.red);
        }
        resultMessage = embedBuilder.build();
        return resultMessage;

    }

}
