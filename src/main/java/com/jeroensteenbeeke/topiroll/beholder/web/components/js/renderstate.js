var renderState = {
		states: {},
		set: function(id,state) {
			this.states[id] = state;
		},
		check: function(id,state) {
			if (id in this.states) {
				return this.states[id] === state;
			}
			
			return false;
		}
}