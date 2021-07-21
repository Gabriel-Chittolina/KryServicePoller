export function onError(error) {
    let message = error.toString();
  
    if (error.status === 401) {
      message = "Wrong Credentials.";
    }
    else if(error.status === 422){
        message = "Username already in use! Try another";
    }
  
    alert(message);
  }