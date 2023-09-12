#!/usr/bin/env groovy


node {
        def WORKSPACE = pwd()
        env.KUBECONFIG = pwd() + "/.kubeconfig"
}



pipeline {


    agent {
        label 'docker-slave'
    }



    stages {


        stage('Checkout') {

            steps {
                script {
                    checkout scm
                }
            }


        }
        //=================

        stage('Logging see Jenkins Log') {

                    steps {

                            sh """
                                java -version
                                gradle -version
                                npm -version
                                mvn help:evaluate -Dexpression=settings.localRepository

                               """
                    }

          }



        stage('Gradle Build') {


            steps {



                    sh """
                             set +x
                             echo "workspace:" ${WORKSPACE}
                             echo "User:" `whoami`
                             gradle  clean --no-daemon
                             gradle  -Pprofile=prod build


                                                         """



                script {

                    step([$class: 'ArtifactArchiver', artifacts: '**/build/libs/*.war, **/build/libs/*.jar', fingerprint: true])
                }
            }


        }


       //=====================
        stage('Create  Image Builder') {
            when {
                expression {
                    openshift.withCluster() {
                        openshift.withProject() {
                            return !openshift.selector("bc", "hisd3mk2srv-demo").exists();
                        }
                    }
                }
            }
            steps {
                script {
                    openshift.withCluster() {
                        openshift.withProject() {
                            openshift.newBuild("--name=hisd3mk2srv-demo","--binary=true")
                        }
                    }
                }
            }
        }



        stage("Build To Demo") {


            steps {
                    login()


                    sh """

                        rm -rf oc-build && mkdir -p oc-build/
                        cp build/libs/hismk2-0.0.1-SNAPSHOT.jar oc-build/HISD3.jar
                        cp -r fonts    oc-build/fonts
                        cp Dockerfile  oc-build/Dockerfile
                        oc start-build hisd3mk2srv-demo --from-dir=oc-build
                    """

            }

        }

        //=======================




    }


}


def login() {
}

def processStageResult() {

    if (currentBuild.result != null) {
        sh "exit 1"
    }
}