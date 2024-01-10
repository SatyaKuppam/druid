package org.apache.druid.emitter.prometheus.metrics;

import com.fasterxml.jackson.annotation.JsonProperty;
import com.fasterxml.jackson.annotation.JsonTypeName;
import lombok.Getter;
import org.apache.druid.emitter.prometheus.PrometheusEmitterConfig;

import java.util.SortedSet;

@JsonTypeName(MetricType.DimensionMapNames.HISTOGRAM)
public class Histogram extends Metric<io.prometheus.client.Histogram> {
    private final double[] buckets;
    public Histogram(
            @JsonProperty("dimensions") SortedSet<String> dimensions,
            @JsonProperty("type") MetricType type,
            @JsonProperty("help") String help,
            @JsonProperty("buckets") double[] buckets
    ) {
        super(dimensions, type, help);
        this.buckets = buckets;
    }

    @Override
    public void record(String[] labelValues, double value) {
        this.getCollector().labels(labelValues).observe(value);
    }

    @Override
    public void createCollector(String name, PrometheusEmitterConfig emitterConfig) {
        super.configure(name, emitterConfig);
        this.setCollector(
                new io.prometheus.client.Histogram.Builder()
                        .namespace(this.getNamespace())
                        .name(this.getFormattedName())
                        .labelNames(this.getDimensions())
                        .buckets(this.buckets)
                        .help(help)
                        .register()
        );
    }
}
