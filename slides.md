---
theme: default
colorSchema: dark
title: We Need to Talk About Limits
info: |
  ## We Need to Talk About Limits
  A presentation about resource management in modern engineering
---

# We Need to Talk About Limits

Resource Management in the Age of the Cloud

---

# Why are we having this talk?
Any of these sound familiar?

- "K8s is OOM killing my containers!"
- "WTF? we're paying 1M$/month for DynamoDB???"
- Paying for 10TB logs/day and still can't debug your system?
- Cluster simultenously at overcapacity _and_ 5% utilization?
- "Postmortem revealed outage was due to all database connections hogged"

---

# The Age of Limitless

The cloud promised us infinite scale

- **Unlimited** - Scale to infinity
- **Pay as you go** - No upfront capacity planning
- **Move fast** - Don't worry about constraints
- **Distributed** - Free from single machine hardware limitations

But did we forget something important?

---

# The Reckoning

When "unlimited" meets reality

- "Surprise" AWS bills
- Services crashing under load
- Performance degradation
- Cascading failures

> Although our tech credit cards may not have a spending limit, that doesn't mean there isn't a pay day...

---
layout: statement

---

## Parkinson's Law:

# Work expands to consume all available resources

---

# Example: GC

```shell
$ time -l java -Xmx512m mandelbrot.java
Computation completed in 0.57 seconds
        0.93 real         6.47 user         0.07 sys
           305479680  maximum resident set size
                  ...
           263996568  peak memory footprint

$ time -l java -Xmx64m mandelbrot.java
Computation completed in 0.53 seconds
        0.89 real         6.13 user         0.07 sys
           172638208  maximum resident set size
                  ...
           136446960  peak memory footprint
```

Same work, same code, 50% memory usage!

---
layout: statement

---

# If you don't set a limit, the system will find one for you 
(usually catastrophically)

---

# How to get promoted in 5 easy steps

1. Write code, insert busy-wait loop consuming 90% of CPU
1. Deploy code to production, wait 3 month
1. Initiate a cost optimization project, remove busy-wait loop
1. Scale down 90% of infra, report savings to management
1. Profit

<v-click>
<div class="mt-14">

# How do you know you don't have 90% waste _right now_?
</div>
</v-click>
---
layout: statement
---

# All unoptimized systems should be assumed wasteful until optimized

---

# In the good old days...

### Allocations were static
- Adding servers took months
- Allocations were mostly manual
- Capacity planning was the norm

**We were disciplined, because we had to**

### We've become spoiled brats
- "Just add more servers"
- "Scale horizontally"
- "The cloud will handle it"
- *Shocked pikachu face when it doesn't*

---
layout: two-cols-header
---

# This is NOT FinOps

::right::

## FinOps 
Focus: cost
- Not considered core to design
- Bolt-on
- Largely reactive
- Not related to reliability
- Fix cost sprawl after the fact

::left::

## Limits
Focus: reliability
- Core part of the design
- Inherent part of design & development
- Proactive
- Cost control is an aspect of reliability
- Prevent cost sprawl before it happens

---

# Limits are a fundamental Engineering technique

- Control system
- Monitoring/alerting
- Coordination (budgets)
- Reliability and isolation (bulkheading)
- Cost control


---
layout: image-right
image: public/limits-illustration.svg
backgroundSize: contain
---
# Concepts

- **Soft limit** - non critical warning threshold
  - Trigger alerts
  - Enter degraded mode
  - Trigger resource scavenging
- **Hard limit** - critical threshold
  - Deny allocations
  - Crash defensively
  - Stall/pause
- **Reserve** - capacity for emergency

---

# Limits? what limits?

**Operating System:**
- POSIX rlimits (CPU, memory, file descriptors)
- cgroups (containers, Kubernetes, systemd)

**Cloud Platform:**
- Service quotas and rate limits
- Built-in throttling mechanisms

**Application:**
- Connection pools, rate limiters, circuit breakers
- Timeouts, backpressure, graceful degradation

**Every layer needs limits. No exceptions.**

---

# Everything has a limit. EVERY THING

- DB calls per client request
- LLM tokens
- Time
- Log events
- Metrics
- async/await callbacks
- Open sockets
- DNS Zone records

---
layout: statement
---

## Error: Not enough resources

# It's not a bug, It's a feature

---

# Limits should be grounded in reality!

Seen in the wild
- 10 sec connection timeout in DC LAN
- 4 CPU cores for Node.js container
- Unlimited request body in HTTP POST
- Unlimited filters for Lucene queries

## Instead
- Use Percentiles/Histograms
- Establish "reasonable" limits based on mechanics / physics
- Calculate and design for _peak_ usage


---
layout: statement
---

# If you haven't seen the (soft) limit breached, it's too big

---
layout: statement
---

# But what about Auto scaling?

---
layout: fine-print
---


# Autoscaling: License to waste

**The promise:** The system will automatically scale to meet demand

**In reality:** "Yo John system is slow just add more servers"

### Autoscaling incetivizes inefficiency

::fine-print::

_Health warning:_ Autoscaling degrades performance, efficiency, reliability and masks various systemic issues. <br/> Consult your Doctor before using Autoscaling 

---

# What's wrong with this picture?

```yaml
# k8s pod spec
    resources:
      requests:
        memory: "0.5Gi"
        cpu: "500m"
      limits:
        memory: "2Gi"
        cpu: "2000m"
```

<v-click>
You think you told k8s to give you more resources

But in reality, you asked k8s to kill your pod on starvation
</v-click>

---

# Overcommitment: The Double-Edged Sword

Selling more resources than you actually have

- Pod A requests: 16GB, limit: 32GB
- Pod B requests: 16GB, limit: 32GB
- Pod C requests: 16GB, limit: 32GB
- Node: 64GB RAM: **Total requests: 48GB ✓ (fits!)**
- **Total limits: 96GB ✗**

**This fails catastrophically when:**
- Pods actually use what they're allowed
- OOM killer starts evicting pods
- Cascading failures across services
- "But it worked in testing!" (where you had spare capacity)

**Rule:** Overcommitment is borrowing from future stability. Know what you're trading.

---

# Resource Budgets

A coordination system for teams and components

**The problem:** Multiple teams/services share finite resources

**The solution:** Explicit allocation as contracts
- Team A: 100GB memory, 20 cores (cache)
- Team B: 50GB memory, 100 cores (batch, runs at night)
- Team C: 150GB memory, 200 DB connections (API)

**Benefits:** No surprises, no finger-pointing, capacity planning becomes possible

---
layout: section
---

# Resource-Oriented Design

A methodology for building resilient systems

---

# Limits as design princile
**The mindset shift:** Think about constraints first, features second

1. **Identify resources** - What can run out?
1. **Define limits** - What's the max safe usage?
1. **Budget** - Coordinate shared resource usage
4. **Add monitoring** - Track usage, soft limit breaches
5. **Design for degradation** - Use soft limits to trigger stress response

---

# When You Hit the Limit

The art of graceful degradation

- **Prioritize:** Critical requests first
- **Queue:** Buffer non-critical work
- **Shed load:** Reject low-priority requests
- **Degrade features:** Turn off non-essentials
- **Fail fast:** Don't waste resources on doomed requests
- **Communicate:** Return meaningful errors (429, 503)
- **Backpressure:** Let upstream know you're overwhelmed so they can slow down

---

# The Cardinal Sins

- **Unlimited by default** - Hope is not a strategy
- **Unrealistic limits** - practically the same as unlimited
- **Ignoring soft limits** - Can't fix what you don't see
- **Set and forget** - Limits need adjustment over time
- **No testing** - You will discover your limits in production

Practically every disaster starts with "we didn't think we needed limits"

---

# Conclusion

You Gotta Have Limits!

- The cloud hasn't eliminated the need for resource management—it's made it more important
- Limits aren't constraints, they're safeguards
- Resource-oriented design prevents surprises and enables scale
- "Unlimited" means you haven't encountered the limit yet 



---

# The only unlimited thing is stupidity. Everything else has a limit

<div class="mt-50">

# Questions?

Let's talk about your limits

Thank you!

</div>
---