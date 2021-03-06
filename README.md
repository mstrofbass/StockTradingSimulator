# stock-data-analysis

This repository contains code related to a two part stock trading application. This portion ingests historical data, applies a set of rules to the data, then outputs the trades generated by the rules. The output gets analyzed to identify the best performing rule (where "best performing" may be more complex than "highest return"). Once the best performing rule is identified, it is implemented in the other part of the application, which applies the rule to real-time streaming data and places trades accordingly.

Regrettably, the bulk of the logic in this portion of the application is contained within the strategies, which have been removed. Otherwise, the most relevant code is in the `src.com.disposis.StockDataAnalysis.JobManager`, `src.com.disposis.StockDataAnalysis.JobService`, and `src.com.disposis.StockDataAnalysis.SimulateTrades` packages.

A few notes:

* I was in the middle of a major refactor when I dropped this project in favor of a Python version, so some portions may be a bit wonky.
* As part of the refactor, it integrates with a job server I wrote to specifically manage multiple copies of this application running on multiple servers (a rudimentary cluster).
* I didn't have the opportunity to do a refactor to clean anything up after the "first" version, so things are still a bit messy.
* Either my IDE or Git randomly does some weird stuff with tabs so if the tab spacing randomly changes, that's why.