# 🦠 TG Covid-19 Stats

## Overview
I think that one way to fight Covid-19 is to spread information to the population.

[Covid-19 Stats](https://github.com/replydev/Covid-Stats) it's an easy deployable Telegram Bot that fetch italy Covid-19 statistics
and dynamically organizes them in a graph, to look at the trend of the epidemic over time.

## Which data Covid-19 Stats uses?
This bot uses ONLY official data pushed by the official [Github Account of the Italian Civil Protection](https://github.com/pcm-dpc/COVID-19).

## How to build
Be sure that you have the latest [Intellij IDEA](https://www.jetbrains.com/idea/) installed in your system.
You may also need to install Java Development Kit 1.8+, I suggest the [AdoptOpenJDK](https://adoptopenjdk.net/releases.html) distribution.

To build Covid-19 Graphs follow these steps:

- Clone this repository using Intellij IDEA.
- Open the project and click in the Maven button.
- Click the "M" button and type the following command: `mvn clean compile assembly:single`.
- You will find the compiled jar in the `target/` directory.

## How to deploy

- Push the compiled jar to your server or wherever you want to deploy the bot.
- Create a new Telegram Bot using [BotFather](https://t.me/BotFather).
- After creating your new bot please note your **API Token** and your **bot username**.
- Start the BOT using the start.sh script, [findable](https://github.com/replydev/Covid-Stats/blob/master/start.sh) in this repository.
- The first run will fail, but the program will create an example config located in `config/config.json`
- Edit BOT_TOKEN and BOT_USERNAME attributes.
- Edit UPDATE_TIME inserting the specific hour of the day when you want the bot to update his dataset. I advice `18:00` or `18:30`. 
- Restart the bot and enjoy 🎉.

## Contribution
This probably is the first project that I made for others and not only for me, so this gives another meaning to the word **contribution**.

I want this to become a community project so any contribution, as always, is welcome!
Just make a pull request and if your code meet my (low) code standards I will be happy to merge your **contribution**.

###Stay Home, Stay Safe 😷
