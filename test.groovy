node {
    stage("Pull Repo"){
        git branch: 'solution', url: 'https://github.com/AigerimMaden/terraform-task.git'    }
    stage("Pull Repo"){
        ssh 'echo "Terraform Init"'
    }
    stage("Pull Repo"){
        ssh 'echo "Terraform Apply"'
    }
}