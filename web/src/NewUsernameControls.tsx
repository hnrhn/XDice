import React, {useState} from "react";
import {sendChangeUsernameMessage} from "./XDiceSocket";
import {NewUsernameProps} from "./Interfaces";
import {keyWasEnter} from "./utils";

export default function NewUsernameControls(props: NewUsernameProps) {
    const [newUsernameInputText, setNewUsernameInputText] = useState("");

    function requestUsernameUpdate() {
        sendChangeUsernameMessage(newUsernameInputText)
            .then(_ => {
                setNewUsernameInputText("");
                props.onSubmitted();
            });
    }

    return (
        <div id="newUsernameControls" className="centered">
            <label id="newUsernameLabel" htmlFor="newUsernameInput">New username:</label>
            <input id="newUsernameInput" type="text" value={newUsernameInputText} onChange={e => setNewUsernameInputText(e.currentTarget.value)} onKeyUp={(e) => keyWasEnter(e) && requestUsernameUpdate()} />
            <button id="submitNewUsernameButton" type="submit" onClick={requestUsernameUpdate}>Submit</button>
        </div>
    );
}