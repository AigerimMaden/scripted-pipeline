properties([
    parameters([
        string(defaultValue: '', description: 'Please enter VM IP', name: 'nodeIP', trim: true)
        ])
    ])
if (nodeIP.length() > 6) {
    node {
        stage('Pull Repo') {
            git branch: 'master', changelog: false, poll: false, url: 'https://github.com/AigerimMaden/ansible-spring.git'
        }
        withEnv(['ANSIBLE_HOST_KEY_CHECKING=False', 'SPRING_REPO=https://github.com/spring-projects/spring-petclinic.git', 'SPRING_BRANCH=master']) {
            stage("Install Prerequisites"){
                ansiblePlaybook credentialsId: 'jenkins-master-ssh-key', inventory: '${nodeIP},', playbook: 'prerequisites.yaml'
                }
            stage('Pull Repo') {
                git branch: 'master', changelog: false, poll: false, url: 'https://github.com/spring-projects/spring-petclinic.git'
                }
            stage("Install Java"){
                ansiblePlaybook credentialsId: 'jenkins-master-ssh-key', inventory: '${nodeIP},', playbook: 'java.yaml'
                }
            stage("Install mvn"){
                ansiblePlaybook credentialsId: 'jenkins-master-ssh-key', inventory: '${nodeIP},', playbook: 'mvn.yaml'
                }
            stage("Start spring"){
                ansiblePlaybook credentialsId: 'jenkins-master-ssh-key', inventory: '${nodeIP},', playbook: 'start.yaml'
                }
        }  
    }
}
else {
    error 'Please enter valid IP address'
}