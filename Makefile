.PHONY: all
all:
	make cluster
	make kafka
	make redis
	make mysql
	make rabbitmq
	make monitoring
	make services

.PHONY: cluster
cluster:
	kind create cluster --name tracing-demo

.PHONY: delete-cluster
delete-cluster:
	kind delete cluster --name tracing-demo

.PHONY: kafka
kafka:
	helm repo add strimzi https://strimzi.io/charts/
	helm repo update
	helm upgrade --install strimziop strimzi/strimzi-kafka-operator -n kafka --create-namespace --version 0.44.0
	kubectl apply -f infra/kafka/cluster.yaml -n kafka
	kubectl wait kafka/my-cluster --for=condition=Ready --timeout=300s -n kafka
	kubectl apply -f infra/kafka/topics.yaml
	kubectl wait kafkatopic/message-inbound kafkatopic/opentelemetry-spans --for=condition=Ready --timeout=300s -n kafka
	kubectl wait pod --all --for=condition=Ready --namespace=kafka --timeout 300s
	helm repo add kafka-ui https://provectus.github.io/kafka-ui-charts
	helm repo update
	helm upgrade --install kafka-ui kafka-ui/kafka-ui -n kafka --values infra/kafka/kafka-ui.yaml --version v0.7.0
	kubectl wait pod --all --for=condition=Ready --namespace=kafka --timeout 300s

.PHONY: redis
redis:
	kubectl create namespace redis --dry-run=client -o yaml | kubectl apply -f -
	helm upgrade --install my-release oci://registry-1.docker.io/bitnamicharts/redis --set architecture=standalone -n redis --set global.redis.password=redis
	kubectl wait pod --all --for=condition=Ready --namespace=redis --timeout 300s

.PHONY: mysql
mysql:
	kubectl create namespace mysql --dry-run=client -o yaml | kubectl apply -f -
	kubectl apply -f infra/mysql/db.yaml
	kubectl wait pod --all --for=condition=Ready --namespace=mysql --timeout 300s

.PHONY: rabbitmq
rabbitmq:
	kubectl create namespace rabbitmq --dry-run=client -o yaml | kubectl apply -f -
	kubectl apply -f infra/rabbitmq/cluster.yaml
	kubectl wait pod --all --for=condition=Ready --namespace=rabbitmq

monitoring:
	kubectl create namespace monitoring --dry-run=client -o yaml | kubectl apply -f -
	helm repo add grafana https://grafana.github.io/helm-charts
	helm repo add prometheus-community https://prometheus-community.github.io/helm-charts
	helm repo add jetstack https://charts.jetstack.io --force-update
	helm repo add open-telemetry https://open-telemetry.github.io/opentelemetry-helm-charts
	helm repo update
	helm upgrade --install --namespace monitoring alloy grafana/alloy -f infra/monitoring/alloy.yaml
	helm upgrade --install cert-manager jetstack/cert-manager --namespace cert-manager --create-namespace --version v1.11.0 --set installCRDs=true
	helm upgrade --install st prometheus-community/kube-prometheus-stack --namespace monitoring --values infra/monitoring/prometheus.yaml --version 65.3.1
	helm upgrade --install tmp grafana/tempo-distributed --namespace monitoring --values infra/monitoring/tempo.yaml --version 1.19.0
	helm upgrade --install loki grafana/loki -n monitoring --values infra/monitoring/loki.yaml --version 6.18.0
	helm upgrade --install receiver open-telemetry/opentelemetry-collector --namespace monitoring --values infra/monitoring/opentelemetry-collector-receiver.yaml --set image.repository="otel/opentelemetry-collector-contrib"
	helm upgrade --install exporter open-telemetry/opentelemetry-collector --namespace monitoring --values infra/monitoring/opentelemetry-collector-exporter.yaml --set image.repository="otel/opentelemetry-collector-contrib"
	kubectl apply -f infra/monitoring/dashboards.yaml
	kubectl wait pod --all --for=condition=Ready --namespace=monitoring --timeout 300s

.PHONY: services
services:
	skaffold run

port-forward-grafana:
	kubectl port-forward -n monitoring svc/st-grafana 3000:80
	