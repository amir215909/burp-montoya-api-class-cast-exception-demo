## Overview
This is a demo for reproducing the class cast exception when retrieving the list of HttpRequestResponse objects
from burp project data storage. 

## Steps to reproduce
1. Clone the repository.
2. Build the extension with `./gradlew shadow`.
3. Add a host to scope.
4. Send a request to scope.
5. Unload the extension. (The HttpRequestResponses should be saved to project data storage)
6. Load the extension. (It should load the saved HttpRequestResponses which is where the exception happens)

Output: `Error while retrieving: java.lang.ClassCastException - class burp.Zr4q cannot be cast to class burp.api.montoya.http.message.HttpRequestResponse (burp.Zr4q and burp.api.montoya.http.message.HttpRequestResponse are in unnamed module of loader 'app')`