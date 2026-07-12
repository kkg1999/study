// find unique paths from (bottom left) to (bottom right)
// moves allowed: row wise right and diagonal right

#include <vector>
#include <iostream>
using namespace std;


int unique_paths(int row, int col) // grid size
{
	vector<int> col1(row, 0);

	col1[0] = 1;// base case
	for(int c=1; c<col; c++){
		vector<int> col2(row, 0);
		for(int r=0; r <= min(c, row-1); r++){
			col2[r] += col1[r];
			if(r>0) col2[r] += col1[r-1];
			if(r<c && r+1<row) col2[r] += col1[r+1];
		}
		swap(col1, col2);
	}

	cout<<col1[0]<<endl;
	return col1[0];
}

int main()
{

	unique_paths(6, 6);

	return 0;
}
