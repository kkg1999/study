#include <iostream>
#include <thread>
#include <string>
#include <chrono>
using namespace std;

void sayHello(string s, int n) 
{
    for(int i=0; i<n; i++){
        cout << s << endl;
        this_thread::sleep_for(chrono::milliseconds(500));
    }
}

int main() 
{
    unsigned int hardware_threads = thread::hardware_concurrency();
    cout << "Your machine can run " << hardware_threads << " threads simultaneously.\n";

    thread t1(sayHello, "Hello from thread 1", 5);
    thread t2(sayHello, "Hello from thread 2", 5);
    
    this_thread::sleep_for(chrono::milliseconds(5000));
    cout << "Hello, World!" << endl;

    t1.join();
    t2.join();

    
    return 0;
}
