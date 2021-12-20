import React, {useEffect, useState} from 'react';
import './App.css';
import NavBar from "./NavBar";
import Footer from "./Footer";
import {ConfigItem, Message, User} from "./Interfaces";
import Home from "./Home";
import {Room} from "./Room";
import FourOhFour from "./FourOhFour";
import {ErrorPopup} from "./ErrorPopup";
import CreateJoinChooser from "./CreateJoinChooser";
import {CreateRoom} from "./CreateRoom";
import {JoinRoom} from "./JoinRoom";

export default function App() {
    let testingConfig: ConfigItem[] = [
        {displayOrder: 1, displayName: "Default Die", value: "d20"},
        {displayOrder: 2, displayName: "Count Successes", value: "True"},
        {displayOrder: 3, displayName: "Success Value(s)", value: "9 or 10"},
        {displayOrder: 4, displayName: "Add Totals", value: "False"},
        {displayOrder: 5, displayName: "+Z Behaviour", value: "None"},
        {displayOrder: 6, displayName: "Explode Behaviour", value: "None"},
        {displayOrder: 7, displayName: "Explode Values", value: "N/A"},
        {displayOrder: 8, displayName: "Critical Fail", value: "True"}
    ];

    function changePageTo(pageName: string) {
        setPage(pageName);
        window.history.pushState({}, "", pageName);
    }

    const [userId, setUserId] = useState("");
    const [username, setUsername] = useState("");
    const [roomCode, setRoomCode] = useState("");
    const [userList, setUserList] = useState<User[]>([]);
    const [messages, setMessages] = useState<Message[]>([]);
    const [wsOpen, setWsOpen] = useState<boolean>(false);
    const [activeConfig, setActiveConfig] = useState<ConfigItem[]>(testingConfig);
    const [page, setPage] = useState<string>(window.location.href.split("/").slice(-1)[0]);
    const [error, setError] = useState<string>("");

    useEffect(() => {
        const errorTimer = setTimeout(() => {
            setError("");
        }, 5000);
        return () => clearTimeout(errorTimer);
    }, [error]);

    function CurrentPage(props: {page: string}) {
        switch (props.page) {
            case "":
            case "home":
                return <Home />
            case "room":
                return <Room
                    activeConfig={activeConfig}
                    setActiveConfig={setActiveConfig}
                    messages={messages}
                    setMessages={setMessages}
                    roomCode={roomCode}
                    setRoomCode={setRoomCode}
                    userId={userId}
                    setUserId={setUserId}
                    username={username}
                    setUsername={setUsername}
                    userList={userList}
                    setUserList={setUserList}
                    wsOpen={wsOpen}
                    setWsOpen={setWsOpen}

                    setError={setError}
                    changePageTo={changePageTo}
                />
            case "create-join":
                return <CreateJoinChooser createRoomPath={"create-room"} joinRoomPath={"join-room"} changePageTo={changePageTo} />
            case "create-room":
                return <CreateRoom changePageTo={changePageTo} createJoinChooserPath={"create-join"} setWsOpen={setWsOpen} setRoomCode={setRoomCode} onError={setError} />
            case "join-room":
                return <JoinRoom changePageTo={changePageTo} createJoinChooserPath={"create-join"} setWsOpen={setWsOpen} setRoomCode={setRoomCode} onError={setError} />
           default:
               return <FourOhFour />
        }
    }

    return (
        <div>
            <NavBar changePageTo={changePageTo}/>
            <CurrentPage page={page} />
            {error !== "" && <ErrorPopup error={error} />}
            <Footer />
        </div>
    );
}
