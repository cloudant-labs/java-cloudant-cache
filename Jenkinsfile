#!groovy

/*
 * Copyright © 2017 IBM Corp. All rights reserved.
 *
 * Licensed under the Apache License, Version 2.0 (the "License"); you may not use this file
 * except in compliance with the License. You may obtain a copy of the License at
 *
 * http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software distributed under the
 * License is distributed on an "AS IS" BASIS, WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND,
 * either express or implied. See the License for the specific language governing permissions
 * and limitations under the License.
 */

stage('Build') {
    // Checkout, build and assemble the source and doc
    node {
        checkout scm
        sh './gradlew clean assemble'
        stash name: 'built'
    }
}

stage('Test') {
    node {
        // Unstash the built code
        unstash name: 'built'
        withCredentials([[$class: 'UsernamePasswordMultiBinding', credentialsId: 'clientlibs-test', usernameVariable: 'DB_USER', passwordVariable: 'DB_PASSWORD']]) {
            // Run the core and in-process tests
            // Note Redis server not available in this env, so those tests excluded (but tested by Travis CI)
            try {
                sh "./gradlew -Dtest.couch.url=https://${env.DB_USER}:${env.DB_PASSWORD}@clientlibs-test.cloudant.com :cloudant-client-cache:test :cloudant-client-cache-in-process:test"
            } finally {
                junit '**/build/test-results/*.xml'
            }
        }
    }
}

// Publish the master branch
stage('Publish') {
    if (env.BRANCH_NAME == "master") {
        node {
            checkout scm // re-checkout to be able to git tag
            unstash name: 'built'
            // read the version name and determine if it is a release build
            version = readFile('VERSION').trim()
            isReleaseVersion = !version.toUpperCase(Locale.ENGLISH).contains("SNAPSHOT")

            // Upload using the ossrh creds (upload destination logic is in build.gradle)
            withCredentials([usernamePassword(credentialsId: 'ossrh-creds', passwordVariable: 'OSSRH_PASSWORD', usernameVariable: 'OSSRH_USER'), usernamePassword(credentialsId: 'signing-creds', passwordVariable: 'KEY_PASSWORD', usernameVariable: 'KEY_ID'), file(credentialsId: 'signing-key', variable: 'SIGNING_FILE')]) {
                sh './gradlew -Dsigning.keyId=$KEY_ID -Dsigning.password=$KEY_PASSWORD -Dsigning.secretKeyRingFile=$SIGNING_FILE -DossrhUsername=$OSSRH_USER -DossrhPassword=$OSSRH_PASSWORD upload'
            }

            // if it is a release build then do the git tagging
            if (isReleaseVersion) {

                // Read the CHANGES.md to get the tag message
                tagMessage = ''
                for (line in readFile('CHANGES.md').readLines()) {
                    if (!''.equals(line)) {
                        // append the line to the tagMessage
                        tagMessage = "${tagMessage}${line}\n"
                    } else {
                        break
                    }
                }

                // Use git to tag the release at the version
                try {
                    // Awkward workaround until resolution of https://issues.jenkins-ci.org/browse/JENKINS-28335
                    withCredentials([[$class: 'UsernamePasswordMultiBinding', credentialsId: 'github-token', usernameVariable: 'GIT_USERNAME', passwordVariable: 'GIT_PASSWORD']]) {
                        sh "git config user.email \"nomail@hursley.ibm.com\""
                        sh "git config user.name \"Jenkins CI\""
                        sh "git config credential.username ${env.GIT_USERNAME}"
                        sh "git config credential.helper '!echo password=\$GIT_PASSWORD; echo'"
                        sh "git tag -a ${version} -m '${tagMessage}'"
                        sh "git push origin ${version}"
                    }
                } finally {
                    sh "git config --unset credential.username"
                    sh "git config --unset credential.helper"
                }
            }
        }
    }
}
