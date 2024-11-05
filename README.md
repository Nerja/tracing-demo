# Distributed Tracing Demo
## Requirements
### Hardware
The PoC has been tested on the following configurations:

* Ubuntu 20.04 LTS - 12 cores(24 threads) @4.7gHZ - 32 GB RAM
* Ubuntu 22.04 LTS - @2.6gHZ - 32 GB RAM
* MacOSX Ventura 13.6.7 - Apple M1 - 16 GB RAM (Docker Desktop)
* MacOSX Sonoma 14.5 - Apple M3 Max - 36 GB RAM (Docker Desktop)
* MacOSX Ventura 13.6.7- Apple M1 Pro - 16 GB RAM (Docker Desktop)

### Installed tools
* Make
* Java JDK 21
* Docker
* Kind (https://kind.sigs.k8s.io/)
* Helm
* Kubectl
* Skaffold (https://skaffold.dev/)

## Quick Start
<em>Depending on your laptop and internet connectivity the below setup could take up to 20min or more</em>

1. Clone the repository

2. `cd tracing-demo`

3. Ensure that you are not authenticated against an important K8s cluster

4. Run `make all`

5. Wait until the command completes

6. Run `make port-forward-grafana`

7. Open a web browser and go to http://localhost:3000

8. Log in with username `admin` and password `prom-operator`

9. Navigate to the Grafana dashboard called `General Monitoring`

10. Play around :)
