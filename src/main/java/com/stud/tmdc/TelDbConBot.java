package com.stud.tmdc;

import com.stud.tmdc.HttpReqResp;

import java.util.HashMap;
import java.util.Map;

import org.telegram.telegrambots.api.methods.send.SendMessage;
import org.telegram.telegrambots.api.objects.Update;
import org.telegram.telegrambots.bots.TelegramLongPollingBot;
import org.telegram.telegrambots.exceptions.TelegramApiException;

public class TelDbConBot extends TelegramLongPollingBot {
    @Override
    public void onUpdateReceived(Update update) {
    	if (update.hasMessage() && update.getMessage().hasText()) {
    		
            String message_text = update.getMessage().getText();
            long chat_id = update.getMessage().getChatId();

            String[] requestParamsKeys = {"table","method","id"};
            String[] requestParamsValues = message_text.substring(1).split("_");
            String result="";

            Map<String, String> requestParams = new HashMap<String, String>();

            for(int i=0;i<requestParamsValues.length;i++)
            	requestParams.put(requestParamsKeys[i], requestParamsValues[i]);

            if(requestParams.get("method") != null) {
	            switch(requestParams.get("method")) {
	            case "get":
	            	result = HttpReqResp.getAction(requestParams);
	            	break;
	            case "post":
	            	result = HttpReqResp.postAction(requestParams);
	            	break;
	            case "put":
	            	result = HttpReqResp.putAction(requestParams);
	            	break;
	            case "delete":
	            	result = HttpReqResp.deleteAction(requestParams);
	            	break;
	            }
	            
	            SendMessage msg = new SendMessage()
	            		.setChatId(chat_id)
	            		.setText(result);
	            try {
	            	sendMessage(msg);
	            }
	            catch(TelegramApiException ex) {
	            	ex.printStackTrace();
	            }
            }
            else {
            	SendMessage msg = new SendMessage()
            			.setChatId(chat_id)
            			.setText("Wrong command");
            	try {
            		sendMessage(msg);
            	}
            	catch(TelegramApiException ex) {
            		ex.printStackTrace();
            	}
            }
        }
    }

    @Override
    public String getBotUsername() {
        return "TelDbConBot";
    }

    @Override
    public String getBotToken() {
        return "596223428:AAHTWAkDjd_B2YG1wI0uUOSDlhOChwihoqM";
    }
}