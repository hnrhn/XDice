import React from "react";
import {ChatBoxProps} from "./Interfaces";

export default function ChatBox(props: ChatBoxProps) {
    return (
        <div id="chatBox">
            <div>
                {
                    props.messages.map(msg => {
                        return (
                            <div key={msg.key} className={props.userId === msg.mentionedUserId ? "chatContainer highlighted" : "chatContainer"}>
                                <div className={"chatUser"}>{props.users.find(u => u.userId === msg.authorId)?.username}</div>
                                <div className={"chatMessage"}>{msg.mentionedUserId ?? false ? `@${props.users.find(u => u.userId === msg.mentionedUserId)?.username}: ` : ""}{msg.messageContent}</div>
                            </div>
                        );
                    })
                }
            </div>
        </div>
    );
}