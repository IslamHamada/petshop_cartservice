node {
    def repourl = "islamhamada/petshop"
    def mvnHome = tool name: 'maven', type: 'maven'
    def mvnCMD = "${mvnHome}/bin/mvn"
    def version = sh(script: "date +%s", returnStdout: true).trim()
    stage('Checkout Contracts') {
        checkout([$class: 'GitSCM',
                  branches: [[name: '*/main']],
                  userRemoteConfigs: [[credentialsId: 'git',
                                       url: 'https://github.com/IslamHamada/petshop_contracts.git']]])
    }
    stage('Build Contracts') {
        sh("${mvnCMD} clean install")
    }
    stage('Checkout Cart-Service'){
        checkout([$class: 'GitSCM',
                  branches: [[name: '*/hetzner']],
                  userRemoteConfigs: [[credentialsId: 'git',
                                       url: 'https://github.com/IslamHamada/petshop_cartservice.git']]])
    }
    stage('Build and Push Cart-Service') {
        withCredentials([usernamePassword(credentialsId: 'dockerhub', usernameVariable: 'DOCKER_USER', passwordVariable: 'DOCKER_PASS')]) {
            sh("${mvnCMD} clean install jib:build -DREPO_URL=${repourl} -DVERSION=${version} -Djib.to.auth.username=$DOCKER_USER -Djib.to.auth.password=$DOCKER_PASS")
        }
    }
    stage('Deploy') {
        sh("sed -i 's|IMAGE_URL|${repourl}|g' k8s/deployment.yaml")
        sh("sed -i 's|TAG|cart-service-${version}|g' k8s/deployment.yaml")
        step([$class: 'KubernetesEngineBuilder',
              projectId: env.PROJECT_ID,
              clusterName: env.CLUSTER,
              location: env.ZONE,
              manifestPattern: 'k8s/deployment.yaml',
              credentialsId: env.PROJECT_ID,
              verifyDeployments: true])
    }
}