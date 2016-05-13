# Commons RDF (incubating) release process

1. Clean build: `mvn clean install`
2. RAT checking: `mvn apache-rat:check`
3. Prepare the release: `mvn release:prepare -DreleaseVersion=0.2.0-incubating -DdevelopmentVersion=0.3.0-incubating-SNAPSHOT -DautoVersionSubmodules=true`
4. Perform the release: `mvn release:perform -Prelease`
5. Close the staging repository at https://repository.apache.org/#stagingRepositories
6. Push the code: `git push` and tag `git push --tags`
7. Cast the vote mail to dev@commonsrdf

Notice that the `maven-release-plugin` is configured to use the local git repository as well as not push changes 
to `origin`. Therefore the process can be reverted (e.g., `git reset HEAD~1`) at any time before the sixth step.

Acknowledgements to the [Marmotta's release process](https://wiki.apache.org/marmotta/ReleaseProcess) that heavily 
inspired this one.
