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

# Commons RDF Site

Some common tasks:

* Build the site: `mvn clean package site` and then access the generated site at `target/site/index.html`

* Publish the site: `mvn clean package site scm-publish:publish-scm`

You will need to have `svn` installed.

Further details at https://commons.apache.org/site-publish.html

The first time you publish, you might need to tell `svn` your Apache password
by doing the commit manually:

```
stain@biggie:~/src/incubator-commonsrdf$ cd target/site-content/
stain@biggie:~/src/incubator-commonsrdf/target/site-content$ svn commit -m "Updated website"
Authentication realm: <https://svn.apache.org:443> ASF Committers
Password for 'stain': *******************

Sending        apidocs/org/apache/commons/rdf/api/BlankNode.html
Sending        apidocs/org/apache/commons/rdf/api/IRI.html
Sending        index.html
Sending        mail-lists.html
Sending        project-info.html
Transmitting file data ......................
Committed revision 961349.
```
