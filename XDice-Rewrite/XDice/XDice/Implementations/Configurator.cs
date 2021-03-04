using System;
using System.Linq;
using System.Threading.Tasks;
using Discord.WebSocket;
using XDice.Constants;
using XDice.Enums;
using XDice.Interfaces;

namespace XDice.Implementations
{
    public class Configurator : IConfigurator
    {
        public async Task<string?> Configure(SocketMessage configMessage)
        {
            if (!(configMessage.Author as SocketGuildUser)!.GuildPermissions.ManageGuild ||
                !configMessage.Content.StartsWith(GeneralConstants.ConfigCommandPrefix))
            {
                return $"{configMessage.Author.Mention}: XDice is being configured by your server's admin and is currently unavailable.";
            }

            var serverId = (configMessage.Author as SocketGuildUser)!.Guild.Id.ToString();

            var config = ConfigLoader.Load(serverId);
            var command = configMessage.Content.ToLowerInvariant().Remove(0, GeneralConstants.ConfigCommandPrefix.Length);

            if (command == "delete")
            {
                var configChannel = (configMessage.Author as SocketGuildUser)!.Guild.Channels.FirstOrDefault(channel => channel.Name == GeneralConstants.ConfigurationChannelName);
                if (configChannel != null)
                {
                    await configChannel.DeleteAsync();
                    return null;
                }
            }

            if (!config.IsConfigModeActive)
            {
                return GeneralConstants.ConfigurationModeNotActiveResponse;
            }

            if (command == "cancel")
            {
                // TODO: Ensure original config is restored here.
                return $"Discarding all changes and exiting configuration mode.\nSend the command {GeneralConstants.ConfigDeleteChannelCommand} to remove the {GeneralConstants.ConfigurationChannelName} channel";
            }

            try
            {
                var split = command.Split(' ');
                var currentStep = split[0];
                var commandParam = split.Length == 2 ? split[1] : "";

                if (currentStep != config.CurrentConfigStep.ToString().ToLower())
                {
                    throw new Exception();
                }

                if (currentStep == ConfigStep.Begin.ToString().ToLower())
                {
                    config.CurrentConfigStep = ConfigStep.DefaultDice;
                    return string.Format(ConfigHelperTemp.BeginStep, new Random().Next(1000, 10000), ConfigStep.DefaultDice.ToString().ToLower());
                }

                if (currentStep == ConfigStep.DefaultDice.ToString().ToLower())
                {
                    var selection = commandParam.Replace("d", string.Empty); // In case anyone types "d20" instead of "20".
                    config.DefaultDice = int.Parse(selection);
                    config.CurrentConfigStep = ConfigStep.CountSuccesses;
                    return string.Format(ConfigHelperTemp.CountSuccessesStep, selection);
                }

                if (currentStep == ConfigStep.CountSuccesses.ToString().ToLower())
                {
                    switch (commandParam)
                    {
                        case "yes":
                            config.CountSuccessesModeActive = true;
                            config.CurrentConfigStep = ConfigStep.SuccessOn;
                            return string.Format(ConfigHelperTemp.SuccessOnStep, ConfigStep.SuccessOn.ToString().ToLower());
                        case "no":
                            config.CountSuccessesModeActive = false;
                            config.CurrentConfigStep = ConfigStep.AddTotal;
                            return string.Format(ConfigHelperTemp.AddTotalStep, ConfigStep.AddTotal.ToString().ToLower());
                    }
                }

                if (currentStep == ConfigStep.SuccessOn.ToString().ToLower())
                {
                    config.SuccessOn = split.Skip(1).Select(int.Parse);
                    config.CurrentConfigStep = ConfigStep.PlusBehaviour;

                    return string.Format($"Nice!\n\n{ConfigHelperTemp.PlusBehaviourStep}", ConfigStep.PlusBehaviour.ToString().ToLower(), "~~", "");
                }

                if (currentStep == ConfigStep.AddTotal.ToString().ToLower())
                {
                    switch (commandParam)
                    {
                        case "yes":
                            config.AddTotalModeActive = true;
                            config.CurrentConfigStep = ConfigStep.PlusBehaviour;
                            return $"Nice! A big ol' YES on adding up your totals. You got it!\n\n{(string.Format(ConfigHelperTemp.PlusBehaviourStep, ConfigStep.PlusBehaviour.ToString().ToLower(), "", "~~"))}";
                        case "no":
                            config.AddTotalModeActive = false;
                            config.CurrentConfigStep = ConfigStep.ExplodeBehaviour;
                            return $"No assistance needed on the ol' numbers, you've got it!\n\n{(string.Format(ConfigHelperTemp.ExplodeBehaviourStep, ConfigStep.ExplodeBehaviour.ToString().ToLower(), config.PlusBehaviour == PlusBehaviour.Add ? "~~" : ""))}";
                        default:
                            throw new Exception();
                    }
                }

                if (currentStep == ConfigStep.PlusBehaviour.ToString().ToLower())
                {
                    var validNumbers = Enum.GetValues<PlusBehaviour>().Select(enumValue => (int)enumValue);
                    var chosenNumber = int.Parse(commandParam);
                    if (validNumbers.Contains(chosenNumber))
                    {
                        config.PlusBehaviour = (PlusBehaviour)chosenNumber;
                        config.CurrentConfigStep = ConfigStep.ExplodeBehaviour;
                    }
                    else
                    {
                        throw new Exception();
                    }
                    
                    return $"Done and done!\n\n{(string.Format(ConfigHelperTemp.ExplodeBehaviourStep, ConfigStep.ExplodeBehaviour.ToString().ToLower(), config.PlusBehaviour == PlusBehaviour.Add ? "~~" : ""))}";
                }

                if (currentStep == ConfigStep.ExplodeBehaviour.ToString().ToLower())
                {
                    var validNumbers = Enum.GetValues<ExplodeBehaviour>().Select(enumValue => (int) enumValue);
                    var chosenNumber = int.Parse(commandParam);
                    if (validNumbers.Contains(chosenNumber))
                    {
                        config.ExplodeBehaviour = (ExplodeBehaviour) chosenNumber;
                    }
                    else
                    {
                        throw new Exception();
                    }

                    if (chosenNumber != (int) ExplodeBehaviour.None)
                    {
                        config.CurrentConfigStep = ConfigStep.ExplodeOn;
                        return string.Format(ConfigHelperTemp.ExplodeOnStep, ConfigStep.ExplodeOn.ToString().ToLower());
                    }

                    if (config.CountSuccessesModeActive)
                    {
                        config.CurrentConfigStep = ConfigStep.CritFailBehaviour;
                        return string.Format(ConfigHelperTemp.CritFailStep, ConfigStep.CritFailBehaviour.ToString());
                    }

                    config.CurrentConfigStep = ConfigStep.Confirm;
                    return string.Format(ConfigHelperTemp.ConfirmStep, ConfigStep.Confirm.ToString().ToLower(), Helper.GetHelp());
                }

                if (currentStep == ConfigStep.ExplodeOn.ToString().ToLower())
                {
                    config.ExplodeOn = split.Skip(1).Select(int.Parse);
                    config.CurrentConfigStep = ConfigStep.CritFailBehaviour;
                    return string.Format(ConfigHelperTemp.CritFailStep, ConfigStep.CritFailBehaviour.ToString().ToLower());
                }

                if (currentStep == ConfigStep.CritFailBehaviour.ToString().ToLower())
                {
                    config.CritFailBehaviour = commandParam switch
                    {
                        "yes" => CritFailBehaviour.OnesWithNoSuccesses,
                        "no" => CritFailBehaviour.None,
                        _ => config.CritFailBehaviour
                    };

                    config.CurrentConfigStep = ConfigStep.Confirm;
                    return string.Format(ConfigHelperTemp.ConfirmStep, ConfigStep.Confirm.ToString().ToLower(), Helper.GetHelp());
                }

                if (currentStep == ConfigStep.Confirm.ToString().ToLower())
                {
                    config.CurrentConfigStep = ConfigStep.Begin;
                    // config.Save();
                    config.IsConfigModeActive = false;
                    return "";
                }

                throw new Exception();
            }
            catch (Exception e)
            {
                Console.WriteLine(e);
                return "Uh oh, that didn't work. Check your spelling and try again!";
            }
        }
    }
}