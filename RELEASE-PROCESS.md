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

# Commons RDF release process

1. Update documentation (`RELEASE-NOTES.md`, `README.md`, version numbers in `src/site/`)
2. Clean build: `mvn clean install`
3. RAT checking: `mvn apache-rat:check`
4. Prepare the release: `mvn release:prepare -DreleaseVersion=0.4.0 -DdevelopmentVersion=0.5.0-SNAPSHOT -DautoVersionSubmodules=true`
5. Perform the release: `mvn release:perform -Prelease`
6. Close the staging repository at https://repository.apache.org/#stagingRepositories
7. Push the code: `git push` and tag `git push --tags`
8. Cast the `[VOTE]` mail to dev@commons

Notice that the `maven-release-plugin` is configured to use the local git repository as well as not push changes 
to `origin`. Therefore the process can be reverted (e.g., `git reset HEAD~1`) at any time before the sixth step.

Acknowledgements to the [Marmotta's release process](https://wiki.apache.org/marmotta/ReleaseProcess) that heavily 
inspired this one.
