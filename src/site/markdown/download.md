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

# Download Commons RDF


## Maven

Apache Commons RDF (incubating) is available from
[Maven Central](http://central.maven.org/maven2/org/apache/commons/commons-rdf-api/),
mirrored from
[ASF's Maven repository](https://repository.apache.org/content/repositories/releases/org/apache/commons/commons-rdf-api/).
For convenience of IDE users, the Maven artifacts include `-javadoc.jar` and
`-sources.jar`, however you might prefer the
online [API javadoc](apidocs/)
and the [source code releases](#Source_code) (see below).

To use Commons RDF with [Maven](https://maven.apache.org/), add to your `pom.xml`:

```xml
<dependencies>
  <dependency>
      <groupId>org.apache.commons</groupId>
      <artifactId>commons-rdf-api</artifactId>
      <version>0.1.0-incubating</version>
  </dependency>
  <!-- and optionally: -->
  <dependency>
      <groupId>org.apache.commons</groupId>
      <artifactId>commons-rdf-simple</artifactId>
      <version>0.1.0-incubating</version>
      <optional>true</optional>
  </dependency>
</dependencies>
```

The `<version>` above might not be up to date,
see the [source code releases](#Source_code) below to find the latest version.

See the [user guide](userguide.html) for documentation of the
Apache Commons RDF API.  

## Source code

Here you can find all source releases published by Apache Commons RDF (incubating).

### 0.1.0-incubating

**Apache Commons RDF 0.1.0-incubating** was published on 2015-05-15, and is available for download
from official mirrors of the
ASF Distribution Directory [incubator/commonsrdf](https://www.apache.org/dyn/closer.cgi/incubator/commonsrdf/0.1.0-incubating/):

* [apache-commons-rdf-0.1.0-incubating-src.zip](https://www.apache.org/dyn/closer.cgi/incubator/commonsrdf/0.1.0-incubating/apache-commons-rdf-0.1.0-incubating-src.zip)
  ([asc](https://dist.apache.org/repos/dist/release/incubator/commonsrdf/0.1.0-incubating/apache-commons-rdf-0.1.0-incubating-src.zip.asc),
  [md5](https://dist.apache.org/repos/dist/release/incubator/commonsrdf/0.1.0-incubating/apache-commons-rdf-0.1.0-incubating-src.zip.md5),
  [sha1](https://dist.apache.org/repos/dist/release/incubator/commonsrdf/0.1.0-incubating/apache-commons-rdf-0.1.0-incubating-src.zip.sha1))
* [apache-commons-rdf-0.1.0-incubating-src.tar.gz](https://www.apache.org/dyn/closer.cgi/incubator/commonsrdf/0.1.0-incubating/apache-commons-rdf-0.1.0-incubating-src.tar.gz)
  ([asc](https://dist.apache.org/repos/dist/release/incubator/commonsrdf/0.1.0-incubating/apache-commons-rdf-0.1.0-incubating-src.tar.gz.asc),
  [md5](https://dist.apache.org/repos/dist/release/incubator/commonsrdf/0.1.0-incubating//apache-commons-rdf-0.1.0-incubating-src.tar.gz.md5),
  [sha1](https://dist.apache.org/repos/dist/release/incubator/commonsrdf/0.1.0-incubating/apache-commons-rdf-0.1.0-incubating-src.tar.gz.sha1))

After downloading the files, check the signatures using the following [KEYS](https://dist.apache.org/repos/dist/release/incubator/commonsrdf/KEYS)
file. The [changelog](https://issues.apache.org/jira/secure/ReleaseNote.jspa?projectId=12316620&amp;version=12332056)
is available from the [Apache Commons RDF Jira](https://issues.apache.org/jira/browse/COMMONSRDF).
