# Commons RDF (incubating) release process

1. Clean build: `mvn clean install`
2. RAT checking: `mvn apache-rat:check`
3. Prepare the release: `mvn release:prepare -DreleaseVersion=0.2.0-incubating -DdevelopmentVersion=0.3.0-incubating-SNAPSHOT -DautoVersionSubmodules=true`
4. Perform the release: `mvn release:perform`
5. Close the staging repository at https://repository.apache.org/#stagingRepositories
6. Push the code: `git push` and tag `git push --tags`
6. Cast the vote mail to dev@commonsrdf
