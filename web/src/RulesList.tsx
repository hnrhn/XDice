import React, {useState} from "react";
import {ConfigItem} from "./Interfaces";

export default function RulesList(props: { config: ConfigItem[], roomCode: string }) {
    const [copied, setCopied] = useState(false);
    const roomCodeParts = props.roomCode.split("#");
    let roomName = roomCodeParts[0];
    let roomNum = roomCodeParts[1];

    function copyRoomCodeToClipboard() {
        navigator.clipboard.writeText(props.roomCode)
            .then(() => {
                setCopied(true);
                setTimeout(() => setCopied(false), 2000);
            })
    }

    return (
        <div id="ruleSidebar" className="sidebar">
            <h2>Active Rules</h2>
            <hr />
            <ul>
                {props.config.map(c => <li key={c.displayOrder}>{c.displayName}<ul><li>{c.value}</li></ul></li>)}
            </ul>
            <div className={"fixedToBottom"}>
                <span className={copied ? "" : "fakeLink"} onClick={copyRoomCodeToClipboard}>
                    {
                        copied
                        ? (
                            <span>Copied!</span>
                        )
                        : (
                            <>
                                <span>{roomName}</span>
                                <span style={{"color": "gray"}}>#{roomNum}</span>
                            </>
                        )
                    }
                </span>
            </div>
        </div>
    );
}