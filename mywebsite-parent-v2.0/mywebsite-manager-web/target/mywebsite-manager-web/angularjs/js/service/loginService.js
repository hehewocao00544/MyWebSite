app.service('loginService', function ($http) {

    this.login = function (entity) {

        return $http.post('/login', entity);
    };
});