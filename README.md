Ziebel
====================
[![Build Status](https://travis-ci.org/sadv1r/ziebel.svg?branch=master)](https://travis-ci.org/sadv1r/ziebel)
[![Quality Gate](https://sonarcloud.io/api/project_badges/measure?project=ru.sadv1r:ziebel&metric=alert_status)](https://sonarcloud.io/dashboard/index/ru.sadv1r:ziebel)
[![Coverage](https://sonarcloud.io/api/project_badges/measure?project=ru.sadv1r:ziebel&metric=coverage)](https://sonarcloud.io/component_measures?id=ru.sadv1r:ziebel&metric=coverage)
[![Known Vulnerabilities](https://snyk.io/test/github/sadv1r/ziebel/badge.svg?targetFile=pom.xml)](https://snyk.io/test/github/sadv1r/ziebel?targetFile=pom.xml)
[![FOSSA Status](https://app.fossa.io/api/projects/custom%2B2836%2Fgithub.com%2Fsadv1r%2Fziebel.svg?type=shield)](https://app.fossa.io/projects/custom%2B2836%2Fgithub.com%2Fsadv1r%2Fziebel?ref=badge_shield)

Ziebel enables developers to more easily write applications with Siebel integration.

Ziebel enables you to develop persistent classes following natural Object-oriented idioms including inheritance, polymorphism, association, composition, and the Java collections framework. Ziebel requires no interfaces or base classes for persistent classes and enables any class or data structure to be persistent.

Ziebel supports lazy initialization and numerous fetching strategies with automatic versioning and time stamping.
Ziebel consistently offers superior performance over straight SiebelDataBean usage, both in terms of developer productivity and runtime performance.

## Preface
Working with both Object-Oriented software and Siebel can be cumbersome and time consuming. Development costs are significantly higher due to a paradigm mismatch between how data is represented in objects versus relational databases. Ziebel is an Object/Relational Mapping (ORM) solution for Java environments. The term Object/Relational Mapping refers to the technique of mapping data between an object model representation to a relational data model representation. See http://en.wikipedia.org/wiki/Object-relational_mapping for a good high-level discussion.
Although having a strong background in Siebel and SQL is not required to use Ziebel.

Ziebel takes care of the mapping from Java classes to Siebel components, and from Java data types to Siebel data types. In addition, it provides data query and retrieval facilities. It can significantly reduce development time otherwise spent with manual data handling in SQL and Siebel. Ziebel’s design goal is to relieve the developer from 95% of common data persistence-related programming tasks by eliminating the need for manual, hand-crafted data processing using SQL and Siebel. However, Ziebel does not hide the power of SQL from you and guarantees that your investment in relational technology and knowledge is as valid as always.

Ziebel can certainly help you to remove or encapsulate Siebel-specific code and streamlines the common task of translating result sets from a tabular representation to a graph of objects.

## Obtaining Ziebel
### The Ziebel Modules/Artifacts
Ziebel’s functionality is split into a number of modules/artifacts meant to isolate dependencies (modularity).

1. Ziebel-core
The main (core) Ziebel module. Defines Siebel integration main parts. //TODO re-think

2. Ziebel-entity
Defines its ORM features and APIs as well as the various integration SPIs.

3. Ziebel-query

4. Ziebel-cache\
//TODO ...

### Maven Repository Artifacts
The authoritative repository for Ziebel artifacts is the Sonatype Maven repository.
The Ziebel artifacts are synced to Maven Central as part of an automated job (some small delay may occur).

The Ziebel artifacts are published under the ru.sadv1r.Ziebel groupId.