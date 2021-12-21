/* TODO: Semantic a11y */
/* eslint-disable jsx-a11y/anchor-is-valid,no-script-url */
import React, {useState} from "react";
import {openSocket, sendCreateRoomMessage, socket} from "./XDiceSocket";
import {keyWasEnter} from "./utils";

export function CreateRoom(props: { changePageTo: Function, createJoinChooserPath: string, setWsOpen: Function, setRoomCode: Function, onError: Function }) {
    const [username, setUsername] = useState("");
    const [roomName, setRoomName] = useState("");
    const [password, setPassword] = useState("");
    const [showPasswordField, setShowPasswordField] = useState(false);
    const [submitting, setSubmitting] = useState(false);

    async function validateAndSend() {
        if (username === "" || roomName === "") {
            return;
        }

        await sendCreateRequestToServer();
    }

    async function sendCreateRequestToServer() {
        if (submitting) {
            return;
        }

        setSubmitting(true);

        if (socket === null || socket === undefined || socket.readyState === socket.CLOSED) {
            await openSocket("ws://localhost:4567/ws", props.setWsOpen);
        }

        try {
            await sendCreateRoomMessage(roomName, username, showPasswordField ? password : "");
            props.setRoomCode("Loading...");
            props.changePageTo("room");
        } catch (e) {
            props.onError(e);
        }
    }

    return (
        <div className={"centered"}>
            <div id={"createRoom"}>
                <a href="javascript:void(0);" onClick={() => props.changePageTo(props.createJoinChooserPath)}>&lt; Go Back</a>
                <br />
                <br />
                <table style={{"minWidth": "600px"}}>
                    <tbody>
                        <tr>
                            <td>
                                <label htmlFor={"createUsername"}>Choose a Username:</label>
                            </td>
                            <td>
                                <input id={"createUsername"} className={"fullWidthInput"} type={"text"} onChange={e => setUsername(e.currentTarget.value)} onKeyUp={e => keyWasEnter(e) && validateAndSend()} />
                            </td>
                        </tr>
                        <tr>
                            <td>
                                <label htmlFor={"createRoomName"}>Choose a Room Name:</label>
                            </td>
                            <td>
                                <input id={"createRoomName"} className={"fullWidthInput"} type={"text"} onChange={e => setRoomName(e.currentTarget.value)} onKeyUp={e => keyWasEnter(e) && validateAndSend()} />
                            </td>
                        </tr>
                        {showPasswordField
                            ? (
                                <tr>
                                    <td>
                                        <label htmlFor={"createPassword"}>Password:</label>
                                    </td>
                                    <td>
                                        <input id={"createPassword"} type={"text"} onChange={e => setPassword(e.currentTarget.value)} onKeyUp={e => keyWasEnter(e) && validateAndSend()} /> <a href="#" onClick={() => setShowPasswordField(false)}>X</a>
                                    </td>
                                </tr>
                            )
                            : (
                                <tr>
                                    <td>
                                        <i><a href="#" onClick={() => setShowPasswordField(true)}>Set a password?</a></i>
                                    </td>
                                </tr>
                            )
                        }
                    </tbody>
                </table>
                <br />
                <button className={"fullWidthButton"} onClick={sendCreateRequestToServer}>{submitting ? "Loading..." : "Create"}</button>
            </div>
        </div>
    )
}