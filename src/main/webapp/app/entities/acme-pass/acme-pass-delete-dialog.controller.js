(function () {
	'use strict';

	angular
		.module('acmeApp')
		.controller('ACMEPassDeleteController', ACMEPassDeleteController);

	ACMEPassDeleteController.$inject = ['$uibModalInstance', 'entity', 'ACMEPass'];

	function ACMEPassDeleteController($uibModalInstance, entity, ACMEPass) {
		var vm = this;

		vm.acmePass = entity;
		vm.clear = clear;
		vm.confirmDelete = confirmDelete;

		function clear() {
			$uibModalInstance.dismiss('cancel');
		}

		function confirmDelete(id) {
			ACMEPass.delete({id: id},
			function () {
				$uibModalInstance.close(true);
			});
		}
	}
})();
