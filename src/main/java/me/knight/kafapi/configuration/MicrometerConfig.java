package me.knight.kafapi.configuration;

import io.micrometer.core.aop.TimedAspect;
import io.micrometer.core.instrument.Clock;
import io.micrometer.core.instrument.MeterRegistry;
import io.micrometer.statsd.StatsdConfig;
import io.micrometer.statsd.StatsdFlavor;
import io.micrometer.statsd.StatsdMeterRegistry;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.boot.actuate.autoconfigure.metrics.MeterRegistryCustomizer;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

@Configuration
public class MicrometerConfig {
	
	@Value("${spring.application.name}")
	private String applicationName;
	
	@Bean
	MeterRegistryCustomizer<MeterRegistry> metricsCommonTags() {
	  return registry -> registry.config().commonTags("application", applicationName);
	}

	@Bean
	MeterRegistry registry (){

		StatsdConfig config = new StatsdConfig() {
			@Override
			public String get(String k) {
				return null;
			}


			@Override
			public StatsdFlavor flavor() {
				return StatsdFlavor.DATADOG;
			}
		};

		return new StatsdMeterRegistry(config, Clock.SYSTEM);
	}

	@Bean
	TimedAspect timedAspect (MeterRegistry meterRegistry){
		return new TimedAspect(meterRegistry);
	}
	
}