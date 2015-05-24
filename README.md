## Usage

#### Créer le conteneur `ctr_gatling`

    sudo docker run -d -t -p 8080:80 --name ctr_gatling img_gatling

#### Accéder au conteneur `ctr_gatling`

    # http://localhost:8080
    
    #lancer un test
    sudo nsenter --target $(sudo docker inspect --format {{.State.Pid}} ctr_gatling) --mount --uts --ipc --net --pid
    cd /usr/local/gatling/bin
    ./gatling.sh
	

    
    
