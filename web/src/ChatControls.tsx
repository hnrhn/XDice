import React, {useEffect, useState} from "react";
import {sendChatMessage} from "./XDiceSocket";
import NewUsernameControls from "./NewUsernameControls";
import {ChatControlsProps} from "./Interfaces";

export default function ChatControls(props: ChatControlsProps) {
    const [sendActive, setSendActive] = useState(true);
    const [message, setMessage] = useState("");
    const [newUsernameHidden, setNewUsernameHidden] = useState(true);

    useEffect(() => {
        document.getElementById("newMessageInput")!.focus();
    })

    const checkForSubmit = (e: React.KeyboardEvent) => e.code === "Enter";

    function sendToXDice() {
        if (!sendActive) {
            return;
        }

        setSendActive(false);
        if (message.length === 0) {
            setSendActive(true);
            return;
        }

        sendChatMessage(props.userId, message)
            .then(_ => {
                setMessage("");
                setSendActive(true);
            });
    }

    return (
        <>
            <div id="newMessageControls" className="centered">
                <span id="currentUsername" className={newUsernameHidden ? "" : "activeUsernameButton"} onClick={() => setNewUsernameHidden(!newUsernameHidden)}>@{props.username}</span>
                <input id="newMessageInput" aria-label="Roll string input" value={message} onChange={e => setMessage(e.currentTarget.value)} onKeyUp={e => checkForSubmit(e) && sendToXDice()} />
                <button id="sendMessageButton" type="submit" onClick={sendToXDice}>Enter</button>
            </div>
            {
                newUsernameHidden
                    ? null
                    : <NewUsernameControls username={props.username} roomCode={props.roomCode} onSubmitted={() => setNewUsernameHidden(true)}/>
            }
        </>
    );
}