package me.wheelershigley.www.solace_jobs.data;

import eu.pb4.common.economy.api.CommonEconomy;
import eu.pb4.common.economy.api.EconomyAccount;
import eu.pb4.common.economy.api.EconomyCurrency;
import eu.pb4.common.economy.api.EconomyProvider;
import me.wheelershigley.www.solace_jobs.SolaceJobs;
import me.wheelershigley.www.solace_jobs.data.config.ConfigurationsCodec;
import me.wheelershigley.www.solace_jobs.data.employments.EmploymentsCodec;
import me.wheelershigley.www.solace_jobs.data.jobs.JobDefinition;
import me.wheelershigley.www.solace_jobs.data.players.JobsPlayerData;
import me.wheelershigley.www.solace_jobs.data.players.PlayersJobsDataCodec;
import net.minecraft.resources.Identifier;
import net.minecraft.server.MinecraftServer;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.util.RandomSource;

import java.util.Collection;
import java.util.HashMap;
import java.util.HashSet;
import java.util.UUID;

public record ActionReward(
    double experience,
    double job_experience,
    double currency
) {
    private static final HashMap<UUID, Double> unpaidAmounts = new HashMap<>();

    private static EconomyProvider economyProvider;
    private static EconomyCurrency economyCurrency;

    public void give(
        ServerPlayer player,
        JobDefinition job,
        RandomSource random
    ) {
        boolean isInJob = EmploymentsCodec.INSTANCE.getInstanceData()
            .employments.getOrDefault( player.getUUID(), new HashSet<>() )
            .contains(job.identifier)
        ;
        if(!isInJob) {
            return;
        }

        giveExperience(player, random);
        giveJobExperience(player, job);
        giveCurrency(player);
    }

    public static void initialize(MinecraftServer server) {
        final String provider_name = ConfigurationsCodec.INSTANCE.getInstanceData().getData().economyProviderName();
        final String currency_name = ConfigurationsCodec.INSTANCE.getInstanceData().getData().economyCurrencyName();
        final Identifier currencyIdentifier = Identifier.fromNamespaceAndPath(provider_name, currency_name);

        /* Provider */ {
            economyProvider = CommonEconomy.getProvider(provider_name);

            if(economyProvider == null) {
                StringBuilder errorBuilder = new StringBuilder();
                errorBuilder
                    .append("\r\n")
                    .append("Economy-Provider, \"").append(provider_name).append("\" not found!\r\n")
                    .append("The following provider(s) were found:\r\n")
                ;

                Collection<EconomyProvider> providers = CommonEconomy.providers();
                for(EconomyProvider provider : providers) {
                    errorBuilder.append("\t").append( provider.id() ).append("\r\n");
                }
                errorBuilder.append("To use economic-rewards, trouble-shoot your provider-name.");

                SolaceJobs.LOGGER.error( errorBuilder.toString() );
                return;
            } else {
                SolaceJobs.LOGGER.info("Economy-Provider \"{}\" found!", provider_name);
            }
        }
        /* Currency */ {
            economyCurrency = CommonEconomy.getCurrency(server, currencyIdentifier);

            if(economyCurrency == null) {
                StringBuilder errorBuilder = new StringBuilder();
                errorBuilder
                    .append("\r\n")
                    .append("Economy-Currency, \"").append(currencyIdentifier).append("\" not found!\r\n")
                    .append("The following currency/currencies were found:\r\n")
                ;

                Collection<EconomyCurrency> economyCurrencies = CommonEconomy.getCurrencies(server);
                for(EconomyCurrency economyCurrency1 : economyCurrencies) {
                    errorBuilder.append("\t").append( economyCurrency1.id() ).append("\r\n");
                }
                errorBuilder.append("To use economic-rewards, trouble-shoot your currency-name.");

                SolaceJobs.LOGGER.error( errorBuilder.toString() );
                return;
            } else {
                SolaceJobs.LOGGER.info("Economy-Currency \"{}\" found!", currencyIdentifier);
            }
        }
    }

    private void giveExperience(ServerPlayer player, RandomSource random) {
        double amount = (
            this.experience * ConfigurationsCodec.INSTANCE.getInstanceData().getData().global_experience_multiplier()
        );

        //any remainder will sometimes be added: to, on average, approximate that amount
        double remainder = (experience % 1.0);
        if(
            0 < remainder
            && random.nextDouble() <= remainder
        ) {
            amount += 1.0;
        }

        player.giveExperiencePoints( (int)amount );
    }

    private void giveJobExperience(
        ServerPlayer player,
        JobDefinition job
    ) {
        JobsPlayerData playerData = PlayersJobsDataCodec.DATA.get( player.getUUID() );
        if(playerData == null) {
            playerData = new JobsPlayerData( new HashMap<>() );
            PlayersJobsDataCodec.DATA.set( player.getUUID(), playerData);
        }

        double experience = playerData.experiences().getOrDefault(job.identifier, 0.0);
        experience += this.job_experience * ConfigurationsCodec.INSTANCE.getInstanceData().getData().global_job_experience_multiplier();

        playerData.experiences().put(job.identifier, experience);
    }

    private void giveCurrency(ServerPlayer player) {
        if(economyProvider == null || economyCurrency == null) {
            return;
        }
        double payment = unpaidAmounts.getOrDefault(player.getUUID(), 0.0);
        payment += this.currency * ConfigurationsCodec.INSTANCE.getInstanceData().getData().global_earnings_multiplier();

        EconomyAccount account = economyProvider.getAccount(
            player,
            economyProvider.defaultAccount(player, economyCurrency)
        );
        if(account == null) {
            return;
        }

        //pay currency
        long actual_payment = (long)(100*payment);
        if(actual_payment != 0) {
            account.increaseBalance(actual_payment);
        }

        payment %= 0.01;
        unpaidAmounts.put(player.getUUID(), payment);
    }
}
