import {ChangeUsernameRequest, ChatMessage, CreateRoomRequest, JoinRoomRequest, WebSocketMessage} from "./Interfaces";

export let socket: WebSocket;

export function openSocket(websocketAddress: string, onOpen: Function) {
    socket = new WebSocket(websocketAddress);
    socket.onopen = () => onOpen(true);
}

export function closeSocket() {
    socket.close(1000, "User disconnected");
}

export async function sendCreateRoomMessage(requestedRoomName: string, requestedUserName: string, password?: string) {
    const request: CreateRoomRequest = {
        roomName: requestedRoomName,
        ownerName: requestedUserName,
        password: password
    };

    await sendWebsocketMessage("CreateRoom", request);
}

export async function sendJoinRoomMessage(requestedRoomCode: string, requestedUserName: string, password?: string) {
    const request: JoinRoomRequest = {
        roomCode: requestedRoomCode,
        username: requestedUserName,
        password: password
    };

    await sendWebsocketMessage("JoinRoom", request);
}

export async function sendChatMessage(authorId: string, message: string) {
    const ts = Date.now();
    const request: ChatMessage = {
        authorId: authorId,
        mentionedUserId: null,  // TODO: Add a way for users to mention each other.
        messageContent: message,
        timestamp: ts,
        key: `${ts}${authorId}`
    };

    await sendWebsocketMessage("Chat", request);
}

export async function sendChangeUsernameMessage(newUsername: string) {
    const request: ChangeUsernameRequest = {
        newUsername: newUsername
    };
    await sendWebsocketMessage("ChangeUsername", request);
}

async function sendWebsocketMessage(type: string, data: object) {
    const submission: WebSocketMessage = {
        type: type,
        data: JSON.stringify(data)
    }

    let succeeded = false;
    for (let i = 0; i < 100; i++) {
        if (socket.readyState === socket.OPEN) {
            socket.send(JSON.stringify(submission));
            succeeded = true;
            break;
        }
        await new Promise(resolve => setTimeout(resolve, 50));
    }

    if (!succeeded) {
        // eslint-disable-next-line no-throw-literal
        throw "Failed to connect to server.";
    }
}
