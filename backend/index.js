const http = require("http");
const server = http.createServer();
const io = require("socket.io")(server);

let users = []; //Socket - Username

io.on("connect", (socket) => {
  users[socket.id] = {}; //On ajoute l'utilisateur dans la liste
  console.log("Connexion nouvel utilisateur !");
  //On liste les utilisateurs disponibles, en faisant :
  // - On prend les entrées de users
  // - On filtre les utilisateurs qui n'ont pas de room et qui ne sont pas le socket actuel
  // - On prend les id des utilisateurs restants

  socket.on("msgSent", (msg) => {
    console.log(msg);
    if (users[socket.id]["room"] !== undefined) {
      console.log(msg);
      io.to(users[socket.id]["room"]).emit("msgReceived", msg);
    } else {
      socket.emit("error", "NO_ROOM");
    }
  });

  socket.on("endChatRequest", () => {
    console.log(users);
    if (users[socket.id]["room"] !== undefined) {
      io.to(users[socket.id]["room"]).emit("endChat");
      users[users[socket.id]["room"]]["room"] = undefined;
      users[socket.id]["room"] = undefined;
    } else {
      socket.emit("error", "NO_ROOM");
    }
    console.log(users);
  });

  socket.on("disconnect", () => {
    users = users.filter(([socketId, user]) => socketId != socketId);
  });

  //Après avoir tapé un nom et cliqué sur le bouton "recherche"
  socket.on("beginSearch", (name) => {
    if (!users[socket.id]) {
      //Si l'utilisateur n'est pas dans la liste
      users[socket.id] = {}; //On ajoute l'utilisateur dans la liste
    }
    users[socket.id]["name"] = name;
    console.log("Recherche de la part de : ", name);

    //On refuse s'il est déjà en match (REFAIRE POUR QUE CA MARCHE)
    //if(users[socket.id]["room"] != undefined) return;

    const availableUsers = Object.entries(users)
      .filter(
        ([socketId, user]) =>
          socketId != socket.id && !user.room && user["name"] != undefined
      )
      .map(([socketId, user]) => socketId);
    console.log("Personnes dispo : ", availableUsers);
    if (availableUsers.length > 0) {
      let randomUser =
        availableUsers[Math.floor(Math.random() * availableUsers.length)];

      users[socket.id]["room"] = randomUser;
      users[randomUser]["room"] = socket.id;
      console.log(
        "Match entre",
        users[socket.id]["name"],
        "et",
        users[randomUser]["name"]
      );
      io.to(socket.id).emit("match", users[randomUser]["name"]);
      io.to(randomUser).emit("match", users[randomUser]["name"]);
    }
  });

  socket.on("*", function (event, data) {
    console.log(event);
    console.log(data);
  });
});

server.listen(3000, () => {
  console.log("Serveur Socket.IO en écoute sur le port 3000");
});
