const io = require("socket.io")(3000);

let users = []; //Socket - Username

io.on("connect", (socket) => {
  users[socket.id] = { name: "Anonymous" }; //Nom par défaut

  //On liste les utilisateurs disponibles, en faisant :
  // - On prend les entrées de users
  // - On filtre les utilisateurs qui n'ont pas de room et qui ne sont pas le socket actuel
  // - On prend les id des utilisateurs restants
  const availableUsers = Object.entries(users)
    .filter(([socketId, user]) => socketId != socket.id && !user.room)
    .map(([socketId, user]) => socketId);

  if (availableUsers.length > 0) {
    let randomUser =
      availableUsers[Math.floor(Math.random() * availableUsers.length)];

    users[socket.id]["room"] = randomUser;
    users[randomUser]["room"] = socket.id;

    io.to(socket.id).emit("match", "ALLOO");
  }

  //S'il veut changer de nom
  socket.on("updateName", (name) => {
    users[socket.id]["name"] = name;
  });

  socket.on("msgSent", (msg) => {
    console.log(users);
    if (users[socket.id]["room"] !== undefined) {
      console.log(msg);
      io.to(users[socket.id]["room"]).emit("msgReceived", msg);
    }
  });

  socket.on("disconnect", () => {
    users = users.filter(([socketId, user]) => socketId != socketId);
  });
});
