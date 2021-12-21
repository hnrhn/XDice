/* TODO: Semantic a11y */
/* eslint-disable no-script-url,jsx-a11y/anchor-is-valid */
import React, {useState} from "react";
import {openSocket, sendJoinRoomMessage, socket} from "./XDiceSocket";
import {keyWasEnter} from "./utils";

export function JoinRoom(props: { changePageTo: Function, createJoinChooserPath: string, setWsOpen: Function, setRoomCode: Function, onError: Function }) {
    const [username, setUsername] = useState("");
    const [roomName, setRoomName] = useState("");
    const [password, setPassword] = useState("");
    const [submitting, setSubmitting] = useState(false);

    async function validateAndSend() {
        if (username === "" || roomName === "") {
            return;
        }

        await sendJoinRequestToServer();
    }

    async function sendJoinRequestToServer() {
        if (submitting) {
            return;
        }

        setSubmitting(true);

        if (socket === null || socket === undefined || socket.readyState === socket.CLOSED) {
            await openSocket("ws://localhost:4567/ws", props.setWsOpen);
        }

        try {
            await sendJoinRoomMessage(roomName, username, password);
            props.setRoomCode("Loading...");
            props.changePageTo("room");
        } catch (e) {
            props.onError(e);
        }
    }

    return (
        <div className={"centered"}>
            <div id={"joinRoom"}>
                <a href="javascript:void(0);" onClick={() => props.changePageTo(props.createJoinChooserPath)}>&lt; Go Back</a>
                <br />
                <br />
                <table style={{"minWidth": "600px"}}>
                    <tbody>
                        <tr>
                            <td>
                                <label htmlFor={"joinUsername"}>Your username:</label>
                            </td>
                            <td>
                                <input id={"joinUsername"} className={"fullWidthInput"} type={"text"} onChange={e => setUsername(e.currentTarget.value)} onKeyUp={e => keyWasEnter(e) && validateAndSend()} />
                            </td>
                        </tr>
                        <tr>
                            <td>
                                <label htmlFor={"joinRoomName"}>Room Code:</label>
                            </td>
                            <td>
                                <input id={"joinRoomName"} className={"fullWidthInput"} type={"text"} onChange={e => setRoomName(e.currentTarget.value)} onKeyUp={e => keyWasEnter(e) && validateAndSend()} />
                            </td>
                        </tr>
                        <tr>
                            <td>
                                <label htmlFor={"joinPassword"}>Password (optional):</label>
                            </td>
                            <td>
                                <input id={"joinPassword"} className={"fullWidthInput"} type={"text"} onChange={e => setPassword(e.currentTarget.value)} onKeyUp={e => keyWasEnter(e) && validateAndSend()} />
                            </td>
                        </tr>
                    </tbody>
                </table>
                <br />
                <button className={"fullWidthButton"} onClick={sendJoinRequestToServer}>{submitting ? "Loading..." : "Join"}</button>
            </div>
        </div>
    )
}