<!--

    Licensed to the Apache Software Foundation (ASF) under one
    or more contributor license agreements. See the NOTICE file
    distributed with this work for additional information
    regarding copyright ownership. The ASF licenses this file
    to you under the Apache License, Version 2.0 (the
    "License"); you may not use this file except in compliance
    with the License.  You may obtain a copy of the License at

        http://www.apache.org/licenses/LICENSE-2.0

    Unless required by applicable law or agreed to in writing, software
    distributed under the License is distributed on an "AS IS" BASIS,
    WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
    See the License for the specific language governing permissions and
    limitations under the License.

-->

# Commons RDF Project Maturity Report

This is a report of Commons RDF following the
[Maturity Model for Apache Projects](https://community.apache.org/apache-way/apache-project-maturity-model.html) (2016-10).

**NOTE**: Commons RDF [graduated on 2016-11-28](https://lists.apache.org/thread.html/a2e4081e53bde4d1ce2f98c13d1f3c9a783aaf2c5f1acec9c7b22e1b@%3Cgeneral.incubator.apache.org%3E)
from the [Apache Incubator](http://incubator.apache.org/) to 
become a sub-project (component) of [Apache Commons](https://commons.apache.org/).
The links below may not have been updated following graduation.

## Code

### CD10
_The project produces Open Source software, for distribution to the public at no charge._

All outputs of Apache Commons RDF is open source, covered by
[Apache License 2.0](https://github.com/apache/incubator-commonsrdf/blob/master/LICENSE) and
[distributed](https://commonsrdf.incubator.apache.org/download.html)
at no charge.


### CD20
_The project's code is easily discoverable and publicly accessible._

Menu on https://commonsrdf.incubator.apache.org/ links to [Source (git)](https://git-wip-us.apache.org/repos/asf/incubator-commonsrdf.git),
[Source (GitHub mirror)](https://github.com/apache/incubator-commonsrdf/), as well as
source releases under [Download](https://commonsrdf.incubator.apache.org/download.html)


### CD30
_The code can be built in a reproducible way using widely available standard tools._

See [build instructions](https://github.com/apache/incubator-commonsrdf#building), requires only
Apache Maven 3.2 and Java JDK 8.  Build independently verified by
[Jenkins](https://builds.apache.org/search/?q=incubator-commonsrdf)
and
[Travis CI](https://travis-ci.org/apache/incubator-commonsrdf).

[![Build Status](https://travis-ci.org/apache/incubator-commonsrdf.svg?branch=master)](https://travis-ci.org/apache/incubator-commonsrdf)

### CD40
_The full history of the project's code is available via a source code control system, in a way that allows any released version to be recreated._

Full history in [git.apache.org](https://git-wip-us.apache.org/repos/asf?p=incubator-commonsrdf.git). Each release has a
corresponding [tag](https://git-wip-us.apache.org/repos/asf?p=incubator-commonsrdf.git;a=tags)

The git commits includes pre-Apache history imported from https://github.com/commons-rdf/commons-rdf through software grant.


### CD50
_The provenance of each line of code is established via the source code control system, in a reliable way based on strong authentication of the committer. When third-party contributions are committed, commit messages provide reliable information about the code provenance._

Only `@apache.org` users have commit access. [Pull requests](https://github.com/apache/incubator-commonsrdf/pulls?q=is%3Apr%20)
are acknowledged in [pom.xml](https://github.com/apache/incubator-commonsrdf/blob/0.3.0-incubating/pom.xml#L116).

No contributions have been received so far from developers who have not signed an ICLA with ASF.

## Licenses and Copyright

### LC10
_The code is released under the Apache License, version 2.0._

[Apache License 2.0](https://github.com/apache/incubator-commonsrdf/blob/master/LICENSE) only.

### LC20
_Libraries that are mandatory dependencies of the project's code do not create more restrictions than the Apache License does._

Dependencies are [ASF-compatible](https://www.apache.org/legal/resolved)
open source licenses like Apache License 2.0, BSD, MIT, Eclipse Distribution License.

For details see each module:


* [Parent dependencies](https://commonsrdf.incubator.apache.org/dependencies.html)
* [API dependencies](https://commonsrdf.incubator.apache.org/commons-rdf-api/dependencies.html)
* [Simple dependencies](https://commonsrdf.incubator.apache.org/commons-rdf-simple/dependencies.html)
* [RDF4J dependencies](https://commonsrdf.incubator.apache.org/commons-rdf-rdf4j/dependencies.html)
* [Jena dependencies](https://commonsrdf.incubator.apache.org/commons-rdf-jena/dependencies.html)
* [JSONLD-Java dependencies](https://commonsrdf.incubator.apache.org/commons-rdf-jsonld-java/dependencies.html)
* [Integration test dependencies](https://commonsrdf.incubator.apache.org/commons-rdf-integration-tests/dependencies.html)

### LC30
_The libraries mentioned in LC20 are available as Open Source software._

Yes, see [LC20](#LC20).

### LC40

_Committers are bound by an Individual Contributor Agreement (the "Apache iCLA") that defines which code they are allowed to commit and how they need to identify code that is not their own._

All [Commons RDF committers](http://people.apache.org/phonebook.html?podling=commonsrdf) have ASF ICLAs on file and are Apache committers.

### LC50

_The copyright ownership of everything that the project produces is clearly defined and documented._

ASF copyright asserted in [NOTICE](https://github.com/apache/incubator-commonsrdf/blob/master/NOTICE)
and in ASF file headers.  

For details, see RAT report for each module:

* [Parent RAT report](https://commonsrdf.incubator.apache.org/rat-report.html)
* [API RAT report](https://commonsrdf.incubator.apache.org/commons-rdf-api/rat-report.html)
* [Simple RAT report](https://commonsrdf.incubator.apache.org/commons-rdf-simple/rat-report.html)
* [RDF4J RAT report](https://commonsrdf.incubator.apache.org/commons-rdf-rdf4j/rat-report.html)
* [Jena RAT report](https://commonsrdf.incubator.apache.org/commons-rdf-jena/rat-report.html)
* [JSONLD-Java RAT report](https://commonsrdf.incubator.apache.org/commons-rdf-jsonld-java/rat-report.html)
* [Integration test RAT report](https://commonsrdf.incubator.apache.org/commons-rdf-integration-tests/rat-report.html)


## Releases

### RE10

_Releases consist of source code, distributed using standard and open archive formats that are expected to stay readable in the long term._

[Releases](https://archive.apache.org/dist/incubator/commonsrdf/) archives are in `.tar.gz` and `.zip` formats.

All releases are made to [dist.apache.org](https://www.apache.org/dist/incubator/commonsrdf/) and
archived on [archive.apache.org](https://archive.apache.org/dist/incubator/commonsrdf/).


### RE20

_Releases are approved by the project's PMC (see CS10), in order to make them an act of the Foundation._

All ASF releases of Commons RDF are subject to a
[vote on dev@](https://lists.apache.org/list.html?dev@commonsrdf.incubator.apache.org:lte=100M:%5BVOTE%5D)
following the [ASF voting policy](https://www.apache.org/foundation/voting.html).

* [0.3.0-incubating VOTE](https://lists.apache.org/thread.html/d7e8d9b2276fed6b688b64d9096f02631a66eb01aaa9dde35d31bdf1@%3Cdev.commonsrdf.incubator.apache.org%3E) and [RESULT](https://lists.apache.org/thread.html/b3589f5fe64edb0b7cdb5d99750ff586d429df404e044d5664eb2068@%3Cgeneral.incubator.apache.org%3E)
* [0.2.0-incubating VOTE](https://lists.apache.org/thread.html/39066964fc8cbc3a634b7a847e23920a49d207ac8336d33b578e91a2@1463093135@%3Cdev.commonsrdf.incubator.apache.org%3E) and [RESULT](https://lists.apache.org/thread.html/c34a91b8d326badbd56ede16a7d4bbb9f5f69d5687991a63270b6981@1463641004@%3Cdev.commonsrdf.incubator.apache.org%3E)
* [0.1.0-incubating VOTE](https://lists.apache.org/thread.html/d7e8d9b2276fed6b688b64d9096f02631a66eb01aaa9dde35d31bdf1@%3Cdev.commonsrdf.incubator.apache.org%3E) and [RESULT](https://lists.apache.org/thread.html/0a5238f066d913cfb1b9e05f00ccfad27ade4939e20c67d8fd11ead0@1431673779@%3Cdev.commonsrdf.incubator.apache.org%3E)

### RE30

_Releases are signed and/or distributed along with digests that can be reliably used to validate the downloaded archives._

All releases have corresponding [PGP asc files](https://www.apache.org/dist/incubator/commonsrdf/0.3.0-incubating/apache-commons-rdf-0.3.0-incubating-src.zip.asc)
with a corresponding key listed in [KEYS](https://www.apache.org/dist/incubator/commonsrdf/KEYS).

Releases also have [.md5](https://www.apache.org/dist/incubator/commonsrdf/0.3.0-incubating/apache-commons-rdf-0.3.0-incubating-src.zip.md5) and
[.sha1](https://www.apache.org/dist/incubator/commonsrdf/0.3.0-incubating/apache-commons-rdf-0.3.0-incubating-src.zip.sha1) hashes, which corresponds
to the hashes in the vote emails and in Maven Central.

### RE40

_Convenience binaries can be distributed alongside source code but they are not Apache Releases -- they are just a convenience provided with no guarantee._

Convenience binaries are deployed to [Maven Central](https://repo1.maven.org/maven2/org/apache/commons/) under the `org.apache.commons` group ID, which include
the [source release](https://repo1.maven.org/maven2/org/apache/commons/commons-rdf-parent/0.3.0-incubating/) corresponding to the
the dist files. (see [RE30](#RE30) )


### RE50

_The release process is documented and repeatable to the extent that someone new to the project is able to independently generate the complete set of artifacts required for a release._

Documented as [RELEASE-PROCESS.md](https://github.com/apache/incubator-commonsrdf/blob/master/RELEASE-PROCESS.md) however this must be updated
to align with [Apache Commons release process](https://commons.apache.org/releases/index.html) with regards to site publication.

## Quality

### QU10
_The project is open and honest about the quality of its code. Various levels of quality and maturity for various modules are natural and acceptable as long as they are clearly communicated._

Commons RDF [use semantic versioning](https://commonsrdf.incubator.apache.org/#Modules).

[Experimental features](https://commonsrdf.incubator.apache.org/apidocs/org/apache/commons/rdf/experimental/RDFParser.html) are clearly documented as such.


### QU20
_The project puts a very high priority on producing secure software._

Following Apache Commons procedures, Commons RDF uses reports like
[Findbugs](https://commonsrdf.incubator.apache.org/commons-rdf-simple/findbugs.html),
[PMD](https://commonsrdf.incubator.apache.org/commons-rdf-simple/pmd.html),
[JDepend](https://commonsrdf.incubator.apache.org/commons-rdf-simple/jdepend-report.html)
and
[JaCoCo](https://commonsrdf.incubator.apache.org/commons-rdf-simple/jacoco/index.html).



### QU30
_The project provides a well-documented channel to report security issues, along with a documented way of responding to them._

Menu links to [Commons Security](https://commons.apache.org/security.html) - security reports
are reported to `private@commons` [Commons PMC](https://people.apache.org/phonebook.html?ctte=commons)
which include several of the Commons RDF committers.


### QU40
_The project puts a high priority on backwards compatibility and aims to document any incompatible changes and provide tools and documentation to help users transition to new features._

Even during `0.x` development the project has strived for backwards compatibility between releases,
see for instance this [japicmp report](https://commonsrdf.incubator.apache.org/commons-rdf-api/japicmp.html) and the use of
[@Deprecated](https://commonsrdf.incubator.apache.org/apidocs/org/apache/commons/rdf/api/RDFTermFactory.html).

(Note that the first 1.0 release is likely to remove those deprecated methods/classes from the 0.x series)


### QU50
_The project strives to respond to documented bug reports in a timely manner._

[Jira](https://issues.apache.org/jira/browse/COMMONSRDF) is used
both for feature suggestions and bug reports.


## Community

### CO10
_The project has a well-known homepage that points to all the information required to operate according to this maturity model._

Home page is https://commonsrdf.incubator.apache.org/

The pre-ASF website http://commons-rdf.github.io/ redirects to the above.

NOTE: As the project is proposing to graduate to [Apache Commons](https://commons.apache.org/) PMC,
the URL will change (with HTTP redirection) to https://commons.apache.org/proper/commons-rdf/

### CO20
_The community welcomes contributions from anyone who acts in good faith and in a respectful manner and adds value to the project._

Commons RDF [welcome contributions](https://commonsrdf.incubator.apache.org/contributing.html) through
mailing list and [pull requests](https://github.com/apache/incubator-commonsrdf/pulls).

### CO30
_Contributions include not only source code, but also documentation, constructive bug reports, constructive discussions, marketing and generally anything that adds value to the project._

Commons RDF  recognize all contributions, in particular new features from committers
are [raised as pull requests](https://github.com/apache/incubator-commonsrdf/pulls?q=is%3Apr+is%3Aclosed)
and discussed on `dev@commonsrdf`.

NOTE: As the project is proposing to graduate to Apache Commons, the mailing list will change to
`dev@commons` using the subject tag `[RDF]`.

Commons RDF also recognize non-code contributions, for instance
[this review](https://github.com/apache/incubator-commonsrdf/pull/23) meant
Adam Soraka was [listed as a reviewer](https://commonsrdf.incubator.apache.org/team-list.html)


### CO40
_The community is meritocratic and over time aims to give more rights and responsibilities to contributors who add value to the project._

Commons RDF recognizes contributors, but has not elected any new committers during incubation.

Most of the people involved with Apache Commons are already ASF committers; note
that [any ASF committer have write-access to Apache Commons code](https://commons.apache.org/index.html)
which Commons RDF will honour after graduation.


### CO50
_The way in which contributors can be granted more rights such as commit access or decision power is clearly documented and is the same for all contributors._

`TODO`: This documentation should be covered by
Apache Commons PMC, but don't currently have a good page for it beyond
[Volunteering](https://commons.apache.org/volunteering.html) and
[Contributing patches](https://commons.apache.org/patches.html).

### CO60
_The community operates based on consensus of its members (see CS10) who have decision power. Dictators, benevolent or not, are not welcome in Apache projects._

As this project was to form an API across existing RDF frameworks,
the quest for consensus has actually been a bit too strong in this project,
which caused several committers to retire from the project.

It has been agreed to aim for Apache Commons PMC as home, which
already operates on strong consensus model.

### CO70
_The project strives to answer user questions in a timely manner._

User engagement is currently done via
[dev@commonsrdf](https://lists.apache.org/list.html?dev@commonsrdf.incubator.apache.org)
but will transition to
[user@commons](https://lists.apache.org/list.html?user@commons.apache.org).


## Consensus Building

### CS10
_The project maintains a public list of its contributors who have decision power -- the project's PMC (Project Management Committee) consists of those contributors._

All contributors listed on [Team page](https://commonsrdf.incubator.apache.org/team-list.html) with their role indicated (e.g. PPMC Member).


## CS20
_Decisions are made by consensus among PMC members and are documented on the project's main communications channel. Community opinions are taken into account but the PMC has the final word if needed._

All decisions are made on [dev@commonsrdf](https://lists.apache.org/list.html?dev@commonsrdf.incubator.apache.org)
without much consideration about who is in PMC or not.

Note that some discussions happen in GitHub pull requests or
Jira issues, which are mirrored to the mailing list.

## CS30
_Documented voting rules are used to build consensus when discussion is not sufficient._

This only happened [once](https://lists.apache.org/thread.html/faba667bf0073c65ca75333733cabb5fc6a7bc0e6c9342b0edec6fad@1427234823@%3Cdev.commonsrdf.incubator.apache.org%3E)
which [caused  disagreements](https://lists.apache.org/thread.html/9210348a357827f31389f2f8f841cfcf8a7751791c6e677629bea566@1427276263@%3Cdev.commonsrdf.incubator.apache.org%3E).

## CS40
_In Apache projects, vetoes are only valid for code commits and are justified by a technical explanation, as per the Apache voting rules defined in CS30._

No vetoes so far, except soft vetoes during pull request reviews.


## CS50
_All "important" discussions happen asynchronously in written form on the project's main communications channel. Offline, face-to-face or private discussions 11 that affect the project are also documented on that channel._

All discussions are on list, see [CS20](#CS20).

## Independence

## IN10
_The project is independent from any corporate or organizational influence._

No particular influence has been noted from the corporations who employ
the committers, however the project communities
Commons RDF is integrating with have obviously
had an influence on its development.


## IN20
_Contributors act as themselves as opposed to representatives of a corporation or organization._

Commons RDF contributors have very much acted as themselves.
