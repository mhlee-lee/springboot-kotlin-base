package org.test.kotlin_base.common.configuration

import io.netty.channel.ChannelOption
import io.netty.channel.epoll.EpollChannelOption
import io.netty.handler.timeout.ReadTimeoutHandler
import io.netty.handler.timeout.WriteTimeoutHandler
import org.springframework.context.annotation.Bean
import org.springframework.context.annotation.Configuration
import org.springframework.http.MediaType
import org.springframework.http.client.reactive.ReactorClientHttpConnector
import org.springframework.http.codec.json.Jackson2JsonDecoder
import org.springframework.http.codec.json.Jackson2JsonEncoder
import org.springframework.web.reactive.function.client.ExchangeStrategies
import org.springframework.web.reactive.function.client.WebClient
import org.test.kotlin_base.common.objectMapper
import reactor.netty.http.client.HttpClient
import reactor.netty.resources.ConnectionProvider
import java.time.Duration
import java.util.concurrent.TimeUnit

@Configuration
class WebClientConfiguration {
    @Bean
    fun getWebClientBuilder(): WebClient.Builder {
        return WebClient.builder()
            .clientConnector(ReactorClientHttpConnector(httpClient))
            .exchangeStrategies(defaultExchangeStrategies)
    }

    @Bean
    fun getWebClient(webClientBuilder: WebClient.Builder) = webClientBuilder.build()

    private val defaultExchangeStrategies = ExchangeStrategies
        .builder()
        .codecs { configurer ->
            configurer.defaultCodecs().apply {
                jackson2JsonEncoder(Jackson2JsonEncoder(objectMapper, MediaType.APPLICATION_JSON))
                jackson2JsonDecoder(Jackson2JsonDecoder(objectMapper, MediaType.APPLICATION_JSON))
                maxInMemorySize(-1)
            }
        }.build()

    private val connectionProvider = ConnectionProvider.builder("webclient-provider")
        .maxIdleTime(Duration.ofMinutes(2))
        .maxLifeTime(Duration.ofMinutes(2))
        .evictInBackground(Duration.ofMinutes(1))
        .lifo()
        .metrics(true)
        .build()

    private val timeout: Int = 10000
    private val keepAliveIdleSec: Int = 300
    private val keepAliveIntervalSec: Int = 60
    private val keepAliveCnt: Int = 1

    private val httpClient = HttpClient.create(connectionProvider)
        .option(ChannelOption.CONNECT_TIMEOUT_MILLIS, timeout)
        .option(ChannelOption.SO_KEEPALIVE, true)
        .option(EpollChannelOption.TCP_KEEPIDLE, keepAliveIdleSec)
        .option(EpollChannelOption.TCP_KEEPINTVL, keepAliveIntervalSec)
        .option(EpollChannelOption.TCP_KEEPCNT, keepAliveCnt)
        .doOnConnected { conn ->
            conn.addHandlerLast(ReadTimeoutHandler(timeout.toLong(), TimeUnit.MILLISECONDS))
            conn.addHandlerLast(WriteTimeoutHandler(timeout.toLong(), TimeUnit.MILLISECONDS))
        }
}
