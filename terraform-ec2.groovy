properties([
    parameters([
        booleanParam(defaultValue: true, description: 'Do you want to run terrform apply', name: 'terraform_apply'),
        booleanParam(defaultValue: false, description: 'Do you want to run terrform destroy', name: 'terraform_destroy'),
        choice(choices: ['dev', 'qa', 'prod'], description: '', name: 'environment'),
        string(defaultValue: '', description: 'Provide AMI ID', name: 'ami_id', trim: false)
    ])
])
def aws_region_var = ''
if(params.environment == "dev"){
    aws_region_var = "us-east-1"
}
else if(params.environment == "qa"){
    aws_region_var = "us-east-2"
}
else if(params.environment == "prod"){
    aws_region_var = "us-west-2"
}
def tf_vars = """
    s3_bucket = \"my-content-aigerim\"
    s3_folder_project = \"terraform_ec2\"
    s3_folder_region = \"us-east-1\"
    s3_folder_type = \"class\"
    s3_tfstate_file = \"infrastructure.tfstate\"
    environment = \"${params.environment}\"
    region      = \"${aws_region_var}\"
    public_key  = \"ssh-rsa AAAAB3NzaC1yc2EAAAADAQABAAABgQDNTLos5MAIRhPcbYAsHxx6HFywBsBPyzWgundc6zxI9UUkdxGJmpCHI0rGQ/T6nbJzy4rFDB1z+51IVTy+A11WLwMlKpp9aPGCowkf9fGFSUHI0TEDUvRzogwk+GK2g+pTSRqaB/blvSqls0gcRUN7m6ZCvqQATNIS7jBXAMFqDPi3FUEcDzb0BgeeUhEaGBI7z0RM0U5ThUjrNj0YuOxs6g04ZY4v1Mjvy7h3uQRz8wL8OoulWmDi6zFOjHcEJCo/U1/ntbkfHnicywWNGPo/SMGLhBA5a2nZyUFLuO95K2IoxgPJCpxcyUbP0047KEbyUkss1xx+QmfCGk9ShIXoq0KGZwpNgb3GPxP7SzjgSl44qWcXs8+dwiqdSk5OeeoTtwzc+x/ktKXlPvUXnMxr2DVFpxR798NXgOh/bMJPOV5Fs0l5VunchrYFJRIn56N9dn2Ov+1mv//+QMbaPON2NpLOcZgbglxxFFJ32awebK4xcBFUygfNUd4HIqWnGaM= User@DESKTOP-N1IV3VT"
    ami_id      = \"${params.ami_id}\"
"""
node{
    stage("Pull Repo"){
        cleanWs()
        git url: 'https://github.com/ikambarov/terraform-ec2.git'
    }
    withCredentials([usernamePassword(credentialsId: 'jenkins-aws-access-key', passwordVariable: 'AWS_SECRET_ACCESS_KEY', usernameVariable: 'AWS_ACCESS_KEY_ID')]) {
        withEnv(["AWS_REGION=${aws_region_var}"]) {
            stage("Terrraform Init"){
                writeFile file: "${params.environment}.tfvars", text: "${tf_vars}"
                sh """
                    bash setenv.sh ${environment}.tfvars
                    terraform-0.13 init
                """
            }        
            if (terraform_apply.toBoolean()) {
                stage("Terraform Apply"){
                    sh """
                        terraform-0.13 apply -var-file ${environment}.tfvars -auto-approve
                    """
                }
            }
            else if (terraform_destroy.toBoolean()) {
                stage("Terraform Destroy"){
                    sh """
                        terraform-0.13 destroy -var-file ${environment}.tfvars -auto-approve
                    """
                }
            }
            else {
                stage("Terraform Plan"){
                    sh """
                        terraform-0.13 plan -var-file ${environment}.tfvars
                    """
                }
            }
        }        
    }    
}