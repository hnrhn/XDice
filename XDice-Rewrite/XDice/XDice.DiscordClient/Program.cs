using System;
using System.Threading;
using System.Threading.Tasks;
using Discord;
using Discord.WebSocket;
using XDice.Implementations;

namespace XDice.DiscordClient
{
    class Program
    {
        private readonly DiscordSocketClient _client;

        public Program()
        {
            _client = new DiscordSocketClient();
            _client.Log += LogAsync;
            _client.Ready += ReadyAsync;
            _client.MessageReceived += MessageReceivedAsync;
        }

        public static void Main()
        {
            new Program().MainAsync().GetAwaiter().GetResult();
        }

        private async Task MainAsync()
        {
            await _client.LoginAsync(TokenType.Bot, "YOUR_TOKEN_HERE");
            await _client.StartAsync();

            await Task.Delay(Timeout.Infinite);
        }

        private Task LogAsync(LogMessage logMessage)
        {
            // TODO: Logging I guess?
            return Task.CompletedTask;
        }

        private Task ReadyAsync()
        {
            Console.WriteLine($"Logged in as {_client.CurrentUser}");
            return Task.CompletedTask;
        }

        private async Task MessageReceivedAsync(SocketMessage message)
        {
            if (message.Author.Id == _client.CurrentUser.Id)
            {
                return;
            }
        }
    }
}