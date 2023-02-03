package com.websocket.chat.service;

import com.websocket.chat.model.Agrees;
import com.websocket.chat.model.ChatMessage;
import com.websocket.chat.model.Guiltys;
import com.websocket.chat.repo.ChatRoomRepository;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.data.redis.listener.ChannelTopic;
import org.springframework.stereotype.Service;

import java.util.ArrayList;
import java.util.List;

@RequiredArgsConstructor
@Service
@Slf4j
public class ChatService {

    private final ChannelTopic channelTopic;
    private final RedisTemplate redisTemplate;
    private final ChatRoomRepository chatRoomRepository;
    public int jurorsVoteCount = 0;
    public int crimeVoteCount = 0;
//    public List<Agrees> agrees = new ArrayList<Agrees>(2);
//    public List<Guiltys> guilty = new ArrayList<Guiltys>(2);
    public List<String> agrees = new ArrayList<String>(4);
    public List<String> guiltys = new ArrayList<String>(4);
    /**
     * destination정보에서 roomId 추출
     */
    public String getRoomId(String destination) {
        int lastIndex = destination.lastIndexOf('/');
        if (lastIndex != -1)
            return destination.substring(lastIndex + 1);
        else
            return "";
    }

    /**
     * 채팅방에 메시지 발송
     */
    public void sendChatMessage(ChatMessage chatMessage) {
        chatMessage.setUserCount(chatRoomRepository.getUserCount(chatMessage.getRoomId()));
        ChatMessage resultMessage = new ChatMessage(null, chatMessage.getRoomId(), "공지" , "",0);
        if (ChatMessage.MessageType.ENTER.equals(chatMessage.getType())) {
            chatMessage.setMessage(chatMessage.getSender() + "님이 방에 입장했습니다.");
            chatMessage.setSender("[알림]");
            log.info("방 입장");
//            log.info(agrees[0].getName());
        } else if (ChatMessage.MessageType.MAKE.equals(chatMessage.getType())) {
            chatMessage.setMessage(chatMessage.getSender() + "님이 배심원단을 구성하셨습니다.");
            chatMessage.setSender("[알림]");
            log.info("배심원단 구성 완료");
        } else if (ChatMessage.MessageType.YES.equals(chatMessage.getType())) {
            jurorsVoteCount = jurorsVoteCount + 1;
            chatMessage.setMessage(chatMessage.getSender() + "님이 찬반 투표하셨습니다.");
            agrees.add(chatMessage.getSender());
            agrees.add("찬성");
            chatMessage.setSender("[알림]");
//            if (agrees.get(0).getName().equals("")) {
//                agrees.get(0).setName(chatMessage.getSender());
//                agrees.get(0).setVote(true);
//            } else {
//                agrees.get(1).setName(chatMessage.getSender());
//                agrees.get(1).setVote(true);
//            }
            log.info("찬반투표 입력들어옴");
        } else if (ChatMessage.MessageType.NO.equals(chatMessage.getType())) {
            jurorsVoteCount = jurorsVoteCount + 1;
            chatMessage.setMessage(chatMessage.getSender() + "님이 찬반 투표하셨습니다.");
            agrees.add(chatMessage.getSender());
            agrees.add("반대");
            chatMessage.setSender("[알림]");
//            if (agrees.get(0).getName().equals("")) {
//                agrees.get(0).setName(chatMessage.getSender());
//                agrees.get(0).setVote(false);
//            } else {
//                agrees.get(1).setName(chatMessage.getSender());
//                agrees.get(1).setVote(false);
//            }
            log.info("찬반투표 입력들어옴");
        } else if (ChatMessage.MessageType.GUILTY.equals(chatMessage.getType())) {
            crimeVoteCount = crimeVoteCount + 1;
            chatMessage.setMessage(chatMessage.getSender() + "님이 유무죄 투표를 하셨습니다.");
            guiltys.add(chatMessage.getSender());
            guiltys.add("유죄");
            chatMessage.setSender("[알림]");

//            if (guilty.get(0).getName().equals("")) {
//                guilty.get(0).setName(chatMessage.getSender());
//                guilty.get(0).setVote(true);
//            } else {
//                guilty.get(1).setName(chatMessage.getSender());
//                guilty.get(1).setVote(true);
//            }
            log.info("유무죄투표 입력들어옴");
        } else if (ChatMessage.MessageType.NOTGUILTY.equals(chatMessage.getType())) {
            crimeVoteCount = crimeVoteCount + 1;
            chatMessage.setMessage(chatMessage.getSender() + "님이 유무죄 투표를 하셨습니다.");
            guiltys.add(chatMessage.getSender());
            guiltys.add("무죄");
            chatMessage.setSender("[알림]");


//            if (guilty.get(0).getName().equals("")) {
//                guilty.get(0).setName(chatMessage.getSender());
//                guilty.get(0).setVote(false);
//            } else {
//                guilty.get(1).setName(chatMessage.getSender());
//                guilty.get(1).setVote(false);
//            }
            log.info("유무죄투표 입력들어옴");
        } else {
            chatMessage.setMessage(chatMessage.getSender() + "님의 입력이 정상적이지 않습니다.");
            chatMessage.setSender("[에러]");
        }

        if (jurorsVoteCount == 2) {
//            chatMessage.setMessage(chatMessage.getMessage() + agrees.get(0) + " 님의 투표는 " + agrees.get(1)
//                                    + " " + agrees.get(2) + " 님의 투표는 " + agrees.get(3) + " 입니다");
            resultMessage.setMessage(agrees.get(0) + " 님의 투표는 " + agrees.get(1)
                    + " " + agrees.get(2) + " 님의 투표는 " + agrees.get(3) + " 입니다");
            log.info(agrees.get(0));
            log.info(agrees.get(1));
            log.info(agrees.get(2));
            log.info(agrees.get(3));

            int yes = 0;
            int no = 0;
            for (int i=1; i <= 3; i+=2) {
                if (agrees.get(i).equals("찬성")) {
                    yes += 1;
                } else {
                    no += 1;
                }
            }
//            if(yes<=no) {
//                chatMessage.setMessage(chatMessage.getMessage() + "따라서 반대입니다");
//            } else {
//                chatMessage.setMessage(chatMessage.getMessage() + "따라서 찬성입니다");
//            }
            if(yes<=no) {
                resultMessage.setMessage(resultMessage.getMessage() + "따라서 반대입니다");
            } else {
                resultMessage.setMessage(resultMessage.getMessage() + "따라서 찬성입니다");
            }

            agrees = new ArrayList<String>(4);
            chatMessage.setSender("[알림]");
            jurorsVoteCount = 0;
            log.info("찬반투표 2회 완료");
        }

        if (crimeVoteCount == 2) {
//            chatMessage.setMessage(chatMessage.getMessage() + "\n2명의 유무죄 투표가 완료되었습니다.");
            resultMessage.setMessage("2명의 유무죄 투표가 완료되었습니다.");

            int guilty = 0;
            int notguilty = 0;
            for (int i=1; i <= 3; i+=2) {
                if (guiltys.get(i).equals("유죄")) {
                    guilty += 1;
                } else {
                    notguilty += 1;
                }
            }
//            if (notguilty != 0) {
//                chatMessage.setMessage(chatMessage.getMessage() + "\n무죄 투표가" + notguilty + "표가 투표되어 무죄로 판결되었습니다.");
//            } else {
//                chatMessage.setMessage(chatMessage.getMessage() + "\n무죄 투표가" + notguilty + "표가 투표되어 유죄로 판결되었습니다.");
//            }
            if (notguilty != 0) {
                resultMessage.setMessage(resultMessage.getMessage() + "\n무죄 투표가" + notguilty + "표가 투표되어 무죄로 판결되었습니다.");
            } else {
                resultMessage.setMessage(resultMessage.getMessage() + "\n무죄 투표가" + notguilty + "표가 투표되어 유죄로 판결되었습니다.");
            }
            guiltys = new ArrayList<String>(4);
            chatMessage.setSender("[알림]");
            crimeVoteCount = 0;
            log.info("유무죄투표 2회 완료");
        }
        redisTemplate.convertAndSend(channelTopic.getTopic(), chatMessage);
        if (!resultMessage.getMessage().equals("")) {
            redisTemplate.convertAndSend(channelTopic.getTopic(), resultMessage);
        }
    }
}
