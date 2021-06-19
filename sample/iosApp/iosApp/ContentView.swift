import SwiftUI
import shared

struct ContentView: View {
	let greet = Greeting().greeting()
    @ObservedObject var model = TestModel()

	var body: some View {
        Text(greet).onAppear(perform: {
            model.loadRequest()
        })
	}
}

struct ContentView_Previews: PreviewProvider {
	static var previews: some View {
	ContentView()
	}
}

class TestModel : ObservableObject {
    lazy var client = NetworkClient()
    
    func loadRequest() {
        client.request{ (json) in
           print(json)
        }
    }
}
