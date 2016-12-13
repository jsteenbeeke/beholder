var renderState = {
		states: {},
		set: function(id,state) {
			this.states[id] = state;
		},
		check: function(id,state) {
			if (id in this.states) {
				var actual = this.states[id];
				
				return actual === state;
			}
			
			return false;
		}
}