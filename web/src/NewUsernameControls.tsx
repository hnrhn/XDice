import React, {useState} from "react";
import {sendChangeUsernameMessage} from "./XDiceSocket";
import {NewUsernameProps} from "./Interfaces";

export default function NewUsernameControls(props: NewUsernameProps) {
    const [newUsernameInputText, setNewUsernameInputText] = useState("");

    function requestUsernameUpdate() {
        sendChangeUsernameMessage(newUsernameInputText)
            .then(_ => {
                setNewUsernameInputText("");
                props.onSubmitted();
            });
    }

    const checkForNewUsernameSubmit = (e: React.KeyboardEvent) =>  e.code === "Enter";

    return (
        <div id="newUsernameControls" className="centered">
            <label id="newUsernameLabel" htmlFor="newUsernameInput">New username:</label>
            <input id="newUsernameInput" type="text" value={newUsernameInputText} onChange={e => setNewUsernameInputText(e.currentTarget.value)} onKeyUp={(e) => checkForNewUsernameSubmit(e) && requestUsernameUpdate()} />
            <button id="submitNewUsernameButton" type="submit" onClick={requestUsernameUpdate}>Submit</button>
        </div>
    );
}