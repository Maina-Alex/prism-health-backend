db.createUser(
    {
        user : "prismhealth",
        pwd  : "prismhealth",
        roles : [
            {
                role : "readWrite",
                db   : "UsersDb"
            }
        ]
    }
)