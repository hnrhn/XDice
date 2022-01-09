import React from "react";
import {closeSocket} from "./XDiceSocket";
import {User} from "./Interfaces";

export default function UserList(props: { users: User[] }) {
    return (
        <div id="userListSidebar" className="sidebar">
            <h2>Users</h2>
            <hr />
            <ul id="userList">
                {props.users.filter(u => !u.isHidden).map(u => <li key={u.userId}>{u.username}{u.isOwner ? " *" : ""}</li>)}
            </ul>
            <button className={"fullWidthButton lockedToBottom redButton"} onClick={closeSocket}>Leave Room</button>
        </div>
    );
}