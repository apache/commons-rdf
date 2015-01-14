# Release

* Open an issue on Github to let others know that a release is planned soon to allow for comments
* Make sure that the latest snapshot on Sonatype OSS Snapshots repository is current by deploying the snapshot before release
* Make sure that TravisCI is building successfully
* When/if comments on the release are resolved or they are all positive, change version number manually or using:
 * mvn versions:set
* Then commit to git:
 * git commit pom.xml
* Then deploy to Sonatype using:
 * mvn -Psonatype-oss,java8 clean deploy
 * mvn -Psonatype-oss,java6 clean deploy
* Login to https://oss.sonatype.org/ and close the repository to ensure that it was a successful deployment
* If the deployment was successful, then release the repository (closing the repository does not do the release)
