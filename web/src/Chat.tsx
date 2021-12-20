import React from "react";
import ChatBox from "./ChatBox";
import ChatControls from "./ChatControls";
import {ChatProps} from "./Interfaces";

export default function Chat(props: ChatProps) {
    return (
        <div>
            <ChatBox messages={props.messages} userId={props.userId} users={props.users}/>
            <ChatControls roomCode={props.roomCode} userId={props.userId} username={props.username}/>
        </div>
    );
}