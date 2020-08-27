node ('master'){
    stage('Init'){
        withCredentials([sshUserPrivateKey(credentialsId: 'jenkins-master-ssh-key', keyFileVariable: 'sshkey', passphraseVariable: '', usernameVariable: 'sshuser')]) {
            sh 'ssh -o StrictHostKeyChecking=no -i $sshkey root@161.35.109.58 yum install epel-release -y'
        }
    }
}