db.createUser({
    user: 'dev_user',
    pwd: 'dev_user',
    roles: [
        {
            role: 'readWrite',
            db: 'wizard'
        }
    ]
})
