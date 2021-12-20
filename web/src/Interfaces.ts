export interface WebSocketMessage {
    type: string,
    data: string
}

export interface RoomDetails {
    users: User[]
}

export interface Message {
    authorId: string,
    username: string,
    mentionedUserId?: string,
    messageContent: string,
    timestamp: number,
    key: string
}

export interface ChatProps {
    roomCode: string,
    userId: string,
    username: string,
    messages: Message[],
    users: User[]
}

export interface ChatBoxProps {
    messages: Message[],
    users: User[]
    userId: string,
}

export interface ChatControlsProps {
    userId: string,
    username: string,
    roomCode: string,
}

export interface NewUsernameProps {
    username: string,
    roomCode: string,
    onSubmitted: Function,
}

export interface ConfigItem {
    displayOrder: number,
    displayName: string,
    value: string
}

export interface RoomCreatedJoined {
    user: User
    roomCode: string
}

export interface User {
    userId: string,
    username: string,
    isOwner: boolean
}

export interface ErrorResponse {
    errorText: string
}

export interface CreateRoomRequest {
    roomName: string,
    ownerName: string,
    password?: string
}
export interface JoinRoomRequest {
    roomCode: string,
    username: string,
    password?: string
}

export interface ChatMessage {
    authorId: string,
    mentionedUserId: string | null,
    messageContent: string,
    timestamp: number,
    key: string
}

export interface ChangeUsernameRequest {
    newUsername: string
}
