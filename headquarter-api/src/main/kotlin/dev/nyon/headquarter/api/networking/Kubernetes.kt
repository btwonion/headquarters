package dev.nyon.headquarter.api.networking

import io.fabric8.kubernetes.client.KubernetesClient
import io.fabric8.kubernetes.client.KubernetesClientBuilder

val kubernetesClient: KubernetesClient = KubernetesClientBuilder().build()
