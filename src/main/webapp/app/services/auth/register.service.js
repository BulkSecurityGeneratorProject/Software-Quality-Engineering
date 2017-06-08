(function () {
	'use strict';

	angular
		.module('acmeApp')
		.factory('Register', Register);

	Register.$inject = ['$resource'];

	function Register($resource) {
		return $resource('api/register', {}, {});
	}
})();
