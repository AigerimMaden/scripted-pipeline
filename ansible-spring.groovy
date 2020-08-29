properties([
    parameters([
        string(defaultValue: '', description: 'Please enter VM IP', name: 'nodeIP', trim: true)
        ])
    ])
if (nodeIP.length() > 6) {
    node {
        stage('Pull Repo') {
            git branch: 'master', changelog: false, poll: false, url: 'https://github.com/ikambarov/ansible-Flaskex.git'
        }
        withEnv(['ANSIBLE_HOST_KEY_CHECKING=False', 'FLASKEX_REPO=https://github.com/spring-projects/spring-petclinic', 'FLASKEX_BRANCH=master']) {
            stage("Install Prerequisites"){
                ansiblePlaybook credentialsId: 'jenkins-master-ssh-key', inventory: '${nodeIP},', playbook: 'prerequisites.yml'
                }
        }  
    }
}
else {
    error 'Please enter valid IP address'
}