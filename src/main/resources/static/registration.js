document.addEventListener("DOMContentLoaded", () => {
    document.getElementById("registerButton").addEventListener("click", async (event) => {
        event.preventDefault();

        try {
            const username = document.getElementById("username").value;
            const challenge = await startRegistration(username);
            const { publicKey, credentialId } = await createCredential(username, challenge);
            await finishRegistration(username, publicKey, credentialId);
            alert("Registration successful. Redirecting to login page...");
            window.location.href = "/authentication";
        } catch (error) {
            console.error("Registration failed", error);
            alert("Registration failed");
        }
    });
});


async function startRegistration(username) {
    const response = await fetch("/register/start", {
        method: "POST",
        headers: {
            "Content-Type": "application/json",
        },
        body: JSON.stringify({ username }),
    });
    if (!response.ok) {
        throw new Error("Failed to start registration");
    }
    const { challenge } = await response.json();
    return challenge;
}

async function createCredential(username, challenge) {
    const credential = await navigator.credentials.create({
        publicKey: {
            challenge: new Uint8Array(atob(challenge).split("").map(c => c.charCodeAt(0))),
            rp: { name: "Demo Application" },
            user: {
                id: new TextEncoder().encode(username),
                name: username,
                displayName: "John Doe",
            },
            pubKeyCredParams: [
                { type: "public-key", alg: -7 },  // EC P256
                { type: "public-key", alg: -257 },  // RSA
            ],
        }
    });

    return {
        publicKey: btoa(String.fromCharCode(...new Uint8Array(credential.response.getPublicKey()))),
        credentialId: btoa(String.fromCharCode(...new Uint8Array(credential.rawId))),
    };
}

async function finishRegistration(username, publicKey, credentialId) {
    const response = await fetch("/register/finish", {
        method: "POST",
        headers: {
            "Content-Type": "application/json",
        },
        body: JSON.stringify({
            username,
            publicKey,
            credentialId
        })
    });
    if (!response.ok) {
        throw new Error("Failed to finish registration");
    }
}