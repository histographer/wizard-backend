echo "Creating users..."
mongo admin --host wizard_test_mongo -u root -p secret --eval "db.createUser({user: 'test_user', pwd: 'test_password',roles: [{role: 'readWrite', db: 'wizard'}]});"
echo "Users created."