def installTrivy(){
    sh 'docker pull aquasec/trivy:latest'
}

def installKubeScore(){
    sh 'docker pull zegl/kube-score:latest'
}