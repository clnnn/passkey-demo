document.addEventListener("DOMContentLoaded", () => {
    document.getElementById("loginButton").addEventListener("click", async (event) => {
        event.preventDefault();

        try {
            const username = document.getElementById("username").value;
            const { challenge, credentialId } = await startAuthentication(username);
            const { signedChallenge, clientDataJSON, authenticatorData} = await getCredential(username, challenge, credentialId);
            await finishAuthentication(username, challenge, signedChallenge, clientDataJSON, authenticatorData);
            alert("Login successful");
        } catch (error) {
            console.error("Login failed", error);
            alert("Login failed");
        }
    });
});

async function startAuthentication(username) {
    const response = await fetch("/auth/start", {
        method: "POST",
        headers: {
            "Content-Type": "application/json",
        },
        body: JSON.stringify({ username }),
    });
    if (!response.ok) {
        throw new Error("Failed to start authentication");
    }
    return await response.json();
}

async function getCredential(username, challenge, credentialId) {
    const credential = await navigator.credentials.get({
        publicKey: {
            challenge: new Uint8Array(atob(challenge).split("").map(c => c.charCodeAt(0))),
            allowCredentials: [{
                type: "public-key",
                id: new Uint8Array(atob(credentialId).split("").map(c => c.charCodeAt(0))),
            }],
        }
    });


    return {
        signedChallenge: btoa(String.fromCharCode(...new Uint8Array(credential.response.signature))),
        clientDataJSON: btoa(String.fromCharCode(...new Uint8Array(credential.response.clientDataJSON))),
        authenticatorData: btoa(String.fromCharCode(...new Uint8Array(credential.response.authenticatorData))),
    };
}

async function finishAuthentication(username, challenge, signedChallenge, clientDataJSON, authenticatorData) {
    const response = await fetch("/auth/finish", {
        method: "POST",
        headers: {
            "Content-Type": "application/json",
        },
        body: JSON.stringify({
            username,
            challenge,
            signedChallenge,
            clientDataJSON,
            authenticatorData,
        })
    });
    if (!response.ok) {
        throw new Error("Failed to finish authentication");
    }
}
