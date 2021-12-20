import React, {useEffect} from "react";
import RulesList from "./RulesList";
import Chat from "./Chat";
import UserList from "./UserList";
import {ConfigItem, ErrorResponse, Message, RoomCreatedJoined, RoomDetails, User, WebSocketMessage} from "./Interfaces";
import {openSocket, socket} from "./XDiceSocket";
import * as Constants from './WsConstants';

export function Room(props: {
    wsOpen: boolean,
    setWsOpen: Function,
    messages: Message[],
    setMessages: Function,
    userId: string,
    setUserId: Function,
    username: string,
    setUsername: Function,
    userList: User[],
    setUserList: Function,
    roomCode: string,
    setRoomCode: Function,
    activeConfig: ConfigItem[],
    setActiveConfig: Function,
    setError: Function,
    changePageTo: Function
}) {
    useEffect(() => {
        if (socket === null || socket === undefined || socket.readyState === socket.CLOSED) {
            openSocket("ws://localhost:4567/ws", props.setWsOpen);
        }
    }, [props.setWsOpen]);

    useEffect(() => {
        if (props.wsOpen) {
            socket.onclose = () => {
                props.setRoomCode("");
                props.setWsOpen(false);
                props.setMessages([]);
                props.setError("Connection to Server closed");
            };
            socket.onmessage = (e) => {
                const responseObject: WebSocketMessage = JSON.parse(e.data);

                switch (responseObject.type) {
                    case Constants.ChatType:
                        props.setMessages((messages: Message[]) => [...messages, (JSON.parse(responseObject.data) as Message)]);
                        return;
                    case Constants.ChangeUsernameType:
                        const updatedUser: User = JSON.parse(responseObject.data) as User
                        if (updatedUser.userId === props.userId) {
                            props.setUsername(updatedUser.username);
                        }
                        return;
                    case Constants.RoomDetailsType:
                        props.setUserList((JSON.parse(responseObject.data) as RoomDetails).users);
                        return;
                    case Constants.RoomCreatedType:
                    case Constants.RoomJoinedType:
                        const roomCreatedJoined = JSON.parse(responseObject.data) as RoomCreatedJoined;
                        props.setUserId(roomCreatedJoined.user.userId);
                        props.setUsername(roomCreatedJoined.user.username);
                        props.setRoomCode(roomCreatedJoined.roomCode);
                        return;
                    case Constants.UpdateConfigType:
                        props.setActiveConfig(JSON.parse(responseObject.data) as ConfigItem[]);
                        return;
                    case Constants.ErrorType:
                        const errorResponse = JSON.parse(responseObject.data) as ErrorResponse;
                        props.setError(errorResponse.errorText);
                        props.changePageTo("create-join");
                        return;
                    default:
                        break;
                }
            };
        }
    }, [props]);

    if (!(props.wsOpen && props.roomCode !== undefined && props.roomCode !== null && props.roomCode !== "")) {
        props.changePageTo("create-join");
        return null;
    }

    return (
        <div id="columnContainer">
            <RulesList roomCode={props.roomCode} config={props.activeConfig}/>
            <Chat roomCode={props.roomCode} userId={props.userId} username={props.username} messages={props.messages} users={props.userList}/>
            <UserList users={props.userList}/>
        </div>
    );
}